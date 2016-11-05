package com.boubei.tss.dm.data.sqlquery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.XMLDocUtil;

public class SQLExcutor {

    static Logger log = Logger.getLogger(SQLExcutor.class);
    
    /**
     * 自己解析SQL语句的select字段。Oracle会自动把字段转成大写，容易混乱
     */
    boolean selfParse;
    
    public List<String> selectFields = new ArrayList<String>();
    
    public int count;
    public List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    
    public SQLExcutor() {
    	this.selfParse = true;
    }
    
    public SQLExcutor(boolean selfParse) {
    	this.selfParse = selfParse;
    }
    
    public Document getGridTemplate() {
    	StringBuffer sb = new StringBuffer();
        sb.append("<grid><declare sequence=\"true\">");
        if(selectFields.size() > 0) {
            for(String filed : selectFields) {
                sb.append("<column name=\"" + filed + "\" mode=\"string\" caption=\"" + filed + "\" />");
            }
        }
        else {
        	sb.append("<column name=\"没有查询到数据\" mode=\"string\" caption=\"没有查询到数据\" />");
        }

        sb.append("</declare><data></data></grid>");
        
    	return XMLDocUtil.dataXml2Doc(sb.toString());
    }
    
    private int getFirstIndex(String script, String keyword) {
    	// 一个复杂的SQL可能同时有大小写的From
    	int index1 = script.indexOf(keyword.toLowerCase());
    	int index2 = script.indexOf(keyword.toUpperCase());
    	if(index1 > 0 && index2 > 0) {
    		return Math.min(index1, index2);
    	}
    	else{
    		return Math.max(index1, index2);
    	}
    }
    
    private void fetchSelectFields(String sql) {
    	if(selectFields.size() > 0) return;
    	
        sql = sql.trim();
        int selectIndex = getFirstIndex(sql, "select");
        
    	// 一个复杂的SQL可能同时有大小写的select/from
        int fromIndex = getFirstIndex(sql, "from");
        
        String fieldsStr = sql.substring(selectIndex + 6, fromIndex);
        String[] fileds = fieldsStr.split(",");
        for(String filed : fileds) {
            StringTokenizer st = new StringTokenizer(filed.trim()); 
            
            String displayName = "";
            while (st.hasMoreElements()) {
                displayName = st.nextToken().trim(); 
            }
            
            if( !EasyUtils.isNullOrEmpty(displayName) ) {
            	selectFields.add(displayName);
            }
        }
    }

    public Object getFirstRow(String columnName) {
    	if(result.size() >= 1) {
    		return result.get(0).get(columnName);
    	}
    	return null;
    }
    
    public static List<Map<String, Object>> query(String dataSource, String sql, Object...params) {
    	SQLExcutor ex = new SQLExcutor(false);
		ex.excuteQuery(sql, dataSource, params);
		return ex.result;
    }
    
    public void excuteQuery(String sql, String datasource) {
        excuteQuery(sql, new HashMap<Integer, Object>(), datasource);
    }

    public void excuteQuery(String sql, AbstractSO so) {
        excuteQuery(sql, SOUtil.generateQueryParametersMap(so));
    }
    
    public void excuteQuery(String sql, AbstractSO so, String datasource) {
        excuteQuery(sql, SOUtil.generateQueryParametersMap(so), datasource);
    }
    
    public void excuteQuery(String sql) {
        excuteQuery(sql, new HashMap<Integer, Object>(), 0, 0);
    }
 
    public void excuteQuery(String sql, String dataSource, Object...params) {
    	Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
    	if(params != null) {
        	int index = 1;
        	for(Object param : params) {
        		paramsMap.put(index++, param);
        	}
        }
        excuteQuery(sql, paramsMap, 0, 0, dataSource);
    }
    
    public void excuteQuery(String sqlCollection, int index, Map<Integer, Object> paramsMap) {
    	String sql = SqlConfig.getScript(sqlCollection, index);
        excuteQuery(sql, paramsMap, 0, 0);
    }

    public void excuteQuery(String sql, Map<Integer, Object> paramsMap) {
        excuteQuery(sql, paramsMap, 0, 0);
    }
 
    public void excuteQuery(String sql, Map<Integer, Object> paramsMap, int page, int pagesize) {
        excuteQuery(sql, paramsMap, page, pagesize, DMConstants.getDefaultDS());
    }

    public void excuteQuery(String sql, Map<Integer, Object> paramsMap, String datasource) {
        excuteQuery(sql, paramsMap, 0, 0, datasource);
    }

    public void excuteQuery(String sql, Map<Integer, Object> paramsMap, Connection conn) {
        excuteQuery(sql, paramsMap, 0, 0, conn);
    }

