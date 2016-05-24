package com.boubei.tss.framework.component.cache;

import org.junit.Assert;
import org.junit.Test;

public class CacheHelperTest {

	@Test
	public void test() {
		Assert.assertNotNull(CacheHelper.getLongCache());
		Assert.assertNull(CacheHelper.getLongerCache());
		Assert.assertNotNull(CacheHelper.getNoDeadCache());
		Assert.assertNotNull(CacheHelper.getShortCache());
		Assert.assertNotNull(CacheHelper.getShorterCache());
	}

}
