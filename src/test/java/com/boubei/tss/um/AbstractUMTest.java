package com.boubei.tss.um;

import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.AbstractTssTest;
import com.boubei.tss.um.permission.ResourcePermission;
import com.boubei.tss.um.service.IGroupService;

public abstract class AbstractUMTest extends AbstractTssTest { 
 
	@Autowired protected ResourcePermission resourcePermission;
	@Autowired protected IGroupService groupService;
 
    /**
     * 初始化UM、CMS、Portal相关应用、资源类型、权限选型信息
     */
    protected void init() {
        super.init();
        
        // 补全SQL初始化出来的系统级用户组
        Long[] groupIds = new Long[] {-1L, -2L, -3L, -7L};
        for(Long groupId : groupIds) {
        	resourcePermission.addResource(groupId, UMConstants.GROUP_RESOURCE_TYPE_ID);
        }
    }

}
