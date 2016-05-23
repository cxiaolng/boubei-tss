package com.boubei.tss.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

public class XMLDocUtilTest {
 
	@Test
    public void testMap2DataNode() {
		
		new XMLDocUtil();
		
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("tel", new Object[] { "057188889999", "13588899889" });
        map.put("email", "jinpj@g-soft.com.cn");
        map.put("age", new Integer(24));
        map.put("id", new Object[] { new Integer(23), "<![CDATA[sss]]>" });
        map.put("addr", null);

        Element node = XMLDocUtil.map2DataNode(map, "row");
        
        String dataXml = "<row><id><![CDATA[23]]></id><id><![CDATA[&lt;![CDATA[sss]]&gt;]]></id><email><![CDATA[jinpj@g-soft.com.cn]]></email><age><![CDATA[24]]></age><tel><![CDATA[057188889999]]></tel><tel><![CDATA[13588899889]]></tel></row>";
        assertEquals(dataXml, node.asXML());

        XMLDocUtil.dataNodes2Map(node);
		XMLDocUtil.dataXml2Doc(dataXml);
		
		try {
			XMLDocUtil.dataXml2Doc(dataXml + "<error>");
		} catch (Exception e) {
			Assert.assertTrue("由dataXml生成doc出错", true);
		}
		
		try {
			XMLDocUtil.createDoc("META-INF/notExsits.xml");
		} catch (Exception e) {
			Assert.assertTrue("定义的文件没有找到", true);
		}
		
		Document doc = XMLDocUtil.createDoc("tss/cache.xml");
		XMLDocUtil.getNodeText(node);
		
		XMLDocUtil.selectNodes(doc, "//id");
		XMLDocUtil.selectNodes(node, "//tel");
    }
    
	@Test
    public void testMap2AttributeNode() {
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("email", "jinpj@g-soft.com.cn");
        map.put("age", new Integer(24));
        map.put("addr", null);

        Element node = XMLDocUtil.map2AttributeNode(map, "row");
        assertEquals("<row email=\"jinpj@g-soft.com.cn\" age=\"24\"/>", node.asXML());
    
        assertEquals(2, XMLDocUtil.dataNode2Map(node).size());
        assertEquals("", XMLDocUtil.getNodeText(node));
	}
}

