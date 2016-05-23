package com.boubei.tss.framework.exception.convert;

import junit.framework.Assert;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;

public class DefaultExceptionConvertorTest {
	
	@Test
	public void test() {
		DefaultExceptionConvertor c = new DefaultExceptionConvertor();
		
		Exception ex = new ConstraintViolationException("ConstraintViolationException delete error", null, "delete");
		Exception rlt = c.convert(ex );
		Assert.assertEquals(DefaultExceptionConvertor.ERROR_2, rlt.getMessage());
		
		ex = new ConstraintViolationException("ConstraintViolationException insert error", null, "insert");
		rlt = c.convert(ex );
		Assert.assertEquals(DefaultExceptionConvertor.ERROR_1, rlt.getMessage());
		
		ex = new javax.persistence.OptimisticLockException("org.hibernate.StaleObjectStateException: " +
				"Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): " +
				"[com.boubei.tss.um.entity.User#194]");
		rlt = c.convert(ex );
		Assert.assertEquals(DefaultExceptionConvertor.ERROR_3, rlt.getMessage());
	}

}
