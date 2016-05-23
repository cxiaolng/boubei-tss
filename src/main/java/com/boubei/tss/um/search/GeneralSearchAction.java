package com.boubei.tss.um.search;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.web.dispaly.grid.GridDataEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Role;
import com.boubei.tss.um.service.IRoleService;

@Controller
@RequestMapping( {"/auth/search", "/auth/service"} )
public class GeneralSearchAction extends BaseActionSupport {
	
	@Autowired private GeneralSearchService service;
	@Autowired private IRoleService roleService;
 
	/**
	 * 一个组下面所有用户的因转授而获得的角色的情况
	 */
	@RequestMapping("/subauth/{groupId}")
	public void searchUserSubauth(HttpServletResponse response, @PathVariable("groupId") Long groupId) {
		List<?> list = service.searchUserSubauthByGroupId(groupId);
		GridDataEncoder gridEncoder = new GridDataEncoder(list, UMConstants.GENERAL_SEARCH_STRATEGY_GRID);
				
        print("SUBAUTH_RESULT", gridEncoder);
	}
	
	/**
	 * 根据用户组查询组下用户（需是登陆用户可见的用户）的角色授予情况
	 */
	@RequestMapping("/roles/{groupId}")
	public void searchRolesByGroup(HttpServletResponse response, @PathVariable("groupId") Long groupId) {
		List<?> list = service.searchUserRolesMapping(groupId);
		GridDataEncoder gridEncoder = new GridDataEncoder(list, UMConstants.GENERAL_SEARCH_ROLE_GRID);

		print("ROLE_RESULT", gridEncoder);
	}
	
	/**
	 * 拥有同一个角色的所有用户列表
	 */
	@RequestMapping("/role/users/{roleId}")
	public void searchUsersByRole(HttpServletResponse response, @PathVariable("roleId") Long roleId) {
		List<?> list = service.searchUsersByRole(roleId);
		GridDataEncoder gridEncoder = new GridDataEncoder(list, UMConstants.GENERAL_SEARCH_USER_GRID);

        print("ROlE_USERS_RESULT", gridEncoder);
	}
	
	/**
	 * 或许当前用户有查看权限的角色，用于前台生成角色下拉列表
	 */
	@RequestMapping(value = "/roles", method = RequestMethod.GET)
	@ResponseBody
	public List<?> getVisiableRoles() {
		List<?> list = roleService.getAllVisiableRole();
		List<Object[]> returnList = new ArrayList<Object[]>();
		for(Object temp : list) {
			Role role = (Role) temp;
			if( ParamConstants.FALSE.equals(role.getIsGroup()) 
					&& ParamConstants.FALSE.equals(role.getDisabled()) ) {
				returnList.add(new Object[]{ role.getId(), role.getName() });
			}
		}
		return returnList;
	}
}