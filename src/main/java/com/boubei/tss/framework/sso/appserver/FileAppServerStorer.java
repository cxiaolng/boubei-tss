package com.boubei.tss.framework.sso.appserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.XMLDocUtil;

/**
 * <p>
 * 应用访问配置信息管理器（类型：XML文件）
 * </p>
 * <servers>
 *   <server code="TSS" name="TSS" sessionIdName="JSESSIONID" baseURL="http://localhost:8088/tss"/>
 * </servers>
 */
public class FileAppServerStorer implements IAppServerStorer {
	
	Logger log = Logger.getLogger(FileAppServerStorer.class);

	private static final String DEFAULT_CONFIG_FILE = "tss/appServers.xml";

    private Map<String, AppServer> cache;
    
    public FileAppServerStorer() {
        cache = new HashMap<String, AppServer>();
		init();
    }

	/**
	 * 初始化文件应用服务器列表
	 */
	private synchronized void init() {
		Document doc; 
		try {
			doc = XMLDocUtil.createDoc(DEFAULT_CONFIG_FILE);
		} 
		catch(Exception e) {
			return;
		}
		
		if(cache.size() > 0) {
			return; // 如果已经被其他线程初始化，则不再重复初始化
		}
		
		List<Element> list = XMLDocUtil.selectNodes(doc, "/servers/server");
		for (Element appServerNode : list) {
			 AppServer bean = new AppServer();
		     BeanUtil.setDataToBean(bean, XMLDocUtil.dataNode2Map(appServerNode));
			 cache.put(bean.getCode(), bean);
		}
	}
 
	/**
	 * 根据应用服务器编号获取应用服务器对象
	 */
	public AppServer getAppServer(String code) {
		AppServer appServer = (AppServer) cache.get(code);
		if (appServer == null) {
			throw new BusinessException("系统【"
					+ Context.getApplicationContext().getCurrentAppCode() + "】中没有应用【"+ code + "】的相关访问配置信息");
		}
		return appServer;
	}
 
	public Collection<AppServer> getAppServers() {
		return cache.values();
	}
}
