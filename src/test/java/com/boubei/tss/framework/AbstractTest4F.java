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

import com.boubei.tss.H2DBServer;
import com.boubei.tss._TestUtil;
import com.boubei.tss.framework.component.log.LogService;
import com.boubei.tss.framework.component.param.ParamService;
import com.boubei.tss.framework.sso.DemoOperator;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;

@ContextConfiguration(
	  locations={
		    "classpath:META-INF/spring-test.xml",  
		    "classpath:META-INF/spring-framework.xml",
		    "classpath:META-INF/spring-mvc.xml"
	  }   
) 
@TransactionConfiguration(defaultRollback = true) // DB数据自动回滚
public abstract class AbstractTest4F extends AbstractTransactionalJUnit4SpringContextTests { 
 
    protected static Logger log = Logger.getLogger(AbstractTest4F.class);    
    
    protected H2DBServer dbserver;
    
    @Autowired protected LogService logService;
    @Autowired protected ParamService paramService;
    
    protected HttpServletResponse response;
    protected MockHttpServletRequest request;
    
    @Before
    public void setUp() throws Exception {
        Global.setContext(super.applicationContext);
        
        dbserver = new H2DBServer();
        
        _TestUtil.excuteSQL(_TestUtil.getInitSQLDir() + "/framework");
        
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
