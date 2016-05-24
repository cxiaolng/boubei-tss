package com.boubei.tss.framework.component.param;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss._TestUtil;
import com.boubei.tss.framework.AbstractTest4F;

public class ParamActionTest extends AbstractTest4F {
	
	@Autowired private ParamAction action;

    @Test
	public void testParamAction() {
		Param paramGroup = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "测试参数组1");
        Param comboParam = ParamManager.addComboParam(paramGroup.getId(), "book", "可选书籍");
        
        ParamManager.addParamItem(comboParam.getId(), "Thinking in JAVA", "Thinking in JAVA", ParamConstants.COMBO_PARAM_MODE);
        ParamManager.addParamItem(comboParam.getId(), "Effictive JAVA", "Effictive JAVA", ParamConstants.COMBO_PARAM_MODE);
        ParamManager.addParamItem(comboParam.getId(), "Design Pattern", "Design Pattern", ParamConstants.COMBO_PARAM_MODE);
        
        Param paramGroup2 = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "测试参数组2");
        Param treeParam = ParamManager.addTreeParam(paramGroup2.getId(), "group", "用户组织");
        
        Param temp = ParamManager.addParamItem(treeParam.getId(), "group1", "研发部", ParamConstants.TREE_PARAM_MODE);
        ParamManager.addParamItem(temp.getId(), "group2", "IT部", ParamConstants.TREE_PARAM_MODE);
        ParamManager.addParamItem(treeParam.getId(), "group3", "财务部", ParamConstants.TREE_PARAM_MODE);
        
        temp.setDescription("update param item");
        action.saveParam(response, temp); // update param item
        
		action.get2Tree(response);
		
		action.copyParam(response, treeParam.getId(), paramGroup.getId().toString());
		
		action.getCanAddParamsTree(response, treeParam.getId());
		
		action.getParamInfo(request, response, ParamConstants.NORMAL_PARAM_TYPE);
		action.getParamInfo(request, response, ParamConstants.GROUP_PARAM_TYPE);
		action.getParamInfo(request, response, ParamConstants.ITEM_PARAM_TYPE);
		
		request.addParameter("mode", ParamConstants.SIMPLE_PARAM_MODE.toString());
		request.addParameter("paramId", treeParam.getId().toString());
		action.getParamInfo(request, response, ParamConstants.NORMAL_PARAM_TYPE);
		
		action.saveParam(response, comboParam);
		action.initAppConfig(response, "test", "test", "test");
		
		assertEquals("<server code=test framework=\"tss\" name=test sessionIdName=\"JSESSIONID\" baseURL=test/>", 
				ParamConfig.getAttribute("test"));
		
		action.saveParamValue(response, "test", "test test");
		assertEquals("test test", ParamConfig.getAttribute("test"));
		
		Param paramGroup3 = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "测试参数组3");
		action.moveParam(response, treeParam.getId(), paramGroup3.getId().toString());
		
		action.sortParam(response, paramGroup.getId(), paramGroup3.getId(), 1);
		action.sortParam(response, paramGroup.getId(), paramGroup3.getId(), -1);
		
		action.startOrStopParam(response, paramGroup.getId(), 1);
		action.startOrStopParam(response, comboParam.getId(), 0);
		
		action.get2Tree(response);
		
		log.debug(action.getSimpleParam2Json("application.code"));
		log.debug(action.getComboParam2Json("book", false));
		log.debug(action.getComboParam2Json("group", true));
		
		log.debug(action.getComboParam2Json("notExsit", true));
		log.debug(action.getComboParam2Json("book", true));
		
		action.delParam(response, paramGroup.getId());
		action.delParam(response, paramGroup2.getId());
		
		action.get2Tree(response);
		
		_TestUtil.printLogs(logService);
	}
}
