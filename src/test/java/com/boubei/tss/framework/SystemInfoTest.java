package com.boubei.tss.framework;

import junit.framework.Assert;

import org.junit.Test;

public class SystemInfoTest {
	
	@Test
	public void testGetVersion() {
		Object[] result = new SystemInfo().getVersion();
		Assert.assertEquals("dev", result[1]);
	}
	
	@Test
	public void getThreadInfos() {
		Object[] result = new SystemInfo().getThreadInfos();
		Assert.assertTrue( (Integer)result[0] > 0 );
	}
	
	@Test
	public void testLiceence() {
		Assert.assertTrue(SystemInfo.validateTSS());
	}

}
