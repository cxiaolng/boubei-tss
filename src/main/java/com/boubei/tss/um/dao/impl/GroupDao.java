package com.boubei.tss.um.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.boubei.tss.framework.persistence.TreeSupportDao;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.persistence.pagequery.PaginationQueryByHQL;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.dao.IGroupDao;
import com.boubei.tss.um.dao.IResourceTypeDao;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.helper.UMQueryCondition;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.EasyUtils;
 
@Repository("GroupDao")
public class GroupDao extends TreeSupportDao<Group> implements IGroupDao {

    public GroupDao() {
		super(Group.class);
	}

    @Autowired private IResourceTypeDao resourceTypeDao;	

    public Group saveGroup(Group group) {
    	if( group.getId() == null ) {
    		create(group);
    	} 
    	else {
    		// 因Decode拦截器里保存了一次了，此时group已经是PO状态，再merge会报乐观锁
    	}
    	
		return group;
	}
 
	public Group removeGroup(Group group) {
        List<?> groups = getChildrenById(group.getId()); //列表中包含了Group本身
        List<?> users = getUsersByGroupIdDeeply(group.getId());
        
        for(Iterator<?> it = users.iterator(); it.hasNext();){
            User user = (User) it.next();
            deleteAll(getEntities("from GroupUser gu where gu.userId = ?", new Object[]{user.getId()}));
            deleteAll(getEntities("from RoleUser ru where ru.id.userId = ? and ru.strategyId is null", new Object[]{user.getId()}));
        }
        
        for(Iterator<?> it = groups.iterator(); it.hasNext();){
            Group temp = (Group) it.next();
            deleteAll(findGroup2UserByGroupId(temp.getId()));
            deleteAll(findGroup2RoleByGroupId(temp.getId()));
        }
        
        deleteAll(users);
        deleteAll(groups);
        return group;
	}
    
    public Group removeAssistmentGroup(Group group) {
        deleteAll(getChildrenById(group.getId()));
        return group;
    }

	public List<?> findRolesByGroupId(Long groupId) {
		String hql = "select distinct r from RoleGroup rg, Role r where rg.roleId = r.id and rg.groupId = ? and rg.strategyId is null ";
		return getEntities(hql, groupId);
	}

	public List<?> findGroup2UserByGroupId(Long groupId) {
        return getEntities("from GroupUser gu where gu.groupId = ?", groupId);
	}

	public List<?> findGroup2RoleByGroupId(Long groupId) {
        return getEntities("from RoleGroup rg where rg.groupId = ? and rg.strategyId is null ", groupId);
	}
 
	public Group findMainGroupByUserId(Long userId){
        //如此取出来的组唯一，即用户所在的主用户组
		String hql = "select g from GroupUser gu, Group g where gu.groupId = g.id and gu.userId = ? and g.groupType = ? ";
		List<?> list = getEntities(hql, userId, Group.MAIN_GROUP_TYPE);
        return list.size() > 0 ? (Group)list.get(0) : null;
	}
    
    public List<?> findGroupsByUserId(Long userId) {
        String hql = "select distinct g from GroupUser gu, Group g where gu.groupId = g.id and gu.userId = ? ";
        return getEntities(hql, userId);
    }
    
    public List<?> getFatherGroupsByUserId(Long userId){
        Group mainGroup = findMainGroupByUserId(userId);
        List<Group> parents = getParentsById(mainGroup.getId(), UMConstants.MAIN_GROUP_ID);
		return parents.subList(1, parents.size()); // 去掉"主用户组"
    }
 
	public List<?> getVisibleSubGroups(Long groupId){
		Group group = getEntity(groupId);
        String permissionTable = resourceTypeDao.getPermissionTable(UMConstants.TSS_APPLICATION_ID, group.getResourceType());
        
        String hql = PermissionHelper.permissionHQL(entityName, permissionTable, " and o.decode like ? ", true);
        return getEntities(hql, Environment.getUserId(), UMConstants.GROUP_VIEW_OPERRATION, group.getDecode() + "%" );
	}
 
	public List<?> getMainAndAssistantGroups(Long operatorId) {
		List<Group> groups = new ArrayList<Group>(); 
	    groups.addAll(getGroupsByType(operatorId, UMConstants.GROUP_VIEW_OPERRATION, Group.MAIN_GROUP_TYPE));
	    groups.addAll(getGroupsByType(operatorId, UMConstants.GROUP_VIEW_OPERRATION, Group.ASSISTANT_GROUP_TYPE));
		return groups;
	}
	
	public List<Group> getGroupsByType(Long operatorId, String operationId, Integer groupType) {
        String permissionTable = resourceTypeDao.getPermissionTable(UMConstants.TSS_APPLICATION_ID, UMConstants.GROUP_RESOURCE_TYPE_ID);
        
        String hql = PermissionHelper.permissionHQL(entityName, permissionTable);
        List<?> allGroups = getEntities(hql, operatorId, operationId);
        
        List<Group> resultList = new ArrayList<Group>();
        for( Object temp : allGroups ){
            Group group = (Group) temp;
            if( groupType.equals(group.getGroupType()) ) {
            	resultList.add(group);
            }
        }
        return resultList;
	}
	
	public List<?> getParentGroupByGroupIds(List<Long> groupIds, Long operatorId, String operationId) {
        if( EasyUtils.isNullOrEmpty(groupIds)) return new ArrayList<Group>();
        
		String permissionTable = resourceTypeDao.getPermissionTable(UMConstants.TSS_APPLICATION_ID, UMConstants.GROUP_RESOURCE_TYPE_ID);
		
		String hql = "select distinct o " + PermissionHelper.formatHQLFrom(entityName, permissionTable) + " , Group child " +
		        PermissionHelper.permissionConditionII() + 
		        " and child.id in (:groupIds) and child.decode like o.decode||'%' and o.id not in (-1, -7)" + 
		        PermissionHelper.ORDER_BY;
		
		return getEntities(hql, 
				new Object[]{"operatorId", "operationId", "groupIds"}, 
				new Object[]{operatorId, operationId, groupIds} );
	}

