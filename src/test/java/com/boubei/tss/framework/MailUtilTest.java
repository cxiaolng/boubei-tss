package com.boubei.tss.framework;

import org.junit.Test;

import com.boubei.tss.framework.MailUtil;

public class MailUtilTest {
	
	@Test
	public void test1() {
		try {
			MailUtil.send("这是一封简单的测试邮件", "看到的人都会长命百岁");
		} catch(Exception e) {
		}
	}
	
	@Test
	public void test2() {
		try {
			MailUtil.send("test", "test", null);
		} catch(Exception e) {
		}
		
		try {
			MailUtil.send("test", "test", new String[]{});
		} catch(Exception e) {
		}
	}

}
