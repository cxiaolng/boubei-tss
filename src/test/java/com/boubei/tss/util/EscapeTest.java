package com.boubei.tss.util;

import junit.framework.Assert;

import org.junit.Test;

public class EscapeTest {
	
	@Test
	public void testEscape() {
		String teststr = "1234 abcd[]()<+>,.~\\";    
        String escapedStr = Escape.escape(teststr);
		Assert.assertEquals("1234%20abcd%5B%5D()%3C%2B%3E%2C.~%5C", escapedStr);    
        Assert.assertEquals(teststr, Escape.unescape(escapedStr));    
        
        teststr = "言彤";    
        escapedStr = Escape.escape(teststr);
        Assert.assertEquals(teststr, Escape.unescape(escapedStr));    
        
        teststr = "傅思";    
        escapedStr = Escape.escape(teststr);
        Assert.assertEquals(teststr, Escape.unescape(escapedStr));    
        
        // 下面为失败的转换示例
        try {
			printStringByByte("一");
			printStringByByte("言");
	        printStringByByte("彤");
	        printStringByByte("二");
	        printStringByByte("思");
	        
	        String loginName = "言彤X";
	        System.out.println(loginName = StringUtil.convertCoding(loginName, "UTF-8", "GBK"));
	        System.out.println(loginName = new String(loginName.getBytes("GBK"), "UTF-8"));
	        System.out.println("----------------------------");
	        
	        loginName = "思傅X";
	        System.out.println(loginName = StringUtil.convertCoding(loginName, "UTF-8", "GBK"));
	        System.out.println(loginName = new String(loginName.getBytes("GBK"), "UTF-8"));
	        System.out.println("----------------------------");
	        
	        loginName = "傅思X";
	        System.out.println(loginName = StringUtil.convertCoding(loginName, "UTF-8", "GBK"));
	        System.out.println(loginName = new String(loginName.getBytes("GBK"), "UTF-8"));
	        
		} catch (Exception e) {
			 
		}
    }
    
    static void printStringByByte(String s) throws Exception{
        byte[] bytes;
        bytes = s.getBytes("UTF-8");
        for(int i = 0; i < bytes.length; i++){
            System.out.print(bytes[i] + " ");
        }
        System.out.println("");
        bytes = s.getBytes("GBK");
        for(int i = 0; i < bytes.length; i++){
            System.out.print(bytes[i] + " ");
        }
        System.out.println("\n----------------------------");
    }   

}
