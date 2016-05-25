package com.boubei.tss.cache.aop;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.TimeWrapper;
import com.boubei.tss.cache.extension.CacheLife;
import com.boubei.tss.framework.exception.BusinessException;
 
/**
 * 将耗时查询（如report）的执行中的查询缓存起来。
 * 如果同一个用户对同一报表，相同查询条件的查询还在执行中，则让后面的请求进入等待状态。
 * 等第一次的查询执行完成，然后后续的查询直接取缓存里的数据。
 * 这样可以防止用户重复点击查询，造成性能瓶颈。 
 * 
 * 测试时，可以把第一次查询设置断点断住，来模拟耗时的report查询过程。
 */
@Component("queryCacheInterceptor")
public class QueryCacheInterceptor implements MethodInterceptor {

    protected Logger log = Logger.getLogger(this.getClass());

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod(); /* 获取目标方法 */
        Object[] args = invocation.getArguments();    /* 获取目标方法的参数 */
        
        QueryCached annotation = targetMethod.getAnnotation(QueryCached.class); // 取得注释对象
        if (annotation == null) {
        	return invocation.proceed(); /* 如果没有配置缓存，则直接执行方法并返回结果 */
        }
 
		Pool cache = JCache.getInstance().getPool(CacheLife.SHORT.toString());
		
		Class<?> declaringClass = targetMethod.getDeclaringClass();
		String key = "QC_" + declaringClass.getName() + "." + targetMethod.getName();
        key += "(";
        if(args != null && args.length > 0) {
        	int index = 0;
        	for(Object arg : args) {
        		if( index++ > 0) {
        			key += ", ";
        		}
        		key += arg;
        	}
        }
        key += ")";
        TimeWrapper cacheItem = (TimeWrapper) cache.getObject(key);
		
		Object returnVal;
		long currentThread = Thread.currentThread().getId();
		
		if (cacheItem != null) {
			Integer count = (Integer) cacheItem.getValue();
			cacheItem.update( ++count );
			log.debug(currentThread + " QueryCache【"+key+"】= " + count);
			
			// 等待执行中的上一次请求先执行完成； 
			long start = System.currentTimeMillis();
			while( cache.getObject(key) != null ) {
				Thread.sleep(500 * count);
				log.debug(currentThread + " QueryCache waiting...");
				
				// 超过10分钟，说明执行非常缓慢，则不再继续等待，同时抛错提示用户。
				if(System.currentTimeMillis() - start > 10*60*1000) {
					throw new BusinessException("本次请求执行缓慢，请稍后再操作。");
				}
			}
			
			returnVal = invocation.proceed(); // 此时去执行查询，结果已经在3分钟的cache中
		} 
		else {
			cache.putObject(key, 1); // 缓存执行信息
			log.debug(currentThread + " QueryCache【"+key+"】first executing...");
			
			/* 执行方法，如果非空则Cache结果  */
			try {
				returnVal = invocation.proceed();
			} catch(Exception e) {
				throw e;
			} finally {
				cache.removeObject(key); // 移除缓存的执行信息（出现异常时也要移除）
			}
		}
 
        return returnVal;
    }
}
