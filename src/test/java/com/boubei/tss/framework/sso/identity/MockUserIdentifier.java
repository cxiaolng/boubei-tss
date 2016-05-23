package com.boubei.tss.framework.sso.identity;

import com.boubei.tss.framework.exception.UserIdentificationException;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.identifier.BaseUserIdentifier;
 

public class MockUserIdentifier extends BaseUserIdentifier {
 
    protected IOperator validate() throws UserIdentificationException {
        throw new UserIdentificationException();
    }

}
