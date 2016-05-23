package com.boubei.tss.um.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.dispaly.ErrorMessageEncoder;
import com.boubei.tss.framework.web.dispaly.XmlPrintWriter;
import com.boubei.tss.framework.web.dispaly.xmlhttp.XmlHttpEncoder;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;

/**
 * 获取用户的密码提示问题
 */
@WebServlet(urlPatterns="/getQuestion.in")
public class GetQuestion extends HttpServlet {
 
	private static final long serialVersionUID = -740569423483772472L;
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html;charset=UTF-8");
    	
    	String loginName = request.getParameter("loginName");
		IUserService service = (IUserService) Global.getBean("UserService");
		User user = service.getUserByLoginName(loginName);
		if ( user == null ) {
			ErrorMessageEncoder encoder = new ErrorMessageEncoder("用户【" + loginName + "】不存在");
			encoder.print(new XmlPrintWriter(response.getWriter()));
			return;
		} 
		
		String question = user.getPasswordQuestion();
		if ( question == null ) {
			ErrorMessageEncoder encoder = new ErrorMessageEncoder("【" + loginName + "】没有设置密码提示问题，请联系管理员重置密码");
			encoder.print(new XmlPrintWriter(response.getWriter()));
		} 
		else {
            XmlHttpEncoder encoder = new XmlHttpEncoder();
			encoder.put("Question", question);
			encoder.print(new XmlPrintWriter(response.getWriter()));
		}
	}
}

	