    public void excuteQuery(String sql, Map<Integer, Object> paramsMap, int page, int pagesize, String datasource) {
        Pool connpool = JCache.getInstance().getPool(datasource);
        Cacheable connItem = connpool.checkOut(0);
        Connection conn = (Connection) connItem.getValue();

        try {
            this.excuteQuery(sql, paramsMap, page, pagesize, conn);
        } catch (Exception e) {
        	this.result = null;
        	throw new BusinessException(e.getMessage());
        } finally {
            // 返回连接到连接池
            connpool.checkIn(connItem);
        }
    }
    
    private PreparedStatement prepareStatement(Connection conn, String sql, Map<Integer, Object> paramsMap) throws SQLException {
    	PreparedStatement pstmt = conn.prepareStatement(sql);
        if (paramsMap != null) {
        	log.debug("params : " + paramsMap);
            for (Entry<Integer, Object> entry : paramsMap.entrySet()) {
                Object value = entry.getValue();
                if(value instanceof Date) {
            		value = new Timestamp(((Date)value).getTime());
            	}
				pstmt.setObject(entry.getKey(), value); // 从1开始，非0
            }
        }
        
        return pstmt;
    }

    public void excuteQuery(String sql, Map<Integer, Object> paramsMap, int page, int pagesize, Connection conn) {
        String queryDataSql = sql;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        result = new ArrayList<Map<String, Object>>();
        
        String dbUrl = null, driveName;
        try {
        	dbUrl = conn.getMetaData().getURL();
            driveName = conn.getMetaData().getDriverName();
            log.debug(" database url: 【" + dbUrl + "】。");
            log.debug(" database diverName: 【 " + driveName + "】。");
            
            // 如果不分页查询 ，则不执行count(*)查询
            if (page > 0 && pagesize > 0) {
            	// 已经取到总记录Count值，则不再执行count(*)查询
            	if(count <= 0) {
            		String queryCountSql = " select count(*) from (\n " + sql + " \n) t ";
                    
                    pstmt = prepareStatement(conn, queryCountSql, paramsMap);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                    
                    pstmt.close();
                    rs.close();
            	}

                int fromRow = pagesize * (page - 1);
                int toRow = pagesize * page;

                // 各种数据库的分页不一样
                if (driveName.startsWith("MySQL")) {
                    queryDataSql = sql + "\n LIMIT " + (fromRow) + ", " + pagesize;
                } else if (driveName.startsWith("Oracle")) {
                    queryDataSql = "SELECT * FROM ( SELECT t.*, ROWNUM RN FROM (\n " + sql + " \n) t WHERE ROWNUM <= " + toRow + ") WHERE RN > " + fromRow;
                } else {
                    // TODO 支持其他数据源在此扩展
                } 
            }

            log.debug(" excute query sql: \n" + queryDataSql);
            pstmt = prepareStatement(conn, queryDataSql, paramsMap);
            rs = pstmt.executeQuery();
            
            if(selfParse) {
            	fetchSelectFields(sql);
            }

            while (rs.next()) {
                Map<String, Object> rowData = new LinkedHashMap<String, Object>();

                // 从1开始，非0
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int fieldNum =  selfParse ? selectFields.size() : rsMetaData.getColumnCount();
				for(int index = 1; index <= fieldNum; index++) {
					String columnName;
	                if(selfParse) {
	                	columnName = selectFields.get(index - 1);
	                } 
	                else {
	                	columnName = rsMetaData.getColumnLabel(index).toLowerCase();
	                	if(columnName.equals("rn")) continue;
	                	
	                	if( result.isEmpty() && !selectFields.contains(columnName) ) {
							selectFields.add(columnName);
						}
	                }
	                
	                Object value = rs.getObject(index);
					rowData.put(columnName, value);
                }

                result.add(rowData);
            }
        } 
        catch (SQLException e) {
            String exMsg = "异常：" +e.getMessage()+ " ";
            log.debug(exMsg + "\n   数据源：" + dbUrl + ",\n   参数：" + paramsMap + ",\n   脚本：" + sql);
            throw new BusinessException(exMsg);
        } 
        finally {
            try { if (pstmt != null) pstmt.close(); } 
            catch (Exception e) { }
            
            try { if (rs != null) rs.close(); } 
            catch (Exception e) { }
        }
    }

    // 执行单条sql，带参数，一般为insert 或 update 语句
    public static void excute(String sql, Map<Integer, Object> paramsMap, String datasource) {
        List<Map<Integer, Object>> paramsMapList = new ArrayList<Map<Integer, Object>>();
        paramsMapList.add(paramsMap);
        excuteBatch(sql, paramsMapList, datasource);
    }
 
    // 直接执行的sql，不带参数， create table/drop table/insert/delete/update等
    public static void excute(String sql, String datasource) {
        Pool connpool = JCache.getInstance().getPool(datasource);
        Cacheable connItem = connpool.checkOut(0);
        Connection conn = (Connection) connItem.getValue();
 
        try {
        	excute(sql, conn);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        } finally {
            connpool.checkIn(connItem);
        }
    }
    
