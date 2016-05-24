package com.boubei.tss.um.servlet;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.um.AbstractTest4UM;

public class ResetPasswordTest extends AbstractTest4UM {
    
	@Test
    public void testDoPost() {
        MockHttpServletRequest request = new MockHttpServletRequest(); 
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        request.addParameter("userId", "-1");
        request.addParameter("password", "********");
        request.addParameter("newPassword", "123456789");
        request.addParameter("type", "not-reset");
         
        ResetPassword servlet = new ResetPassword();
        try {
            servlet.init();
            try {
                servlet.doPost(request, response);
                Assert.fail("should throw exception but didn't.");
            } catch (Exception e) {
                assertTrue("旧密码输入不正确", true);
            } 
            
            request.removeParameter("password");
            request.removeParameter("type");
            request.addParameter("password", "123456");
            request.addParameter("type", "reset");
            try {
                servlet.doPost(request, response);
                Assert.fail("should throw exception but didn't.");
            } catch (Exception e) {
                assertTrue("您的密码强度不够，请重新设置一个强度更强的密码！", true);
            } 
            
            request.removeParameter("password");
            request.addParameter("password", "W23Dfr.,!wd");
            request.addParameter("type", "reset");
            servlet.doGet(request, response);
            
        } catch (Exception e) {
            Assert.assertFalse("Test servlet error:" + e.getMessage(), true);
        } finally {
            servlet.destroy();
        }
    }
    
}
