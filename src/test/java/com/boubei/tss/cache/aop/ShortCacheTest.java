package com.boubei.tss.cache.aop;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.AbstractTest4TSS;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.CacheHelper;

/**
 * 测试10分钟Cache
 * 
缓存池【服务数据缓存（短期），77.0】的当前快照：
----------------【SHORT_free】池中数据项列表，共【2】个 --------------
  key: com.boubei.tss.cache.aop.IService.f1(), value: 82, hit: 21
  key: com.boubei.tss.cache.aop.IService.f2(), value: 0, hit: 99
----------------------------------- END ---------------------------
----------------【SHORT_using】池中数据项列表，共【0】个 --------------
----------------------------------- END ---------------------------
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
