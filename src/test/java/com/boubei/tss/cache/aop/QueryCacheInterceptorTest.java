package com.boubei.tss.cache.aop;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.AbstractTest4TSS;

public class QueryCacheInterceptorTest extends AbstractTest4TSS {
	
	@Autowired IService service;
 
    @Test
    public void test() {     
        
        final List<Object> results = new ArrayList<Object>();
        for(int i = 0; i < 10; i++) {
        	new Thread() {
        		public void run() {
					Object ret = service.f1();
					
					// 查看打出来的是不是同一个对象，是的话说明cache拦截器在queryCache拦截器后执行，正常。
					System.out.println("------" + Thread.currentThread().getId() + "------" + ret); 
					for(Object obj : results) {
						Assert.assertEquals(obj, ret);
					}
					results.add(ret);
    			}
        	}.start();
        	
        	try { Thread.sleep(5); } catch (InterruptedException e) { }
        	
        }
        
        try { Thread.sleep(5000); } catch (InterruptedException e) { }
    }
    


}
