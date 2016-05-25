package com.boubei.tss.cms.job;

import com.boubei.tss.cms.service.IArticleService;
import com.boubei.tss.cms.service.IChannelService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.IdentityCard;
import com.boubei.tss.framework.sso.TokenUtil;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.framework.timer.AbstractJob;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.util.EasyUtils;

public abstract class AbstractCMSJob extends AbstractJob {
	
	// jobConfig 为 siteIds
	protected void excuteJob(String jobConfig) {
		String token = TokenUtil.createToken("1234567890", UMConstants.ADMIN_USER_ID);
		IdentityCard card = new IdentityCard(token, OperatorDTO.ADMIN);
		Context.initIdentityInfo(card); // 模拟管理员登录
		
		JobStrategy strategy = getJobStrategy();
        log.info("开始执行定时策略【" + strategy.name + "】");
        
        try {
        	String[] jobConfigs = EasyUtils.split(jobConfig, ","); 
        	for(int i = 0; i < jobConfigs.length; i++) {
        		excuteCMSJob(jobConfigs[i]);
    		}
            
        } catch (RuntimeException e) {
            throw new BusinessException("excute Strategy 出错，策略名称 = " + strategy.name, e);
        } 
	}
 
	protected IChannelService getChannelService() {
		return (IChannelService) Global.getBean("ChannelService");
	}
	
	protected IArticleService getArticleService() {
		return (IArticleService) Global.getBean("ArticleService");
	}
	
	protected abstract void excuteCMSJob(String jobConfig);
	
	protected abstract JobStrategy getJobStrategy();
}
