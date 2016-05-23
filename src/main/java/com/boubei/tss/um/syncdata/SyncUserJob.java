package com.boubei.tss.um.syncdata;

import java.util.Map;

import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.component.progress.Progress;
import com.boubei.tss.framework.component.progress.Progressable;
import com.boubei.tss.framework.component.timer.AbstractJob;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.service.IGroupService;
import com.boubei.tss.util.EasyUtils;

/**
 * 自动同步用户
 * 
 * com.boubei.tss.um.syncdata.SyncUserJob | 0 06 * * * ? | 4,V5
 * 
 */
public class SyncUserJob extends AbstractJob {
	
	ISyncService syncService = (ISyncService) Global.getBean("SyncService");
	IGroupService groupService = (IGroupService) Global.getBean("GroupService");
 
	/* 
	 * jobConfig的格式为 : 
	 * 		groupId1,applicationId1
	 * 		groupId2,applicationId2
	 */
	protected void excuteJob(String jobConfig) {
		log.info("开始用户信息自动同步......");
		
		String[] jobConfigs = EasyUtils.split(jobConfig, "\n");
		
		for(int i = 0; i < jobConfigs.length; i++) {
			String info[] = EasyUtils.split(jobConfigs[i], ",");
			if(info.length < 2) continue;
			 
			Long groupId = EasyUtils.obj2Long(info[0]);
			Group group = groupService.getGroupById(groupId);
	        String fromGroupId = group.getFromGroupId();
	        if ( EasyUtils.isNullOrEmpty(fromGroupId) ) {
	            log.error("自动同步用户时，组【" + group.getName() + "】的对应外部应用组的ID（fromGroupId）为空。");
	            continue;
	        }
	        
	        Map<String, Object> datasMap = syncService.getCompleteSyncGroupData(groupId, info[1], fromGroupId);
	        ((Progressable) syncService).execute(datasMap, new Progress(10000));
		}
		
		log.info("完成用户信息自动同步。");
	}
	 
}
