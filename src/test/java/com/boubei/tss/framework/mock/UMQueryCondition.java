package com.boubei.tss.framework.mock;

import java.util.HashMap;
import java.util.Map;

import com.boubei.tss.framework.persistence.pagequery.MacrocodeQueryCondition;

public class UMQueryCondition extends MacrocodeQueryCondition {
 
	private String userName;   // 姓名
	
    public Map<String, Object> getConditionMacrocodes() {
        Map<String, Object> map = new HashMap<String, Object>() ;
        map.put("${userName}",   " and o.userName = :userName");
 
        return map;
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
 
}
