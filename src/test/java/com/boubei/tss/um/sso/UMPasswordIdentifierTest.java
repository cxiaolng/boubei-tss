package com.boubei.tss.um.sso;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.um.AbstractTest4UM;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;
import com.boubei.tss.util.InfoEncoder;

public class UMPasswordIdentifierTest extends AbstractTest4UM {
	
	@Autowired IUserService userService;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		User admin = userService.getUserById(-1L);
		admin.setPassword("123456");
		userService.createOrUpdateUser(admin, "-2", "-1");
	}
	
	@Test
	public void testIdentifyInUM() {
		initContext();
		
		request.addParameter(SSOConstants.LOGINNAME_IN_SESSION, InfoEncoder.simpleEncode("Admin", 100));
		request.addParameter(SSOConstants.USER_PASSWORD, InfoEncoder.simpleEncode("123456", 100));
		
		UMPasswordIdentifier indentifier = new UMPasswordIdentifier();
		
		try {
			indentifier.identify();

		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		} 
	}
	
	// 密码错误，然后跳转至OA等其他系统再次验证
	@Test
	public void testIdentifyInOA() {
		initContext();

		request.addParameter(SSOConstants.LOGINNAME_IN_SESSION, InfoEncoder.simpleEncode("Admin", 100));
		request.addParameter(SSOConstants.USER_PASSWORD, InfoEncoder.simpleEncode("wrongpassword", 100));

		UMPasswordIdentifier indentifier = new UMPasswordIdentifier();
		
		// 连续10次以上输错密码，触发锁定账号
		for(int i=0; i < 12; i++) {
			try {
				indentifier.identify();
				Assert.fail("should throw exception but didn't.");
			} catch (Exception e) {
				assertTrue(e.getMessage(), true);
			}
		}
	}

}
