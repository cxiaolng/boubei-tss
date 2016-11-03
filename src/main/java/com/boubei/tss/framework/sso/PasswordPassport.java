package com.boubei.tss.framework.sso;

import javax.servlet.http.HttpSession;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.util.InfoEncoder;

/**
 * <p>
 * 账号密码通行证：从Context.getRequestContext() 获取用户的登陆帐号和密码。
 * 
 * 通行证需要经过验证通过后再正式发放IdentityCard（身份证书）。
 * </p>
 */
public class PasswordPassport {

    /** 用户登录名 */
    private String loginName;

    /** 用户密码 */
    private String password;

    /**
     * 默认构造函数
     */
    public PasswordPassport() throws BusinessException {
        RequestContext requestContext = Context.getRequestContext();
        HttpSession session = requestContext.getSession();
        
        // 检查图形验证码，如果有的话
        String loginCheckKey = requestContext.getValue(SSOConstants.LOGIN_CHECK_KEY);
        Object lckInSession = session.getAttribute(SSOConstants.LOGIN_CHECK_KEY);
        if(loginCheckKey !=null || lckInSession != null) {
        	if( !lckInSession.toString().equals(loginCheckKey) ) {
        		session.removeAttribute(SSOConstants.LOGIN_CHECK_KEY); // 一次验证不成功，就要重新生成验证码(在生成图片验证码时)
        		throw new BusinessException("验证码输入有误，请重新输入。");
        	}
        } 
        
        String loginName = requestContext.getValue(SSOConstants.LOGINNAME_IN_SESSION);
        String password  = requestContext.getValue(SSOConstants.USER_PASSWORD);
        if (loginName == null || password == null) {
            throw new BusinessException("账号或密码不能为空，请重新登录。");
        }
        
        Object randomKey = session.getAttribute(SSOConstants.RANDOM_KEY);
        if(randomKey == null) {
        	randomKey = requestContext.getValue(SSOConstants.RANDOM_KEY);
        }
        if(randomKey == null) {
        	throw new BusinessException("登陆失败，可能是您操作时间过长，请重新输入账户及密码。");
        }
        int _randomKey = Integer.parseInt(randomKey.toString());
        
        this.loginName = InfoEncoder.simpleDecode(loginName, _randomKey);
        this.password = InfoEncoder.simpleDecode(password, _randomKey);
    }

    public String getPassword() {
        return this.password;
    }

    public String getLoginName() {
        return this.loginName;
    }
}
