package com.boubei.tss.framework.sso;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 匿名操作用户
 * </p>
 */
public class AnonymousOperator implements IOperator {

	private static final long serialVersionUID = 5437339121904051176L;

    static final String ANONYMOUS_USER_NAME = "匿名用户"; 
    static final String ANONYMOUS_LOGIN_NAME = "ANONYMOUS"; 
    static final Long   ANONYMOUS_USER_ID = -10000L;// 匿名用户ID
    
    public static AnonymousOperator anonymous = new AnonymousOperator();
    
    private AnonymousOperator() {
    }

    public Long getId() {
        return ANONYMOUS_USER_ID;
    }

    public String getLoginName() {
        return ANONYMOUS_LOGIN_NAME;
    }

    public String getUserName() {
        return ANONYMOUS_USER_NAME;
    }

    public boolean isAnonymous() {
        return true;
    }
 
    public Map<String, Object> getAttributesMap() {
        return new HashMap<String, Object>();
    }
}
