package com.boubei.tss.framework.sso;

import junit.framework.Assert;

import org.junit.Test;

public class IdentityGetterFactoryTest {
	
	@Test
	public void test() {
		IdentityGetter instance = IdentityGetterFactory.getInstance();
		Assert.assertNotNull(instance);
	}

}
