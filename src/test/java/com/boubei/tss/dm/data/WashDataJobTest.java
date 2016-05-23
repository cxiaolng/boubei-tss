package com.boubei.tss.dm.data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.AbstractDMTest;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportDao;
import com.boubei.tss.dm.report.ReportService;

public class WashDataJobTest extends AbstractDMTest {
	
	@Autowired ReportService reportService;
	@Autowired RecordService recordService;
	@Autowired ReportDao reportDao;

	@Test
	public void testWashDataJob() {
		
		// create a report
        Report report = new Report();
        report.setType(Report.TYPE1);
        report.setParentId(Report.DEFAULT_PARENT_ID);
        report.setName("report-WashData");
        report.setScript(" select type, name from dm_report where createTime > ?");
        String paramsConfig = "[ {'label':'创建时间', 'type':'date'} ]"	;
        report.setParam(paramsConfig);
        
        reportService.saveReport(report);
        Long reportId = report.getId();
        
        int index = 0;
        while(index++ < 108) {
        	 Report temp = new Report();
        	 temp.setType(Report.TYPE0);
        	 temp.setParentId(Report.DEFAULT_PARENT_ID);
        	 temp.setName("report-WashData-Group" + index);
        	 reportDao.createWithoutFlush(temp);
        }
        
        // create record1
        String tblDefine = "[ {'label':'类型', 'code':'type', 'type':'number', 'nullable':'false'}," +
        		"{'label':'名称', 'code':'name', 'type':'string', 'nullable':'false'} ]";
		Record record1 = new Record();
		record1.setName("record-WashData");
		record1.setType(1);
		record1.setParentId(0L);
		record1.setDatasource(DMConstants.LOCAL_CONN_POOL);
		record1.setTable("x_tbl_WashData");
		record1.setDefine(tblDefine);
		
		recordService.saveRecord(record1);
		Long record1Id = record1.getId();
		
		// create record2, 测试异常流： type1字段非空，且在report里不存在
		tblDefine = "[ {'label':'类型', 'code':'type1', 'type':'number', 'nullable':'false'}," +
        		"{'label':'名称', 'code':'name', 'type':'string', 'nullable':'false'} ]";
		Record record2 = new Record();
		record2.setName("record-WashData-2");
		record2.setType(1);
		record2.setParentId(0L);
		record2.setDatasource(DMConstants.LOCAL_CONN_POOL);
		record2.setTable("x_tbl_WashData");
		record2.setDefine(tblDefine);
		
		recordService.saveRecord(record2);
		Long record2Id = record2.getId();
        
        String jobConfig = "\n" +
        		record1Id + ":" + reportId + ":param1=today-1:50\n" +          // 正常流
        		record2Id + ":" + reportId + ":param1=today-3,fullUpdate\n";   // 异常流
        
		try {
			WashDataJob job = new WashDataJob();
        	job.excuteJob(jobConfig);
		} 
		catch(Exception e) {
        	log.error(e.getCause());
        }
		// 测试异常流 end ------------
		
		SQLExcutor.excute("delete from dm_report where id > 1", getDefaultSource());
	}
	
    protected String getDefaultSource(){
    	return "connectionpool";
    }
}
