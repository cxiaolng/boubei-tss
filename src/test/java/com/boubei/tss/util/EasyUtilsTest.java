package com.boubei.tss.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

public class EasyUtilsTest {

	@Test
	public void test() {
		
		try{
			EasyUtils.obj2Double("Jon");
		} catch ( Exception e) {
			Assert.assertTrue(e.getMessage(), true);
		}
		try{
			EasyUtils.obj2Int("Jon");
		} catch ( Exception e) {
			Assert.assertTrue(e.getMessage(), true);
		}
		try{
			EasyUtils.obj2Long("Jon");
		} catch ( Exception e) {
			Assert.assertTrue(e.getMessage(), true);
		}
		
		Assert.assertTrue(0d == EasyUtils.obj2Double(null));
		Assert.assertTrue(0 == EasyUtils.obj2Int(null));
		Assert.assertTrue(0l == EasyUtils.obj2Long(null));
		Assert.assertEquals("", EasyUtils.obj2String(null));
		
		Assert.assertEquals("null", EasyUtils.obj2Json(null));
		Assert.assertEquals("{}", EasyUtils.obj2Json( new HashMap<String, String>() ));
		
		Assert.assertTrue(EasyUtils.obj2Double("1.01") == 1.01d);
		Assert.assertTrue(EasyUtils.obj2Int("1") == 1);
		Assert.assertTrue(EasyUtils.obj2Long("1") == 1L);
		Assert.assertEquals("12", EasyUtils.obj2String(12));

		Assert.assertTrue(EasyUtils.isNullOrEmpty(""));
		Assert.assertTrue(!EasyUtils.isNullOrEmpty(new Object()));
		Assert.assertTrue(EasyUtils.isNullOrEmpty(new ArrayList<Object>()));

		String s = "Jinpujun|English|name|is|JonKinga";
		Assert.assertEquals(5, EasyUtils.split(s, "|").length);
		Assert.assertNull( EasyUtils.split(null, "|") );

		String encodeHex = EasyUtils.encodeHex(s.getBytes());
		Assert.assertEquals(s, new String(EasyUtils.decodeHex(encodeHex)));

		Collection<User> list = new ArrayList<User>();
		list.add(new User(1, "Jon1"));
		list.add(new User(2, "Jon2"));
		list.add(new User(3, "Jon3"));
		
		String[] result = EasyUtils.list2Combo(list, "id", "name", "|");
		Assert.assertEquals("1|2|3", result[0]);
		Assert.assertEquals("Jon1|Jon2|Jon3", result[1]);

		Assert.assertEquals("Jon1,Jon2,Jon3", EasyUtils.list2Str(list));
		Assert.assertEquals("Jon1|Jon2|Jon3", EasyUtils.list2Str(list, "|"));
		Assert.assertEquals("", EasyUtils.list2Str(null));
		
		Collection<String> list2 = new ArrayList<String>();
		list2.add("");
		list2.add(null);
		list2.add("Jon3");
		list2.add("");
		Assert.assertEquals(",,Jon3,", EasyUtils.list2Str(list2));

		Assert.assertEquals("%E8%BF%87%E6%B2%B3%E5%8D%92%E5%AD%90", EasyUtils.toUtf8String("过河卒子"));
	}
	
	public static class User {
		Integer id;
		String name;
		
		public User(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public String toString() {
			return name;
		}
	}

}
