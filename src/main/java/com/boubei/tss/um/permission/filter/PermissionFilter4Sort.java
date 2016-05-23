package com.boubei.tss.um.permission.filter;

import java.util.List;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.permission.PermissionHelper;

/**
 * 资源排序时候的权限检查： 判断用户对排序节点的父亲节点是否有排序权限。
 * 
 * void order(Long id, Long targetId, int direction);
 * 
 */
public class PermissionFilter4Sort implements IPermissionFilter {
	
	@Override
	public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {
        String application = tag.application();
		String resourceType = tag.resourceType();
		String operation = tag.operation();
        
		List<Long> permitedResourceIds = helper.getResourceIdsByOperation(application, resourceType, operation);
 
        Long parentResourceId = helper.getParentResourceId(application, resourceType, (Long)args[0]);
        
        if(!permitedResourceIds.contains(parentResourceId)) {
            throw new BusinessException("排序失败，您对进行操作的ID为:" + parentResourceId + " 的对象的子节点没有排序权限！");
        }
	}

}
