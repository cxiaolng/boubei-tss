package com.boubei.tss.framework.web.listenr;

import javax.servlet.ServletContextEvent;

import org.junit.Test;

import com.boubei.tss.framework.web.listener.TSSContextLoaderListener;

public class TSSContextLoaderListenerTest {
	
	@Test
	public void test() {
		TSSContextLoaderListener l = new TSSContextLoaderListener();
		
		ServletContextEvent sce = null;
		
		try {
			l.contextInitialized(sce);
		} 
		catch(Exception e) { }
		
		try {
			l.contextDestroyed(sce);
		} 
		catch(Exception e) { }
	}

}
