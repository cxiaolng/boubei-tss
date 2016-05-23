package com.boubei.tss.framework.web.filter;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.boubei.tss.framework.exception.BusinessServletException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.appserver.AppServer;
import com.boubei.tss.framework.sso.context.ApplicationContext;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.web.rmi.HttpClientUtil;

/**
 * <pre>
 * 在HEAD或参数中获取当前请求设置的目标地址所在应用CODE值，如果此CODE值为当前应用CODE，则直接放行；
 * 否则根据此CODE值获取对应的应用地址，将访问地址重新改写后（如果是登陆用户则在Header中添加令牌），
 * 然后通过HttpClient开源工具包以HTTP方式请求新地址，将返回的结果设置到本次请求的响应。
 * 
 * 因为本filter无法拦截到访问地址为html（比如OA的首页）的页面（目前配置了拦截.action/.do/.in），所以每次调用html页面前，
 * 可以先发一个 OA/test.do?.... 这样的虚拟请求，以先完成OA等应用的登陆过程，同时取到OA登陆成功返回的token，亦访问OA页面的时使用。
 * 
 * 转发的时候把第一次转发过来header头带的appCode值去掉，理论上不会有二次转发的可能。
 * 去掉可以解决这样的问题：当系统1的CMS要访问系统2、系统3等的CMS抓取文章数据时，可以在系统1的CMS的appServer.xml将其它系统的CMS的Code  
 * 按别名来配置（比如CMS2、CMS3），这样就可以将请求转发到真实的地址。而转发的时候去掉appCode，防止目标应用接到转发请求时再次转发请求。 
 * 因为如果 appCode=CMS2 转发到 系统2的CMS（appCode=CMS）上的话，CMS2 != CMS，系统2会再次转发该请求。 
 * @see HttpMethod com.boubei.tss.core.proxy.HttpClientHelper.getHttpMethod(AppServer appServer)
 * 
 * 注：HttpProxyFilter需要配置在XMLHttpDecodeFilter之前，因为转发请求时将XMLHttp请求的参数流直接转发。
 * 而如果先把XML参数流解析了，则因为该流是一次读完的（读了就没了），转发的时候将不复存在。
 * </pre>
 *
 */
@WebFilter(filterName = "HttpProxyFilter", urlPatterns = {"/*"})
public class Filter5HttpProxy implements Filter {

	Logger log = Logger.getLogger(Filter5HttpProxy.class);
 
	public void init(FilterConfig config) throws ServletException {
		log.info("HttpProxyFilter init! appCode=" + Context.getApplicationContext().getCurrentAppCode());
	}
 
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			RequestContext requestContext = Context.getRequestContext();
			ApplicationContext appContext = Context.getApplicationContext();
			if( requestContext == null || appContext == null ) {
			    chain.doFilter(request, response);
			    return;
			}
			
            String targetAppCode  = requestContext.getAppCode();    // 被请求的目标应用Code
			String currentAppCode = appContext.getCurrentAppCode(); // 发送请求当前应用Code
			
