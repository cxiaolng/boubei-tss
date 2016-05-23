package com.boubei.tss.framework.sso.context;

import java.util.Collection;

import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.sso.appserver.AppServer;
import com.boubei.tss.framework.sso.appserver.AppServerStorerFactory;
import com.boubei.tss.framework.sso.appserver.IAppServerStorer;

/**
 * <p>
 * 应用系统上下文信息对象
 * </p>
 */
public class ApplicationContext {
	
    protected IAppServerStorer storer;

    /**
     * 默认构造函数
     */
    public ApplicationContext() {
        storer = AppServerStorerFactory.newInstance().getAppServerStorer();
    }

    /**
     * <p>
     * 获取当前系统编号
     * </p>
     * @return
     */
    public String getCurrentAppCode() {
        return Config.getAttribute(Config.APPLICATION_CODE);
    }

    /**
     * <p>
     * 根据应用系统编号获取应用系统配置信息
     * </p>
     * @param appCode
     * @return
     */
    public AppServer getAppServer(String appCode) {
        return storer.getAppServer(appCode);
    }

    /**
     * <p>
     * 获取当前系统访问配置信息
     * </p>
     * @return
     */
    public AppServer getCurrentAppServer() {
        return getAppServer(getCurrentAppCode());
    }
    
    public Collection<AppServer> getAppServers() {
        return storer.getAppServers();
    }
}
