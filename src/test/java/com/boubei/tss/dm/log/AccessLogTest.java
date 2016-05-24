package com.boubei.tss.dm.log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.dm.AbstractTest4DM;
import com.boubei.tss.dm.data.sqlquery.AbstractSO;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.report.ReportAction;

public class AccessLogTest extends AbstractTest4DM {
	
	@Autowired XXService service;
	@Autowired ReportAction action;
	
	@Test
	public void test() throws InterruptedException {
		
		for(int i = 0; i < 30; i++) {
			service.report1(i, new Date());
		}
		
		Thread.sleep(1*1000);
		
		List<?> logs = permissionHelper.getEntities("from AccessLog");
		Assert.assertTrue(logs.size() >= 0);
		AccessLog firstLog = (AccessLog) logs.get(0);
		firstLog.setId((Long) firstLog.getPK());
		Assert.assertEquals(firstLog.getId(), firstLog.getPK());
		
		SQLExcutor ex = new SQLExcutor();
		
		String sql = "select id, methodName as 方法名称 from dm_access_log";
		ex.excuteQuery(sql);
		ex.excuteQuery(sql, new AbstractSO() {
			private static final long serialVersionUID = 1L;
			public String[] getParameterNames() {
				return null;
			}
		});
		
		HashMap<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		paramsMap.put(1, "0");
		ex.excuteQuery("test1", 1, paramsMap);
		
		ex.excuteQuery(sql, new HashMap<Integer, Object>());
		
		Assert.assertTrue(ex.result.size() > 10);
	}

}
