package com.boubei.tss.modules.log;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.persistence.ICommonDao;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.persistence.pagequery.PaginationQueryByHQL;
 
@Service("LogService")
public class LogServiceImpl implements LogService {
    
    @Autowired private ICommonDao dao;
 
    public List<?> getAllOperateObjects() {
        return dao.getEntities("select distinct o.operateTable from Log o order by o.operateTable");
    }

    public PageInfo getLogsByCondition(LogQueryCondition condition) {
        String hql = " from Log o " 
        		+ " where 1=1 " + condition.toConditionString() 
        		+ " order by o.operateTime desc ";
 
        PaginationQueryByHQL pageQuery = new PaginationQueryByHQL(dao.em(), hql, condition);
        return pageQuery.getResultList();
    }

    public Log getLogById(Long id) {
        return (Log) dao.getEntity(Log.class, id);
    }
}

