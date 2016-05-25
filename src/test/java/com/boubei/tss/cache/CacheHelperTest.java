package com.boubei.tss.cache;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.cache.extension.CacheHelper;

public class CacheHelperTest {

	@Test
	public void test() {
		Assert.assertNotNull(CacheHelper.getLongCache());
		Assert.assertNotNull(CacheHelper.getLongerCache());
		Assert.assertNotNull(CacheHelper.getNoDeadCache());
		Assert.assertNotNull(CacheHelper.getShortCache());
		Assert.assertNotNull(CacheHelper.getShorterCache());
		
		Assert.assertNull( CacheHelper.getLongerCache().getCustomizer().create() );
		Assert.assertTrue( CacheHelper.getLongerCache().getCustomizer().isValid( null ) );
	}

}
