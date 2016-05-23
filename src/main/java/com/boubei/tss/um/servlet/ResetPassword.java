package com.boubei.tss.um.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.SecurityUtil;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.web.dispaly.SuccessMessageEncoder;
import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.helper.PasswordRule;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.um.service.IUserService;

/**
 * <p> 修改密码Servlet </p>
 * <p>
 * 规则：<br>
 * 1、先验证旧密码是否正确（和主用户组密码），不相等则抛出异常结束修改密码流程；<br>
 * 2、修改主用户组里用户密码。（LoginName + Password MD5加密）<br>
 * </p>
 * 
 * request.getParameter("type")
 * 1、verify: 修改密码。需要正确输入旧密码 
 * 2、reset ：根据密码提示重置密码。只需UserID不为空且对应的用户存在
 */
@WebServlet(urlPatterns="/resetPassword.in")
public class ResetPassword extends HttpServlet {

	private static final long serialVersionUID = -740569423483772472L;
    
    IUserService userService = (IUserService) Global.getBean("UserService");
    ILoginService loginService = (ILoginService) Global.getBean("LoginService");
 
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    	
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");

		Long id = Long.valueOf(userId);
		User user = userService.getUserById(id);
        if(user == null) {
            throw new BusinessException("修改密码时找不到用户ID为" + id + "用户，可能已被删除，请联系管理员");
        }
        
        String newPassword;
        String verifyOrReset = request.getParameter("type");
        if( "reset".equals(verifyOrReset) ) { // 根据密码问题重置密码
        	newPassword = password;
        }
        else { // 正常修改密码
        	String oldPassword = user.encodePassword(password);
			if(!user.getPassword().equals(oldPassword)){
            	throw new BusinessException("旧密码输入不正确");
            }
			newPassword = request.getParameter("newPassword");
        }
        
		// 更新密码
		if(SecurityUtil.getSecurityLevel() >= 3) {
			int level = PasswordRule.getStrengthLevel(newPassword, user.getLoginName());
			if( level < PasswordRule.MEDIUM_LEVEL ) {
				throw new BusinessException("您的密码强度不够，请重新设置一个强度更强的密码！");
			}
		}
        loginService.resetPassword(id, newPassword);
        
		SuccessMessageEncoder encoder = new SuccessMessageEncoder("设置新密码成功！");
		encoder.print(new XmlPrintWriter(response.getWriter()));
    }
}

	