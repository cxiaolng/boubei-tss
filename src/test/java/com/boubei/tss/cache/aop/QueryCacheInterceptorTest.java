package com.boubei.tss.cache.aop;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.AbstractTest4TSS;
import com.boubei.tss.cache.extension.CacheHelper;
import com.boubei.tss.modules.param.ParamManager;

/**
71 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】first time executing...
72 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 1
72 QueryCache waiting...
73 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 2
73 QueryCache waiting...
74 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 3
74 QueryCache waiting...
75 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 4
75 QueryCache waiting...
76 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 5
76 QueryCache waiting...
77 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 6
77 QueryCache waiting...
78 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 7
78 QueryCache waiting...
79 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 8
79 QueryCache waiting...
80 QueryCache【QC_com.boubei.tss.cache.aop.IService.f1()】= 9
com.boubei.tss.framework.exception.BusinessException: 当前应用服务器资源紧张，请稍后再查询。10>9

缓存池【服务数据缓存（短期），82.0】的当前快照：
----------------【SHORT_free】池中数据项列表，共【1】个 --------------
  key: QC_com.boubei.tss.cache.aop.IService.f1(), value: 9, hit: 9
 */
public class QueryCacheInterceptorTest extends AbstractTest4TSS {
	
	@Autowired IService service;
 
    @Test
    public void test() {     
    	
    	try {
    		ParamManager.addSimpleParam(0L, QueryCacheInterceptor.MAX_QUERY_REQUEST, "MQR", "9");
    	} catch(Exception e) { }
        
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
        	
        	try { Thread.sleep(15); } catch (InterruptedException e) { }
        	
        }
        System.out.println(CacheHelper.getShortCache());
        
        try { Thread.sleep(5000); } catch (InterruptedException e) { }
    }
 
}
