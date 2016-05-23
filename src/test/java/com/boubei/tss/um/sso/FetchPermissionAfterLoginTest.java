package com.boubei.tss.um.sso;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.sso.SSOConstants;
import com.boubei.tss.um.TxSupportTest4UM;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.service.IUserService;

public class FetchPermissionAfterLoginTest extends TxSupportTest4UM {
	
	@Autowired protected IUserService userService;

	@Test
	public void testExcute() {
		HttpSession session = request.getSession();

		session.setAttribute("LOGIN_MSG", "Login TEST");

		new FetchPermissionAfterLogin().execute();

		List<?> roleIds = (List<?>) session.getAttribute(SSOConstants.USER_RIGHTS_IN_SESSION);
		assertTrue(roleIds.size() > 0);

		Object loginName = session.getAttribute(SSOConstants.LOGINNAME_IN_SESSION);
		assertNotNull(loginName);
		log.debug(loginName);
		
		//-------------------------------------------- 测试新建用户的登陆 -------------------------------------
		// 新建一个用户组
        Group mainGroup = new Group();
        mainGroup.setParentId(UMConstants.MAIN_GROUP_ID);
        mainGroup.setName("R_财务部");
        mainGroup.setGroupType( Group.MAIN_GROUP_TYPE );
        groupService.createNewGroup(mainGroup , "", "");
        log.debug(mainGroup);
        Long mainGroupId = mainGroup.getId();
        
        // 管理员直接在主组下新增用户
        User mainUser = new User();
        mainUser.setLoginName("R_JonKing");
        mainUser.setUserName("R_JK");
        mainUser.setPassword("123456");
        mainUser.setGroupId(mainGroupId);
        userService.createOrUpdateUser(mainUser , "" + mainGroupId, "");
        log.debug(mainUser);
        
        login(mainUser.getId(), mainUser.getLoginName()); // 更好登录用户，看其权限
        
        new FetchPermissionAfterLogin().execute();

		int groupLevel = (Integer) session.getAttribute("GROUP_LEVEL");
		assertTrue( groupLevel == 1 );
		
		String lastGroupName =  (String) session.getAttribute("GROUP_LAST_NAME");
		assertTrue( mainGroup.getName().equals(lastGroupName) );
	}

}
