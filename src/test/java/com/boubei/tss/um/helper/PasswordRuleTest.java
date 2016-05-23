package com.boubei.tss.um.helper;

import org.junit.Assert;
import org.junit.Test;

public class PasswordRuleTest {

	@Test
	public void testPasswordRule() {
		Assert.assertEquals(PasswordRule.UNQUALIFIED_LEVEL, PasswordRule.getStrengthLevel("admin", "admin"));
		
		Assert.assertEquals(PasswordRule.UNQUALIFIED_LEVEL, PasswordRule.getStrengthLevel("123456", "admin"));
		
		Assert.assertEquals(PasswordRule.UNQUALIFIED_LEVEL, PasswordRule.getStrengthLevel("111111", "BL00618"));
		
		Assert.assertEquals(PasswordRule.LOW_LEVEL, PasswordRule.getStrengthLevel("1234567890", "jonking"));
		Assert.assertEquals(PasswordRule.MEDIUM_LEVEL, PasswordRule.getStrengthLevel("j1234567890", "jonking"));
		Assert.assertEquals(PasswordRule.HIGHER_LEVEL, PasswordRule.getStrengthLevel("ax=1234567890", "jonking"));
	}
	
}
