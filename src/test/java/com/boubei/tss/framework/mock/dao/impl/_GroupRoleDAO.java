package com.boubei.tss.framework.mock.dao.impl;

import org.springframework.stereotype.Repository;

import com.boubei.tss.framework.mock.dao._IGroupRoleDAO;
import com.boubei.tss.framework.mock.model._GroupRole;
import com.boubei.tss.framework.persistence.BaseDao;

@Repository("_GroupRoleDAO")
public class _GroupRoleDAO extends BaseDao<_GroupRole> implements _IGroupRoleDAO {

    public _GroupRoleDAO() {
        super(_GroupRole.class);
    }

}
