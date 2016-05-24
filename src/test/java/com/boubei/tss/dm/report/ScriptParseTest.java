package com.boubei.tss.dm.report;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.boubei.tss.dm.AbstractTest4DM;
import com.boubei.tss.framework.sso.context.Context;

public class ScriptParseTest extends AbstractTest4DM {
    
    @Autowired private ReportAction action;
    @Autowired private _Reporter display;

    @Test
    public void test1() {      
    	
        HttpServletResponse response = Context.getResponse();
        MockHttpServletRequest  request = new MockHttpServletRequest();
        
        Report report1 = new Report();
        report1.setType(Report.TYPE1);
        report1.setParentId(Report.DEFAULT_PARENT_ID);
        report1.setName("report-1");
        report1.setScript(" select id, name from dm_report where ${testMacro} and createTime > ? " +
        		" <#if param2 != '-1'> and 1=1 </#if> ");
        
        String paramsConfig = "[ " +
        		"	{'label':'起始时间', 'type':'date', 'nullable':'false'}, " +
        		"	{'label':'分公司'} " +
        		"]"	;
        report1.setParam(paramsConfig);
        
        action.saveReport(response, report1);
        Long reportId = report1.getId();
        
        request.addParameter("param1", "2013-10-01");
        request.addParameter("param2", "-1");
        display.showAsJson(request, reportId.toString());
        
        request.removeParameter("param2");
        request.addParameter("param2", "阿斯达");
        display.showAsJson(request, reportId.toString());
    }
    
}