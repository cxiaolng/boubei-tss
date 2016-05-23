package com.boubei.tss.framework.sso.online;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.boubei.tss.framework.sso.online.CacheOnlineUserManager;
import com.boubei.tss.framework.sso.online.OnlineUser;

/**
 * 内存管理方式在线用户管理系统
 */
public class CacheOnlineUserManagerTest {
    
    protected Logger log = Logger.getLogger(this.getClass());    
 
    @Test
    public void testDBOnlineUserManager() {
    	CacheOnlineUserManager manager = new CacheOnlineUserManager();
        manager.register("token", "TSS",  "sessionId",  new Long(1), "Jon.King");
        manager.register("token", "TSS1", "sessionId1", new Long(1), "Jon.King");
        manager.register("token", "TSS2", "sessionId2", new Long(2), "Jon.King2");
        manager.register("token3", "TSS", "sessionId3", new Long(3), "Jon.King3");

        Set<OnlineUser> userSet = manager.getOnlineUsersByToken("token");
        assertNotNull(userSet);
        assertEquals(3, userSet.size());
        
        Collection<String> onlineUserNames = manager.getOnlineUserNames();
        for(String name : onlineUserNames) {
            log.debug(name);
        }
        
        // testGetAllOnlineInfos4Token
        Set<OnlineUser> userInfos = manager.getOnlineUsersByToken("token");
        assertEquals(3, userInfos.size());
        assertTrue(userInfos.contains(new OnlineUser(new Long(1), "TSS", "sessionId", "token")));
        assertTrue(userInfos.contains(new OnlineUser(new Long(2), "TSS2", "sessionId2", "token")));
        assertTrue(userInfos.contains(new OnlineUser(new Long(1), "TSS1", "sessionId1", "token")));
        
        assertNull(manager.getOnlineUsersByToken("notToken"));
        
        // testIsOnline
        assertTrue(manager.isOnline("token"));
        assertFalse(manager.isOnline("NotLoginToken"));
        
        // testDelete
        assertEquals(3, manager.getOnlineUsersByToken("token").size());

        manager.logout("TSS1", "sessionId1");
        assertEquals(2, manager.getOnlineUsersByToken("token").size());
        
        manager.logout("TSS", "sessionId3");
        assertEquals(2, manager.getOnlineUsersByToken("token").size());
    }

}
