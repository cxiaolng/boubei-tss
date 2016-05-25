package com.boubei.tss.cache;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.boubei.tss.util.BeanUtil;

public class CacheStrategyTest {
	
	Pool pool; 
	
	@Before
	public void setUp() {
		pool = JCache.getInstance().getPool("LONG");
	}
	
	@After
	public void tearDown() {
		pool = null;
	}
	
	@Test
	public void testCacheStrategy() {
		CacheStrategy strategy = pool.getCacheStrategy();
		
		CacheStrategy strategy2 = new CacheStrategy();
		BeanUtil.copy(strategy2, strategy);
		
		Assert.assertTrue(strategy2.equals(strategy));
		
		Assert.assertNotNull(strategy.getPoolInstance());
		
		Assert.assertTrue(5 == strategy.getAccessMethod());
		Assert.assertEquals("LONG", strategy.getCode());
		Assert.assertEquals("服务数据缓存（长期）", strategy.getName());
		Assert.assertEquals("com.boubei.tss.cache.DefaultCacheCustomizer", strategy.getCustomizerClass());
		Assert.assertEquals("com.boubei.tss.cache.ObjectPool", strategy.getPoolClass());
		Assert.assertEquals("com.boubei.tss.cache.MapContainer", strategy.getPoolContainerClass());
		
		Assert.assertTrue(1000000 == strategy.getCyclelife());
		Assert.assertTrue(0  == strategy.getInitNum());
		Assert.assertTrue(10 == strategy.getPoolSize());
		Assert.assertTrue(1000 == strategy.getInterruptTime());
		
		Assert.assertNull(strategy.getParamFile());
		Assert.assertNull(strategy.getRemark());
		Assert.assertEquals(CacheStrategy.TRUE, strategy.getVisible());
		Assert.assertEquals(CacheStrategy.FALSE, strategy.getDisabled());
		
		strategy2.setPoolSize(12);
		strategy2.setInitNum(3);
		strategy2.setName("数据缓存（长期）");
		strategy2.setInterruptTime(2000l);
		strategy2.setCyclelife(3000l);
		strategy2.setPoolContainerClass("com.boubei.tss.cache.MapContainer");
		strategy2.setPoolClass("com.boubei.tss.cache.ReusablePool");
		strategy2.setCustomizerClass("com.boubei.tss.cache.ScannerTaskPoolCustomizer");
		
		strategy.fireEventIfChanged(strategy2);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
