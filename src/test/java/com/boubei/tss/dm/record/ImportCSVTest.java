package com.boubei.tss.dm.record;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss._TestUtil;
import com.boubei.tss.dm.AbstractTest4DM;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.record.ddl._Database;
import com.boubei.tss.dm.record.file.ImportCSV;
import com.boubei.tss.dm.report._Reporter;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.FileHelper;

public class ImportCSVTest extends AbstractTest4DM {
	
	@Autowired RecordService recordService;
	@Autowired _Recorder recorder;
	@Autowired _Reporter reporter;
	
	@Test
	public void test() {
		String tblDefine = 
				"[ " +
					"{'label':'类型', 'type':'number', 'nullable':'false'}," +
	        		"{'label':'名称', 'type':'string'}," +
	        		"{'label':'时间', 'type':'datetime', 'nullable':'false'}" +
        		"]";
		
		Record record = new Record();
		record.setName("record-1-csv");
		record.setType(1);
		record.setParentId(0L);
		
		record.setDatasource(DMConstants.LOCAL_CONN_POOL);
		record.setTable("x_tbl_icsv_1");
		record.setDefine(tblDefine);
		
		recordService.saveRecord(record);
		Long recordId = record.getId();
		
		importCSVData(recordId);
		
		_Database _db = _Database.getDB(record);
		SQLExcutor ex = _db.select();
		Assert.assertEquals( SIZE, ex.result.size() );
		Assert.assertEquals("hehe", ex.result.get(0).get("f2"));
		Assert.assertEquals(DateUtil.parse("2015-10-29"), ex.result.get(1).get("f3"));
		
		// 下载导入模板
		recorder.getImportTL(response, recordId);
		
		reporter.dataExport(0, DMConstants.LOCAL_CONN_POOL, "delete from x_tbl_icsv_1");
		List<?> list = (List<?>) reporter.dataExport(1, DMConstants.LOCAL_CONN_POOL, "select f1 from x_tbl_icsv_1");
		Assert.assertEquals(0, list.size());
	}
	
	static String UPLOAD_PATH = _TestUtil.getTempDir() + "/upload/record/";
	static int SIZE = 10 * 10000;  // 10万 12秒
	
	 // 上传附件
    private void importCSVData(Long recordId) {
    	AfterUpload upload = new ImportCSV();
    	
	    IMocksControl mocksControl =  EasyMock.createControl();
	    HttpServletRequest mockRequest = mocksControl.createMock(HttpServletRequest.class);
	    
	    EasyMock.expect(mockRequest.getParameter("recordId")).andReturn(recordId.toString());
	    EasyMock.expect(mockRequest.getParameter("petName")).andReturn(null);
	    
	    try {
	    	String filename = "1.csv";
			String filepath = UPLOAD_PATH + "/" + filename;
			
			StringBuffer sb = new StringBuffer("类型,名称,时间\n");

			// 压力测试 一次导入10万
			for(int i = 3; i <= SIZE; i++) {
				sb.append(i + ",heihei,2015-11-27\n");
			}
			
			sb.append("1,哈哈,2015-10-29\n");
			sb.append("2,hehe,2015-10-19\n");
//			sb.append(",哈哈,2015-10-29\n") // Test number类型为空
			
			FileHelper.writeFile(new File(filepath), sb.toString()); 
	        
	        mocksControl.replay(); 
			upload.processUploadFile(mockRequest, filepath, filename);
//			upload.processUploadFile(mockRequest, "/Users/jinpujun/Desktop/workspace/temp/1.csv", filename);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Assert.assertFalse(e.getMessage(), true);
		}
    }
}
