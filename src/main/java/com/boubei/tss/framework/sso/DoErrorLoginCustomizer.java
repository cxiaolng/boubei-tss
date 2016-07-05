package com.boubei.tss.framework.sso;

import com.boubei.tss.framework.exception.BusinessException;

/**
 * <p>
 * 默认的登录自定义器：什么事情都不做
 * </p>
 */
public class DoErrorLoginCustomizer implements ILoginCustomizer {
	
	static int count = 1;
 
    public void execute() {
    	if(count++ == 1) {
    		throw new BusinessException("自注册用户不允许直接登陆");
    	}
    	throw new NullPointerException();
    }
}

	