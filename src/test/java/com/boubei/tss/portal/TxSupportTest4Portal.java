package com.boubei.tss.portal;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.component.log.LogService;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.test.IH2DBServer;
import com.boubei.tss.framework.test.TestUtil;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.portal.entity.Structure;
import com.boubei.tss.portal.helper.CreateComponent;
import com.boubei.tss.portal.helper.MovePortalFile;
import com.boubei.tss.portal.service.IComponentService;
import com.boubei.tss.portal.service.IPortalService;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.PermissionService;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.um.service.IResourceService;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;
import com.boubei.tss.util.XMLDocUtil;

/**
 * Junit Test 类里执行构造函数的时候无事务，即构造函数不在单元测试方法的事物边界内。
 */
@ContextConfiguration(
        locations={
            "classpath:META-INF/portal-test-spring.xml",  
            "classpath:META-INF/framework-spring.xml",  
            "classpath:META-INF/um-spring.xml",
            "classpath:META-INF/spring-mvc.xml"
        } 
        , inheritLocations = false // 是否要继承父测试用例类中的 Spring 配置文件，默认为 true
      )
@TransactionConfiguration(defaultRollback = false) // 不自动回滚，否则后续的test中没有初始化的数据
public abstract class TxSupportTest4Portal extends AbstractTransactionalJUnit4SpringContextTests { 
 
    protected Logger log = Logger.getLogger(this.getClass());    
    
    @Autowired protected IResourceService resourceService;
    @Autowired protected ILoginService loginSerivce;
    @Autowired protected PermissionService permissionService;
    @Autowired protected PermissionHelper permissionHelper;
    @Autowired protected LogService logService;
    
    @Autowired protected IH2DBServer dbserver;
 
    @Autowired protected IComponentService componentService;
    @Autowired protected IPortalService portalService;
    
    protected MockHttpServletResponse response;
    protected MockHttpServletRequest request;
    
	protected void initContext() {
		Global.setContext(super.applicationContext);
		
		Context.setResponse(response = new MockHttpServletResponse());
		
		request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SSOConstants.RANDOM_KEY, 100);
		request.setSession(session);
		
