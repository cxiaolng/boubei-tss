package com.boubei.tss.util.proxy;

import junit.framework.Assert;

import org.junit.Test;

public class ProxyProfilerTest {
	
	@Test
	public void test() {
		User object = new User(1, "Jon1");
		
		Animal animal1 = (Animal) ProxyProfiler.frofiler(object);
		Assert.assertTrue(object.getId() == animal1.getId());
		Assert.assertEquals(object.getName(), animal1.getName());
		
		Animal animal2 = (Animal) ProxyProfiler.frofiler(object, new String[]{"getId"});
		Assert.assertTrue(object.getId() == animal2.getId());
		Assert.assertEquals(object.getName(), animal2.getName());
	}
	
	
	public interface Animal {
		Integer getId();
		String getName();
	}
	
	public static class User implements Animal {
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
