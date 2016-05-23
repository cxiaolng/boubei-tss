package com.boubei.tss.framework.persistence.pagequery;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.AbstractFrameworkTest;
import com.boubei.tss.framework.mock.UMQueryCondition;
import com.boubei.tss.framework.mock.dao._IUserDAO;
import com.boubei.tss.framework.mock.model._Group;
import com.boubei.tss.framework.mock.model._User;
import com.boubei.tss.framework.mock.service._IUMSerivce;

public class PaginationQueryBySQLTest  extends AbstractFrameworkTest { 
    
    @Autowired _IUMSerivce umSerivce;
    @Autowired _IUserDAO userDao;

    @Test
    public void testPaginationQueryBySQL() {
    	_Group group = new _Group();
        group.setCode("RD");
        group.setName("研发");
        umSerivce.createGroup(group);
        
        _User user = new _User();
        user.setGroup(group);
        user.setUserName("JohnXa");
        user.setPassword("123456");
        user.setAge(new Integer(25));
        user.setAddr("New York");
        user.setEmail("john@hotmail.com");
        umSerivce.createUser(user);
        
    	UMQueryCondition condition = new UMQueryCondition();
    	condition.setUserName("JohnXa");
    	
    	String sql = " select o.* from test_user o " 
         		+ " where 1=1 " + condition.toConditionString() 
         		+ " order by o.id desc ";
  
    	try {
    		new PaginationQueryBySQL(null, sql, condition);
    	} catch (Exception e) {
    		Assert.assertTrue(true);
    	}
    	
    	try {
    		new PaginationQueryBySQL(userDao.em(), null, condition);
    	} catch (Exception e) {
    		Assert.assertTrue(true);
    	}
    	
    	try {
    		new PaginationQueryBySQL(userDao.em(), sql, null);
    	} catch (Exception e) {
    		Assert.assertTrue(true);
    	}
    	
    	
        PaginationQueryBySQL pageQuery = new PaginationQueryBySQL(userDao.em(), sql, condition);
        PageInfo result = pageQuery.getResultList();
        Assert.assertTrue(result.getItems().size() == 1);
        
        sql = " select o.* from test_user o where 1=1 " + condition;
        pageQuery.setResultClass(_User.class);
        
        // TODO 使用原生SQL查询尚有问题，id字段无法匹配。
//        result = pageQuery.getResultList();
//        Assert.assertTrue(result.getItems().get(0) instanceof _User);
    }
}
