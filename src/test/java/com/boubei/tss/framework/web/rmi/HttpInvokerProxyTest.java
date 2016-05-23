package com.boubei.tss.framework.web.rmi;

import java.util.Random;

import junit.framework.Assert;

import org.apache.commons.httpclient.Cookie;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.boubei.tss.framework.sso.DemoOperator;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.appserver.AppServer;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;

public class HttpInvokerProxyTest {
	
	protected MockHttpServletResponse response;
    protected MockHttpServletRequest request;
    
    @Before
    public void setUp() throws Exception {
        
        Context.setResponse(response = new MockHttpServletResponse());
		
		request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		
		Context.initRequestContext(request);
		
        request.addParameter(RequestContext.PROXY_REAL_PATH, "param/json?code=sysTitle");
        
        String token = TokenUtil.createToken(new Random().toString(), 12L); 
        IdentityCard card = new IdentityCard(token, new DemoOperator(12L));
        Context.initIdentityInfo(card);
    }
	
	@Test
	public void test(){
		AppServer targetAppServer = new AppServer();
		targetAppServer.setCode("TSS");
		targetAppServer.setBaseURL("http://localhost:8111/tss");
		targetAppServer.setName("TSS");
		targetAppServer.setSessionIdName("JSESSIONID");
        
		Cookie[] cookies = new Cookie[3];
		cookies[0] = new Cookie("/", "TSS", "TSS");
		cookies[1] = new Cookie("/", "JSESSIONID", "1234567890");
		cookies[2] = new Cookie("/", "XXX", "XXX");
		
		HttpClientUtil.transmitReturnCookies(cookies , targetAppServer );
		
		Assert.assertEquals(2, Context.getRequestContext().getRequest().getCookies().length);
	}
	
	@Test
	public void test2(){
		AppServer targetAppServer = new AppServer();
		targetAppServer.setCode("TSS");
		targetAppServer.setBaseURL("http://localhost:8111/tss");
		targetAppServer.setName("TSS");
		targetAppServer.setSessionIdName("JSESSIONID");
 
		try {
			HttpClientUtil.getHttpMethod(targetAppServer);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
