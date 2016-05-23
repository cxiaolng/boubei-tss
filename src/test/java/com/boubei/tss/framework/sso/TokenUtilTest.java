package com.boubei.tss.framework.sso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    }

}

	