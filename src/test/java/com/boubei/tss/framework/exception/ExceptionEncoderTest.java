package com.boubei.tss.framework.exception;

import javax.servlet.ServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.framework.sso.context.Context;

public class ExceptionEncoderTest {
	
	@Test
	public void test() {
		ServletResponse response = new MockHttpServletResponse();
        Context.initRequestContext(new MockHttpServletRequest());
		
		Exception be = new BusinessException("test BusinessException encoder");
		ExceptionEncoder.encodeException(response, be);
		
		be = new BusinessServletException(be, false);
		ExceptionEncoder.encodeException(response, be);
		
		be = new BusinessServletException(be, true);
		ExceptionEncoder.encodeException(response, be);
		
		be = new BusinessServletException("xxx");
		ExceptionEncoder.encodeException(response, be);
		
		be = new BusinessServletException("xxx", be);
		ExceptionEncoder.encodeException(response, be);
	}

}
