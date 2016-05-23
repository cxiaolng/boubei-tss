package com.boubei.tss.framework;

import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.boubei.tss.framework.component.log.LogService;
import com.boubei.tss.framework.component.param.ParamService;
import com.boubei.tss.framework.sso.DemoOperator;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.test.IH2DBServer;
import com.boubei.tss.framework.test.TestUtil;

@ContextConfiguration(
	  locations={
			"classpath:META-INF/spring-mvc.xml",
		    "classpath:META-INF/framework-test-spring.xml",  
		    "classpath:META-INF/framework-spring.xml"
	  }   
) 
@TransactionConfiguration(defaultRollback = true) // 自动回滚设置为false，否则数据将不插进去
public abstract class TxTestSupport extends AbstractTransactionalJUnit4SpringContextTests { 
 
    protected static Logger log = Logger.getLogger(TxTestSupport.class);    
    
    @Autowired protected IH2DBServer dbserver;
    
    @Autowired protected LogService logService;
    @Autowired protected ParamService paramService;
    
    protected HttpServletResponse response;
    protected MockHttpServletRequest request;
    
    @Before
    public void setUp() throws Exception {
        Global.setContext(super.applicationContext);
        
        if( !dbserver.isPrepareed() ) {
            TestUtil.excuteSQL(TestUtil.getInitSQLDir() + "/framework");
            dbserver.setPrepareed(true);
        }
        
        String token = TokenUtil.createToken(new Random().toString(), 12L); 
        IdentityCard card = new IdentityCard(token, new DemoOperator(12L));
        Context.initIdentityInfo(card);
        
        request = new MockHttpServletRequest();
        Context.setResponse(response = new MockHttpServletResponse());
    }
 
    @After
    public void tearDown() throws Exception {
        dbserver.stopServer();
    }

}
