package com.boubei.tss.framework.sso;

import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;

/**
 * <p>
 * 环境变量对象：利用线程变量，保存运行时用户信息等参数
 * </p>
 */
public class Environment {
	
	public static boolean isAnonymous() {
		return AnonymousOperator.anonymous.getId().equals( getUserId() );
	}
	
    /**
     * 获取用户ID信息
     */
    public static Long getUserId() {
        IdentityCard card = Context.getIdentityCard();
        if (card == null) {
            return null;
        }
        return card.getOperator().getId();
    }

    /**
     * 获取用户账号（LoginName）
     */
    public static String getUserCode() {
        IdentityCard card = Context.getIdentityCard();
        if (card == null) {
            return null;
        }
        return card.getLoginName();
    }
    
    /**
     * 获取用户姓名
     */
    public static String getUserName() {
        IdentityCard card = Context.getIdentityCard();
        if (card == null) {
            return null;
        }
        return card.getUserName();
    }
    
    public static Object getUserInfo(String field) {
        IdentityCard card = Context.getIdentityCard();
        if ( card != null && card.getOperator() != null ) {
        	return card.getOperator().getAttributesMap().get(field);
        }
        return null;
    }

    /**
     * 获取当前SessionID
     */
    public static String getSessionId() {
        RequestContext requestContext = Context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        return requestContext.getSessionId();
    }

    /**
     * 获取用户客户端IP
     */
    public static String getClientIp() {
        RequestContext requestContext = Context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        return requestContext.getClientIp();
    }
    
    /**
     * 获取应用系统上下文根路径(即发布路径)，一般为"/tss"
     */
    public static String getContextPath(){
        return Context.getApplicationContext().getCurrentAppServer().getPath();
    }
}
