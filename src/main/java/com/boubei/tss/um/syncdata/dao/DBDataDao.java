package com.boubei.tss.um.syncdata.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.um.syncdata.SyncDataHelper;

/** 
 * 从MySQL等数据库里同步用户组织信息
 */
public class DBDataDao extends BaseDBDataDao{
 
    protected Connection getConnection(Map<String, String> map){
        Connection conn = null;
        String url = map.get(SyncDataHelper.URL);
        String userName = map.get(SyncDataHelper.USERNAME);
        String password = map.get(SyncDataHelper.PASSWORD);
        try {
            Class.forName(map.get(SyncDataHelper.DRIVER));
            conn = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            throw new BusinessException("连接外部数据库失败,请检查连接参数", e);
        } catch (ClassNotFoundException e) {
            throw new BusinessException("连接外部数据库失败,驱动程序找不到", e);
        }
        return conn;
    }
}