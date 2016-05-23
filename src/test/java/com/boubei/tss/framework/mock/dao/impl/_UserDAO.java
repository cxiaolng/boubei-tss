package com.boubei.tss.framework.mock.dao.impl;

import org.springframework.stereotype.Repository;

import com.boubei.tss.framework.mock.dao._IUserDAO;
import com.boubei.tss.framework.mock.model._User;
import com.boubei.tss.framework.persistence.BaseDao;

@Repository("_UserDAO")
public class _UserDAO extends BaseDao<_User> implements _IUserDAO {

    public _UserDAO() {
        super(_User.class);
    }

}
