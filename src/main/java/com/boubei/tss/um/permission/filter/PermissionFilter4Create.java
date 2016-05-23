package com.boubei.tss.um.permission.filter;

import java.util.List;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.permission.IResource;
import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 资源新增时候的权限检查： 判断用户在父节点下是否有新增权限。
 * 
 * IResource create***( IResource obj )
 */
public class PermissionFilter4Create implements IPermissionFilter {
	
	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
        String application = tag.application();
		String resourceType = tag.resourceType();
		String createOperation = tag.operation();
 
		IResource resource = (IResource) args[0];
		
		List<Long> permitedResourceIds = helper.getResourceIdsByOperation(application, resourceType, createOperation);
        if( !permitedResourceIds.contains(resource.getParentId()) ) {
            throw new BusinessException("新增失败，您对【ID为:" + resource.getParentId() + " 的节点】下没有新增资源的权限！");
        }
	}

}
