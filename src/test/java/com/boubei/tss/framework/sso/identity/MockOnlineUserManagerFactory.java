package com.boubei.tss.framework.sso.identity;

import com.boubei.tss.framework.sso.online.CacheOnlineUserManager;
import com.boubei.tss.framework.sso.online.OnlineUserManagerFactory;

public class MockOnlineUserManagerFactory extends OnlineUserManagerFactory {

    public static void init() {
        manager = new CacheOnlineUserManager();
    }
}
