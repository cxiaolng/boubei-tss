package com.boubei.tss.um.module;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.um.TxSupportTest4UM;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.action.RoleAction;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.helper.dto.GroupDTO;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.um.service.IRoleService;
import com.boubei.tss.um.service.IUserService;

public class LoginServiceTest extends TxSupportTest4UM {
	
	@Autowired RoleAction roleAction;
    @Autowired IRoleService roleService;
    @Autowired IUserService userService;
    
	Group mainGroup;
    User mainUser;
    Long role1Id;
    
    @Before
    public void setUp() {
    	Global.setContext(super.applicationContext);
        
    	request = new MockHttpServletRequest();
		Context.setResponse(response = new MockHttpServletResponse());
        
        // 初始化虚拟登录用户信息
        login(UMConstants.ADMIN_USER_ID, UMConstants.ADMIN_USER_NAME);
    
        init();
        
        Calendar calendar = new GregorianCalendar();
        calendar.add(UMConstants.ROLE_LIFE_TYPE, UMConstants.ROLE_LIFE_TIME);
        
        // 新建一个用户组
        mainGroup = new Group();
        mainGroup.setParentId(UMConstants.MAIN_GROUP_ID);
        mainGroup.setName("财务部");
        mainGroup.setGroupType( Group.MAIN_GROUP_TYPE );
        groupService.createNewGroup(mainGroup , "", "");
        log.debug(mainGroup);
        
        // 新建一个用户组子组
        Group childGroup = new Group();
        childGroup.setParentId(mainGroup.getId());
        childGroup.setName("财务一部");
        childGroup.setGroupType( mainGroup.getGroupType() );
        groupService.createNewGroup(childGroup , "", "");
        log.debug(childGroup);
        
        // 新增用户
        mainUser = new User();
        mainUser.setLoginName("JonKing");
        mainUser.setUserName("JK");
        mainUser.setPassword("123456");
        mainUser.setGroupId(mainGroup.getId());
        userService.createOrUpdateUser(mainUser , "" + mainGroup.getId(), "");
        log.debug(mainUser);
 
        // 新建角色
        Role role1 = new Role();
        role1.setIsGroup(0);
        role1.setName("办公室助理");
        role1.setParentId(0L);
        role1.setStartDate(new Date());
        role1.setEndDate(calendar.getTime());
        request.addParameter("Role2UserIds", UMConstants.ADMIN_USER_ID + "," + mainUser.getId());
        request.addParameter("Role2GroupIds", "" + mainGroup.getId());
        roleAction.saveRole(response, request, role1);
        role1Id = role1.getId();
 
    }
    
	@Test
	public void test() {
		Long userId = mainUser.getId();
		Long groupId = mainGroup.getId();
		
		List<Long> roleIds = loginSerivce.getRoleIdsByUserId(userId);
		Assert.assertTrue(roleIds.size() == 1); // role1
		Assert.assertTrue(1 ==  roleIds.get(0));
		
		Object[] rootGroup = loginSerivce.getRootGroupByUserId(userId);
		List<Object[]> fatherGroups = loginSerivce.getGroupsByUserId(userId);
		Assert.assertEquals(fatherGroups.get(0)[1], rootGroup[1]);
		
		List<GroupDTO> groups = loginSerivce.getGroupTreeByGroupId(groupId);
		Assert.assertTrue(groups.size() == 2);
		
		GroupDTO gDTO = groups.get(0);
		gDTO.setDisabled(0);
		
		List<OperatorDTO> users1 = loginSerivce.getUsersByGroupId(groupId);
		List<OperatorDTO> users2 = loginSerivce.getUsersByRoleId(role1Id);
		Assert.assertTrue(users1.size() == 1); // JK
		Assert.assertTrue(users2.size() == 2); // Admin, JK
		Assert.assertEquals("JK", users1.get(0).getAttributesMap().get("userName"));
		
		loginSerivce.resetPassword(userId, "abc123456=11");
		
		String[] emails = loginSerivce.getContactInfos("-1@tssRole,-2@tssGroup", false);
		Assert.assertEquals(1, emails.length);
	}
}
