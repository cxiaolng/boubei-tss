package com.boubei.tss.cache.aop;

import java.lang.reflect.Method;
import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.CacheHelper;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.util.EasyUtils;
 
/**
 * 将耗时查询（如report）的执行中的查询缓存起来。
 * 如果同一个用户对同一报表，相同查询条件的查询还在执行中，则让后面的请求进入等待状态。
 * 等第一次的查询执行完成，然后后续的查询直接取缓存里的数据。
 * 这样可以防止用户重复点击查询（以及频繁且耗时的查询），造成性能瓶颈。 
 * 
 * QC_cache项数(I) + QC_cache项的命中次数之和(H) == 当前等待线程数X，当X如果非常大，系统会崩溃，
 * 需要设定一个阈值V，当 X > V , 直接抛出异常
 * 
 * 测试时，可以把第一次查询设置断点断住，来模拟耗时的report查询过程。
 */
@Component("queryCacheInterceptor")
public class QueryCacheInterceptor implements MethodInterceptor {

    protected Logger log = Logger.getLogger(this.getClass());
    
    public static final String MAX_QUERY_REQUEST = "MAX_QUERY_REQUEST";
    public static final String DEFAULT_MAX = "100";

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod(); /* 获取目标方法 */
        Object[] args = invocation.getArguments();    /* 获取目标方法的参数 */
        
        QueryCached annotation = targetMethod.getAnnotation(QueryCached.class); // 取得注释对象
        if (annotation == null) {
        	return invocation.proceed(); /* 如果没有配置缓存，则直接执行方法并返回结果 */
        }
 
		Pool qCache = CacheHelper.getShortCache(); /* 使用10分钟Cache */
		
		// 检查当前等待线程数（执行中 + 等待中）
		int X = countThread(qCache), 
			V = EasyUtils.obj2Int( ParamManager.getValue(MAX_QUERY_REQUEST, DEFAULT_MAX) );
		if( X > V ) {
			throw new BusinessException("当前应用服务器资源紧张，请稍后再查询。" + X + ">" + V);
		}
        
        String qKey = "QC_" + CacheInterceptor.cacheKey(targetMethod, args);
        Cacheable qcItem = qCache.getObject(qKey); // item.hit++
		
		Object returnVal;
		long currentThread = Thread.currentThread().getId();
		
		if (qcItem != null) {
			Integer hit = qcItem.getHit();
			qcItem.update( hit ); // 记录是第几个到访
			log.debug( currentThread + " QueryCache【"+qKey+"】= " + hit );
			
			// 等待执行中的上一次请求先执行完成； 
			long start = System.currentTimeMillis();
			while( qCache.contains(qKey) ) { // 说明NO.1 Query还在执行中
				log.debug(currentThread + " QueryCache waiting...");
				Thread.sleep( 500 * Math.min(20, qcItem.getHit()) );  // 等待的线程越多，则sleep时间越长
				
				// 超过10分钟，说明执行非常缓慢，则不再继续等待，同时抛错提示用户。
				if(System.currentTimeMillis() - start > 10*60*1000) {
					throw new BusinessException("本次请求执行缓慢，请稍后再查询。");
				}
			}
			
			// QC_cache 已经被destroy
			returnVal = invocation.proceed(); // 此时去执行查询，结果已经在3分钟的cache中
		} 
		else {
			qCache.putObject(qKey, 0); // 缓存执行信息
			log.debug(currentThread + " QueryCache【"+qKey+"】first time executing...");
			
			/* 执行方法，如果非空则Cache结果  */
			try {
				returnVal = invocation.proceed();
			} catch(Exception e) {
				throw e;
			} finally {
				qCache.destroyByKey(qKey); // 移除销毁缓存的执行信息（出现异常时也要移除）
			}
		}
 
        return returnVal;
    }
    
    private int countThread(Pool cache) {
    	int I = 0, H = 0;
    	Collection<Cacheable> items = cache.listItems();
    	for(Cacheable item : items) {
    		String key = item.getKey().toString();
			if( key.startsWith("QC_") ) {
				I++;
	    		H += item.getHit();
			}
    	}
    	return I + H;
    }
}
