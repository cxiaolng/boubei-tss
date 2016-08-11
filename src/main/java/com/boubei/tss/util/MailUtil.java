package com.boubei.tss.util;

import java.util.Arrays;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.boubei.tss.modules.param.ParamConfig;

public class MailUtil {
	
	protected static Logger log = Logger.getLogger(MailUtil.class);
	
	private static JavaMailSenderImpl mailSender;
	
	// 邮箱信息配置到参数管理里
	public static String MAIL_SERVER = "email.server"; 
	public static String MAIL_SERVER_USER = "email.server.user"; 
	public static String MAIL_SERVER_PWD  = "email.server.pwd"; 
	public static String SEND_FROM = "email.from";
	public static String SEND_TO   = "email.to";
	
	public static JavaMailSender getMailSender() {
		if(mailSender != null) {
			return mailSender;
		}
		
		mailSender = new JavaMailSenderImpl();
		mailSender.setHost(ParamConfig.getAttribute(MAIL_SERVER));
		mailSender.setPort(25);
		
		String auth_user = ParamConfig.getAttribute(MailUtil.MAIL_SERVER_USER);
		if( !EasyUtils.isNullOrEmpty(auth_user) ) {
			mailSender.setUsername( auth_user );
			String pwd = ParamConfig.getAttribute(MailUtil.MAIL_SERVER_PWD);
			mailSender.setPassword( InfoEncoder.simpleDecode(pwd, 12) ); 
			mailSender.getJavaMailProperties().put("mail.smtp.auth", true);
		}
		
		return mailSender;
	}
	
	public static String getEmailFrom() {
		return ParamConfig.getAttribute(SEND_FROM);
	}
	
	public static String[] getEmailTo() {
		return ParamConfig.getAttribute(SEND_TO).split(",");
	}
	
	public static void send(String subject, String text) {
		send(subject, text, getEmailTo());
	}
	
	public static void send(String subject, String text, String receiver[]) {
		if(receiver == null || receiver.length == 0) {
			log.error("收件人不能为空"); 
			return;
		}
		
		SimpleMailMessage mail = new SimpleMailMessage();
		try {
			mail.setTo(receiver);
			mail.setFrom(getEmailFrom()); // 发送者,这里还可以另起Email别名，不用和xml里的username一致
			mail.setSubject(subject);    // 主题
			mail.setText(text);         // 邮件内容
			getMailSender().send(mail);
		} 
		catch (Exception e) {
			log.error("发送文本邮件(" +subject+ ")出错了, " + receiver + "：" + e.getMessage());
		}
	}
	
	public static void sendHTML(String subject, String htmlText, String receiver[]) {
		// 邮件内容，注意加参数true
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<style type='text/css'> " );
		html.append("	body { margin:0; padding:5px; font-family: 微软雅黑; font-size: 15px;}");
		html.append("</style>");
		html.append("</head>");
		html.append("<body>");
		html.append(htmlText);
		html.append("</body>");
		html.append("</html>");
		
		JavaMailSenderImpl sender = (JavaMailSenderImpl) MailUtil.getMailSender();
		MimeMessage mailMessage = sender.createMimeMessage();
		try {
			// 设置utf-8或GBK编码，否则邮件会有乱码
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
			messageHelper.setTo(receiver);   // 接受者
			messageHelper.setFrom(MailUtil.getEmailFrom());  // 发送者
			messageHelper.setSubject(subject); // 主题
			messageHelper.setText(html.toString(), true);
			sender.send(mailMessage);
		} 
		catch (Exception e) {
			log.error("发送报表邮件(" +subject+ ")出错了, " + Arrays.asList(receiver) + ", " + e.getMessage());
		}
	}
}
