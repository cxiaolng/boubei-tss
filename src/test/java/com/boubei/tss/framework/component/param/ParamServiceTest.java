package com.boubei.tss.framework.component.param;

import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.framework.AbstractFrameworkTest;

public class ParamServiceTest extends AbstractFrameworkTest {
    
    /** 导入application.properties文件 */
    @Test
    public void testImportApplicationProperties(){
        ResourceBundle resources = ResourceBundle.getBundle("application", Locale.getDefault());
        if (resources == null) return;
        
        Param group = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "系统参数");
        for (Enumeration<String> enumer = resources.getKeys(); enumer.hasMoreElements();) {
            String key = enumer.nextElement();
            String value = resources.getString(key);
            ParamManager.addSimpleParam(group.getId(), key, key, value);
        }
        
        // test Param Manager
        String testCode = "application.code";
		Assert.assertEquals("TSS", ParamManager.getSimpleParam(testCode).getValue());
        Assert.assertEquals("TSS", ParamManager.getValue(testCode));
        Assert.assertEquals("TSS", ParamManager.getValueNoSpring(testCode));
        try {
        	ParamManager.getValueNoSpring("not-exsits");
        	Assert.fail("should throw exception but didn't.");
        } catch (Exception e) {
        	Assert.assertTrue("读取不到参数值则抛出异常", true);
        }
        
        Assert.assertEquals("TSS", paramService.getParam(testCode).getValue());
        
        printParams();
        paramService.delete(group.getId());
    }
    
    private void printParams() {
        List<?> list = paramService.getAllParams(false);
        assertTrue(list.size() > 0);
        
        for(Object temp :list) {
            Param p = (Param) temp;
            log.debug(p.getAttributes4XForm());
        }
    }
    
    /** CRUD/排序/移动/复制/停用启用等  */
    @Test
    public void testParamFunction() {
        Param paramGroup = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "测试参数组1");
        String comboParamCode = "book";
		Param comboParam = ParamManager.addComboParam(paramGroup.getId(), comboParamCode, "可选书籍");
        
		ParamManager.addParamItem(comboParam.getId(), "Thinking in JAVA", "Thinking in JAVA", ParamConstants.COMBO_PARAM_MODE);
		ParamManager.addParamItem(comboParam.getId(), "Effictive JAVA", "Effictive JAVA", ParamConstants.COMBO_PARAM_MODE);
		ParamManager.addParamItem(comboParam.getId(), "Design Pattern", "Design Pattern", ParamConstants.COMBO_PARAM_MODE);
        
        Param paramGroup2 = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "测试参数组2");
        String treeParamCode = "group";
		Param treeParam = ParamManager.addTreeParam(paramGroup2.getId(), treeParamCode, "组织");
        
        Param temp = ParamManager.addParamItem(treeParam.getId(), "group1", "组一", ParamConstants.TREE_PARAM_MODE);
        ParamManager.addParamItem(temp.getId(), "group2", "组二", ParamConstants.TREE_PARAM_MODE);
        ParamManager.addParamItem(treeParam.getId(), "group3", "组三", ParamConstants.TREE_PARAM_MODE);
        
        printParams();
        paramService.startOrStop(treeParam.getId(), 1);
        paramService.startOrStop(treeParam.getId(), 0);
        
        paramService.sortParam(paramGroup2.getId(), paramGroup.getId(), -1);
        paramService.copyParam(treeParam.getId(), paramGroup.getId());
        
        printParams();
        
        // test Param Manager
        List<Param> list = ParamManager.getComboParam(comboParamCode);
        Assert.assertEquals(3, list.size());
        
        list = ParamManager.getTreeParam(treeParamCode);
        Assert.assertEquals(3, list.size());
        
        Param simpleParam = ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, "test1", "test1", "test1");
        Assert.assertEquals("test1", simpleParam.getName());
        
        simpleParam.setName(null);
        simpleParam.setHidden(1);
        simpleParam.setUdf2("udf2");
        simpleParam.setUdf3("udf3");
        paramService.saveParam(simpleParam);
        Assert.assertEquals("test1", simpleParam.getCode());
        
        try {
        	ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, "test1", "test1", "test1");
        } catch(Exception e) {
        	log.debug(e.getMessage());
        }
    }
}
