package com.boubei.tss.cms.helper;

import java.util.Iterator;
import java.util.List;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.permission.filter.IPermissionFilter;
import com.boubei.tss.um.permission.filter.PermissionTag;

/**
 * 某一树枝类型（包括所有子节点 或 包括所有父节点）的过滤拦截器。
 * 如果发现对该树枝某一节点没有权限，则从返回列表里移除。
 */
public class PermissionFilter4ChannelBranch implements IPermissionFilter {

    public void doFilter(Object args[], Object returnValue, PermissionTag tag, PermissionHelper helper) {

	    if(args.length < 2 || !(args[1] instanceof String)) return;
	    
	    List<?> resources = (List<?>) returnValue;
	    
	    String resourceType = CMSConstants.RESOURCE_TYPE_CHANNEL;
	    String operation = (String) args[1];
	    
        List<Long> permitedResourceIds = helper.getResourceIdsByOperation(tag.application(), resourceType, operation);
 
        for( Iterator<?> it = resources.iterator(); it.hasNext(); ) {
            Channel resource = (Channel)it.next();
            if( !permitedResourceIds.contains(resource.getId())) {
                it.remove(); // 没被授权的，移除掉
            }
        }
	}

}
