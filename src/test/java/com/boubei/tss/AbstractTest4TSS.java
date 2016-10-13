package com.boubei.tss;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.log.LogService;
import com.boubei.tss.modules.param.ParamService;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.PermissionService;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.um.service.IResourceService;
import com.boubei.tss.util.XMLDocUtil;

/**
 * Junit Test 类里执行构造函数的时候无事务，即构造函数不在单元测试方法的事物边界内。
 */
@ContextConfiguration(
        locations={
            "classpath:META-INF/spring-test.xml",  
            "classpath:META-INF/spring-framework.xml",  
            "classpath:META-INF/spring-um.xml",
            "classpath:META-INF/spring-dm.xml",
            "classpath:META-INF/spring-mvc.xml"
        } 
        , inheritLocations = false // 是否要继承父测试用例类中的 Spring 配置文件，默认为 true
      )
@TransactionConfiguration(defaultRollback = true) // 自动回滚，每个用力测试完成后自动清空产生的数据
public abstract class AbstractTest4TSS extends AbstractTransactionalJUnit4SpringContextTests { 
 
    protected Logger log = Logger.getLogger(this.getClass());    
    
    @Autowired protected IResourceService resourceService;
    @Autowired protected ILoginService loginSerivce;
    @Autowired protected PermissionService permissionService;
    @Autowired protected PermissionHelper permissionHelper;
    @Autowired protected LogService logService;
    @Autowired protected ParamService paramService;
    
    @Autowired protected H2DBServer dbserver;
 
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
    
    @Before
    public void setUp() throws Exception {
        initContext();
        init();
    }
    
    @After
    public void tearDown() throws Exception {
//    	if(dbserver != null) {
//    		dbserver.stopServer();
//          dbserver = null;
//    	}
    }
 
    /**
     * 初始化CMS的动态属性相关模板
     */
    protected void init() {
    	/* 
    	 * 初始化数据库脚本。
    	 * 此处直接通过jdbc（ stmt.execute(sql) ）向H2插入了初始数据，没法在spring-test框架里自动回滚。
    	 * 通过hibernate生成的数据能回滚，因其事务由spring-test控制。
    	 */
    	if(paramService.getParam(0L) == null) {
			String sqlpath = _TestUtil.getInitSQLDir();
	    	log.info( " sql path : " + sqlpath);
	        _TestUtil.excuteSQL(sqlpath);
	        _TestUtil.excuteSQL(sqlpath + "/um");
    	}
    	
    	// 初始化虚拟登录用户信息
        login(UMConstants.ADMIN_USER_ID, UMConstants.ADMIN_USER_NAME);
        
        /* 初始化应用系统、资源、权限项 */
        Document doc = XMLDocUtil.createDocByAbsolutePath(_TestUtil.getSQLDir() + "/tss-resource-config.xml");
        resourceService.setInitial(true);
        resourceService.applicationResourceRegister(doc, UMConstants.PLATFORM_SYSTEM_APP);
        resourceService.setInitial(false);
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
}
