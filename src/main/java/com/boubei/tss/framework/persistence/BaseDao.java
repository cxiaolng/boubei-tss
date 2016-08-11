package com.boubei.tss.framework.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.EasyUtils;

/**
 * DAO的一些基本方法
 */
public abstract class BaseDao<T extends IEntity> implements IDao<T>{
    
    protected final Logger log = Logger.getLogger(getClass());
    
    @PersistenceContext 
    protected EntityManager em; //em.setFlushMode(FlushModeType.COMMIT);
    
    public EntityManager em() {
    	return em;
    }

    protected Class<T> type;
    
    public BaseDao(Class<T> type) {
        this.type = type;
    }
    
    public Class<T> getType() {
    	return type;
    }
    
    public void evict(Object o) {
        em.detach(o); // <==> getHibernateTemplate().evict(o);
    }

    public void flush() {
        em.flush();
    }
 
    public T create(T entity) {
        em.persist(entity);
        em.flush();
        em.refresh(entity);
        return entity;
    }
    
    public Object createObject(Object entity) {
        em.persist(entity);
        em.flush();
        em.refresh(entity);
        return entity;
    }
    
    public T createWithoutFlush(T entity) {
        em.persist(entity);
        return entity;
    }
    
    public Object createObjectWithoutFlush(Object entity) {
        em.persist(entity);
        return entity;
    }
    
    public Object update(Object entity) {
    	em.merge(entity);
        em.flush();
        return entity;
    }
    
    public Object updateWithoutFlush(Object entity) {
        em.merge(entity);
        return entity;
    }
    
	public Object refreshEntity(Object entity) {
		em.merge(entity);
        return entity;
    }

    /**
     * 根据主键值删除对象记录
     * @param clazz
     * @param id
     */
    public Object delete(Class<?> clazz, Serializable id) {
        Object entity = em.find(clazz, id);
        em.remove(entity);
        return entity;
    }
    
    public T deleteById(Serializable id) {
        T entity = getEntity(id);
        this.delete(entity);
        return entity;
    }

    public Object delete(Object entity) {
        em.remove(entity);
        return entity;
    }

    public void deleteAll(Collection<?> c) {
        for (Iterator<?> it = c.iterator(); it.hasNext();) {
            delete(it.next());
        }
    }
 
    /**
     * 根据主键值获取对象 
     * @param clazz
     * @param id
     * 
     * @return
     */
    public IEntity getEntity(Class<?> clazz, Serializable id) {
        return (IEntity) em.find(clazz, id);
    }
    
    public T getEntity(Serializable id) {
        return em.find(type, id);
    }

    /**
     * 根据HQL语句和参数值获取对象列表.
     * 
     * @param hql
     * @return
     */
    public List<?> getEntities(String hql, Object...conditionValues) {
        Query query = em.createQuery(hql);
        if (conditionValues != null)
            for (int i = 0; i < conditionValues.length; i++) {
                Object param = conditionValues[i];
                if (param == null) {
                    throw new BusinessException("HQL【" + hql  + "】参数有误，第 " + (i + 1) + " 个参数值为Null！");
                }
                query.setParameter(i + 1, param); // 从1开始，非0
            }
        List<?> results = query.getResultList();
        return results == null ? new ArrayList<Object>() : results;
    }

    /**
     * 根据HQL语句、参数名、参数值获取对象列表.
     * 支持in查询,  in ( :names ), 参数要求位数组
     * 
     * @param hql
     * @return
     */
    public List<?> getEntities(String hql, Object[] conditionNames, Object[] conditionValues) {
        Query query = em.createQuery(hql);
        if (conditionValues != null)
            for (int i = 0; i < conditionValues.length; i++) {
                Object param = conditionValues[i];
                if (param == null) {
                    throw new BusinessException("HQL【" + hql  + "】参数有误，第 " + (i + 1) + " 个参数值为Null！");
                }
                
                if (param instanceof Object[])
                    query.setParameter((String) conditionNames[i], Arrays.asList((Object[]) param)); // in查询，接收List类型，不支持数组
                else
                    query.setParameter((String) conditionNames[i], param);
            }
        List<?> results = query.getResultList();
        return results == null ? new ArrayList<Object>() : results;
    }
    
