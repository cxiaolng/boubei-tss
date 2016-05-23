package com.boubei.tss;

import junit.framework.Assert;

import org.junit.Test;

import com.boubei.tss.framework.Framework;

public class TSSLicense {
 
	@Test
	public void testLiceence() {
		Assert.assertTrue(Framework.validateTSS());
	}

}
