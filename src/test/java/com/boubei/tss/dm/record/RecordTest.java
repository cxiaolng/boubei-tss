package com.boubei.tss.dm.record;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.AbstractDMTest;
import com.boubei.tss.dm.record.permission.RecordResource;
import com.boubei.tss.framework.component.log.LogQueryCondition;
import com.boubei.tss.framework.component.log.LogService;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.context.Context;

public class RecordTest extends AbstractDMTest {
    
    @Autowired private RecordAction action;
    @Autowired private LogService logService;
    @Autowired private _Recorder _recorder;
    
    @Test
    public void testRecordCRUD() {
        
        HttpServletResponse response = Context.getResponse();
        MockHttpServletRequest  request = new MockHttpServletRequest();
        
        request.addParameter("parentId", Record.DEFAULT_PARENT_ID.toString());
        action.getRecord(request, response, Record.TYPE0);
        
        Record group1 = new Record();
        group1.setType(Record.TYPE0);
        group1.setParentId(Record.DEFAULT_PARENT_ID);
        group1.setName("record-group-1");
        action.saveRecord(response, group1);
        
        Record group2 = new Record();
        group2.setType(Record.TYPE0);
        group2.setParentId(Record.DEFAULT_PARENT_ID);
        group2.setName("record-group-2");
        action.saveRecord(response, group2);
        
        group2.setDisabled(null);
        Assert.assertEquals(ParamConstants.FALSE, group2.getDisabled());
        
        // test create record
        Record record1 = new Record();
        record1.setType(Record.TYPE1);
        record1.setParentId(group1.getId());
        record1.setName("record-1");
        record1.setDatasource(DMConstants.LOCAL_CONN_POOL);
        record1.setTable("");
        record1.setDefine("[ {'label':'类型', 'code':'f1', 'type':'number', 'nullable':'false'}," +
        		"{'label':'名称', 'code':'f2', 'type':'string'}," +
        		"{'label':'时间', 'code':'f3', 'type':'datetime', 'nullable':'false'}]");
        record1.setCustomizePage("../xx.html");
        record1.setCustomizeJS(" function() f1() { } ");
        record1.setCustomizeGrid(" function() gf1() { } ");
        record1.setBatchImp(ParamConstants.TRUE);
        record1.setCustomizeTJ("");
        record1.setRemark("test record");
        action.saveRecord(response, record1);
        Assert.assertEquals(record1.getResourceType(), new RecordResource().getResourceType());
        
        // test update record
        record1.setTable("x_tbl_test");
        action.saveRecord(response, record1);
        
        action.getAllRecord(response);
        action.getAllRecordGroups(response);
        
        request.addParameter("recordId", record1.getId().toString());
        action.getRecord(request, response, Record.TYPE1);
        
        action.sort(response, group1.getId(), group2.getId(), 1);
        action.move(response, record1.getId(), group2.getId());
        
        action.getAllRecord(response);
        
        // test permission
        action.getOperations(response, record1.getId());
        
        action.startOrStop(response, record1.getId(), ParamConstants.TRUE);
        try{
	        _recorder.getDefine(record1.getId());
	        Assert.fail("should throw exception but didn't.");
	    } catch (Exception e) {
	    	Assert.assertTrue("该数据录入已被停用，无法再录入数据！", true);
	    }
        action.startOrStop(response, record1.getId(), ParamConstants.FALSE);
        _recorder.getDefine(record1.getId());
        
        action.delete(response, record1.getId());
        action.getAllRecord(response);
        
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
}
