package com.boubei.tss.portal.engine;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.framework.sso.context.Context;

public class FMSupportActionTest extends FMSupportAction {
	
	@Test
	public void test() {
		Context.setResponse(new MockHttpServletResponse());
		super.printHTML(12L, "${html}", true);
	}
}