    /**
     * 根据原生SQL查询
     * 
     * @param nativeSql
     * @param entityClazz
     * @param params
     * @return
     */
    public List<?> getEntitiesByNativeSql(String nativeSql, Class<?> entityClazz, Object...params) {
        Query query = em.createNativeQuery(nativeSql, entityClazz);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param == null) {
                    throw new BusinessException("SQL【" + nativeSql + "】参数有误，第 " + (i + 1) + " 个参数值为Null!");
                }
                query.setParameter(i + 1, param);  // 从1开始，非0
            }
        }
        return query.getResultList();
    }
    
    public List<?> getEntitiesByNativeSql(String nativeSql, Object...params) {
        Query query = em.createNativeQuery(nativeSql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param == null) {
                    throw new BusinessException("SQL【" + nativeSql + "】参数有误，第 " + (i + 1) + " 个参数值为Null!");
                }
                query.setParameter(i + 1, param);  // 从1开始，非0
            }
        }
        return query.getResultList();
    }
    
    /**
     * 执行HQL语句，一般为delete、update类型
     * @param hql
     */
    public void executeHQL(String hql, Object...params){
        Query query = em.createQuery(hql);
        if(params != null && params.length > 0) {
            for(int i = 0; i < params.length; i++){
                query.setParameter(i + 1, params[i]); // 从1开始，非0
            }
        }
        
        query.executeUpdate();
        em.flush();
        em.clear();        
    }
    
    public void executeHQL(String hql, String[] argNames, Object[] params){
        Query query = em.createQuery(hql);
        if(params != null && params.length > 0) {
            for(int i = 0; i < params.length; i++){
                query.setParameter(argNames[i], params[i]);
            }
        }
        
        query.executeUpdate();
        em.flush();
        em.clear();        
    }
    
    public void executeSQL(String sql, Object...params){
        Query query = em.createNativeQuery(sql);
        if(params != null && params.length > 0) {
            for(int i = 0; i < params.length; i++){
                query.setParameter(i + 1, params[i]); // 从1开始，非0
            }
        }
        
        query.executeUpdate();
        em.flush();
        em.clear();        
    }
    
    public void executeSQL(String sql, String[] argNames, Object[] params){
        Query query = em.createNativeQuery(sql);
        if(params != null && params.length > 0) {
            for(int i = 0; i < params.length; i++){
                query.setParameter(argNames[i], params[i]);
            }
        }
        
        query.executeUpdate();
        em.flush();
        em.clear();        
    }
    
    public void insertIds2TempTable(Collection<?> list) {
        if( !EasyUtils.isNullOrEmpty(list) ) {
        	Collection<Temp> c = new HashSet<Temp>();
            for(Object id : list){
                Temp temp = new Temp( EasyUtils.obj2Long(id) );
                c.add(temp);
            }
            this.insert2TempTable(c);
        }
    }
    
    public void insertIds2TempTable(Collection<? extends Object[]> list, int idIndex) {
        if( !EasyUtils.isNullOrEmpty(list) ) {
        	Collection<Temp> c = new HashSet<Temp>();
            for(Object[] objs : list){
                Temp temp = new Temp( (Long) objs[idIndex] );
                c.add(temp);
            }
            this.insert2TempTable(c);
        }
    }
 
    public void insertEntityIds2TempTable(Collection<? extends IEntity> list) {
        if( !EasyUtils.isNullOrEmpty(list) ) {
        	Collection<Temp> c = new HashSet<Temp>();
            for(IEntity entity : list){
                Temp temp = new Temp( (Long) entity.getPK() );
                c.add(temp);
            }
            this.insert2TempTable(c);
        }
    }
    
    public void insert2TempTable(Collection<Temp> list) {
        clearTempTable();
        
        if( !EasyUtils.isNullOrEmpty(list) ) {
        	Set<Temp> set = new HashSet<Temp>(list); // 剔除重复的
            for(Temp temp : set){
                createObjectWithoutFlush(temp);
            }
        }
    }
    
    public void clearTempTable() {
        deleteAll( getEntities("from Temp") );
    }
}