			// 没有取到被请求的应用Code，或者被请求的应用为当前应用，则直接请求本应用
			if (targetAppCode == null || targetAppCode.equals(currentAppCode)) {
				chain.doFilter(request, response);
			} 
			else {
				AppServer targetAppServer = appContext.getAppServer(targetAppCode);
				log.debug("向目标应用：" + targetAppServer.getName() + "（" + targetAppServer.getCode() + "）" + "转发请求。appCode=" + currentAppCode);
				
				proxy4ForeignApplication(targetAppServer, (HttpServletResponse) response);
			}
		} catch (Exception e) {
			throw new BusinessServletException(e);
		}
	}

	/**
	 * <p>
	 * 请求转向不同同服务器的其他应用
	 * </p>
	 * @param appServer
	 * @param req
	 * @param response
	 * @throws IOException
	 * @throws BusinessServletException
	 */
	private void proxy4ForeignApplication(AppServer appServer, HttpServletResponse response) throws IOException, BusinessServletException {
        HttpClient client = HttpClientUtil.getHttpClient(appServer); // 创建HttpClient对象
		HttpState httpState = client.getState();
		
		/* 设置用户令牌相关Cookie，包括一个token Cookie和一个 sessionId Cookie */
        boolean isSecure = Context.getRequestContext().isSecure(); //是否https请求
        String currentAppCode = Context.getApplicationContext().getCurrentAppCode(); //当前应用code
        String domain = appServer.getDomain();
        String path   = appServer.getPath(); 
        if (Context.isOnline()) {
            httpState.addCookie(new Cookie(domain, RequestContext.USER_TOKEN, Context.getToken(), path, null, isSecure)); //token = ****************
        }
        if (Environment.getSessionId() != null) {
            httpState.addCookie(new Cookie(domain, currentAppCode, Environment.getSessionId(), path, null, isSecure)); // TSS = JSessionId
        }
		
		HttpMethod httpMethod = HttpClientUtil.getHttpMethod(appServer); // 创建HttpPost对象（等价于一个Post Http Request）
		try {
			// 请求
            int statusCode = client.executeMethod(httpMethod);
			if (statusCode == HttpStatus.SC_OK) {
				// 转发返回信息
				transmitResponse(appServer, response, client, httpMethod);
			} 
			else if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER)
					|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			    
				dealWithRedirect(appServer, response, client, httpMethod);
			} 
			else {
				throw new BusinessServletException(appServer.getName() + "（" + appServer.getCode() + "）连接错误，错误代码：" + statusCode);
			}
		} finally {
			httpMethod.releaseConnection();
		}
	}

	/**
	 * <p>
	 * 处理请求自动转向问题
	 * </p>
	 * @param appServer
	 * @param response
	 * @param client
	 * @param httppost
	 * @throws IOException
	 * @throws BusinessServletException
	 */
	private void dealWithRedirect(AppServer appServer, HttpServletResponse response, HttpClient client, HttpMethod httpMethod) 
            throws IOException, BusinessServletException {
	    
		Header location = httpMethod.getResponseHeader("location");
		httpMethod.releaseConnection();
		
		String redirectURI =  (location == null ? null : location.getValue());
		if (redirectURI == null || "".equals(redirectURI)) {
			throw new BusinessServletException(appServer.getName() + "（" + appServer.getCode() + "）返回错误的自动转向地址信息");
		}
		
        GetMethod redirect = new GetMethod(redirectURI);
        try {
            client.executeMethod(redirect); // 发送Get请求
            
            transmitResponse(appServer, response, client, redirect);
            
        } finally {
            redirect.releaseConnection();
        }
	}

	/**
	 * <p>
	 * 转发返回数据，包括转发请求的cookie、Header、以及返回数据流（response）设置到转发前请求的响应对象（response）中。
	 * </p>
	 * @param appServer 当前应用相关信息
	 * @param response  转发前的响应对象（即进本filter时的response）
	 * @param client    请求客户端对象
	 * @param httpMethod  Http请求
	 * @throws IOException
	 */
	private void transmitResponse(AppServer appServer, HttpServletResponse response, HttpClient client,
            HttpMethod httpMethod) throws IOException {
        
		// 转发返回的Cookies信息
	    org.apache.commons.httpclient.Cookie[] cookies = client.getState().getCookies();
        for (int i = 0; i < cookies.length; i++) {
            String cookieName = cookies[i].getName();
            if (cookieName.equals(Context.getApplicationContext().getCurrentAppCode())) continue;
            
            if (cookieName.equals(appServer.getSessionIdName())) {
                cookieName = appServer.getCode();
            }
            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(cookieName, cookies[i].getValue());
            cookie.setPath(Context.getApplicationContext().getCurrentAppServer().getPath());
            response.addCookie(cookie);
        }
        
        // 转发返回的Header信息
        Header contentType = httpMethod.getRequestHeader(HttpClientUtil.CONTENT_TYPE);
        if(contentType != null) {
            response.setContentType(contentType.getValue());
        }
        
        // 转发返回数据流信息
        ServletOutputStream out = response.getOutputStream();
        InputStream in = httpMethod.getResponseBodyAsStream();
        byte[] data = new byte[1024];
        int len = 0;
        while ((len = in.read(data)) > 0) {
            out.write(data, 0, len);
        }
        out.flush();
        out.close();
        out = null;
        
        in.close();
        in = null;
	}
 
	public void destroy() { }
	
}