	public List<?> getVisibleMainUsers(Long operatorId) {
	    // 先查出有查看权限的用户组
	    List<Group> groups = new ArrayList<Group>(); 
	    groups.addAll(getGroupsByType(operatorId, UMConstants.GROUP_VIEW_OPERRATION, Group.MAIN_GROUP_TYPE));
	    groups.addAll(getGroupsByType(operatorId, UMConstants.GROUP_VIEW_OPERRATION, Group.ASSISTANT_GROUP_TYPE));
	    
	    List<Long> groupIds = new ArrayList<Long>();
	    for(Object temp : groups) {
	        groupIds.add(((Group) temp).getId());
	    }
	    
	    if(groupIds.isEmpty()) {
	        return new ArrayList<User>();
	    }
	    
        String hql = " select distinct u from User u, GroupUser gu where u.id = gu.userId  and gu.groupId in (:groupIds) ";
        return getEntities(hql, new Object[] {"groupIds"}, new Object[]{ groupIds });
	}

	public List<User> getUsersByGroupIdDeeply(Long groupId){
		List<Group> sonGroups = this.getChildrenById(groupId);
		List<Long> sonGroupIds = new ArrayList<Long>();
		for(Group son : sonGroups) {
			sonGroupIds.add(son.getId());
		}
		
        return getUsersByGroupIds(sonGroupIds);
	}

	public List<User> getUsersByGroupIds(Collection<Long> groupIds){
        if( EasyUtils.isNullOrEmpty(groupIds) ) return new ArrayList<User>();
        
        String hql = "select distinct u, g.id as groupId, g.name as groupName " +
        		" from User u, GroupUser gu, Group g " + 
        		" where u.id = gu.userId and gu.groupId = g.id and g.id in (:groupIds) ";

        List<?> list = getEntities(hql, new Object[]{"groupIds"}, new Object[]{groupIds});
		return fillGroupInfo2User(list);
	}

	public List<User> getUsersByGroupId(Long groupId) {
		String hql = "select distinct u, g.id as groupId, g.name as groupName" +
				" from User u, GroupUser gu, Group g " +
                " where u.id = gu.userId and gu.groupId = g.id and g.id = ? ";
		return fillGroupInfo2User(getEntities(hql, groupId));
	}

    /**
     * 将用户所属的用户组ID，NAME设置到用户对象中
     * @param users
     * @return
     */
    private List<User> fillGroupInfo2User(List<?> users){
        List<User> result = new ArrayList<User>();
        if( EasyUtils.isNullOrEmpty(users) ) return result;
        
        for (Object temp : users) {
            Object[] objs = (Object[]) temp;
            User user = (User) objs[0];
            user.setGroupId( EasyUtils.obj2Long(objs[1]) );
            user.setGroupName((String) objs[2]);
            result.add(user);
        }    
        return result;
    }

	private List<Long> getChildrenGroupIds(Long groupId){
    	List<?> groups = getChildrenById(groupId);
    	List<Long> groupIds = new ArrayList<Long>();
        for( Object temp : groups ){
            Group group = (Group) temp;
            groupIds.add(group.getId());
        }
        return groupIds;
	}

    public boolean isOperatorInGroup(Long groupId, Long operatorId) {
        List<Long> groupIds = getChildrenGroupIds(groupId);
        if( EasyUtils.isNullOrEmpty(groupIds) ) return false;
        
        String hql = "select distinct gu.id from GroupUser gu where gu.groupId in (:groupIds) and gu.userId = :userId";
        List<?> list = getEntities(hql, new Object[]{"groupIds", "userId"}, new Object[]{groupIds, operatorId});
        return !list.isEmpty();
    }
    
    //******************************************* 按组或按查询条件查询用户 *******************************************

    public PageInfo getUsersByGroup(Long groupId, Integer pageNum, String...orderBy) {
        String hql = "select distinct u, g.id as groupId, g.name as groupName "
        		+ " from User u, GroupUser gu, Group g" 
        		+ " where u.id = gu.userId and gu.groupId = g.id and g.id = :groupId ";
        UMQueryCondition condition = new UMQueryCondition();
        condition.setGroupId(groupId);
        condition.getPage().setPageNum(pageNum);
        
        if(orderBy != null) {
        	condition.getOrderByFields().addAll( Arrays.asList(orderBy) );
        } 

        PaginationQueryByHQL pageQuery = new PaginationQueryByHQL(em, hql, condition);
        PageInfo page = pageQuery.getResultList();
        page.setItems( fillGroupInfo2User(page.getItems()) );
        return page;
    }
    
    public PageInfo searchUser(UMQueryCondition condition) {
        List<Long> groupIds = getChildrenGroupIds(condition.getGroupId());
        if( EasyUtils.isNullOrEmpty(groupIds) ) {
        	return null;
        } else {
        	 condition.setGroupIds(groupIds);
        }
        
        String hql = "select distinct u, g.id as groupId, g.name as groupName "
            + " from User u, GroupUser gu, Group g " 
            + " where u.id = gu.userId and gu.groupId = g.id and g.id in (:groupIds) " 
            + condition.toConditionString();
        
        Set<String> set = condition.getIgnoreProperties();
        set.add("groupId");
        
        PaginationQueryByHQL pageQuery = new PaginationQueryByHQL(em, hql, condition);
        PageInfo page = pageQuery.getResultList();
        page.setItems(fillGroupInfo2User(page.getItems()));
        return page;
    }
}
