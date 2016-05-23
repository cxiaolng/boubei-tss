package com.boubei.tss.portal.entity;

import junit.framework.Assert;

import org.junit.Test;

import com.boubei.tss.portal.entity.ThemeInfo.ThemeInfoId;

public class EntityTest {
	
	@Test
	public void testThemeInfo() {
		ThemeInfoId tiId1 = new ThemeInfoId(1L, 2L);
		ThemeInfoId tiId2 = new ThemeInfoId(1L, 2L);
		ThemeInfoId tiId3 = new ThemeInfoId(2L, 2L);
		
		Assert.assertTrue(tiId1.equals(tiId2));
		Assert.assertFalse(tiId1.equals(tiId3));
		
		Assert.assertFalse(tiId1.equals( new Object() ));
		
		Assert.assertTrue(tiId1.hashCode() == tiId2.hashCode());
		
		Assert.assertNotNull(tiId1.getThemeId());
		
		ThemeInfo ti = new ThemeInfo();
		ti.setId(tiId1);
		Assert.assertEquals(tiId1, ti.getPK());
	}
	
	@Test
	public void test() {
		new Component().setId(12L);
		new Navigator().setId(12L);
		new Structure().setId(12L);
		new ThemePersonal().setId(12L);
		
		FlowRate fr = new FlowRate();
		fr.setId(12L);
		Assert.assertEquals(fr.getId(), fr.getPK());
	}
}
