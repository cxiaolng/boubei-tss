package com.boubei.tss.framework.sso.appserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.TxTestSupport;
import com.boubei.tss.framework.component.param.Param;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.component.param.ParamManager;
import com.boubei.tss.framework.component.param.ParamService;
import com.boubei.tss.util.XMLDocUtil;

public class ParamAppServerStorerTest extends TxTestSupport {
    
    @Autowired private ParamService paramService;
    
	 /** 导入应用服务配置文件 appServers.xml */
    @Test
    public void testImportAppServerConfig(){
        Param group = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "应用服务配置");
        
        Document doc = XMLDocUtil.createDoc("tss/appServers.xml");
        List<?> elements = doc.getRootElement().elements();
        for (Iterator<?> it = elements.iterator(); it.hasNext();) {
            Element element = (Element) it.next();
            String name = element.attributeValue("name");
            String code = element.attributeValue("code");
            ParamManager.addSimpleParam(group.getId(), code, name, element.asXML());
        }
        
        List<?> list = paramService.getAllParams(true);
        assertTrue(list.size() > 0);
        
        ParamAppServerStorer paramAppServerStorer = new ParamAppServerStorer();
		AppServer appServer = paramAppServerStorer.getAppServer("TSS");
		assertEquals("TSS", appServer.getCode());
		
		appServer = paramAppServerStorer.getAppServer("TSS");
		assertEquals("TSS", appServer.getCode());
		
		appServer = paramAppServerStorer.getAppServer("CMS");
		assertEquals("CMS", appServer.getCode());
		
		try {
			appServer = paramAppServerStorer.getAppServer("NOT_EXISTS");
			Assert.fail();
		} catch(Exception e) {
			log.debug(e.getMessage());
		}
		
		assertEquals(2, paramAppServerStorer.getAppServers().size());
        
        paramService.delete(group.getId());
    }

}
