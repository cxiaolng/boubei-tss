package com.boubei.tss.um.sso;

import org.junit.Test;

import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.sso.IdentityGetter;
import com.boubei.tss.um.TxSupportTest4UM;

public class UMIdentityGetterTest extends TxSupportTest4UM {
	
	@Test
	public void testTranslator() {
		 IdentityGetter translator = new UMIdentityGetter();
		 translator.getOperator(Environment.getUserId());
	}

}
