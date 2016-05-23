package com.boubei.tss.framework.sso;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.framework.sso.context.Context;

public class EnvironmentTest {
	
	@Test
	public void test() {
		Context.destroy();
		
		Assert.assertNull(Environment.getClientIp());
		Assert.assertEquals("/tss", Environment.getContextPath());
		Assert.assertNull(Environment.getUserCode());
		Assert.assertNull(Environment.getSessionId());
		
		Assert.assertNull(Environment.getUserName());
		Assert.assertNull(Environment.getUserId());
		
		Assert.assertNull(Environment.getUserInfo("fromUserId"));
		
		Assert.assertNull(TokenUtil.getUserIdFromToken(null));
		
		Assert.assertNull(Context.getIdentityCard());
	}

}
