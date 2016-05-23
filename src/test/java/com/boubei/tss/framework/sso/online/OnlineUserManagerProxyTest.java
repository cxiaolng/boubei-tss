package com.boubei.tss.framework.sso.online;

import org.junit.Test;

public class OnlineUserManagerProxyTest {
	
	@Test
	public void test() {
		OnlineUserManagerProxy proxy = new OnlineUserManagerProxy();
		
		Long userId = 12L;
		String token = "token1";
		
		try {
			proxy.register(token, "CMS", "1234567890", userId, "JK");
		} catch(Exception e) {  }
		
		try {
			proxy.getOnlineUserNames();
		} catch(Exception e) {  }
		
		try {
			proxy.getOnlineUsersByToken(token);
		} catch(Exception e) {  }
		
		try {
			proxy.isOnline(token);
		} catch(Exception e) {  }
		
		try {
			proxy.logout("CMS", "1234567890");
		} catch(Exception e) {  }
		
		try {
			proxy.logout(userId);
		} catch(Exception e) {  }
	}

}
