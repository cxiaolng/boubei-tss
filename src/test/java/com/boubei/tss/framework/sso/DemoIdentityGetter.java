package com.boubei.tss.framework.sso;


public class DemoIdentityGetter implements IdentityGetter {
    
    public IOperator getOperator(Long standardUserId) {
        return new DemoOperator(standardUserId);
    }

	public boolean indentify(IPWDOperator operator, String password) {
		return false;
	}
}
