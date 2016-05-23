package com.boubei.tss.framework.persistence.entityaop;

import java.util.Date;

/**
 * 用户操作信息记录接口
 * 
 */
public interface IOperatable {

	public void setCreateTime(Date createTime);

	public void setCreatorId(Long creatorId);

	public void setCreatorName(String creatorName);

	public void setUpdateTime(Date updateTime);

	public void setUpdatorId(Long updatorId);

	public void setUpdatorName(String updatorName);
	
}

	