    public static void excute(String sql, Connection conn) {
    	boolean autoCommit = true;
    	Statement statement = null;
    	try {
    		autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            
    		log.debug(" excute  sql: " + sql);
			statement = conn.createStatement();
			statement.execute(sql);
			conn.commit();
			
			conn.setAutoCommit(autoCommit);
			
		} catch (SQLException e) {
			try { conn.rollback(); } catch (Exception e2) { }
			
			String errorMsg = "异常：" +e.getMessage();
			log.error(errorMsg + "\n SQL: " + sql);
			throw new BusinessException(errorMsg);
			
		} finally {
			try { conn.setAutoCommit(autoCommit); } 
        	catch (Exception e2) { log.error(e2.getMessage(), e2);  }
			
			try {
				if(statement != null) { statement.close(); }
            } catch (Exception e) { }
		}
    }
    
    // 批量执行SQL, 每条SQL的参数放在Map里，"key"值为参数序号。
    public static void excuteBatch(String sql, List<Map<Integer, Object>> paramsList, String datasource) {
        List<Object[]> _paramsList = new ArrayList<Object[]>();
        for(Map<Integer, Object> params : paramsList) {
        	List<Map.Entry<Integer, Object>> list = new ArrayList<Map.Entry<Integer, Object>>(params.entrySet());
    		
    		Collections.sort(list, new Comparator<Map.Entry<Integer, Object>>() {   
    		    public int compare(Map.Entry<Integer, Object> o1, Map.Entry<Integer, Object> o2) {      
    		        return (o1.getKey() - o2.getKey());  // "key"值为参数序号
    		    }
    		});
    		
    		Object[] paramObjs = new Object[list.size()];
    		int index = 0;
    		for(Map.Entry<Integer, Object> entry : list) {
    			paramObjs[index++] = entry.getValue();
    		}
    		_paramsList.add(paramObjs);
        }

        excuteBatchII(sql, _paramsList, datasource);
    }
    
    public static void excuteBatchII(String sql, List<Object[]> paramsList, String datasource) {
    	Pool connpool = JCache.getInstance().getPool(datasource);
        Cacheable connItem = connpool.checkOut(0);
        Connection conn = (Connection) connItem.getValue();
        
        boolean hasException = false;
        try {
        	excuteBatch(sql, paramsList, conn);
        	
        } catch (Exception e) {
        	hasException = true;
        	throw new BusinessException(e.getMessage());
        } finally {
        	connpool.checkIn(connItem);
        	
        	/* 对于执行出错SQL后的Connetion，做销毁处理（因发现conn.rollback后，conn里缓存数据无法清除，导致数据不一致情况出现）
        	 * 问题已解决：conn.setAutoCommit成了false，重新设置为true可以解决上面说的问题 */
        	if( hasException ) { 
        		/*
        		connpool.removeObject(connItem.getKey());
        		connpool.destroyObject(connItem);
        		*/
        	}
        }
    }

    /**
     * 批量执行SQL
     * 
     * 循环里连续的进行插入操作，在开始时设置了：conn.setAutoCommit(false);  最后才进行conn.commit(),
     * 这样即使插入的时候报错，修改的内容也不会提交到数据库，而如果没有手动的进行setAutoCommit(false); 
     * 出错时就会造成，前几条插入，后几条没有会形成脏数据。
     * 
     * 注：设定setAutoCommit(false) 没有在catch中进行Connection的rollBack操作，操作的表就会被锁住，造成数据库死锁。
     */
    public static void excuteBatch(String sql, List<Object[]> paramsList, Connection conn) {
    	if(paramsList == null || paramsList.isEmpty()) return;
    	
    	boolean autoCommit = true;
        PreparedStatement pstmt = null;
        try {
            log.debug("  excuteBatch  sql: " + sql);

            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // 所有sql执行完成后再提交
            
            pstmt = conn.prepareStatement(sql);
        	for (Object[] params : paramsList) {
            	int index = 1;
                for (Object paramValue : params) {
                    pstmt.setObject(index++, paramValue); // 从1开始，非0
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            conn.commit();
            
        } catch (SQLException e) {
        	// 执行回滚
        	try { conn.rollback(); } 
        	catch (Exception e2) { log.error(e2.getMessage(), e2); }
        	
        	String errorMsg = "异常：" +e.getMessage();
        	log.error(errorMsg+ "\n SQL: " + sql);
            throw new BusinessException(errorMsg);
            
        } finally {
        	try { conn.setAutoCommit(autoCommit); } 
        	catch (Exception e2) { log.error(e2.getMessage(), e2);  }
        	
            try { if (pstmt != null) { pstmt.close(); } } 
            catch (Exception e) { }
        }
    }
}
