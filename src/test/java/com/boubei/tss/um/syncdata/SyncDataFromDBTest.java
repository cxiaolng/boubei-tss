package com.boubei.tss.um.syncdata;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.H2DBServer;
import com.boubei.tss.framework.component.progress.Progress;
import com.boubei.tss.framework.component.progress.Progressable;
import com.boubei.tss.um.AbstractUMTest;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.action.GroupAction;
import com.boubei.tss.um.entity.Application;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IResourceService;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

/**
 * 测试用户同步
 */
public class SyncDataFromDBTest extends AbstractUMTest {

	@Autowired GroupAction groupAction;
	
	@Autowired ISyncService syncService;
	@Autowired IResourceService resourceService;
	
	Group mainGroup;
	String applicationId = UMConstants.TSS_APPLICATION_ID;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		mainGroup = new Group();
		mainGroup.setParentId(UMConstants.MAIN_GROUP_ID);
		mainGroup.setName("他山石" + System.currentTimeMillis());
		mainGroup.setGroupType(Group.MAIN_GROUP_TYPE);
		mainGroup.setFromApp(UMConstants.TSS_APPLICATION_ID);
		mainGroup.setFromGroupId("1");
		
		groupService.createNewGroup(mainGroup, "", "-1");
		log.debug(mainGroup + "\n");

		Application application = resourceService.getApplication(UMConstants.TSS_APPLICATION_ID);
        assertNotNull(application);
        application.setDataSourceType(UMConstants.DATA_SOURCE_TYPE_DB);
        URL template = URLUtil.getResourceFileUrl("template/um/syncdata/template_DB.xml");
        String paramDesc = FileHelper.readFile(new File(template.getPath()));
        application.setParamDesc(paramDesc);
        resourceService.saveApplication(application);
        
