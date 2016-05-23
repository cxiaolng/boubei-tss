package com.boubei.tss.um.sso.othersystem;

import java.security.Principal;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.exception.UserIdentificationException;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.IPWDOperator;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.sso.context.RequestContext;
import com.boubei.tss.framework.sso.identifier.BaseUserIdentifier;
import com.boubei.tss.framework.web.RewriteableHttpServletRequest;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.Escape;

/**
 * <p>
 *  LtpaToken身份认证器 <br>
 *  通过验证是否存在LtpaToken以及username来判断用户是否已经登录OA，如果是，则让其在平台登录。 <br>
 *  
 *  用户在OA系统中登录以后，通过以下地址转入到门户中： <br>
 *  http://ip/tss/login.do?identifier=com.***.LtpaTokenIdentifier&username=AdminX&sso=true <br>
 *  需要在TSS的application.properties文件中设置SSO成功后调整的页面地址，例如： <br>
 *  sso.index.page = /tss/index.portal <br>
 *  默认login.do只返回成功信息，但如果有sso=true和sso.index.page的配置同时存在，则会自动sendRedirect至sso.index.page页面。
 * </p>
 */
public class LtpaTokenIdentifier extends BaseUserIdentifier {
    
    public final static String LTPA_TOKEN_NAME = "LtpaToken";
    public final static String LOGIN_NAME = "username";
    
    ILoginService service = (ILoginService) Global.getBean("LoginService");
 
    protected IOperator validate() throws UserIdentificationException {
        RequestContext requestContext = Context.getRequestContext();
        RewriteableHttpServletRequest request = requestContext.getRequest();

        String ltpaToken = requestContext.getValueFromRequest(LTPA_TOKEN_NAME);
        if(ltpaToken == null) {
            throw new UserIdentificationException("LtpaToken为空，用户可能还没有登录OA，请重新登录");
        }
        
        String loginName;
        Principal userPrincipal = request.getUserPrincipal();
        if(userPrincipal != null){
            loginName = userPrincipal.getName();
        } 
        else {
            loginName = requestContext.getValueFromRequest(LOGIN_NAME);
            if(loginName != null){
                loginName = Escape.unescape(loginName);
            }
        }
        
        if(loginName == null) {
        	throw new UserIdentificationException("取不到用户，请确认已经配置好SSO！");
        }
        
        IPWDOperator operator;
        try {
        	operator = service.getOperatorDTOByLoginName(loginName);
		}  catch (BusinessException e) {
			throw new UserIdentificationException(e.getMessage());
		}
        
        return operator;
    }
}
