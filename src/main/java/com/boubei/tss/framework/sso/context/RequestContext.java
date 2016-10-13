package com.boubei.tss.framework.sso.context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.web.RewriteableHttpServletRequest;
import com.boubei.tss.framework.web.wrapper.RewriteableHttpServletRequestWrapper;
import com.boubei.tss.util.EasyUtils;

/**
 * <p> 当前请求上下文路径 </p>
 * <p>
 * RequestContext生命周期基于每次的request请求，每次请求都会起一个新的进程。
 * </p>
 */
public class RequestContext {

	/** 用户令牌属性名 */
	public static final String USER_TOKEN = "token";

	/** 请求所属系统编号属性名 */
	public static final String APPLICATION_CODE = "appCode";

	/** 用户客户端ID属性名称 */
	public static final String USER_CLIENT_IP = "clientIp";

	/** 用户身份证对象Session属性名 */
	public static final String IDENTITY_CARD = "identity_card";

	/** 请求类型参数名称 */
	public static final String ANONYMOUS_REQUEST = "anonymous";

	/** 被合并的请求标记，即子请求标记：值为true的为子请求，否则为正常请求 */
	public static final String MULTI_REQUEST = "Multi-Request";
	
	public static final String REQUEST_TYPE  = "REQUEST-TYPE";  // HTTP请求类型参数名
	public static final String XMLHTTP_REQUEST  = "xmlhttp";    // XMLHTTP请求的请求类型参数值
	public static final String PROXY_REAL_PATH  = "realPath";   // 需要转发请求的真实访问路径属性名
	public static final String USER_INDENTIFIER = "identifier"; // 用户身份认证类名属性名

    /** 获取当前请求HttpServletRequest对象 */
	private RewriteableHttpServletRequest request;
 
	protected RequestContext(HttpServletRequest request) {
		this.request = RewriteableHttpServletRequestWrapper.wrapRequest(request);
	}
	
	public RewriteableHttpServletRequest getRequest() {
		return request;
	}
    
    /**
     * 获取请求ServletPath（相对路径 /login.do, /logout.in 等）
     *
     * @param request
     * @return
     */
    public static String getServletPath(HttpServletRequest request) {
        return request.getServletPath();
    }

	/**
	 * <p>
	 * 获取当前请求客户端真实IP
	 * </p>
	 *
	 * @return
	 */
	public String getClientIp() {
		String clientIp = request.getHeader(USER_CLIENT_IP);
		if ( EasyUtils.isNullOrEmpty(clientIp) ) {
			clientIp = request.getRemoteAddr();
		}
		return clientIp;
	}

	/**
	 * <p>
	 * 获取当前请求对应Session
	 * </p>
	 * @return
	 */
	public HttpSession getSession() {
		return request.getSession();
	}

	/**
	 * <p>
	 * 获取当前请求对应SessionID
	 * </p>
	 * @return
	 */
	public String getSessionId() {
		HttpSession session = getSession();
		return session != null ? session.getId() : null;
	}

	/**
	 * <p>
	 * 获取Session中存放的用户身份证对象
	 * </p>
	 * @return
	 */
	public IdentityCard getIdentityCard() {
		HttpSession session = getSession();
		return session != null ? (IdentityCard) session.getAttribute(IDENTITY_CARD) : null;
	}
 
	/**
	 * <p>
	 * 可以使用匿名用户访问此请求，如果用户已登录或自动登录成功，则使用注册用户登录后访问；
     * 如果注册用户登录不成功或没有登录，也可以使用匿名用户访问此。
	 * </p>
	 * @return
	 */
	public boolean canAnonymous() {
        return "true".equalsIgnoreCase(getValue(ANONYMOUS_REQUEST));
	}
 
	protected void destroy() {
		request = null;
	}

	/**
	 * 获取当前请求用户对应的身份认证对象类名
	 */
	public String getUserIdentifierClassName() {
		return getValue(USER_INDENTIFIER);
	}

	/**
	 * <p>
	 * 以前请求时的Token值，保留在Session中
	 * </p>
	 * @return
	 */
	public String getAgoToken() {
		return (String) getSession().getAttribute(USER_TOKEN);
	}

	/**
	 * <p>
	 * 获取当前请求用户对应令牌（此令牌保存在请求request的header里或cookie里）
	 * </p>
	 * @return
	 */
	public String getUserToken() {
		return getValueFromRequest(USER_TOKEN);
	}

	/**
	 * <p>
	 * 按顺序（header，parameter，cookie）获取参数值
	 * </p>
	 * @param name
	 * @return
	 */
	public String getValueFromRequest(String name) {
		String value = getValue(name);
		if (value != null && !"".equals(value)) {
			return value;
		}
		// cookie
		value = getValueFromCookie(name);
		if (value != null && !"".equals(value)) {
			return value;
		}
		return null;
	}

	/**
	 * <p>
	 * 从请求Header或参数中获取参数值
	 * </p>
	 * @param name
	 * @return
	 */
	public String getValue(String name) {
		// header
		String value = request.getHeader(name);
		if (value != null && !"".equals(value)) {
			return value;
		}
		// parameters
		value = request.getParameter(name);
		if (value != null && !"".equals(value)) {
			return value;
		}
		return null;
	}

	/**
	 * <p>
	 * 从cookie中尝试获取参数值
	 * </p>
	 * @param name
	 * @return
	 */
	private String getValueFromCookie(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * <p>
	 * 获取当前请求的系统Code
	 * </p>
	 * @return
	 */
	public String getAppCode() {
		return getValue(APPLICATION_CODE);
	}

	/**
	 * <p>
	 * 获取真实访问服务路径，用于个别请求访问地址应用转向问题，当真实地址不能匹配过滤器地址时使用此替代方案。
     * 允许在header或parameter中指定真正要转向的地址。
	 * </p>
	 * @return
	 */
	public String getRealPath() {
		return getValue(PROXY_REAL_PATH);
	}

	/**
	 * <p>
	 * 判断请求是否为复合请求，如果是则返回True，否则返回False
	 * </p>
	 * @return
	 */
	public boolean isMultiRequest() {
		return Config.TRUE.equalsIgnoreCase(request.getHeader(MULTI_REQUEST));
	}

	/**
	 * <p>
	 * 判断请求是否为XMLHTTP请求方式
	 * </p>
	 * @return
	 */
	public boolean isXmlhttpRequest() {
		return XMLHTTP_REQUEST.equals(request.getHeader(REQUEST_TYPE));
	}

	/**
	 * <p>
	 * 判断是否为https方式
	 * </p>
	 * @return
	 */
	public boolean isSecure() {
		return request.isSecure();
	}
}
