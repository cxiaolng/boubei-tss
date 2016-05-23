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
public class Filter7AccessingCheckTest {

    private Filter7AccessingCheck filter;

    private IMocksControl mocksControl;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        mocksControl = EasyMock.createControl();
        
        request = mocksControl.createMock(HttpServletRequest.class);
        response = mocksControl.createMock(HttpServletResponse.class);
        session = mocksControl.createMock(HttpSession.class); // EasyMock.createMock(HttpSession.class);
        
        EasyMock.expect(request.getSession()).andReturn(session).times(0, 8);
        EasyMock.expect(request.getHeader(RequestContext.USER_CLIENT_IP)).andReturn("127.0.0.1").times(0, 3);
        EasyMock.expect(request.getContextPath()).andReturn("/tss").times(0, 3);
        
		EasyMock.expect(
				session.getAttribute(SSOConstants.USER_RIGHTS_IN_SESSION))
				.andReturn(Arrays.asList(new Long[] { -100L, 2L, 3L })).times(0, 3);
        
        filter = new Filter7AccessingCheck();
    }
 
    @Test
    public final void testDenied() throws IOException, ServletException {
		EasyMock.expect(request.getRequestURI()).andReturn("/tss/login.do");
		EasyMock.expect(request.getServletPath()).andReturn("/login.do");
		EasyMock.expect(request.getSession(false)).andReturn(session).times(0, 8);
		
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
    public final void testCheckOK() throws IOException, ServletException {
    	EasyMock.expect(request.getRequestURI()).andReturn("/tss/login.do").times(0, 3);
    	EasyMock.expect(request.getServletPath()).andReturn("/login.do").times(0, 3);
    	EasyMock.expect(request.getSession(false)).andReturn(session).times(0, 8);
    	EasyMock.expect(
				session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION))
				.andReturn("J.K").times(0, 3);
    	
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
    
    @Test
    public final void testCheckSessionIsNull() throws IOException, ServletException {
    	EasyMock.expect(request.getRequestURI()).andReturn("/tss/login.html").times(0, 3);
    	EasyMock.expect(request.getServletPath()).andReturn("/login.html").times(0, 3);;
    	EasyMock.expect(request.getSession(false)).andReturn( null ).times(0, 8);
    	
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
    
    @Test
    public final void testCheckSessionIsNull2() throws IOException, ServletException {
    	EasyMock.expect(request.getRequestURI()).andReturn("/tss/framework/module/param/param.htm").times(0, 3);
    	EasyMock.expect(request.getServletPath()).andReturn("/framework/module/param/param.htm").times(0, 3);;
    	EasyMock.expect(request.getSession(false)).andReturn( null ).times(0, 8);
    	
    	response.sendRedirect("/404.html");
		EasyMock.expectLastCall();
		
        mocksControl.replay(); // 让mock 准备重放记录的数据

        filter.doFilter(request, response, new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            }
        });
    }
}
