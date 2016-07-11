package com.boubei.tss.modules.param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.cache.extension.PCache;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.timer.SchedulerBean;

/**
 *  调用参数管理功能入口
 */
public class ParamManager {
	
	/** 监听器列表 */
    public static List<ParamListener> listeners = new ArrayList<ParamListener>();
    
    static {
    	ParamManager.listeners.add(new PCache());
    	ParamManager.listeners.add(new SchedulerBean());
    }
    
    public static ParamService getService() {
        return Global.getParamService();
    }
    
    /**
     * 获取简单类型参数
     * @param code
     * @return
     */
    public static Param getSimpleParam(String code){
    	try{
            return getService().getParam(code);
    	} catch (Exception e) {
    		throw new BusinessException("获取指定的code：" + code + " 参数信息失败!", e);
		}
    }
    
    /**
     * 获取下拉类型参数列表
     * @param code
     * @return
     */
	public static List<Param> getComboParam(String code){
    	try{
            return getService().getComboParam(code);
    	} catch (Exception e) {
    		throw new BusinessException("获取指定的code：" + code + " 参数信息失败!", e);
		}
    }
    
	public static Param getComboParamItem(String code, String x) {
		List<Param> list = getComboParam(code);
		for(Param p : list) {
			if( x != null && (x.equals(p.getText()) || x.equals(p.getValue())) ) {
				return p;
			}
		}
		return null;
    }
	
    /**
     * 获取树型类型参数列表
     * @param code
     * @return
     */
	public static List<Param> getTreeParam(String code){
    	try{
            return getService().getTreeParam(code);
    	} catch (Exception e) {
    		throw new BusinessException("获取指定的code：" + code + " 参数信息失败!", e);
		}
    }
    
    /**
     * 根据参数Code读取参数值
     * @param code
     * @return
     */
    public static String getValue(String code){
		Param param = (Param)getService().getParam(code);
        if(param == null) { 
            throw new BusinessException("code:" + code + " 的参数没有被创建");
        }
        
    	return param.getValue().replace("\n", ""); // 去掉回车
    }
    
    public static String getValueNoSpring(String code){
        String sql = "select p.value from component_param p where p.type = " + ParamConstants.NORMAL_PARAM_TYPE
                   + " and p.code='" + code + "' and p.disabled <> 1";
        
        Pool connectionPool = JCache.getInstance().getConnectionPool();
		Cacheable connItem = connectionPool.checkOut(0);
		Connection conn = (Connection) connItem.getValue();
		
		String value = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                value = rs.getString("value");
                break;
            } 
            if(value == null){
                throw new BusinessException("code:" + code + " 的参数没有被创建");
            }
        } catch(Exception e){
            throw new BusinessException("读取code:" + code + " 的参数出错", e);
        } finally {
        	connectionPool.checkIn(connItem);
        }
        return value;
    }
    
    /** 建参数组 */
    public static Param addParamGroup(Long parentId, String name) {
        Param param = new Param();
        param.setName(name);
        param.setParentId(parentId);
        param.setType(ParamConstants.GROUP_PARAM_TYPE);
        
        getService().saveParam(param);
        return param;
    }
    
    /** 简单参数 */
    public static Param addSimpleParam(Long parentId, String code, String name, String value) {
        Param param = new Param();
        param.setCode(code);
        param.setName(name);
        param.setValue(value);
        param.setParentId(parentId);
        param.setType(ParamConstants.NORMAL_PARAM_TYPE);
        param.setModality(ParamConstants.SIMPLE_PARAM_MODE);
        
        getService().saveParam(param);
        return param;
    }
    
    /** 下拉型参数 */
    public static Param addComboParam(Long parentId, String code, String name) {
        Param param = new Param();
        param.setCode(code);
        param.setName(name);
        param.setParentId(parentId);
        param.setType(ParamConstants.NORMAL_PARAM_TYPE);
        param.setModality(ParamConstants.COMBO_PARAM_MODE);
        
        getService().saveParam(param);
        return param;
    }
    
    /** 树型参数 */
    public static Param addTreeParam(Long parentId, String code, String name) {
        Param param = new Param();
        param.setCode(code);
        param.setName(name);
        param.setParentId(parentId);
        param.setType(ParamConstants.NORMAL_PARAM_TYPE);
        param.setModality(ParamConstants.TREE_PARAM_MODE);
        
        getService().saveParam(param);
        return param;
    }

    /** 新建设参数项 */
    public static Param addParamItem(Long parentId, String value, String text, Integer mode) {
        Param param = new Param();
        param.setValue(value);
        param.setText(text);
        param.setParentId(parentId);
        param.setType(ParamConstants.ITEM_PARAM_TYPE);
        param.setModality(mode);
        
        getService().saveParam(param);
        return param;
    }
}

	