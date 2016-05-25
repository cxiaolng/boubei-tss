package com.boubei.tss.framework.persistence.connpool;

import java.sql.Connection;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.ReusablePool;
import com.boubei.tss.framework.persistence.connpool._Connection.DatasourceConnectionProvider;
import com.boubei.tss.framework.persistence.connpool._Connection.IConnectionProvider;

public class ConnpoollTest {
	
	@Test
	public void test() {
		ReusablePool connpool = (ReusablePool) JCache.getInstance().getConnectionPool();
		
		Cacheable connItem = connpool.checkOut(0);
		
		connpool.destroyObject(connItem);
		connpool.destroyObject(null);
		
		Assert.assertTrue(connpool.getCustomizer().isValid(connItem) == false);
		
		connpool.reload(connItem);
		
		Assert.assertTrue(connpool.getCustomizer().isValid(connItem) == true);
		
		IConnectionProvider provider = new DatasourceConnectionProvider(new Properties());
		try {
			provider.getConnection();
		} catch(Exception e) {
		}
		
		String config = "org.h2.Driver,jdbc:h2:mem:h2db;DB_CLOSE_DELAY=-1,sa,123";
		Connection conn = _Connection.getInstanse(config).getConnection();
		Assert.assertNotNull(conn);
		
		try {
			config = "com.mysql.jdbc.Driver,jdbc:mysql://10.8.9.10:3306/tss,root,123456";
			_Connection.getInstanse(config).getConnection();
			Assert.fail("should throw exception but didn't.");
		} catch (Exception e) {
			Assert.assertTrue("创建数据库连接时候出错", true);
		}
	}
}