		Context.initRequestContext(request);
	}
	
	// for FreeMarker
	protected void initIdentityCard() {
		request = request == null ? new MockHttpServletRequest() : request;
        Context.initRequestContext(request);
        
        IdentityCard card = new IdentityCard("token", OperatorDTO.ADMIN);
        Context.initIdentityInfo(card);
	}
    
    @Before
    public void setUp() throws Exception {
        initContext();
        
        // DB数据在一轮跑多个单元测试中初始化一次就够了。
        if( dbserver.isPrepareed() ) {
            return;
        }
        
        init();
        
        dbserver.setPrepareed(true);
    }
 
    /**
     * 初始化CMS的动态属性相关模板
     */
    protected void init() {
    	// 初始化数据库脚本
    	String sqlpath = TestUtil.getInitSQLDir();
    	log.info( " sql path : " + sqlpath);
        TestUtil.excuteSQL(sqlpath + "/framework");
        TestUtil.excuteSQL(sqlpath + "/um");
    	TestUtil.excuteSQL(sqlpath + "/cms");
    	TestUtil.excuteSQL(sqlpath + "/portal");
    	
    	// 初始化虚拟登录用户信息
        login(UMConstants.ADMIN_USER_ID, UMConstants.ADMIN_USER_NAME);
        
        /* 初始化应用系统、资源、权限项 */
        Document doc = XMLDocUtil.createDocByAbsolutePath(TestUtil.getSQLDir() + "/portal-resource-config.xml");
        resourceService.setInitial(true);
        resourceService.applicationResourceRegister(doc, UMConstants.PLATFORM_SYSTEM_APP);
        resourceService.setInitial(false);
        
        // 门户浏览时，freemarker解析时需要用到request里的参数
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.setSession(request.getSession());
        Context.initRequestContext(request2); 
        
        /* 初始化默认的修饰器，布局器 */
        initializeDefaultElement();
    }
    
    protected static String MODEL_PORTAL_DIR;
    protected static String MODEL_LAYOUT_DIR;
    protected static String MODEL_DECORATOR_DIR;
    protected static String MODEL_PORTLET_DIR;
  
    protected static Component layoutGroup;
    protected static Component defaultLayout;
    protected static Long defaultLayoutId;
    protected static Component decoratorGroup;
    protected static Component defaultDecorator;
    protected static Long defaultDecoratorId;
    
    /**
     * 初始化默认的修饰器，布局器
     */
    private void initializeDefaultElement() {
        // 初始化Portal测试的组件模型存放目录（model目录）及freemarker文件的目录
        String portalTargetPath = TestUtil.getProjectDir() + "/portal/target";
        
        MODEL_PORTAL_DIR = FileHelper.createDir(portalTargetPath + "/portal/model/portal").getPath();
        MODEL_PORTLET_DIR = FileHelper.createDir(portalTargetPath + "/portal/model/portlet").getPath();
        MODEL_LAYOUT_DIR = FileHelper.createDir(portalTargetPath + "/portal/model/layout").getPath();
        MODEL_DECORATOR_DIR = FileHelper.createDir(portalTargetPath + "/portal/model/decorator").getPath();
        
        File freemarkerDir = FileHelper.createDir(portalTargetPath + "/freemarker");
        FileHelper.writeFile(new File(freemarkerDir + "/common.ftl"), "");
        
        layoutGroup = new Component();
        layoutGroup.setName("布局器组");
        layoutGroup.setType(Component.LAYOUT_TYPE);
        layoutGroup.setIsGroup(true);
        layoutGroup.setParentId(PortalConstants.ROOT_ID);   
        layoutGroup = componentService.saveComponent(layoutGroup);
        
        defaultLayout = new Component();
        defaultLayout.setIsDefault(ParamConstants.TRUE);
        defaultLayout.setParentId(layoutGroup.getId());   
        Document document = XMLDocUtil.createDoc("template/initialize/defaultLayout.xml");
        org.dom4j.Element propertyElement = document.getRootElement().element("property");
        String layoutName = propertyElement.elementText("name");
        defaultLayout.setName(layoutName);
        defaultLayout.setPortNumber(new Integer(propertyElement.elementText("portNumber")));
        defaultLayout.setDefinition(document.asXML());
        componentService.saveComponent(defaultLayout);
        defaultLayoutId = defaultLayout.getId();
        
        decoratorGroup = new Component();
        decoratorGroup.setName("修饰器组");
        decoratorGroup.setIsGroup(true);
        decoratorGroup.setType(Component.DECORATOR_TYPE);
        decoratorGroup.setParentId(PortalConstants.ROOT_ID);  
        decoratorGroup = componentService.saveComponent(decoratorGroup);
        
        defaultDecorator = new Component();
        defaultDecorator.setIsDefault(ParamConstants.TRUE);
        defaultDecorator.setParentId(decoratorGroup.getId());
        
        document = XMLDocUtil.createDoc("template/initialize/defaultDecorator.xml");
        propertyElement = document.getRootElement().element("property");
        String decoratorName = propertyElement.elementText("name");
        defaultDecorator.setName(decoratorName);
        defaultDecorator.setDefinition(document.asXML());
        componentService.saveComponent(defaultDecorator);
        defaultDecoratorId = defaultDecorator.getId();
    }
    
    protected void login(Long userId, String loginName) {
    	OperatorDTO loginUser = new OperatorDTO(userId, loginName);
    	String token = TokenUtil.createToken("1234567890", userId); 
        IdentityCard card = new IdentityCard(token, loginUser);
        Context.initIdentityInfo(card);
        
        // 获取登陆用户的权限（拥有的角色）并保存到用户权限（拥有的角色）对应表
        List<Object[]> userRoles = loginSerivce.getUserRolesAfterLogin(userId);
        permissionService.saveUserRolesAfterLogin(userRoles, userId);
    }
    
    protected void importComponent(Long groupId, String filePath) {
    	AfterUpload servlet = new CreateComponent();
    	
	    IMocksControl mocksControl =  EasyMock.createControl();
	    HttpServletRequest mockRequest = mocksControl.createMock(HttpServletRequest.class);
	    EasyMock.expect(mockRequest.getParameter("groupId")).andReturn(groupId.toString());
	    
	    try {
	        mocksControl.replay(); 
	        servlet.processUploadFile(mockRequest, filePath, null);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Assert.assertFalse(e.getMessage(), true);
		}
    }
    
    protected void uploadFile(String contextPath, File file) {
    	AfterUpload servlet = new MovePortalFile();
    	
	    IMocksControl mocksControl =  EasyMock.createControl();
	    HttpServletRequest mockRequest = mocksControl.createMock(HttpServletRequest.class);
	    EasyMock.expect(mockRequest.getParameter("contextPath")).andReturn(contextPath);
	    
	    try {
	    	mocksControl.replay(); 
	        servlet.processUploadFile(mockRequest, file.getPath(), null);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Assert.assertFalse(e.getMessage(), true);
		}
    }
    
    protected Structure createPageOrSection(Structure parent, String name, String code, int type) {
        Structure newps = new Structure();
        newps.setName(name);
        newps.setCode(code);
        newps.setType(type);
        
        if(type == 1) {
            newps.setSupplement("<page><property><name>Jon的门户</name><description><![CDATA[]]></description></property>" +
            		"<script><file><![CDATA[1.js,2.js]]></file><code><![CDATA[]]></code></script>" +
            		"<style><file><![CDATA[1.css,2.css]]></file><code><![CDATA[]]></code></style></page>");
        }
        newps.setParameters("<params><layout>model/layout/***/paramsXForm.xml</layout><decorator>model/decorator/***/paramsXForm.xml</decorator></params>");
        
        newps.setPortalId(parent.getPortalId());
        newps.setParentId(parent.getId());
        newps.setDecorator(defaultDecorator);
        newps.setDefiner(defaultLayout);
        newps = portalService.createStructure(newps);
        
        return newps;
    }
    
    protected Structure createPortletInstance(Structure parent, String name, String code, Component portlet) {
        Structure newps = new Structure();
        newps.setName(name);
        newps.setCode(code);
        newps.setType(Structure.TYPE_PORTLET_INSTANCE);
 
        newps.setParameters("<params><portlet>model/portlet/***/paramsXForm.xml</portlet><decorator>model/decorator/***/paramsXForm.xml</decorator></params>");
        
        newps.setPortalId(parent.getPortalId());
        newps.setParentId(parent.getId());
        newps.setDecorator(defaultDecorator);
        newps.setDefiner(portlet);
        newps = portalService.createStructure(newps);
        
        return newps;
    }
    
    protected Component createTestPortlet() {
    	List<?> list = permissionHelper.getEntities("from Component o where o.type = ? and o.isGroup = ? order by o.decode", 
                Component.PORTLET_TYPE, false);
    	if( list != null && list.size() > 0 ) {
    		return (Component) list.get(list.size() - 1);
    	}
    	
        Component group = new Component();
        group.setName("测试Portlet组" + System.currentTimeMillis());
        group.setType(Component.PORTLET_TYPE);
        group.setParentId(PortalConstants.ROOT_ID);   
        group = componentService.saveComponent(group);
        
        String file = URLUtil.getResourceFileUrl("testdata/DemoPortlet.zip").getPath();
        importComponent(group.getId(), file);
        
        list = permissionHelper.getEntities("from Component o where o.type = ? and o.isGroup = ? order by o.decode", 
                Component.PORTLET_TYPE, false);
        
        assertTrue(list.size() >= 1);
        return (Component) list.get(list.size() - 1);
    }
}
