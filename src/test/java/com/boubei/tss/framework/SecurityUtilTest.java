package com.boubei.tss.framework;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.boubei.tss.framework.SecurityUtil;

public class SecurityUtilTest {
	
	@Test
	public void test() {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		String value = "javascript:alert(222);";
		String result = SecurityUtil._fuckXSS(value, request );
		Assert.assertEquals("alert(222);", result);
	
	}

}
