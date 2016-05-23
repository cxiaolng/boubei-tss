package com.boubei.tss.framework.component.log;

import junit.framework.Assert;

import org.junit.Test;

public class LogTest {

	@Test
	public void test() {
		
		StringBuffer operationCode = new StringBuffer();
		for(int i = 0; i< 120; i++) {
			operationCode.append(i);
		}
		
		Log log = new Log(operationCode.toString(), new Object());
		Assert.assertTrue( log.getOperationCode().length() == 100 );
		Assert.assertTrue("Object[]".equals(log.getContent()));
				
		log = new Log("Test", null);
		Assert.assertTrue("".equals(log.getContent()));
		
		LogQueryCondition lq = new LogQueryCondition();
		lq.setOperationCode("Test");
		Assert.assertEquals("%Test%", lq.getOperationCode());
		
	}
	
}
