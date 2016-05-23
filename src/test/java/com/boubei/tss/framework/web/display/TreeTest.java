package com.boubei.tss.framework.web.display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;
import com.boubei.tss.framework.web.dispaly.tree.DefaultLevelTreeNode;
import com.boubei.tss.framework.web.dispaly.tree.ILevelTreeNode;
import com.boubei.tss.framework.web.dispaly.tree.LevelTreeParser;
import com.boubei.tss.framework.web.dispaly.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.dispaly.tree.TreeEncoder;
import com.boubei.tss.framework.web.dispaly.tree.TreeNodeOption;
import com.boubei.tss.framework.web.dispaly.tree.TreeNodeOptionsEncoder;

public class TreeTest {
	
	@Test
	public void testTree() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		DefaultLevelTreeNode root = new DefaultLevelTreeNode(1L, "root");
		DefaultLevelTreeNode node1 = new DefaultLevelTreeNode(2L, root.getId(), "node1");
		DefaultLevelTreeNode node2 = new DefaultLevelTreeNode(3L, root.getId(), "node2");
		DefaultLevelTreeNode node3 = new DefaultLevelTreeNode(4L, root.getId(), "node3");
		DefaultLevelTreeNode node4 = new DefaultLevelTreeNode(5L, node2.getId(), "node4");
		
		Assert.assertEquals(node1, node1);
		
		List<ILevelTreeNode> list = new ArrayList<ILevelTreeNode>();
		list.add(root);
		list.add(node1);
		list.add(node2);
		list.add(node3);
		list.add(node4);
		
		TreeEncoder treeEncoder = new TreeEncoder(list, new LevelTreeParser());
		
		TreeNodeOptionsEncoder optionsEncoder = new TreeNodeOptionsEncoder();
		TreeNodeOption option = new TreeNodeOption();
		option.setId("1");
		option.setDependId("2");
		option.setText("编辑权限");
		option.toXml();
		optionsEncoder.add(option);
		optionsEncoder.toXml();
		treeEncoder.setOptionsEncoder(optionsEncoder );
		treeEncoder.setRootNodeName("ROOT");
		
		try {
			XmlPrintWriter writer = new XmlPrintWriter(response.getWriter());
			optionsEncoder.print(writer);
			treeEncoder.print(writer);
		} 
		catch (IOException e) {
			Assert.fail();
		}
		
		TreeAttributesMap attributes = root.getAttributes();
		Assert.assertEquals(root.getId(), attributes.get("id"));
		
		attributes.put("status", 1);
		attributes.putAll(attributes);
		
		Assert.assertEquals(3, attributes.getAttributes().size());
		Assert.assertEquals(3, attributes.size());
		Assert.assertTrue(attributes.containsKey("id"));
		Assert.assertTrue(attributes.containsValue("root"));
		
		attributes.remove("id");
		
		Assert.assertEquals(2, attributes.values().size());
		Assert.assertEquals(2, attributes.keySet().size());
		
		attributes.clear();
		Assert.assertTrue(attributes.isEmpty());
	}

}
