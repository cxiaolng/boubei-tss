package com.boubei.tss.dm;

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
import com.boubei.tss.framework.component.param.Param;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.component.param.ParamManager;
import com.boubei.tss.framework.component.param.ParamService;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.test.IH2DBServer;
import com.boubei.tss.framework.test.TestUtil;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.PermissionService;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.um.service.IResourceService;
import com.boubei.tss.util.XMLDocUtil;

@ContextConfiguration(
        locations={
                "classpath:META-INF/dm-spring-test.xml",  
                "classpath:META-INF/framework-spring.xml",  
                "classpath:META-INF/um-spring.xml",
                "classpath:META-INF/dm-spring.xml",
                "classpath:META-INF/spring-mvc.xml"
              }, inheritLocations = false // 是否要继承父测试用例类中的 Spring 配置文件，默认为 true
      )
@TransactionConfiguration(defaultRollback = false) // 自动回滚设置为false，否则数据将不插进去
public abstract class TxTestSupport4DM extends AbstractTransactionalJUnit4SpringContextTests { 
 
	protected Logger log = Logger.getLogger(this.getClass());    
    
    @Autowired protected IH2DBServer dbserver;
    
    @Autowired protected ParamService paramService;
    @Autowired protected IResourceService resourceService;
    @Autowired protected ILoginService loginSerivce;
    @Autowired protected PermissionService permissionService;
    @Autowired protected PermissionHelper permissionHelper;
    
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    
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
    
    protected String getDefaultSource(){
    	return "connectionpool";
    }
 
    @After
    public void tearDown() throws Exception {
        dbserver.stopServer();
    }
    
    /**
     * 初始化DM
     */
    protected void init() {
    	// 初始化数据库脚本
    	String sqlpath = TestUtil.getInitSQLDir();
    	log.info( " sql path : " + sqlpath);
        TestUtil.excuteSQL(sqlpath + "/framework");
        TestUtil.excuteSQL(sqlpath + "/um");
    	TestUtil.excuteSQL(sqlpath + "/dm");
    	
    	// 初始化虚拟登录用户信息
        login(UMConstants.ADMIN_USER_ID, UMConstants.ADMIN_USER_NAME);
        
        /* 初始化应用系统、资源、权限项 */
        Document doc = XMLDocUtil.createDocByAbsolutePath(TestUtil.getSQLDir() + "/dm-resource-config.xml");
        resourceService.setInitial(true);
        resourceService.applicationResourceRegister(doc, UMConstants.PLATFORM_SYSTEM_APP);
        resourceService.setInitial(false);
        
        if(paramService.getParam(DMConstants.DATASOURCE_LIST) == null) {
        	Param dlParam = ParamManager.addComboParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.DATASOURCE_LIST, "数据源列表");
            ParamManager.addParamItem(dlParam.getId(), "connectionpool-1", "数据源1", ParamConstants.COMBO_PARAM_MODE);
        }
        if(paramService.getParam(DMConstants.DEFAULT_CONN_POOL) == null) {
        	ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.DEFAULT_CONN_POOL, "默认数据源", getDefaultSource());
        }
        if(paramService.getParam(DMConstants.TEMP_EXPORT_PATH) == null) {
			String tmpDir = System.getProperty("java.io.tmpdir") + "temp";
			log.info("临时文件导出目录：" + tmpDir);
			ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.TEMP_EXPORT_PATH, "临时文件导出目录", tmpDir);
        }
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
    
	protected void initContext() {
		Global.setContext(super.applicationContext);
		
		Context.setResponse(response = new MockHttpServletResponse());
		
		request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SSOConstants.RANDOM_KEY, 100);
		request.setSession(session);
		
		Context.initRequestContext(request);
	}
}
