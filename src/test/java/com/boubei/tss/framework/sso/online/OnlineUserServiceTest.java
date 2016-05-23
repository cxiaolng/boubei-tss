package com.boubei.tss.framework.sso.online;

import org.junit.Assert;
import org.junit.Test;

public class OnlineUserServiceTest {

	@Test
	public void testOnlineUserService() {
		OnlineUserService onlineUserService = new OnlineUserService();
		
		onlineUserService.getOnlineUserNames();
		onlineUserService.getOnlineUsersByToken("token");
		Assert.assertFalse(onlineUserService.isOnline("token"));
		onlineUserService.logout("token", "1234567890");
	}
	
	@Test
	public void testRemoteOnlineUserManager() {

	}
}
