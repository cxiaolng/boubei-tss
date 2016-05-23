package com.boubei.tss.framework.web.servlet;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import com.boubei.tss.framework.sso.DemoOperator;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.online.OnlineUserManagerFactory;

public class Servlet1LoginTest {
	
	MockMultipartHttpServletRequest request;
	MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
	    request = new MockMultipartHttpServletRequest();
	    response = new MockHttpServletResponse();
	}
	
	@Test
    public void testDoPost() {
		Servlet1Login servlet = new Servlet1Login();
        try {
        	servlet.doPost(request, response);
        	
        	request.addParameter("sso", new String[] {"xxx"});
        	servlet.doPost(request, response);
        	
        	String token = TokenUtil.createToken(new Random().toString(), -12L); 
            IdentityCard card = new IdentityCard(token, new DemoOperator(-12L));
            Context.initIdentityInfo(card);
            
            OnlineUserManagerFactory.getManager().register(token, "TSS", "sessionId1", 
            		Environment.getUserId(), Environment.getUserName());
            
            Servlet2Logout servlet2 = new Servlet2Logout();
            servlet2.doGet(request, response);
            
        } catch (Exception e) {
        	e.printStackTrace();
        	Assert.assertFalse("Test servlet error:" + e.getMessage(), true);
        } finally {
        	servlet.destroy();
        }
    }
}
