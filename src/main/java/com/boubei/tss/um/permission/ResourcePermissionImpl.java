package com.boubei.tss.um.permission;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.TreeSupportDao;
import com.boubei.tss.framework.persistence.entityaop.IDecodable;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.BeanUtil;

/**
 * <p>
 * 操作资源表时相关的补全操作和删除操作
 * </p>
 */
@Component("ResourcePermission")
public class ResourcePermissionImpl extends TreeSupportDao<IDecodable> implements ResourcePermission {
	
    public ResourcePermissionImpl() {
        super(IDecodable.class);
    }

    @Autowired private RemoteResourceTypeDao resourceTypeDao;
 
    public void addResource(Long resourceId, String resourceTypeId) {
        flush();
        String applicationID = PermissionHelper.getApplicationID();
		String permissionTable = resourceTypeDao.getPermissionTable(applicationID, resourceTypeId);
        String resourceTable = resourceTypeDao.getResourceTable(applicationID, resourceTypeId);

        IResource resource = (IResource) getEntity(BeanUtil.createClassByName(resourceTable), resourceId);
        if(resource == null) {
            throw new BusinessException("权限补齐时找不到资源对象，请检查资源视图（" + resourceTable + "）中是否包含该对象？是否过滤掉了？");
        }
        
        String hql = "from " + permissionTable + " t where t.resourceId = ? and t.permissionState = ?";
		List<?> parentPermissions = getEntities(hql, resource.getParentId(), UMConstants.PERMIT_SUB_TREE); 
        for (Object temp : parentPermissions) {
        	AbstractPermission parentPermission = (AbstractPermission) temp;
            PermissionHelper.getInstance().createPermission(parentPermission, resource, permissionTable);
        }
    }

	public void delResource(Long resourceId, String resourceTypeId){	
        flush();
        
        String applicationID = PermissionHelper.getApplicationID();
		String permissionTable  = resourceTypeDao.getPermissionTable(applicationID, resourceTypeId);
        executeHQL("delete " + permissionTable + " t where t.resourceId = ?", resourceId);    // 删除该资源的所有授权信息
	}
 
	public void moveResource(Long resourceId, String resourceTypeId){
		String applicationID = PermissionHelper.getApplicationID();
		String permissionTable = resourceTypeDao.getPermissionTable(applicationID, resourceTypeId);
		String resourceTable = resourceTypeDao.getResourceTable(applicationID, resourceTypeId);
		
		List<?> subTree = getChildrenById(resourceTable, resourceId); // 连同自身节点
		
		IResource resource = (IResource) getEntity(BeanUtil.createClassByName(resourceTable), resourceId);
		String hql = "from " + permissionTable + " t where t.resourceId = ?";
		List<?> parentPermissions = getEntities(hql, resource.getParentId()); 
		
		String deleteHQL = "delete " + permissionTable + " t where t.resourceId = ? and t.roleId = ? " +
				" and t.operationId = ? and t.isGrant = ? and t.isPass = ?";
		
		for (Object temp : parentPermissions) {
        	AbstractPermission parentPermission = (AbstractPermission) temp;
        	Integer parentPermissionState = parentPermission.getPermissionState();
        	
        	// 判断移动后的父节点对各个操作权限的授权是否为全勾；如果为全勾，则移动过去整枝节点的授权状态都为全勾。
        	if(parentPermissionState.equals(UMConstants.PERMIT_SUB_TREE)) {
        		for (Object node : subTree) {
        			IResource child = (IResource) node;
        			
        			// 先删除已经存在的资源操作的权限
					executeHQL(deleteHQL, child.getId(), parentPermission.getRoleId(), 
							parentPermission.getOperationId(), parentPermission.getIsGrant(), parentPermission.getIsPass());
        		
        			// 重新按（祖）父节点的权限（全勾）进行授权
        			PermissionHelper.getInstance().createPermission(parentPermission, child, permissionTable);			
        		}
        	}
        	// 如果父节点是半勾，则移动过来的整枝权限不变
		}
		// TODO 子节点有权限的操作项而继父节点没有的，暂不予处理。如果要保持界面上统一（子节点有勾选的，父节点也必然被勾选）的话，需要考虑删除子节点的这类权限
	}

	public List<?> getParentResourceIds(String applicationId, String resourceTypeId, Long resourceId, 
            String operationId, Long operatorId){	
	    
        String resourceTable = resourceTypeDao.getResourceTable(PermissionHelper.getApplicationID(), resourceTypeId);
        Class<?> resourceClazz = BeanUtil.createClassByName(resourceTable);
        IResource resource = (IResource) getEntity(resourceClazz, resourceId);
        
        String permissionTable = resourceTypeDao.getPermissionTable(applicationId, resourceTypeId);
        String hql = "select distinct t.id from " + resourceClazz.getName() + " t, RoleUserMapping r, " + permissionTable + " v" +
            " where t.id = v.resourceId and v.roleId = r.id.roleId and r.id.userId = ? and v.operationId = ? and ? like t.decode || '%'";
        
        return getEntities(hql, operatorId, operationId, resource.getDecode());
	}

	public List<?> getSubResourceIds(String applicationId, String resourceTypeId, Long resourceId, 
            String operationId, Long operatorId){	
	    
	    String resourceTable = resourceTypeDao.getResourceTable(applicationId, resourceTypeId);
	    Class<?> resourceClazz = BeanUtil.createClassByName(resourceTable);
	    IResource resource = (IResource) getEntity(resourceClazz, resourceId);
        
        String permissionTable = resourceTypeDao.getPermissionTable(applicationId, resourceTypeId);
        String hql = "select distinct t.id from " + resourceTable + " t, RoleUserMapping r, " + permissionTable + " v" +
            " where t.id = v.resourceId and v.roleId = r.id.roleId and r.id.userId = ? and v.operationId = ? and t.decode like ?";
        
        return getEntities(hql, operatorId, operationId, resource.getDecode() + "%");	
	}
}