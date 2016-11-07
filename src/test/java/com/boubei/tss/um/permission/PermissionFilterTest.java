package com.boubei.tss.um.permission;

import org.junit.Test;

import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.filter.PermissionFilter4Branch;
import com.boubei.tss.um.permission.filter.PermissionFilter4Check;

public class PermissionFilterTest {
	
	@Test
	public void test() {
		PermissionHelper helper = new PermissionHelper();
		
		PermissionFilter4Check f1 = new PermissionFilter4Check();
		f1.doFilter(new Object[]{}, null, null, helper);
		f1.doFilter(new Object[]{null}, null, null, helper);
		
		PermissionFilter4Branch f2 = new PermissionFilter4Branch();
		f2.doFilter(new Object[]{null}, null, null, helper);
		f2.doFilter(new Object[]{12L}, null, null, helper);
	}

}
