package com.boubei.tss.um.sso;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.IPWDOperator;
import com.boubei.tss.framework.sso.IdentityGetter;
import com.boubei.tss.framework.sso.PWDOperator;
import com.boubei.tss.um.AbstractTest4UM;
import com.boubei.tss.um.sso.othersystem.LdapIdentifyGetter;

public class UMIdentityGetterTest extends AbstractTest4UM {
	
	@Test
	public void test() {
		 IdentityGetter ig = new UMIdentityGetter();
		 ig.getOperator(Environment.getUserId());
		 Assert.assertFalse( ig.indentify(null, "123456") );
		 
		 IdentityGetter ig2 = new LdapIdentifyGetter();
		 IPWDOperator operator = new PWDOperator(-1L);
		 Assert.assertFalse( ig2.indentify(operator , "123456") );
	}

}
