package com.boubei.tss.um.permission.filter;

import java.util.List;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 删除时候检查用户是否对删除资源有删除权限。
 */
public class PermissionFilter4Check implements IPermissionFilter {
	
	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
	    if(args.length < 1 ) return;
	    
	    Long resourceId = null;
 
	    //  eg: Structure getStructure(Long id)
	    if( args[0] instanceof Long ) {
	        resourceId = (Long) args[0];
	    }
	    
	    if(resourceId == null) return;
        
        List<Long> permitedResourceIds = helper.getResourceIdsByOperation(tag.application(), tag.resourceType(), tag.operation());
 
        if(!permitedResourceIds.contains(resourceId)) {
            throw new BusinessException("检查资源权限失败，您对ID为:" + resourceId + "目标资源没有当前操作所需的权限！");
        }
	}
}
