package com.boubei.tss.dm.report.timer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.dm.TxTestSupport4DM;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.component.param.Param;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.component.param.ParamManager;
import com.boubei.tss.um.UMConstants;

public class ReportJobTest extends TxTestSupport4DM {
	
	@Autowired private ReportService service;

	@Test
	public void testReportJob() {
		
		Param paramL = ParamManager.addComboParam(ParamConstants.DEFAULT_PARENT_ID, UMConstants.EMAIL_MACRO, "常用收件人组");
		ParamManager.addParamItem(paramL.getId(), "jinhetss@163.com", "JK", ParamConstants.COMBO_PARAM_MODE);
        
        Report report1 = new Report();
        report1.setType(Report.TYPE1);
        report1.setParentId(Report.DEFAULT_PARENT_ID);
        report1.setName("report-job-test-012");
        report1.setScript(" select id, name, null as udf1 from dm_report " +
        		" where id > ? <#if param2??> and type <> ${param2} <#else> and type = 1 </#if>" +
        		"	and createTime > ?");
        report1.setDisplayUri("template.html");
        
        String paramsConfig = 
        		"[ {'label':'报表ID', 'type':'Number', 'nullable':'false', 'jsonUrl':'../xxx/list', 'multiple':'true'}," +
        		  "{'label':'报表类型', 'type':'String'}," +
        		  "{'label':'创建时间', 'type':'date'}]"	;
        report1.setParam(paramsConfig);
        
        service.saveReport(report1);
        
        ReportJob job = new ReportJob();
        
        String jobConfig = "\n" +
        				   report1.getId() + ":报表一:jinhetss@163.com,BL00618,-1@tssRole,-2@tssGroup,${JK}:param1=0,param2=0,param3=today-0\n" + 
        		           report1.getId() + ":报表二:BL00618,jinhetss@163.com:param1=0,param3=today-1\n" +
        		           report1.getId() + ":报表三:BL00618,jinhetss@163.com:param1=0,param3=today-1\n" +
        		           report1.getId() + ":报表三\n";
		try{
        	job.excuteJob(jobConfig);
		} catch(Exception e) {
        	log.error(e.getCause());
        }
	}
	
    protected String getDefaultSource(){
    	return "connectionpool";
    }
}
