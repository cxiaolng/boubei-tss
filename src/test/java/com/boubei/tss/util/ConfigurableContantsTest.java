package com.boubei.tss.util;

import junit.framework.Assert;

import org.junit.Test;

public class ConfigurableContantsTest extends ConfigurableContants {
	
	@Test
	public void test() {
		super.init("log4j.properties");
		
		Assert.assertEquals(null, super.getProperty("XXX"));
		Assert.assertEquals("123456", super.getProperty("XXX", "123456"));
	}

}
