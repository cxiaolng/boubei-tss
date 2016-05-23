package com.boubei.tss.dm.ext;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.AbstractDMTest;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.util.EasyUtils;

public class SyncUserRoleJobTest extends AbstractDMTest {

	@Test
	public void testJob() {
		
		SQLExcutor.excute("delete from um_roleuser where userId=123", DMConstants.LOCAL_CONN_POOL);
        
        String jobConfig =  "select 123 user, '102,103,104' role1, '121,122,104' as role2 from dual where sysdate > ? or sysdate > ?@connectionpool";
        
		try {
			SyncUserRoleJob job = new SyncUserRoleJob();
        	job.excuteJob(jobConfig);
        	
		} catch(Exception e) {
			e.printStackTrace();
			Assert.assertFalse(true);
        }
		
		String sql = "select count(*) num from um_roleuser where userId = ?";
		Object result = SQLExcutor.query(DMConstants.LOCAL_CONN_POOL, sql, 123L).get(0).get("num");
		Assert.assertTrue( EasyUtils.obj2Int(result) > 0 );
	}
	
}
