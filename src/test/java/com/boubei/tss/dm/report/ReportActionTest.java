package com.boubei.tss.dm.report;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.AbstractTest4DM;
import com.boubei.tss.dm.report.permission.ReportResource;
import com.boubei.tss.framework.component.log.LogQueryCondition;
import com.boubei.tss.framework.component.log.LogService;
import com.boubei.tss.framework.component.param.Param;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.component.timer.SchedulerBean;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

public class ReportActionTest extends AbstractTest4DM {
    
    @Autowired private ReportAction action;
    @Autowired private LogService logService;
    
    @Test
    public void testReportCRUD() {
        
        HttpServletResponse response = Context.getResponse();
        MockHttpServletRequest  request = new MockHttpServletRequest();
        
        request.addParameter("parentId", Report.DEFAULT_PARENT_ID.toString());
        action.getReport(request, response, Report.TYPE0);
        
        Report group1 = new Report();
        group1.setType(Report.TYPE0);
        group1.setParentId(Report.DEFAULT_PARENT_ID);
        group1.setName("report-group-1");
        action.saveReport(response, group1);
        
        Report group2 = new Report();
        group2.setType(Report.TYPE0);
        group2.setParentId(Report.DEFAULT_PARENT_ID);
        group2.setName("report-group-2");
        action.saveReport(response, group2);
        
        Report report1 = new Report();
        report1.setType(Report.TYPE1);
        report1.setParentId(group1.getId());
        report1.setName("report-1");
        report1.setScript("select * from dm_report where id = ?");
        String paramsConfig = "[ {'label':'报表ID', 'type':'Number', 'jsonUrl':'../xxx/list', 'multiple':'true'}," +
        		"{'label':'组织列表', 'type':'String', 'nullable':'false'}]"	;
        report1.setParam(paramsConfig);
        report1.setDatasource(DMConstants.DEFAULT_CONN_POOL);
        report1.setDisplayUri("template/ichart.html");
        report1.setRemark("test report");
        report1.setDisabled(ParamConstants.FALSE);
        action.saveReport(response, report1);
        Assert.assertEquals(report1.getResourceType(), new ReportResource().getResourceType());
        
        action.getAllReport(response);
        action.getAllReportGroups(response);
        
        request.addParameter("reportId", report1.getId().toString());
        action.getReport(request, response, Report.TYPE1);
        
        action.startOrStop(response, group1.getId(), 1);
        
        action.startOrStop(response, report1.getId(), 1);
        action.startOrStop(response, report1.getId(), 0);
        
        action.copy(response, report1.getId(), group2.getId());
        action.sort(response, group1.getId(), group2.getId(), 1);
        action.move(response, report1.getId(), group2.getId());
        
        action.getAllReport(response);
        
        // test permission 
        action.getOperations(response, report1.getId());
        
        // test get my reports
        List<Long> myreportIds = action.getMyReportIds();
        Assert.assertTrue(myreportIds.size() > 1);
        
        List<Object> myreports = action.getReportsByGroup(group2.getId());
        Assert.assertTrue(myreports.size() > 1);
        
        // test report schedule
        Long reportId = report1.getId();
        String jobConfig = " 0 36 10 * * ? | " +
        		reportId + ":" + report1.getName() + ":lovejava@163.com,BL00618:param1=1";
        action.saveJobParam(response, report1.getId(), jobConfig);
        
        List<Param> list = paramService.getComboParam(SchedulerBean.TIMER_PARAM_CODE);
        Assert.assertEquals(list.size(), 1);
        
        Object[] result = action.getJobParam(response, reportId);
        Assert.assertEquals("0 36 10 * * ?", result[1].toString().trim());
        // test report schedule end
        
        action.delete(response, report1.getId());
        action.getAllReport(response);
        
        try {
            Thread.sleep(1000); // 等待日志异步输出完毕
        } catch (InterruptedException e) {
        }
        
        LogQueryCondition condition = new LogQueryCondition();
        condition.setOperateTimeFrom(new Date(System.currentTimeMillis() - 1000*3600*3));
        PageInfo logsInfo = logService.getLogsByCondition(condition);
        List<?> logs = logsInfo.getItems();
        for(Object temp : logs) {
            log.debug(temp);
        }
    }
    
    @Test
    public void testReportTLs() {
    	String rtd = DMConstants.getReportTLDir();
 		File reportTLDir = new File(URLUtil.getWebFileUrl(rtd).getPath());
 		File newFile = new File(reportTLDir.getPath() + "/1.html");
 		FileHelper.writeFile(newFile, "111111111111");
 		
 		File tempDir1 = FileHelper.createDir(reportTLDir.getPath() + "/temp1");
 		newFile = new File(tempDir1.getPath() + "/2.html");
 		FileHelper.writeFile(newFile, "222222222222");
        
        reportTLDir = new File(URLUtil.getWebFileUrl("modules/" + DMConstants.REPORT_TL_DIR_DEFAULT).getPath());
        newFile = new File(reportTLDir.getPath() + "/3.html");
        FileHelper.writeFile(newFile, "333333333333");
 		
    	action.getReportTLs(response);
    }
}
