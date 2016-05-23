package com.boubei.tss.um.servlet;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.um.TxSupportTest4UM;

public class GetLoginInfoTest extends TxSupportTest4UM {
    
	@Test
    public void testDoPost() {
        MockHttpServletRequest request = new MockHttpServletRequest(); 
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        request.addParameter(SSOConstants.LOGINNAME_IN_SESSION, "Admin");
        
        try {
            GetLoginInfo servlet = new GetLoginInfo();
			servlet.doGet(request, response);
            
        } catch (Exception e) {
        	e.printStackTrace();
        	Assert.assertFalse("Test servlet error:" + e.getMessage(), true);
        }
    }
    
}
