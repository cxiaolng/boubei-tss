package com.boubei.tss.framework.sso.appserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

import com.boubei.tss.framework.component.param.ParamManager;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.XMLDocUtil;

/** 
 * UM的appServer管理启用参数管理(ParamAppServerStorer)，以免增加应用就要修改appServer.xml文件。
 * 
 */
public class ParamAppServerStorer implements IAppServerStorer {
	
	private Map<String, AppServer> cache = new HashMap<String, AppServer>(); 
    
    public AppServer getAppServer(String appCode) {
    	if(cache.get(appCode) != null) {
    		return cache.get(appCode);
    	}
    	
        String appServerXML = null;
        try {
        	// 读取appServer配置不依赖Spring容器，因某些bean初始化本身就要用到AppServer
        	appServerXML = ParamManager.getValueNoSpring(appCode);
        }
        catch(Exception e) { }
        
        if(appServerXML == null) {
            throw new BusinessException("参数管理模块中尚没有应用Code为：" + appCode + " 的应用服务配置信息"); 
        }
        
        try{
            Element appServerNode = XMLDocUtil.dataXml2Doc(appServerXML).getRootElement();
            Map<String, String> attrsMap = XMLDocUtil.dataNode2Map(appServerNode);

            AppServer bean = new AppServer();
            BeanUtil.setDataToBean(bean, attrsMap);
            
            cache.put(appCode, bean);
            return bean;
        } 
        catch(Exception e) {
            throw new BusinessException("参数管理模块中应用Code为：" + appCode + " 的应用服务配置信息有误，请检查！");
        }
    }
    
	public Collection<AppServer> getAppServers() {
		return cache.values();
	}
}

