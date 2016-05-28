package com.boubei.tss;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;
import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.portal.entity.Component;
import com.boubei.tss.portal.entity.Navigator;
import com.boubei.tss.portal.service.IComponentService;
import com.boubei.tss.portal.service.INavigatorService;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.um.permission.PermissionService;
import com.boubei.tss.um.permission.ResourcePermission;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.um.service.IResourceService;
import com.boubei.tss.util.XMLDocUtil;

/**
 * 初始化数据库。
 * 初始化时需要把spring.xml 里 <property name="generateDdl" value="true"/> 设为true
 * 
 * 步骤：
 * 0、将 src/test/resources "Remove from build path"（需使用 src/main/resources目录下的配置文件）
 *    改完后重新 clean 一下 project
 * 0、创建一个空的MySQL库（比如wms），并把application.properties里的连接改过去
 * 1、先放开 step1 和 step2 方法上的@Test注释
 * 2、确保 step1 在 step2 前面执行
 * 3、注释掉 step1 和 step2 方法上的@Test
 * 4、把 application.properties 的数据库连接改回原来的
 * 5、重新执行: mvn clean eclipse:eclipse, 刷新 project
 */
@ContextConfiguration(
        locations={
          "classpath:META-INF/spring-framework.xml",  
          "classpath:META-INF/spring-um.xml",
          "classpath:META-INF/spring.xml"
        } 
      )
@TransactionConfiguration(defaultRollback = false) // 不自动回滚
public class InitDatabase extends AbstractTransactionalJUnit4SpringContextTests { 
 
    Logger log = Logger.getLogger(this.getClass());    
    
    @Autowired private IResourceService resourceService;
    @Autowired private ResourcePermission resourcePermission;
    @Autowired private ILoginService loginSerivce;
    @Autowired private PermissionService permissionService;
    
    @Autowired private IComponentService elementService;
    @Autowired private INavigatorService navigatorService;
    
    @Before
    public void setUp() throws Exception {
        Global.setContext(super.applicationContext);
    }
    
    @Test
    public void step0() { }
    
//    @Test
    public void step1() {
    	log.info("init tss databse step2 starting......");
 
        String sqlpath = _TestUtil.getInitSQLDir();
        _TestUtil.excuteSQL(sqlpath);
 
        log.info("init tss databse step1 over.");
    }
    
//    @Test
    public void step2() { /*-------------------------------初始化系統的必要数据 -------------------- */
        log.info("init tss databse step2 starting......");
      
        // 初始化虚拟登录用户信息
        OperatorDTO loginUser = new OperatorDTO(UMConstants.ADMIN_USER_ID, UMConstants.ADMIN_USER_NAME);
        String token = TokenUtil.createToken("1234567890", UMConstants.ADMIN_USER_ID); 
        IdentityCard card = new IdentityCard(token, loginUser);
        Context.initIdentityInfo(card);
        
        // 获取登陆用户的权限（拥有的角色）并保存到用户权限（拥有的角色）对应表
        List<Object[]> userRoles = loginSerivce.getUserRolesAfterLogin(UMConstants.ADMIN_USER_ID);
        permissionService.saveUserRolesAfterLogin(userRoles, UMConstants.ADMIN_USER_ID);
 
        initUM();
        initDM();
        initPortal();
        
        importSystemProperties();
        
        log.info("init tss databse step2 over.");
    }
 
    /**
     * 初始化UM、CMS、Portal相关应用、资源类型、权限选型信息
     */
    private void initUM() {
        /* 初始化应用系统、资源、权限项 */
        String sqlpath = _TestUtil.getInitSQLDir();
        Document doc = XMLDocUtil.createDocByAbsolutePath(sqlpath + "/../tss-resource-config.xml");
        resourceService.setInitial(true);
        resourceService.applicationResourceRegister(doc, UMConstants.PLATFORM_SYSTEM_APP);
        resourceService.setInitial(false);
        
        // 补全SQL初始化出来的系统级用户组
        Long[] groupIds = new Long[] {-2L, -3L, -7L};
        for(Long groupId : groupIds) {
        	resourcePermission.addResource(groupId, UMConstants.GROUP_RESOURCE_TYPE_ID);
        }
    }
    
