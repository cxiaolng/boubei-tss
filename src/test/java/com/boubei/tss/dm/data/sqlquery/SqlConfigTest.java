package com.boubei.tss.dm.data.sqlquery;

import org.junit.Assert;
import org.junit.Test;

public class SqlConfigTest {

	@Test
	public void test() {
		Assert.assertNull(SqlConfig.getScript("noExists", 1)); 
		
		Assert.assertNull(SqlConfig.getScript("test1", 10));
		
		Assert.assertNotNull(SqlConfig.getScript("test1", 1));
	}
}
