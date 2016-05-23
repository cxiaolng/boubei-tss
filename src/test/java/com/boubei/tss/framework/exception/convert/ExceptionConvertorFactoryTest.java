package com.boubei.tss.framework.exception.convert;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.exception.convert.ExceptionConvertorFactory;
import com.boubei.tss.framework.exception.convert.IExceptionConvertor;

public class ExceptionConvertorFactoryTest {
	
	@Test
	public void testCreateConvter() {
		Assert.assertTrue(ExceptionConvertorFactory.getConvertor() instanceof IExceptionConvertor);
		
		ExceptionConvertorFactory.convertor = null;
		Config.setProperty(ExceptionConvertorFactory.EXCEPTION_CONVERTOR, "com.boubei.tss.framework.exception.convert.DefaultExceptionConvertor");
		Assert.assertTrue(ExceptionConvertorFactory.getConvertor() instanceof IExceptionConvertor);
		
		ExceptionConvertorFactory.convertor = null;
		Config.setProperty(ExceptionConvertorFactory.EXCEPTION_CONVERTOR, 
				"com.boubei.tss.framework.exception.convert.DefaultExceptionConvertor,com.boubei.tss.framework.exception.convert.DefaultExceptionConvertor");
		Assert.assertTrue(ExceptionConvertorFactory.getConvertor() instanceof ExceptionConvertorChain);
	}

}
