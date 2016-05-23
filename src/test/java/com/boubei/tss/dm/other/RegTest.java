package com.boubei.tss.dm.other;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.util.DateUtil;

/**
 * Matcher.find    部分匹配
 * Matcher.matches 全局匹配
 */
public class RegTest {

	@Test
  	public void test() {
  		String paramValue = "today - 7"; // "2012-12-12"
  		Date dateObj;
  		if (Pattern.compile("^today[\\s]*-[\\s]*\\d").matcher(paramValue).matches()) {
  			int deltaDays = Integer.parseInt(paramValue.split("-")[1].trim());
  			Date today = DateUtil.noHMS(new Date());
  			dateObj = DateUtil.subDays(today, deltaDays);
  		} 
		else {
			try {
				dateObj = DateUtil.parse(paramValue);
			} catch(Exception e) {
				dateObj = null;
			}
		}
  			
  		System.out.println(dateObj);
  	}
	
	@Test
  	public void test2() {
		String sql = "select u.id, u.userName Name from um_user u, um_groupuser gu " +
				"where u.id = gu.userId and gu.groupId = ?";
		
		Pattern p = Pattern.compile("text|name|id|pk", Pattern.CASE_INSENSITIVE); // 忽略大小写
		Matcher m = p.matcher(sql);
		Assert.assertTrue( m.find() );

  	}
  	
}
