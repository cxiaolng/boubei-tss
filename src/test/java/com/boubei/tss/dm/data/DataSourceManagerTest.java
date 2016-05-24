package com.boubei.tss.dm.data;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.dm.AbstractTest4DM;

public class DataSourceManagerTest extends AbstractTest4DM {
	
	@Autowired DataSourceManager dsManager;

	@Test
	public void test() {

		dsManager.getCacheConfigs();
		
		String code = "connpool-h2";
		String name = "测试数据源";
		String value = "{\"customizerClass\": \"com.boubei.tss.framework.persistence.connpool.ConnPoolCustomizer\",  " +
				" \"poolClass\": \"com.boubei.tss.cache.extension.ReusablePool\",   " +
				" \"code\": \"connpool-h2\",  " +
				" \"name\": \"测试数据源\",   " +
				" \"cyclelife\": \"180000\",  " +
				" \"paramFile\": \"org.h2.Driver,jdbc:h2:mem:h2db;DB_CLOSE_DELAY=-1;LOCK_MODE=0,sa,123\",   " +
				" \"interruptTime\": \"1000\",    \"initNum\":\"0\",    \"poolSize\": \"10\"}";
		
		// create
		Object result = dsManager.configConnpool(code, name, value);
		Assert.assertEquals("数据源配置成功", result);
		
		// update
		result = dsManager.configConnpool(code, name, value);
		Assert.assertEquals("数据源配置修改成功", result);
		
		String driver = "org.h2.Driver";
		String url  = "jdbc:h2:mem:h2db;DB_CLOSE_DELAY=-1;LOCK_MODE=0";
		String user = "sa";
		String pwd  = "123";
		result = dsManager.testConn(driver, url, user, pwd);
		Assert.assertEquals("测试连接成功", result);
		
		result = dsManager.testConn(driver, url, user, "123456");
		System.out.println(" --------- " + result);
		Assert.assertTrue( result.toString().startsWith("测试连接失败") );
		
		dsManager.delConnpool(response, paramService.getParam(code).getId());
		
		try { Thread.sleep(1000); } catch(Exception e) { }
	}
	
    protected String getDefaultSource(){
    	return "connectionpool";
    }
}
