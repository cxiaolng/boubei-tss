package com.boubei.tss.dm.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.boubei.tss.dm.AbstractTest4DM;
import com.boubei.tss.dm.data.sqlquery.SqlConfig;
import com.boubei.tss.framework.sso.context.Context;

public class SqlDebugTest extends AbstractTest4DM {
    
    @Autowired private ReportAction action;
    @Autowired private _Reporter display;
    
    @Autowired private ReportService service;
    
    @Test
    public void debugSQL() {     
        String sql = SqlConfig.getScript("test1", 1);
        
        final HttpServletResponse response = Context.getResponse();
        final MockHttpServletRequest  request = new MockHttpServletRequest();
        
        Report report1 = new Report();
        report1.setType(Report.TYPE1);
        report1.setParentId(Report.DEFAULT_PARENT_ID);
        report1.setName("report-1");
        report1.setScript(sql);
        
        String paramsConfig = "[ {'label':'报表ID', 'type':'Number', 'nullable':'false'} ]"	;
        report1.setParam(paramsConfig);
        
        action.saveReport(response, report1);
 
        final Long reportId = report1.getId();
        request.addParameter("param1", "-1");
        display.showAsGrid(request, response, reportId, 1, 100);
        
        // test get report with auth check
        Report reportx = service.getReport(reportId, true);
        Assert.assertNotNull(reportx);
        
        // test QueryCacheInterceptor
        final List<Object> results = new ArrayList<Object>();
        for(int i = 0; i < 10; i++) {
        	new Thread() {
        		public void run() {
        			Map<String, String> requestMap = new HashMap<String, String>();
        			requestMap.put("param1", "-12");
					Object ret = service.queryReport(reportId, requestMap , 1, 0, -1L);
					
					// 查看打出来的是不是同一个对象，是的话说明cache拦截器在queryCache拦截器后执行，正常。
					System.out.println("------" + Thread.currentThread().getId() + "------" + ret); 
					for(Object obj : results) {
						Assert.assertEquals(obj, ret);
					}
					results.add(ret);
    			}
        	}.start();
        	
        	try { Thread.sleep(10); } catch (InterruptedException e) { }
        }
        
        try { Thread.sleep(3*1000); } catch (InterruptedException e) { }
    }
    


}
