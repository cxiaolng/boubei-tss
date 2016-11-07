package com.boubei.tss.dm.record;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss._TestUtil;
import com.boubei.tss.dm.AbstractTest4DM;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.record.file.CreateAttach;
import com.boubei.tss.dm.record.file.RecordAttach;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.modules.log.LogService;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;

public class _RecorderTest extends AbstractTest4DM {
	
	@Autowired LogService logService;
	@Autowired RecordService recordService;
	@Autowired _Recorder recorder;
	
	Long recordId;
	
	public void init() {
		super.init();
		
		String tblDefine = "[ {'label':'类型', 'code':'f1', 'type':'number', 'nullable':'false'}," +
        		"{'label':'名称', 'code':'f2', 'type':'string'}," +
        		"{'label':'时间', 'code':'f3', 'type':'datetime', 'nullable':'false'}]";
		
		Record record = new Record();
		record.setName("record-1-1");
		record.setType(1);
		record.setParentId(0L);
		
		record.setDatasource(DMConstants.LOCAL_CONN_POOL);
		record.setTable("x_tbl_12");
		record.setDefine(tblDefine);
		record.setNeedLog(ParamConstants.FALSE);
		
		record.setNeedFile(ParamConstants.TRUE);
		
		recordService.saveRecord(record);
		recordId = record.getId();
	}

	@Test
	public void test() {
		Assert.assertNotNull(recorder.getDefine(recordId));
		
		request.addParameter("f1", "10.9");
		request.addParameter("f2", "just test");
		
		// test 更新时必填字段为空
		try {
			recorder.create(request, response, recordId);
			Assert.fail("should throw exception but didn't.");
		} 
		catch(Exception e) { }
				
		request.addParameter("f3", "2015-04-05");
		recorder.create(request, response, recordId);
		recorder.update(request, response, recordId, 1L);
		
		recorder.getImportTL(response, recordId);
		
		// test 更新时必填字段为空
		try {
			request.removeParameter("f3");
			recorder.update(request, response, recordId, 1L);
			Assert.fail("should throw exception but didn't.");
		} 
		catch(Exception e) { }
		
		List<Map<String, Object>> result = recorder.getDB(recordId).select().result;
		Assert.assertTrue(result.size() == 1);
		
		request = new MockHttpServletRequest();
		request.addParameter("f2", "just test"); // test as query condition
		recorder.showAsGrid(request, response, recordId, 1);
		recorder.showAsJSON(request, recordId, 1);
		
		for(int i = 0; i < 17; i++) {
			request = new MockHttpServletRequest();
			request.addParameter("f1", "12.0");
			request.addParameter("f2", "i'm " + i);
			request.addParameter("f3", "2015-04-05");
			recorder.update(request, response, recordId, 1L); // 多次修改，以生成日志
		}
		
		recorder.updateBatch(response, recordId, "1,2", "f1", "1212");
		result = recorder.getDB(recordId).select().result;
		Map<String, Object> recordItem = result.get(0);
		Assert.assertTrue(recordItem.get("f1").equals(1212.0d));
		
		// test upload record attach
		Long itemId = EasyUtils.obj2Long(recordItem.get("id"));
		uploadDocFile(recordId, itemId);
		List<?> attachList = recorder.getAttachList(recordId, itemId);
		Assert.assertTrue(attachList.size() == 1);
		
		attachList = recorder.getAttachList(recordId);
		Assert.assertTrue(attachList.size() >= 1);
		
		RecordAttach ra = (RecordAttach) attachList.get(0);
		log.info(ra.toString());
		Assert.assertEquals("123.txt", ra.getName());
		Assert.assertEquals(recordId, ra.getRecordId());
		Assert.assertEquals(itemId, ra.getItemId());
		Assert.assertNotNull(ra.getSeqNo());
		Assert.assertEquals("txt", ra.getFileExt());
		Assert.assertNotNull(ra.getFileName());
		Assert.assertNotNull(ra.getUploadDate());
		Assert.assertTrue(ra.isOfficeDoc());
		Assert.assertFalse(ra.isImage());
		
		recorder.showAsGrid(request, response, recordId, 1);
		
		try {
			recorder.downloadAttach(new MockHttpServletResponse(), ra.getId());
			recorder.downloadAttach(new MockHttpServletResponse(), -1010L);
		} catch (Exception e1) {
			Assert.assertTrue("下载附件出错。", true);
		}
		
		recorder.getAttachListXML(response, recordId, itemId);
		
		recorder.deleteAttach(response, ra.getId());
		
		attachList = recorder.getAttachList(recordId, itemId);
		Assert.assertTrue(attachList.size() == 0);
		
		// test delete record
		recorder.delete(response, recordId, 1L);
		
		result = recorder.getDB(recordId).select().result;
		Assert.assertTrue(result.size() == 0);
		
		try { Thread.sleep(1000); } catch (Exception e) { } // 等待修改日志输出
		assertTrue(_TestUtil.printLogs(logService) > 0);
		
		try {
			request = new MockHttpServletRequest();
			request.addParameter("f1", "12.13");
			request.addParameter("f2", "just test end");
			request.addParameter("f3", "2015-04-05");
			recorder.update(request, response, recordId, 1L);
        	Assert.fail("should throw exception but didn't.");
        } catch (Exception e) {
        	Assert.assertTrue("修改出错，该记录不存在，可能已经被删除。", true);
        }
	
		// test delete batch
		for(int i=2; i < 6; i++) {
			request.removeAllParameters();
			request.addParameter("f1", i +".9");
			request.addParameter("f2", "just test");	
			request.addParameter("f3", "2015-04-05");
			recorder.create(request, response, recordId);
		}
		
		result = recorder.getDB(recordId).select().result;
		List<Long> ids = new ArrayList<Long>();
		for(Map<String, Object> item : result) {
			Long _itemId = EasyUtils.obj2Long(item.get("id"));
			uploadDocFile(recordId, _itemId);
			ids.add(_itemId);
		}
		
		recorder.deleteBatch(response, recordId, EasyUtils.list2Str(ids));
		
		result = recorder.getDB(recordId).select().result;
		Assert.assertTrue(result.isEmpty());
		
		Assert.assertTrue( _TestUtil.printEntity(super.permissionHelper, "RecordAttach").isEmpty() ); 
	}
	
	static String UPLOAD_PATH = _TestUtil.getTempDir() + "/upload/record/";
	
	 // 上传附件
    private void uploadDocFile(Long recordId, Object itemId) {
    	AfterUpload upload = new CreateAttach();
    	
	    IMocksControl mocksControl =  EasyMock.createControl();
	    HttpServletRequest mockRequest = mocksControl.createMock(HttpServletRequest.class);
	    
	    EasyMock.expect(mockRequest.getParameter("recordId")).andReturn(recordId.toString());
	    EasyMock.expect(mockRequest.getParameter("itemId")).andReturn(itemId.toString());
	    EasyMock.expect(mockRequest.getParameter("type")).andReturn(RecordAttach.ATTACH_TYPE_DOC.toString());
	    EasyMock.expect(mockRequest.getParameter("petName")).andReturn(null);
	    
	    try {
	    	String filename = "123.txt";
			String filepath = UPLOAD_PATH + "/" + filename;
			FileHelper.writeFile(new File(filepath), "卜贝求真。");
	        
	        mocksControl.replay(); 
			upload.processUploadFile(mockRequest, filepath, filename);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Assert.assertFalse(e.getMessage(), true);
		}
	    
	    _TestUtil.printEntity(super.permissionHelper, "RecordAttach"); 
    }
	
}
