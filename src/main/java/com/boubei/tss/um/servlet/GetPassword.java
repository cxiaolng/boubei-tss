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
import com.boubei.tss.util.EasyUtils;

/**
 * <p>
 * 密码忘记时根据密码提示问题或答案重新设置密码。成功的话则将【用户ID】返回前台。
 * </p>
 */
@WebServlet(urlPatterns="/getPassword.in")
public class GetPassword extends HttpServlet {
 
	private static final long serialVersionUID = -740569423483772472L;
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginName        = request.getParameter("loginName");
		String passwordAnswer   = request.getParameter("passwordAnswer");
		String passwordQuestion = request.getParameter("passwordQuestion");
		
		IUserService service = (IUserService) Global.getBean("UserService");
		User user = service.getUserByLoginName(loginName);
		
		response.setContentType("text/html;charset=UTF-8");
		if ( user == null ) {
			ErrorMessageEncoder encoder = new ErrorMessageEncoder("用户【" + loginName + "】不存在");
			encoder.print(new XmlPrintWriter(response.getWriter()));
		} 
		else {
            String userPasswordAnswer = user.getPasswordAnswer();
            String userPasswordQuestion = user.getPasswordQuestion();
            if ( EasyUtils.isNullOrEmpty(userPasswordQuestion) || EasyUtils.isNullOrEmpty(userPasswordAnswer)) {
                ErrorMessageEncoder encoder = new ErrorMessageEncoder("您没有设置密码保护，无法通过本方式重置密码，请联系管理员。");
                encoder.print(new XmlPrintWriter(response.getWriter()));
            } 
            else if (passwordAnswer.equals(userPasswordAnswer) && passwordQuestion.equals(userPasswordQuestion)) {
				XmlHttpEncoder encoder = new XmlHttpEncoder();
				encoder.put("UserId", user.getId());
				encoder.print(new XmlPrintWriter(response.getWriter()));
			} 
			else {
				ErrorMessageEncoder encoder = new ErrorMessageEncoder("密码提示问题或答案不正确");
				encoder.print(new XmlPrintWriter(response.getWriter()));
			}
		}
	}
}

	