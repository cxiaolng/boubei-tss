package com.boubei.tss.modules.param;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ParamConfigTest {
	
	@Test
	public void testGetAttribute() {
		 assertEquals("TSS", ParamConfig.getAttribute("application.code"));
		 
		 assertEquals("TSS", ParamConfig.getAttribute("application.code", "WMS"));
		 assertEquals("WMS", ParamConfig.getAttribute("xxx.code", "WMS"));
	}

}
