package com.boubei.tss.um.module;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.um.AbstractTest4UM;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.action.MessageAction;
import com.boubei.tss.um.entity.Message;
import com.boubei.tss.um.helper.MessageQueryCondition;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.MailUtil;

public class MessageActionTest extends AbstractTest4UM {
	
	@Autowired MessageAction messageAction;
	
	@Test
	public void test() {
		messageAction.sendEmail("test", "<html><body><h3>生命变的厚重</h3></body></html>", MailUtil.getEmailTo()[0]);
	}
	
	@Test
	public void test2() {
		messageAction.sendHtmlEmail("test", "<html><body><h3>生命变的厚重</h3></body></html>", MailUtil.getEmailTo()[0]);
	}
	
	@Test
	public void test3() {
		messageAction.sendMessage("test", "生命变的厚重", "Admin");
		List<Message> list = messageAction.listMessages();
		Assert.assertTrue(list.size() > 0);
		
		Assert.assertTrue( messageAction.getNewMessageNum() > 0 );
		
		MessageQueryCondition condition = new MessageQueryCondition();
		condition.setContent("厚重");
		condition.setTitle("test");
		condition.setSender(null);
		condition.setSearchTime1(DateUtil.parse("2015-01-01"));
		condition.setSearchTime2(new Date());
		messageAction.listMessages(response, condition, 1);
		
		Message message1 = list.get(0);
		Assert.assertEquals(UMConstants.ADMIN_USER_ID, message1.getReceiverId());
		Assert.assertEquals("test", message1.getTitle());
		Assert.assertEquals("生命变的厚重", message1.getContent());
		Assert.assertEquals(Environment.getUserId(), message1.getSenderId());
		Assert.assertEquals(Environment.getUserName(), message1.getSender());
		Assert.assertNotNull(message1.getSendTime());
		Assert.assertNull(message1.getReadTime());
		
		Long message1Id = message1.getId();
		message1 = messageAction.getMessage(message1Id);
		Assert.assertNotNull(message1.getReadTime());
		
		messageAction.batchRead("view_all");
		messageAction.batchRead(message1Id.toString());
		
		messageAction.deleteMessage(message1Id.toString());
		messageAction.deleteMessage("del_all");
		
		list = messageAction.listMessages();
		Assert.assertTrue(list.size() == 0);
		
		message1.setId((Long) message1.getPK());
		log.debug( message1.getReceiver() );
	}

}
