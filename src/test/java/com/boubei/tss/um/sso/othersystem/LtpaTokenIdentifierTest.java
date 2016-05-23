package com.boubei.tss.um.sso.othersystem;

import java.security.Principal;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.framework.exception.UserIdentificationException;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.um.AbstractUMTest;

public class LtpaTokenIdentifierTest extends AbstractUMTest {
 
	@Test
	public void test() {
		LtpaTokenIdentifier indentifier = new LtpaTokenIdentifier();
		
		try {
			indentifier.identify();
			Assert.fail("should throw exception but didn't.");
		}  catch (UserIdentificationException e) {
			Assert.assertTrue("LtpaToken为空，用户可能还没有登录OA，请重新登录", true);
		}
		
		request.addParameter(LtpaTokenIdentifier.LTPA_TOKEN_NAME, "ltpaTokenXXXXXXXX");
		try {
			indentifier.identify();
			Assert.fail("should throw exception but didn't.");
		}  catch (UserIdentificationException e) {
			Assert.assertTrue("取不到用户，请确认已经配置好SSO！", true);
		}
		
		request.addParameter(LtpaTokenIdentifier.LOGIN_NAME, "AdminXXX");
		try {
			indentifier.identify();
			Assert.fail("should throw exception but didn't.");
		}  catch (UserIdentificationException e) {
			Assert.assertTrue("用户在UM里不存在！", true);
		}
		
		Principal userPrincipal = new Principal() {
			public String getName() {
				return "Admin";
			}
		};
		request.setUserPrincipal( userPrincipal );
		try {
			IdentityCard card = indentifier.identify();
			Assert.assertNotNull(card);
			Assert.assertEquals("Admin", card.getLoginName());
			Assert.assertNotNull( card.getOperator() );
			Assert.assertNotNull( card.getToken() );
			Assert.assertEquals(false, card.isAnonymous());
		} 
		catch (UserIdentificationException e) {
			Assert.fail("身份认证失败：" + e.getMessage());
		}
	}

}
