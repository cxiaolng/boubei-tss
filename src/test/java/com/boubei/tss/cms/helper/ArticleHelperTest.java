package com.boubei.tss.cms.helper;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.cms.entity.Channel;

public class ArticleHelperTest {
	
	@Test
	public void test() {
		Channel channel = new Channel();
		for(int i = 0; i <= 5; i++) {
			channel.setOverdueDate(String.valueOf(i));
			channel.setId( (long) i );
			
			Date overDate = ArticleHelper.calculateOverDate(channel);
			Assert.assertTrue(overDate.after(new Date()));
		}
	}
	
}
