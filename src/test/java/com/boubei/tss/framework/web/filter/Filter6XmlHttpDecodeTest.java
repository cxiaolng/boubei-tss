package com.boubei.tss.framework.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.sso.identity.MockApplicationContext;
import com.boubei.tss.framework.sso.identity.MockFilterChain;
import com.boubei.tss.util.InfoEncoder;


public class Filter6XmlHttpDecodeTest {

    private Filter6XmlHttpDecode filter;
    
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockHttpSession session;

    private FilterChain chain;

    @Before
    public void setUp() throws Exception {
    	
    	request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        
        request.setSession(session);
        request.addHeader(RequestContext.USER_CLIENT_IP, "192.168.0.12");
        request.setCharacterEncoding("UTF-8");
        
        Context.initApplicationContext(MockApplicationContext.getDefaultApplicationContext());
       
        filter = new Filter6XmlHttpDecode();
        FilterConfig filterConfig = new MockFilterConfig();
		filter.init(filterConfig );
        
        chain = new MockFilterChain();
    }
 
    @Test
    public final void test() throws IOException, ServletException {
    	 request.setServletPath("/tss/auth/login.do");
    	 request.addHeader("REQUEST-TYPE", "xmlhttp");
         
         // 由于XmlHttpDecodeFilter配置在AutoLoginFilter之后，所以登录信息需要放在header里传递
         request.addHeader("loginName", "Jon.King");
         request.addHeader("password", "123456");
         request.addHeader("identifier", "com.boubei.tss.framework.sso.DemoUserIdentifier");
         int encodeKey = 44;
         request.addHeader("encodeKey", encodeKey+"");
         
         String body = "<Request>" +
                         "<Param><Name><![CDATA[loginName]]></Name><Value><![CDATA[Jon.King]]></Value></Param>" +
                         "<Param><Name><![CDATA[password]]></Name><Value><![CDATA[123456]]></Value></Param>" +
                       "</Request>";
         body = InfoEncoder.simpleEncode(body, encodeKey); // 对参数数据进行加密
         request.setContent(body.getBytes());
         
        Context.initRequestContext(request);
        filter.doFilter(request, response, chain);
    }
}
