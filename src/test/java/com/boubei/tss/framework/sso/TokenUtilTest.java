package com.boubei.tss.framework.sso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试用户令牌处理
 */
public class TokenUtilTest {
  
	@Test
    public void testCreateToken() {
        String token = TokenUtil.createToken("", null);
        assertNull(token);
        token = TokenUtil.createToken(null, new Long(0));
        assertNull(token);
        token = TokenUtil.createToken("", new Long(0));
        assertNotNull(token);
    }

	@Test
    public void testGetUserIdFromToken() {
        String sessionId = "123123123123sdasd";
        Long userId = new Long(2343);
        String token = TokenUtil.createToken(sessionId, userId);
        Long newUserId = TokenUtil.getUserIdFromToken(token);
        assertEquals(userId, newUserId);
        
        try {
        	token = "3u1dt5T33PNCgByaGoyMFOMw7N973TrsYbIN9AjcpUILtQjEGWFju4mdid01QmHK9o5ipEzBVO3xfREypahK5w==";
        	TokenUtil.getUserIdFromToken(token);
        	Assert.fail("should throw exception, but didn't");
        } catch(Exception e) {
        	Assert.assertTrue("登陆令牌已失效，请注销或刷新页面后重新登录。", true);
        }
    }

}

	