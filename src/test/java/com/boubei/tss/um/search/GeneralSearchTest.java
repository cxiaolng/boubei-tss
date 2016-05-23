package com.boubei.tss.um.search;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.um.TxSupportTest4UM;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.action.RoleAction;
import com.boubei.tss.um.action.SubAuthorizeAction;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.entity.SubAuthorize;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;

/**
 * 授权信息相关搜索测试
 */
public class GeneralSearchTest extends TxSupportTest4UM {
	
    @Autowired GeneralSearchAction action;
    
    @Autowired RoleAction roleAction;
    @Autowired SubAuthorizeAction strategyAction;
    @Autowired IUserService userService;
 
    @Test
    public void testGeneralSearch() {
    	// 新建一个用户组
        Group mainGroup = new Group();
        mainGroup.setParentId(UMConstants.MAIN_GROUP_ID);
        mainGroup.setName("G_财务部");
        mainGroup.setGroupType( Group.MAIN_GROUP_TYPE );
        groupService.createNewGroup(mainGroup , "", "");
        log.debug(mainGroup);
        Long groupId = mainGroup.getId();
        
        // 管理员直接在主组下新增用户
        User user = new User();
        user.setLoginName("G_JonKing");
        user.setUserName("G_JK");
        user.setPassword("123456");
		user.setGroupId(groupId);
        userService.createOrUpdateUser( user , "" + groupId, "");
        log.debug(user);
 
        // 新建角色
        Role role = new Role();
        role.setIsGroup(0);
        role.setName("G_办公室助理");
        role.setParentId(UMConstants.ROLE_ROOT_ID);
        role.setStartDate(new Date());
        Calendar calendar = new GregorianCalendar();
        calendar.add(UMConstants.ROLE_LIFE_TYPE, UMConstants.ROLE_LIFE_TIME);
        role.setEndDate(calendar.getTime());
        
        request.addParameter("Role2UserIds", UMConstants.ADMIN_USER_ID + "," + user.getId());
        request.addParameter("Role2GroupIds", "" + groupId);
        roleAction.saveRole(response, request, role);
        Long roleId = role.getId();
         
        // 新建转授策略
        SubAuthorize strategy = new SubAuthorize();
        strategy.setStartDate(new  Date());
        calendar = new GregorianCalendar();
        calendar.add(UMConstants.STRATEGY_LIFE_TYPE, UMConstants.STRATEGY_LIFE_TIME);
        strategy.setEndDate(calendar.getTime());
        strategy.setName("G_转授策略一");
        
        request.addParameter("Rule2UserIds", user.getId() + "");
        request.addParameter("Rule2GroupIds", groupId + "");
        request.addParameter("Rule2RoleIds", roleId + "");
        strategyAction.saveSubauth(response, request, strategy);
        
    	action.searchUserSubauth(response, groupId);
    	
    	action.searchRolesByGroup(response, roleId);
    	
    	action.searchUsersByRole(response, roleId);
    	
    	List<?> result = action.getVisiableRoles();
    	Assert.assertTrue(result.size() > 0);
    	
    }
}
