package com.boubei.tss.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

	@Test
	public void test() {
		String str = "Portletyyy~!@#$%^&*()_+-=[]{}\\|;':\",./<>?原型实现模型訾鄣迂蟓";

		Assert.assertEquals(str, StringUtil.GBKToUTF8(StringUtil.UTF8ToGBK(str)));
		System.out.println(StringUtil.UTF8ToGBK(StringUtil.GBKToUTF8(str)));
		
		try {
			StringUtil.convertCoding(str, "XXX", "GBK");
		} catch (Exception e) {
			Assert.assertTrue("字符串编码转换失败，不支持的编码方式：XXX", true);
		}
		
		try {
			StringUtil.convertCoding(str, "GBK", "XXX");
		} catch (Exception e) {
			Assert.assertTrue("字符串编码转换失败，不支持的编码方式：XXX", true);
		}
		
		new StringUtil();
	}

}
