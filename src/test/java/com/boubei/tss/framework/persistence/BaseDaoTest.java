package com.boubei.tss.framework.persistence;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.TxTestSupport;
import com.boubei.tss.framework.mock.dao._IGroupDAO;
import com.boubei.tss.framework.mock.model._User;

public class BaseDaoTest extends TxTestSupport { 
    
    @Autowired _IGroupDAO dao;

    @Test
    public void testGetEntitiesByNativeSql() {
    	String nativeSql = "select t.* from test_user t where t.userName = ? ";
		List<?> result = dao.getEntitiesByNativeSql(nativeSql, _User.class, "JohnXa");
    	
		try {
			result = dao.getEntitiesByNativeSql(nativeSql, new Object[] {null});
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		
		try {
			result = dao.getEntitiesByNativeSql(nativeSql, _User.class, new Object[] {null});
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		
		log.debug(result);
    }
    
    @Test
    public void testGetEntities() {
    	String hql = " from _User t where t.userName = ? ";
		List<?> result = dao.getEntities(hql, "JohnXa");
		try {
			result = dao.getEntities(hql, new Object[] {null});
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		
		hql = " from _User t where t.userName = :userName ";
		result = dao.getEntities(hql, new String[] {"userName"}, new Object[] { "JohnXa" });
		try {
			result = dao.getEntities(hql, new String[] {"userName"}, new Object[] {null});
		} catch(Exception e) {
			log.error(e.getMessage());
		}
		
		log.debug(result);
    }
}
