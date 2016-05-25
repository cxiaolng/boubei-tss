package com.boubei.tss.framework.web.servlet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.util.URLUtil;

public class Servlet4UploadTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
	    request = new MockHttpServletRequest();
	    response = new MockHttpServletResponse();
	    
	    request.addParameter("afterUploadClass", 
	    		new String[] {"com.boubei.tss.framework.web.servlet.MyAfterUpload"});
	}
	
	@Test
    public void testDoPost() {
        Servlet4Upload servlet = new Servlet4Upload();
        try {
    		
        	servlet.doPost(new XRequest(), response);
            
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
	
	class XRequest implements HttpServletRequest {
		public AsyncContext getAsyncContext() {
			return null;
		}
		public Object getAttribute(String arg0) {
			return null;
		}
		public Enumeration<String> getAttributeNames() {
			return null;
		}
		public String getCharacterEncoding() {
			return null;
		}
		public int getContentLength() {
			return 0;
		}
		public String getContentType() {
			return null;
		}
		public DispatcherType getDispatcherType() {
			return null;
		}
		public ServletInputStream getInputStream() throws IOException {
			return null;
		}
		public String getLocalAddr() {
			return null;
		}
		public String getLocalName() {
			return null;
		}
		public int getLocalPort() {
			return 0;
		}
		public Locale getLocale() {
			return null;
		}
		public Enumeration<Locale> getLocales() {
			return null;
		}
		public String getParameter(String arg0) {
			return null;
		}
		public Map<String, String[]> getParameterMap() {
			return null;
		}
		public Enumeration<String> getParameterNames() {
			return null;
		}
		public String[] getParameterValues(String arg0) {
			return null;
		}
		public String getProtocol() {
			return null;
		}
		public BufferedReader getReader() throws IOException {
			return null;
		}
		public String getRealPath(String arg0) {
			return null;
		}
		public String getRemoteAddr() {
			return null;
		}
		public String getRemoteHost() {
			return null;
		}
		public int getRemotePort() {
			return 0;
		}
		public RequestDispatcher getRequestDispatcher(String arg0) {
			return null;
		}
		public String getScheme() {
			return null;
		}
		public String getServerName() {
			return null;
		}
		public int getServerPort() {
			return 0;
		}
		public ServletContext getServletContext() {
			return null;
		}
		public boolean isAsyncStarted() {
			return false;
		}
		public boolean isAsyncSupported() {
			return false;
		}
		public boolean isSecure() {
			return false;
		}
		public void removeAttribute(String arg0) {
			
		}
		public void setAttribute(String arg0, Object arg1) {
			
		}
		public void setCharacterEncoding(String arg0)
				throws UnsupportedEncodingException {
			
		}
		public AsyncContext startAsync() throws IllegalStateException {
			return null;
		}
		public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1)
				throws IllegalStateException {
			return null;
		}
		public boolean authenticate(HttpServletResponse arg0)
				throws IOException, ServletException {
			return false;
		}
		public String getAuthType() {
			return null;
		}
		public String getContextPath() {
			return null;
		}
		public Cookie[] getCookies() {
			return null;
		}
		public long getDateHeader(String arg0) {
			return 0;
		}
		public String getHeader(String arg0) {
			return null;
		}
		public Enumeration<String> getHeaderNames() {
			return null;
		}
		public Enumeration<String> getHeaders(String arg0) {
			return null;
		}
		public int getIntHeader(String arg0) {
			return 0;
		}
		public String getMethod() {
			return null;
		}
		public Part getPart(String arg0) throws IOException, ServletException {
			return null;
		}
		public Collection<Part> getParts() throws IOException, ServletException {
			return null;
		}
		public String getPathInfo() {
			return null;
		}
		public String getPathTranslated() {
			return null;
		}
		public String getQueryString() {
			return null;
		}
		public String getRemoteUser() {
			return null;
		}
		public String getRequestURI() {
			return null;
		}
		public StringBuffer getRequestURL() {
			return null;
		}
		public String getRequestedSessionId() {
			return null;
		}
		public String getServletPath() {
			return null;
		}
		public HttpSession getSession() {
			return null;
		}
		public HttpSession getSession(boolean arg0) {
			return null;
		}
		public Principal getUserPrincipal() {
			return null;
		}
		public boolean isRequestedSessionIdFromCookie() {
			return false;
		}
		public boolean isRequestedSessionIdFromURL() {
			return false;
		}
		public boolean isRequestedSessionIdFromUrl() {
			return false;
		}
		public boolean isRequestedSessionIdValid() {
			return false;
		}
		public boolean isUserInRole(String arg0) {
			return false;
		}
		public void login(String arg0, String arg1) throws ServletException {
			
		}
		public void logout() throws ServletException {
			
		}

		
	}
}
