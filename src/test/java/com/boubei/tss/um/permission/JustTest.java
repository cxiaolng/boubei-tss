package com.boubei.tss.um.permission;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.permission.GroupPermission;
import com.boubei.tss.um.entity.permission.GroupResource;
import com.boubei.tss.um.entity.permission.RoleResource;
import com.boubei.tss.um.entity.permission.RoleUserMapping;
import com.boubei.tss.um.entity.permission.RoleUserMappingId;
import com.boubei.tss.um.entity.permission.ViewRoleUser;
import com.boubei.tss.um.entity.permission.ViewRoleUser4SubAuthorize;
import com.boubei.tss.um.entity.permission.ViewRoleUserId;
import com.boubei.tss.um.sso.UMIdentityGetter;

public class JustTest {
	
	@Test
	public void test0() {
		GroupResource g1 = new GroupResource();
		g1.setDecode("00001");
		g1.setId(1L);
		g1.setName("Root");
		g1.setParentId(0L);
		
		GroupPermission p1 = new GroupPermission();
		p1.setId(1L);
	}

	@Test
	public void test1() {
		RoleUserMapping rum1 = new RoleUserMapping();
		
		RoleUserMappingId id1 = new RoleUserMappingId();
		id1.setRoleId(2L);
		id1.setUserId(2L);
		rum1.setId(id1);
		
		RoleUserMapping rum2 = new RoleUserMapping();
		
		RoleUserMappingId id2 = new RoleUserMappingId();
		id2.setRoleId(2L);
		id2.setUserId(2L);
		rum2.setId(id2);
		
		Set<RoleUserMappingId> set = new HashSet<RoleUserMappingId>();
		set.add(id1);
		set.add(id2);
		
		Assert.assertEquals(rum1.toString(), rum1.toString());
		
		Assert.assertEquals(rum1.getId().hashCode(), rum2.getId().hashCode());
	}

	@Test
	public void test2() {
		ViewRoleUser ru1 = new ViewRoleUser();
		
		ViewRoleUserId id1 = new ViewRoleUserId();
		id1.setRoleId(2L);
		id1.setUserId(2L);
		ru1.setId(id1);
		
		ViewRoleUser ru2 = new ViewRoleUser();
		
		ViewRoleUserId id2 = new ViewRoleUserId();
		id2.setRoleId(2L);
		id2.setUserId(2L);
		ru2.setId(id2);
		
		Set<ViewRoleUserId> set = new HashSet<ViewRoleUserId>();
		set.add(id1);
		set.add(id2);
		
		Assert.assertEquals(ru1.toString(), ru1.toString());
		
		Assert.assertEquals(ru1.getId().hashCode(), ru2.getId().hashCode());
	}
	
	@Test
	public void test3() {
		ViewRoleUser4SubAuthorize t = new ViewRoleUser4SubAuthorize();
		t.setId(null);
		t.getId();
		
		Assert.assertEquals(UMConstants.ROLE_RESOURCE_TYPE_ID, new RoleResource().getResourceType());
		
		PermissionDTO pt = new PermissionDTO(new Object[6]);
		pt.getRoleId();
		pt.getPermissionState();
		
		new UMIdentityGetter().indentify(null, "123456");
	}
}
