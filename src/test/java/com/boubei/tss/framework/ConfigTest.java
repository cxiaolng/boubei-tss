package com.boubei.tss.framework;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {
	
	@Test
	public void testConfig() {
		Assert.assertEquals(2, Config.getAttributesSet("test").size());
		
		Assert.assertEquals("TSS", Config.getAttribute("application.code"));
		
		Assert.assertTrue(Config.isH2Database());
		Assert.assertFalse(Config.isMysqlDatabase());
	}
}
