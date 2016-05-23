/* ==================================================================   
 * Created [2007-1-9] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
*/

package com.boubei.tss.framework.test;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.apache.log4j.Logger;

/**
 * <p>
 * Profiler.java
 * </p>
 * <p>性能测量CGLIB实现，适合所有对象，尤其是没有实现接口的对象。
 * </p><p>（注，使用本类的前提是:对象的初始化需由CGLIB完成）
 * </p><p>计算对象方法的执行时间。
 * </p><p>可指定拦截对象需要被拦截测试的具体方法列。
 * </p>
 * 
 * 与Dynamic Proxy中的Proxy和InvocationHandler相对应，Enhancer和MethodInterceptor <br/>
 * 在CGLib中负责完成(代理对象创建)和(方法截获处理), (产生的是目标类的子类)而不是通过接口来实现方法拦截的，<br/>
 * Enhancer主要是用于构造动态代理子类来实现拦截，MethodInterceptor（扩展了Callback接口）主要用于实现around advice（AOP中的概念）。
 */
public class CGLIBProfiler implements MethodInterceptor {
    
	private Logger log = Logger.getLogger(CGLIBProfiler.class);

    /**
     * 默认拦截所有的方法。
     * @param clazz  指定拦截对象
     * @return
     */
    public Object getProxy(Class<?> clazz) {
        return getProxy(clazz, null);
    }
    
    /**
     * 指定拦截对象，同时指定拦截对象需要被拦截测试的具体方法列。
     * @param clazz  指定拦截对象
     * @param invokeMethods 指定拦截对象需要被拦截测试的具体方法列
     * @return
     */
    public Object getProxy(Class<?> clazz, String[] invokeMethods) {
        Callback[] callbacks = new Callback[] { this,  NoOp.INSTANCE };
        
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(this.new CallbackFilterImpl());
        return enhancer.create(); // enhancer.create(paramTypes, params);
    } 

    public Object intercept(Object o, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proxy.invokeSuper(o, args);
        long endTime = System.currentTimeMillis();
        log.info("方法【" + method.getName() + "(" + Arrays.asList(args) + ")】执行时间为【" + (endTime - startTime) + "】ms.");
        
        return result;
    }
    
    private class CallbackFilterImpl implements CallbackFilter {  
        
        private String[] ignoreMethods = new String[] {"print", "set", "getWriter", "doAfterSave"};
 
        /*
         * 定义过滤或者拦截哪些方法，0：拦截 1：不拦截
         */
        public int accept(Method method) {      
            String methodName = method.getName();
            if(ignoreMethods != null && ignoreMethods.length > 0) {
                for(String temp : ignoreMethods) {
                    if(methodName.startsWith(temp)) {
                        return 1;
                    }
                }
                return 0;
            }
            
            return 1;
        }
    }
}


