package com.boubei.tss.framework.timer;

import org.junit.Test;

import com.boubei.tss.framework.sso.Environment;

public class DemoJobTest {

	@Test
	public void test() {
		new DemoJob().excuteJob("XXX");
		
		System.out.println(Environment.getUserCode());
	}

}
