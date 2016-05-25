package com.boubei.tss.dm.report;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.dm.AbstractTest4DM;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;

public class DataServiceListTest extends AbstractTest4DM {
    
    @Autowired private ReportAction action;
    @Autowired private _Reporter display;
    
    protected void init() {
    	super.init();
        
        if(paramService.getParam(DMConstants.DATA_SERVICE_CONFIG) == null) {
        	String dsVal = "/tss/btr/orgs|分公司列表,/tss/btr/centers|分拨列表";
        	ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.DATA_SERVICE_CONFIG, "特殊数据服务", dsVal);
        }
    }

    @Test
    public void test1() {  
    	
        HttpServletResponse response = Context.getResponse();
        
        Report report1 = new Report();
        report1.setType(Report.TYPE1);
        report1.setParentId(Report.DEFAULT_PARENT_ID);
        report1.setName("report-1");
        report1.setScript(" select id, name from dm_report where ${testMacro} and createTime > ? " +
        		" <#if param2 != '-1'> and 1=1 </#if> ");
        
        action.saveReport(response, report1);
        
        action.getDateServiceList(response);
    }
    
}