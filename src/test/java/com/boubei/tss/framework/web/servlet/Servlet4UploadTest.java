package com.boubei.tss.framework.web.servlet;

import java.io.FileInputStream;
import java.net.URL;

import javax.servlet.http.Part;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import com.boubei.tss.util.URLUtil;

public class Servlet4UploadTest {

	private MockMultipartHttpServletRequest request;
	private MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
	    request = new MockMultipartHttpServletRequest();
	    response = new MockHttpServletResponse();
	    
	    request.addParameter("afterUploadClass", 
	    		new String[] {"com.boubei.tss.framework.web.servlet.MyAfterUpload"});
	}
	
//	@Test
    public void testDoPost() {
        Servlet4Upload servlet = new Servlet4Upload();
        try {
        	MultipartFile file = new MockMultipartFile("file", "test file".getBytes());
    		request.addFile(file);
    		
        	servlet.doPost(request, response);
            
        } catch (Exception e) {
        	Assert.assertFalse("Test servlet error:" + e.getMessage(), true);
        } finally {
        	servlet.destroy();
        }
    }
	
	@Test
    public void testDoUpload() {
        Servlet4Upload servlet = new Servlet4Upload();
        try {
        	IMocksControl mocksControl = EasyMock.createControl();
        	Part part = mocksControl.createMock(Part.class);
            
            EasyMock.expect(part.getHeader("content-disposition")).andReturn("attachment;filename=\"1234.txt\"");
            
            URL url = URLUtil.getResourceFileUrl("application.properties");
            String log4jPath = url.getPath(); 
            EasyMock.expect(part.getInputStream()).andReturn(new FileInputStream(log4jPath));
            
            EasyMock.replay(part); // 让mock 准备重放记录的数据
            
			servlet.doUpload(request, part);
        } 
        catch (Exception e) {
        	Assert.assertFalse("Test servlet error:" + e.getMessage(), true);
        } finally {
        	servlet.destroy();
        }
    }
}
