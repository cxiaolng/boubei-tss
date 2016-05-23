package com.boubei.tss.um.servlet;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.um.AbstractUMTest;

public class RegisterTest extends AbstractUMTest {
    
	@Test
    public void testDoPost() {
        MockHttpServletRequest request = new MockHttpServletRequest(); 
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        request.addParameter("loginName", "JinPujun");
        request.addParameter("password", "JinPujun");
        request.addParameter("userName", "JinPujun");
        request.addParameter("employeeNo", "JinPujun");
        request.addParameter("sex", "1");
        request.addParameter("email", "jinpujun@hotmail.com");
        request.addParameter("birthday", "1983-06-22");
        request.addParameter("address", "hangzhou zhejiang");
        request.addParameter("telephone", "88888888");
        request.addParameter("postalCode", "317000");
        request.addParameter("passwordQuestion", "");
        request.addParameter("passwordAnswer", "");
        request.addParameter("certificate", "");
        request.addParameter("certificateNo", "");
        
        Register registerServlet = new Register();
        
        try {
            registerServlet.init();
            registerServlet.doGet(request, response);
            
        } catch (Exception e) {
        	Assert.assertFalse("Test servlet error:" + e.getMessage(), true);
        } finally {
            registerServlet.destroy();
        }
    }
}
