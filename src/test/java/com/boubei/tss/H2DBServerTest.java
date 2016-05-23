package com.boubei.tss;

import org.junit.Test;


public class H2DBServerTest {
	
	@Test
	public void testH2() {
		for(int i = 0; i < 3; i++) {
			new Thread() {
				public void run() {
					H2DBServer h2 = new H2DBServer();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					
					h2.stopServer();
				}
			}.start();
		}
		
		try {
			Thread.sleep(1000*3);
		} catch (InterruptedException e) {
		}
	}

}
