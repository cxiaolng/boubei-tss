package com.boubei.tss.framework.sso.identity;

import com.boubei.tss.framework.sso.DoNothingLoginCustomizer;
import com.boubei.tss.framework.sso.LoginCustomizerFactory;
 

public class MockLoginCustomizerFactory extends LoginCustomizerFactory {

    public static void init() {
        customizer = new DoNothingLoginCustomizer();
    }
}
