package com.boubei.tss.util;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class DateUtilTest {
	
	@Test
	public void test() {
		
		Date date = new Date();
		
		String formatStr = DateUtil.format(date);
		System.out.println(formatStr);
		
		String formatStr2 = DateUtil.format(date, "yyyy-MM-dd HH:mm");
		System.out.println(formatStr2);
		
		Assert.assertTrue(formatStr2.startsWith(formatStr));
		
		formatStr = DateUtil.formatCare2Second(date);
		System.out.println(formatStr);
		Assert.assertTrue(formatStr.startsWith(formatStr2));
		
		Assert.assertTrue(date.after(DateUtil.parse(formatStr)));
		
		Assert.assertEquals("", DateUtil.formatCare2Second(null));
		Assert.assertTrue( DateUtil.format(date, "").length() > 0 );
		Assert.assertEquals("", DateUtil.format(null, "yyyy-MM-dd HH:mm"));
		Assert.assertEquals("", DateUtil.formatCare2Second(null));
		
		Assert.assertNotNull(DateUtil.parse("2013-11-07 11:26:05"));
		Assert.assertNotNull(DateUtil.parse("2013/11/07 11:26:05"));
		Assert.assertNotNull(DateUtil.parse("2013-11-07"));
		Assert.assertNotNull(DateUtil.parse("2013/11/07"));
		
		Assert.assertNotNull(DateUtil.parse("2013/11/07 11:26"));
		Assert.assertEquals(DateUtil.parse("2013-11-07 11:26"), DateUtil.parse("2013/11/07 11:26"));
		
		try {
			DateUtil.parse("2013/、11-07");
		} catch (Exception e) {
			Assert.assertTrue("非法日期字符串，解析失败", true);
		}
		
	}
	
	@Test
	public void test2() {
		Date day1 = DateUtil.today();
		Date day2 = DateUtil.addDays(day1, 10);
		Date day3 = DateUtil.subDays(day1, 10);
		
		Assert.assertEquals(21, DateUtil.daysBetweenFromAndTo(day3, day2).size());
		
		DateUtil.getDay(day1);
		int month = DateUtil.getMonth(day1);
		int year = DateUtil.getYear(day1);
		
		Date day4 = DateUtil.noHMS(new Date());
		Assert.assertEquals(0, DateUtil.getHour(day4));
		
		DateUtil.isMonthEnd(day1);
		
		Assert.assertEquals("202006", DateUtil.toYYYYMM(2020, 6));
		Assert.assertEquals("202012", DateUtil.toYYYYMM(2020, 12));
		
		Assert.assertEquals(DateUtil.toYYYYMM(year, month), DateUtil.toYYYYMM(day1));
	}

}