        // 准备数据
        initSourceTable();
	}
 
	private void initSourceTable() {
		Connection conn = dbserver.getH2Connection();
		
        try {
        	if( conn.isClosed() ) {
        		dbserver = new H2DBServer();
        		conn = dbserver.getH2Connection();
        	}
        	
        	if( conn.isClosed() ) {
        		Assert.fail("H2 connection is closed!");
        	}
        		
            Statement stat = conn.createStatement();
            stat.execute("create table if not exists xxx_group" + 
                    "(" + 
                        "id            NUMBER(19) not null, " + 
                        "parentId      NUMBER(19), " + 
                        "seqNo         NUMBER(10), " + 
                        "name          VARCHAR2(50 CHAR), " + 
                        "description   VARCHAR2(255 CHAR)" + 
                    ");" +
                    " alter table xxx_group add primary key (id); ");
            
            stat.execute("create table if not exists xxx_user" + 
                    "(" + 
                        "id           NUMBER(19) not null, " + 
                        "groupId      NUMBER(19) not null, " + 
                        "sex          NUMBER(1), " + 
                        "birthday     TIMESTAMP(6), " + 
                        "employeeNo   VARCHAR2(255 CHAR), " + 
                        "loginName    VARCHAR2(255 CHAR), " + 
                        "userName     VARCHAR2(255 CHAR), " + 
                        "password     VARCHAR2(255 CHAR), " + 
                        "email        VARCHAR2(255 CHAR), " + 
                        "disabled     NUMBER(1)" + 
                    ");" +
                    " alter table xxx_user add primary key (id); ");
            
            PreparedStatement ps = conn.prepareStatement("insert into xxx_group values (?, ?, ?, ?, ?)");
            ps.setObject(1, 1L);
            ps.setObject(2, 0L);
            ps.setObject(3, 1);
            ps.setObject(4, "它山石");
            ps.setObject(5, "test test");
            ps.executeUpdate();
            
            ps.setObject(1, 2L);
            ps.setObject(2, 1L);
            ps.setObject(3, 1);
            ps.setObject(4, "子组1");
            ps.setObject(5, "test 1");
            ps.executeUpdate();
            
            ps.setObject(1, 3L);
            ps.setObject(2, 1L);
            ps.setObject(3, 2);
            ps.setObject(4, "子组2");
            ps.setObject(5, "test 2");
            ps.executeUpdate();
            
            ps = conn.prepareStatement("insert into xxx_user values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setObject(1, 1L);
            ps.setObject(2, 1L);
            ps.setObject(3, 1);
            ps.setObject(4, new Timestamp(new Date().getTime()));
            ps.setObject(5, "BL00618-001");
            ps.setObject(6, "JonKing-001");
            ps.setObject(7, "怒放的生命");
            ps.setObject(8, "123456");
            ps.setObject(9, "jinpujun@gmail.com");
            ps.setObject(10, 0);
            ps.executeUpdate();
            
            ps.setObject(1, 2L);
            ps.setObject(2, 3L);
            ps.setObject(3, 1);
            ps.setObject(4, new Timestamp(new Date().getTime()));
            ps.setObject(5, "BL00619");
            ps.setObject(6, "Waitwint");
            ps.setObject(7, "过河卒子");
            ps.setObject(8, "123456");
            ps.setObject(9, "waitwind@gmail.com");
            ps.setObject(10, 0);
            ps.executeUpdate();
            
        } catch (Exception e) {
            logger.error(e);
        }  
    }
 
	@Test
	public void testSyncDB() {
		Long mainGroupId = mainGroup.getId();
		String fromGroupId = mainGroup.getFromGroupId();
		Map<String, Object> datasMap = syncService.getCompleteSyncGroupData(mainGroupId, applicationId , fromGroupId);
		List<?> groups = (List<?>)datasMap.get("groups");
        List<?> users  = (List<?>)datasMap.get("users");
        int totalCount = users.size() + groups.size();
	    
        Assert.assertTrue( totalCount == 5); // 三个组 + 两个用户
		Progress progress = new Progress(totalCount);
		((Progressable)syncService).execute(datasMap, progress );
		
		List<User> userList = groupService.getUsersByGroupId(mainGroupId); 
		Assert.assertTrue(userList.size() >= 1);
		User user1 = userList.get(0);
		Assert.assertEquals("JonKing-001", user1.getLoginName());
		Assert.assertEquals("怒放的生命", user1.getUserName());
		Assert.assertEquals("BL00618-001", user1.getEmployeeNo());
		Assert.assertEquals("1", user1.getSex());
		Assert.assertEquals("jinpujun@gmail.com", user1.getEmail());
		
		// 再增加同步一次，相同账号用户更更新。及重名组已经存在的情况（新建的子组，非同步过来）
		groups = permissionHelper.getEntities("from Group g where g.groupType=1 order by  g.decode");
		Group tg = (Group) groups.get(groups.size() - 1);
		tg.setFromApp(null);
		tg.setFromGroupId(null);
		groupService.editExistGroup(tg, "-1", "-1");
		
		((Progressable)syncService).execute(datasMap, progress );
		userList = groupService.getUsersByGroupId(mainGroupId); 
		Assert.assertTrue(userList.size() >= 1);
		
		// testSyncUserJob
		SyncUserJob job = new SyncUserJob();
        String jobConfig = mainGroupId + ",tss";
		job.excuteJob(jobConfig);
	}

	@Test
	public void syncData() {
		String fromGroupId = mainGroup.getFromGroupId();
		mainGroup.setFromGroupId(null);
		try {
			groupAction.syncData(response, applicationId, mainGroup.getId());
			Assert.fail("should throw exception but didn't.");
		} catch (Exception e) {
			Assert.assertTrue("导入组的对应外部应用组的ID（fromGroupId）为空", true);
		}
		
		//TODO 进度条需要单独起线程，里面没有事务。
		try {
			mainGroup.setFromGroupId(fromGroupId);
			groupAction.syncData(response, applicationId, mainGroup.getId());
		} catch (Exception e) {
			Assert.assertTrue("进度条需要单独起线程，里面没有事务.no transaction is in progress", true);
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
	}
}
