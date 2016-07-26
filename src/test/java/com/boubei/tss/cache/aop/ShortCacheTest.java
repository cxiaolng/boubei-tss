package com.boubei.tss.cache.aop;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.AbstractTest4TSS;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.CacheHelper;

/**
 * 测试10分钟Cache，bi正式环境有点异常
 */
public class ShortCacheTest extends AbstractTest4TSS {
	
	@Autowired IService service;
 
    @Test
    public void test() {     
        
        for(int i = 0; i < 100; i++) {
        	new Thread() {
        		public void run() {
					service.f1();
    			}
        	}.start();
        	
        	try { Thread.sleep(5); } catch (InterruptedException e) { }
        }
        
        try { Thread.sleep(15*1000); } catch (InterruptedException e) { }
        
        for(int i = 0; i < 100; i++) {
        	service.f2();
        }
        
        Pool shortCache = CacheHelper.getShortCache();
		System.out.println( shortCache );
    }
    
}
