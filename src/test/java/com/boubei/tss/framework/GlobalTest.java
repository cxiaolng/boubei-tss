package com.boubei.tss.framework;

import org.junit.Assert;
import org.junit.Test;

public class GlobalTest {
	
	@Test
	public void test() {
		Assert.assertNotNull(Global.getContext());
		
		try {
			Global.getRemoteOnlineUserManager();
			Assert.fail("should throw exception but didn't.");
		} catch(Exception e) {
			Assert.assertTrue("不存在", true);
		}
		
		Global.destroyContext();
	}
}
