package com.boubei.tss.util.proxy;

import org.junit.Test;

public class TestMemory {
	
	/**
	 * Runtime.getRuntime().totalMemory() ==> byte
	 */
	@Test
	public void test1() {
		int size = 1000000;
		
		System.gc();
		printMemoryCost();

		Object[] data = new Object[size];
		for (int i = 0; i < size; i++) {
			data[i] = Integer.valueOf(i);
		}
		printMemoryCost();
		data = null;
		System.gc();
		
		Object[] data2 = new Object[size];
		for (int i = 0; i < size; i++) {
			data2[i] =  Long.valueOf(i);
		}
		printMemoryCost();
		data2 = null;
		System.gc();
		
		Object[] data3 = new Object[size];
		for (int i = 0; i < size; i++) {
			data3[i] =  String.valueOf(i);
		}
		printMemoryCost();
    }

	private void printMemoryCost() {
		Runtime rt = Runtime.getRuntime();
		long costMemory0 = (rt.totalMemory() - rt.freeMemory()) / (1024*1024);
		System.out.println("memory cost: " + costMemory0 + "M");
	}
	
}
