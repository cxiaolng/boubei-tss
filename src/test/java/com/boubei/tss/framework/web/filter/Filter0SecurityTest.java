package com.boubei.tss.framework.web.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.sso.context.RequestContext;

/**
 * 单个的Mock对象，利用静态导入EasyMock，通过createMock(interfaceName.class)
 * 多个Mock对象，通过ImocksControl管理。
 */
public class Filter0SecurityTest {

    private Filter0Security filter;

    private IMocksControl mocksControl;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        mocksControl = EasyMock.createControl();
        
        request = mocksControl.createMock(HttpServletRequest.class);
        response = mocksControl.createMock(HttpServletResponse.class);
        session = mocksControl.createMock(HttpSession.class);
        
        EasyMock.expect(request.getSession()).andReturn(session).times(0, 8);
        EasyMock.expect(request.getSession(false)).andReturn(session).times(0, 8);
        EasyMock.expect(request.getHeader(RequestContext.USER_CLIENT_IP)).andReturn("127.0.0.1").times(0, 3);
        EasyMock.expect(request.getContextPath()).andReturn("/tss").times(0, 3);
        
		EasyMock.expect(request.getServerName()).andReturn("www.boubei.com");
        
        filter = new Filter0Security();
    }
 
    @Test
    public final void testPass() throws IOException, ServletException {
    	EasyMock.expect(request.getHeader("referer")).andReturn("http://www.boubei.com/tss/index.html");
		EasyMock.expect(request.getRequestURI()).andReturn("/tss/login.html");
		EasyMock.expect(request.getServletPath()).andReturn("/login.html");
		
		response.sendRedirect("/404.html");
		EasyMock.expectLastCall();
		
		EasyMock.expect(
				session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION))
				.andReturn("Jane").times(0, 3);
		
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
    
    @Test
    public final void testPass2() throws IOException, ServletException {
    	EasyMock.expect(
				session.getAttribute(SSOConstants.USER_RIGHTS_IN_SESSION))
				.andReturn(Arrays.asList(new Long[] { -10000L, 12L })).times(0, 3);
    	
    	EasyMock.expect(request.getHeader("referer")).andReturn("http://www.boubei.com/tss/index.html");
		EasyMock.expect(request.getRequestURI()).andReturn("/tss/index.html");
		EasyMock.expect(request.getServletPath()).andReturn("/index.html");
		
		response.sendRedirect("/404.html");
		EasyMock.expectLastCall();
		
		EasyMock.expect(
				session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION))
				.andReturn("Jane").times(0, 3);
		
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
    
    @Test
    public final void testPass3() throws IOException, ServletException {
    	EasyMock.expect(
				session.getAttribute(SSOConstants.USER_RIGHTS_IN_SESSION))
				.andReturn(null).times(0, 3);
    	
    	EasyMock.expect(request.getHeader("referer")).andReturn("http://www.boubei.com/tss/index.html");
		EasyMock.expect(request.getRequestURI()).andReturn("/tss/data/json/122");
		EasyMock.expect(request.getServletPath()).andReturn("/data/json/122");
		EasyMock.expect(request.getHeader("REQUEST-TYPE")).andReturn("xmlhttp");
		
		EasyMock.expect(
				session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION))
				.andReturn("Jane").times(0, 3);
		
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
    
    @Test
    public final void testPass4() throws IOException, ServletException {
    	EasyMock.expect(
				session.getAttribute(SSOConstants.USER_RIGHTS_IN_SESSION))
				.andReturn(null).times(0, 3);
    	
    	EasyMock.expect(request.getHeader("referer")).andReturn("http://www.boubei.com/tss/index.html");
		EasyMock.expect(request.getRequestURI()).andReturn("/tss/data/export/122/1/1000000");
		EasyMock.expect(request.getServletPath()).andReturn("/data/export/122/1/1000000");
		
		EasyMock.expect(
				session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION))
				.andReturn("Jane").times(0, 3);
		
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
    
    @Test
    public final void testDeny() throws IOException, ServletException {
    	EasyMock.expect(
				session.getAttribute(SSOConstants.USER_RIGHTS_IN_SESSION))
				.andReturn(null).times(0, 3);
    	
    	EasyMock.expect(request.getHeader("referer")).andReturn("http://www.boubei.com/tss/index.html");
		EasyMock.expect(request.getRequestURI()).andReturn("/tss/data/json/122");
		EasyMock.expect(request.getServletPath()).andReturn("/data/json/122");
		EasyMock.expect(request.getHeader("REQUEST-TYPE")).andReturn("http");
		
		/* 没有返回值void方法的mock方式 */
		response.sendRedirect("/tss/404.html");
		EasyMock.expectLastCall();
		
		EasyMock.expect(
				session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION))
				.andReturn("Jane").times(0, 3);
		
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
    
    @Test
    public final void testDeny2() throws IOException, ServletException {
    	
		EasyMock.expect(request.getHeader("referer")).andReturn("http://www.12306.com/tss/index.html");
		
		/* 没有返回值void方法的mock方式 */
		response.sendRedirect("/tss/404.html");
		EasyMock.expectLastCall();
		
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
}
