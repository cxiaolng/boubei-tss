package com.boubei.tss.dm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.util.DateUtil;

public class DMUtilTest {
	
	@Test
	public void test() {
		Assert.assertNull(DMUtil.preTreatValue(null, "string"));
		
		Assert.assertEquals("JK", DMUtil.preTreatValue("JK", null));
		Assert.assertEquals("JK", DMUtil.preTreatValue("JK", "hidden"));
		
		Assert.assertEquals(12L, DMUtil.preTreatValue("12", "number"));
		Assert.assertEquals(12.2, DMUtil.preTreatValue("12.2", "number"));
		
		Assert.assertEquals(DateUtil.parse("2015-04-06"), DMUtil.preTreatValue("2015-04-06", "date"));
		Assert.assertEquals(DateUtil.parse("2015-04-06 06:06:06"), DMUtil.preTreatValue("2015-04-06 06:06:06", "datetime"));
		Assert.assertNull(DMUtil.preTreatValue("2015-04-06T06:06:06", "datetime"));
		
		// test freemarker error print
		// Expression param1 is undefined on line 1, column 22 in t.ftl
		Map<String, String> map = new HashMap<String, String>();
		map.put("report.info", "报表【120, 报表120, 创建人, 修改人】");
		
		DMUtil.freemarkerParse("<#if p1??> <#else> ${param1} </#if>", map);
	}

}