    // 数据源配置
    private void initDM() {
    	Param group = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "数据源配置");
    	ParamManager.addSimpleParam(group.getId(), DMConstants.DEFAULT_CONN_POOL, "默认数据源", "connectionpool");
        
        Param dlParam = ParamManager.addComboParam(group.getId(), DMConstants.DATASOURCE_LIST, "数据源列表");
        ParamManager.addParamItem(dlParam.getId(), "connectionpool", "本地数据源", ParamConstants.COMBO_PARAM_MODE);
    }
    
    /** 初始化默认的修饰器，布局器 */
    private void initPortal() {
        Component layoutGroup = new Component();
        layoutGroup.setName("布局器组");
        layoutGroup.setIsGroup(true);
        layoutGroup.setType(Component.LAYOUT_TYPE);
        layoutGroup.setParentId(PortalConstants.ROOT_ID);   
        layoutGroup = elementService.saveComponent(layoutGroup);
        
        Component defaultLayout = new Component();
        defaultLayout.setIsDefault(ParamConstants.TRUE);
        defaultLayout.setParentId(layoutGroup.getId());   
        Document document = XMLDocUtil.createDoc("template/portal/defaultLayout.xml");
        org.dom4j.Element propertyElement = document.getRootElement().element("property");
        String layoutName = propertyElement.elementText("name");
        defaultLayout.setName(layoutName);
        defaultLayout.setPortNumber(new Integer(propertyElement.elementText("portNumber")));
        defaultLayout.setDefinition(document.asXML());
        elementService.saveComponent(defaultLayout);
        
        Component decoratorGroup = new Component();
        decoratorGroup.setName("修饰器组");
        decoratorGroup.setIsGroup(true);
        decoratorGroup.setType(Component.DECORATOR_TYPE);
        decoratorGroup.setParentId(PortalConstants.ROOT_ID);  
        decoratorGroup = elementService.saveComponent(decoratorGroup);
        
        Component defaultDecorator = new Component();
        defaultDecorator.setIsDefault(ParamConstants.TRUE);
        defaultDecorator.setParentId(decoratorGroup.getId());
        
        document = XMLDocUtil.createDoc("template/portal/defaultDecorator.xml");
        propertyElement = document.getRootElement().element("property");
        String decoratorName = propertyElement.elementText("name");
        defaultDecorator.setName(decoratorName);
        defaultDecorator.setDefinition(document.asXML());
        elementService.saveComponent(defaultDecorator);
        
        Component portletGroup = new Component();
        portletGroup.setName("portlet组");
        portletGroup.setIsGroup(true);
        portletGroup.setType(Component.PORTLET_TYPE);
        portletGroup.setParentId(PortalConstants.ROOT_ID);   
        portletGroup = elementService.saveComponent(portletGroup);
        
        // 新建一个应用菜单组（不依附于门户）
        Navigator appMenuGroup = new Navigator();
        appMenuGroup.setName("应用菜单组");
        appMenuGroup.setType(Navigator.TYPE_MENU);
        appMenuGroup.setParentId(PortalConstants.ROOT_ID);
        navigatorService.saveNavigator(appMenuGroup);
    }
    
    
    @Autowired private ParamService paramService;
 
    /**
     * 导入 application.properties文件 和 appServers.xml
     */
    public void importSystemProperties(){
        String name = "系统参数";
        Param param = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, name);
        ResourceBundle resources = ResourceBundle.getBundle("application", Locale.getDefault());
        if (resources == null) return;
        
        for (Enumeration<String> enumer = resources.getKeys(); enumer.hasMoreElements();) {
            String key = enumer.nextElement();
            String value = resources.getString(key);
            ParamManager.addSimpleParam(param.getId(), key, key, value);
        }
 
        Param paramGroup = ParamManager.addParamGroup(ParamConstants.DEFAULT_PARENT_ID, "应用服务配置");
        
        Document doc = XMLDocUtil.createDoc("tss/appServers.xml");
        List<?> elements = doc.getRootElement().elements();
        for (Iterator<?> it = elements.iterator(); it.hasNext();) {
            org.dom4j.Element element = (org.dom4j.Element) it.next();
            String appName = element.attributeValue("name");
            String appCode = element.attributeValue("code");
            ParamManager.addSimpleParam(paramGroup.getId(), appCode, appName, element.asXML());
        }
    }
}
