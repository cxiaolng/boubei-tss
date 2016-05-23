package com.boubei.tss.um.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.dispaly.SuccessMessageEncoder;
import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;

/**
 * <p> 用户注册Servlet </p>
 * <p>
 * 因普通的Action会被要求登录用户才能访问，所以这里采用Servlet来实现注册功能。
 * </p>
 */
@WebServlet(urlPatterns="/register.in")
public class Register extends HttpServlet {

    private static final long serialVersionUID = -740569423483772472L;
    
    private IUserService service;
 
	public void init() {
		service = (IUserService) Global.getBean("UserService");
	}
	
	public void destroy() {
		service = null;
	}
	
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        
        // 前台传递的参数
        user.setLoginName(request.getParameter("loginName"));
        user.setPassword(request.getParameter("password"));
        user.setUserName(request.getParameter("userName"));
        user.setEmail(request.getParameter("email"));
        user.setPasswordQuestion(request.getParameter("passwordQuestion"));
        user.setPasswordAnswer(request.getParameter("passwordAnswer"));
        
        service.registerUser(user);

        response.setContentType("text/html;charset=UTF-8");
        SuccessMessageEncoder encoder = new SuccessMessageEncoder("用户注册成功！", "用户注册成功！");
        encoder.print(new XmlPrintWriter(response.getWriter()));
    }
}
