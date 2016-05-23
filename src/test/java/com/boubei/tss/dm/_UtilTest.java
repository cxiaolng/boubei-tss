package com.boubei.tss.dm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.util.DateUtil;

public class _UtilTest {
	
	@Test
	public void test() {
		Assert.assertNull(_Util.preTreatValue(null, "string"));
		
		Assert.assertEquals("JK", _Util.preTreatValue("JK", null));
		Assert.assertEquals("JK", _Util.preTreatValue("JK", "hidden"));
		
		Assert.assertEquals(12L, _Util.preTreatValue("12", "number"));
		Assert.assertEquals(12.2, _Util.preTreatValue("12.2", "number"));
		
		Assert.assertEquals(DateUtil.parse("2015-04-06"), _Util.preTreatValue("2015-04-06", "date"));
		Assert.assertEquals(DateUtil.parse("2015-04-06 06:06:06"), _Util.preTreatValue("2015-04-06 06:06:06", "datetime"));
		Assert.assertNull(_Util.preTreatValue("2015-04-06T06:06:06", "datetime"));
		
		// test freemarker error print
		// Expression param1 is undefined on line 1, column 22 in t.ftl
		Map<String, String> map = new HashMap<String, String>();
		map.put("report.id", "120");
		map.put("report.name", "报表120");
		
		_Util.freemarkerParse("<#if p1??> <#else> ${param1} </#if>", map);
	}

}
