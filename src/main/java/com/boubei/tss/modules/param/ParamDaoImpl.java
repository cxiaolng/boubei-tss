package com.boubei.tss.modules.param;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.boubei.tss.framework.persistence.TreeSupportDao;

@Repository("ParamDao")
public class ParamDaoImpl extends TreeSupportDao<Param> implements ParamDao {

	public ParamDaoImpl() {
        super(Param.class);
    }
	
	public List<?> getAllParam(boolean includeHidden) {
		if(includeHidden) {
			return getEntities("from Param p order by p.decode");
		}
		return getEntities("from Param p where p.hidden <> 1 order by p.decode");
	}
	
	public Param getParamByCode(String code){
		String hql = "from Param p where p.code = ? and p.disabled <> 1 order by p.decode";
        List<?> list = getEntities(hql, code);
        return list.size() > 0 ? (Param) list.get(0) : null;
	}
	
	public List<?> getCanAddGroups(){
		String hql = "from Param p where p.type = ? and p.hidden <> 1 order by p.decode";
		return getEntities(hql, ParamConstants.GROUP_PARAM_TYPE);
	}
}