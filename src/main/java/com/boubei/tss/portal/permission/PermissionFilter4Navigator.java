package com.boubei.tss.portal.permission;

import java.util.Iterator;
import java.util.List;

import com.boubei.tss.portal.PortalConstants;
import com.boubei.tss.portal.entity.Navigator;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.filter.IPermissionFilter;
import com.boubei.tss.um.permission.filter.PermissionTag;

/**
 * 菜单权限过滤拦截器。
 * 分两层过滤： 
 *  1、菜单本身授权过滤 
 *  2、如果菜单里应用到门户实例，则需要根据登陆用户门户实例的授权信息的再对菜单进行一次过滤
 */
public class PermissionFilter4Navigator implements IPermissionFilter {

	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
        List<?> resources = (List<?>) returnValue;
        
        String application = UMConstants.TSS_APPLICATION_ID;
        String navigatorResourceType = PortalConstants.NAVIGATOR_RESOURCE_TYPE;
        String structureResourceType = PortalConstants.PORTAL_RESOURCE_TYPE;
        
        List<Long> menuPermissions = helper.getResourceIdsByOperation(application, navigatorResourceType, PortalConstants.NAVIGATOR_VIEW_OPERRATION);
        List<Long> psPermissions   = helper.getResourceIdsByOperation(application, structureResourceType, PortalConstants.PORTAL_VIEW_OPERRATION);
        
        for(Iterator<?> it = resources.iterator(); it.hasNext();) { 
            Navigator menu = (Navigator) it.next();
            
            // 菜单本身授权过滤。
            if( !menuPermissions.contains(menu.getId()) ){
                it.remove();
                continue;
            }
            
            // 对菜单引用到的portlet实例（门户结构的一种）进行过滤
            if(menu.getType().equals(Navigator.TYPE_MENU)) continue;
            
            if(menu.getContent() == null) continue;
            
            if( psPermissions.contains(menu.getContent().getId()) ) continue;  
            
            it.remove();
        }
	}

}
