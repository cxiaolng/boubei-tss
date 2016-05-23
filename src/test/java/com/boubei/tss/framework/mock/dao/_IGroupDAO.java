package com.boubei.tss.framework.mock.dao;

import com.boubei.tss.framework.mock.model._Group;
import com.boubei.tss.framework.persistence.ITreeSupportDao;

public interface _IGroupDAO extends ITreeSupportDao<_Group> {
 
    void deleteGroup(_Group group);
    
}