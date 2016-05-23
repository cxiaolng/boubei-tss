package com.boubei.tss.framework;

import junit.framework.Assert;

import org.junit.Test;

public class FrameworkTest {
	
	@Test
	public void testGetVersion() {
		Object[] result = new Framework().getVersion();
		Assert.assertEquals("dev", result[1]);
	}
	
	@Test
	public void getThreadInfos() {
		Object[] result = new Framework().getThreadInfos();
		Assert.assertTrue( (Integer)result[0] > 0 );
	}
	
//	@Test
	public void testLiceence() {
		Assert.assertTrue(Framework.validateTSS());
	}

}
