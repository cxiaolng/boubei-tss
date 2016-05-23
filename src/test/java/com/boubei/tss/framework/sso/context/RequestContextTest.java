package com.boubei.tss.framework.sso.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
 
public class RequestContextTest {

    private IMocksControl mocksControl;

    private HttpServletRequest request;

    private RequestContext context;
 
    @Before
    public void setUp() throws Exception {
        mocksControl =  EasyMock.createControl();
        request = mocksControl.createMock(HttpServletRequest.class);
        
        context = new RequestContext(request);
    }
 
    @Test
    public void testGetClientIpFromHeader() {
        EasyMock.expect(request.getHeader(RequestContext.USER_CLIENT_IP)).andReturn("127.0.0.1").times(0, 3);
        
        mocksControl.replay(); 
        assertEquals("127.0.0.1", context.getClientIp());
    }
 
    @Test
    public void testCanAnonymous4Header() {
        EasyMock.expect(request.getHeader(RequestContext.ANONYMOUS_REQUEST)).andReturn("true").atLeastOnce();
        EasyMock.expect(request.getCharacterEncoding()).andReturn("UTF-8").atLeastOnce();
 
        mocksControl.replay(); 
        assertTrue(context.canAnonymous());
    }
 
    @Test
    public void testCanAnonymous4Parameter() {
        EasyMock.expect(request.getHeader(RequestContext.ANONYMOUS_REQUEST)).andReturn("").atLeastOnce();
        EasyMock.expect(request.getParameter(RequestContext.ANONYMOUS_REQUEST)).andReturn("true").atLeastOnce();
        EasyMock.expect(request.getCharacterEncoding()).andReturn("UTF-8").atLeastOnce();
 
        mocksControl.replay(); 
        assertTrue(context.canAnonymous());
    }
 
    @Test
    public void testGetUserToken4Cookie() {
        EasyMock.expect(request.getHeader(RequestContext.USER_TOKEN)).andReturn(null).atLeastOnce();
        EasyMock.expect(request.getParameter(RequestContext.USER_TOKEN)).andReturn("").atLeastOnce();
        
        String token = "1234567890";
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("test", "test");
        cookies[1] = new Cookie(RequestContext.USER_TOKEN, token);
        EasyMock.expect(request.getCookies()).andReturn(cookies).atLeastOnce();
 
        mocksControl.replay(); 
        assertEquals(token, context.getUserToken());
    }
}
