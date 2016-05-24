package com.boubei.tss.um.module;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss._TestUtil;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.um.AbstractTest4UM;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.action.UserAction;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.helper.UMQueryCondition;
import com.boubei.tss.um.service.IResourceService;
import com.boubei.tss.um.service.IUserService;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.DateUtil;

/**
 * 用户相关模块的单元测试
 */
public class UserModuleTest extends AbstractTest4UM {
    
	@Autowired UserAction action;
    
    @Autowired IUserService service;
    @Autowired IResourceService appService;
    
    Group mainGroup;
    Long  mainGroupId;
    
    Group assitantGroup;
    User user1;
    
    public void init() {
    	super.init();
        
    	// 检查初始化的组是否存在
    	List<?> groups = groupService.findGroups();
    	for(Object temp : groups) {
            log.debug(temp);
        }
        log.debug("\n");
    	
        mainGroup = new Group();
        mainGroup.setParentId(UMConstants.MAIN_GROUP_ID);
        mainGroup.setName("主用户组一");
        mainGroup.setGroupType( Group.MAIN_GROUP_TYPE );
        groupService.createNewGroup(mainGroup , "", "-1");
        log.debug(mainGroup + "\n");
        
        mainGroupId = mainGroup.getId();
        
        // 管理员直接在主组下新增用户
        user1 = new User();
        user1.setLoginName("U_JonKing");
        user1.setUserName("U_JK");
        user1.setPassword("123456");
		user1.setGroupId(mainGroupId);
		user1.setAddress("ZheJiang HZ");
		user1.setCertificate("身份证");
		user1.setCertificateNo("332624******");
		user1.setPostalCode("210000");
		user1.setTelephone("88819585");
        service.createOrUpdateUser(user1, "" + mainGroupId, "-1");
        log.debug(user1);
        
        List<User> users = groupService.getUsersByGroupId(mainGroupId);
        assertEquals(1, users.size());
        log.debug(users.get(0) + "\n");
        
        // 建一个辅助用户组
        assitantGroup = new Group();
        assitantGroup.setParentId(UMConstants.ASSISTANT_GROUP_ID);
        assitantGroup.setName("第一纵队");
        assitantGroup.setGroupType( Group.ASSISTANT_GROUP_TYPE );
        groupService.createNewGroup(assitantGroup , user1.getId() + "", "-1");
        log.debug(assitantGroup + "\n");
    }
    
    @After
    public void tearDown() throws Exception {
    	_TestUtil.printLogs(logService);
    	super.tearDown();
    }
    
    @Test
    public void testOverdue() {
    	service.overdue();
    }
    
    @Test
    public void getUsersByGroup() {
    	action.getUsersByGroupId(response, mainGroupId, 1);
    	
    	List<User> mainUsers  = groupService.getUsersByGroupId(mainGroupId);
        assertEquals(1, mainUsers.size());
       
		action.searchUser(response, 1, mainGroupId, "U_JK");
		
		PageInfo pageInfo = service.searchUser(mainGroupId, "U_JK", 1);
		Assert.assertTrue(pageInfo.getItems().size() > 0);
		
		pageInfo = service.searchUser(mainGroupId, "金", 1);
		Assert.assertTrue(pageInfo.getItems().size() == 0);
    }
    
    @Test
    public void getUMQueryCondition() {
		UMQueryCondition userQueryCon = new UMQueryCondition();
        userQueryCon.setGroupId(mainGroupId);
        userQueryCon.getPage().setPageNum(1);
		userQueryCon.setBirthday(new Date());
		userQueryCon.setCertificateNo("332624");
		userQueryCon.setEmployeeNo("");
		userQueryCon.setGroupIds(Arrays.asList(mainGroupId));
		userQueryCon.setLoginName("U_JonKing");
		userQueryCon.setUserName("U_JK");
		
		userQueryCon.toConditionString();
    }
    
    @Test
    public void testUserCRUD() {
    	// 删除用户和辅助用户组的联系
    	action.deleteUser(response, assitantGroup.getId(), user1.getId());
    	
    	// 删除用户
    	action.deleteUser(response, mainGroupId, user1.getId());
    	
    	// 删除系统管理员
    	try {
    		action.deleteUser(response, UMConstants.MAIN_GROUP_ID, UMConstants.ADMIN_USER_ID);
    		Assert.fail("should throw exception but didn't.");
        } catch (Exception e) {
        	Assert.assertTrue("当前用户正在使用中，无法自我删除！", true);
        }
		
		// 新增用户
		user1.setId(null);
		request.addParameter("User2GroupExistTree", "" + mainGroupId);
    	request.addParameter("User2RoleExistTree", "-1");
		action.saveUser(response, request, user1);
		log.debug(user1);
        
        // 注册一个用户
        User user2 = new User();
        user2.setLoginName("U_JonKing-R");
        user2.setUserName("U_JK-R");
        user2.setPassword("123456");
        action.registerUser(response, user2);
        log.debug(user2);
        
        // 修改用户
        user2.setUserName("JK-2");
        action.modifyUserSelf(response, user2);
        
        // 注册一个同名用户
        User user3 = new User();
        BeanUtil.copy(user3, user2);
        user3.setId(null);
    	try {
    		action.registerUser(response, user3);
        	Assert.fail("该抛异常而没有抛！");
        } catch (Exception e) {
        	Assert.assertTrue("相同登陆账号已经存在,请更换账号.", true);
        }
    }
  
    @Test
    public void getUserInfo() {
    	action.getUserInfoAndRelation(response, UMConstants.DEFAULT_NEW_ID, mainGroupId); // 获取新增用户模板
        action.getUserInfoAndRelation(response, user1.getId(), mainGroupId); // 获取编辑用户模板
        
        action.getOnlineUserInfo(response);
        
        action.getUserInfo(response);
        
        action.getRegisterForm(response);
        
        action.getForgetPasswordInfo(response);
        
        action.getOperatorInfo(response);
        
        action.getUserHas();
    }
    
    @Test
    public void startOrStopUser() {
    	action.startOrStopUser(response, mainGroupId, user1.getId(), ParamConstants.TRUE);
        action.startOrStopUser(response, mainGroupId, user1.getId(), ParamConstants.FALSE);
        
        action.startOrStopUser(response, mainGroupId, user1.getId(), ParamConstants.TRUE);
        
        Long assistantGroupId = assitantGroup.getId();
		groupService.startOrStopGroup(assistantGroupId, ParamConstants.TRUE);
		groupService.startOrStopGroup(mainGroupId, ParamConstants.TRUE);
		
        action.startOrStopUser(response, assistantGroupId, user1.getId(), ParamConstants.FALSE);
        
        user1 = service.getUserById(user1.getId());
        user1.setAccountLife(DateUtil.parse("2012-12-12"));
        service.createOrUpdateUser(user1,  "" + mainGroupId, "-1");
        try {
        	action.startOrStopUser(response, assistantGroupId, user1.getId(), ParamConstants.FALSE);
        	Assert.fail("should throw exception but didn't.");
        } catch (Exception e) {
        	Assert.assertTrue("该用户已经过期，不能启用！", true);
        }
    }
    
    @Test
    public void initUserInfo() {
		 action.initAuthenticateMethod(response, mainGroupId);
		 action.uniteAuthenticateMethod(response, mainGroupId, "com.boubei.tss.um.sso.UMPasswordIdentifier");
		
		 action.initPassword(response, mainGroupId, user1.getId(), "369852");
		 action.initPassword(response, mainGroupId, 0L, "369852");
    }
}
