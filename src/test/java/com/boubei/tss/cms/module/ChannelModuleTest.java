package com.boubei.tss.cms.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.boubei.tss.cms.AbstractTestSupport;
import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.entity.permission.ChannelResource;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.test.TestUtil;

/**
 * 文章站点栏目相关模块的单元测试。
 */
public class ChannelModuleTest extends AbstractTestSupport {
 
	@Test
    public void testChannelModule() {
        // 新建站点
        Channel site = new Channel();
        site.setName("我的门户" + System.currentTimeMillis());
        site.setPath(super.tempDir1.getPath());
        site.setDocPath("doc");
        site.setImagePath("img");
        site.setOverdueDate("0");
        
        channelAction.saveSite(response, site);
        Long siteId = site.getId();
        assertNotNull(siteId);
        
        channelAction.saveSite(response, site);
        
        channelAction.getSiteDetail(response, CMSConstants.DEFAULT_NEW_ID);
        channelAction.getSiteDetail(response, siteId);
        
        // 新建栏目
        channelAction.getChannelDetail(response, CMSConstants.DEFAULT_NEW_ID, siteId);
        
        Channel channel1 = new Channel();
        channel1.setName("时事评论");
        channel1.setParentId(siteId);
        channelAction.saveChannel(response, channel1);
        Long channelId = channel1.getId();
        assertNotNull(channelId);
        Assert.assertEquals(channel1.getResourceType(), new ChannelResource().getResourceType());
        
        channelAction.getChannelDetail(response, channelId, channel1.getParentId());
        
        channelAction.saveChannel(response, channel1); // update
        
        Channel channel2 = super.createChannel("体育新闻", channel1, siteId);
        Channel channel3 = super.createChannel("NBA战况", channel2, channel2.getId());
        
        channel2.setOverdueDate("1");
        channel3.setOverdueDate("2");
        
        List<?> list = channelService.getAllSiteChannelList();
        assertTrue(list.size() >= 3);
        for(Object temp : list) {
            log.debug(temp);
        }
        
        // 栏目排序
        channelAction.sortChannel(response, channelId, channel2.getId(), 1);
        log.debug(channel1);
        log.debug(channel2);
        assertTrue(channel1.getSeqNo() > channel2.getSeqNo());
        
        // 栏目移动
        channelAction.moveChannel(response, channel3.getId(), channelId);
        assertTrue(channel3.getDecode().startsWith(channel1.getDecode()));
        assertEquals(channel3.getParentId(), channelId);
        
        // 停用启用
        channelAction.disable(response, siteId);
        
        site = channelService.getChannelById(siteId);
        channel1 = channelService.getChannelById(channel1.getId());
        assertEquals(site.getDisabled(), ParamConstants.TRUE);
        assertEquals(channel1.getDisabled(), ParamConstants.TRUE);
        
        channelAction.enable(response, siteId);
        
        site = channelService.getChannelById(siteId);
        channel1 = channelService.getChannelById(channel1.getId());
        assertEquals(site.getDisabled(), ParamConstants.FALSE);
        assertEquals(channel1.getDisabled(), ParamConstants.FALSE);
        
        channelAction.disable(response, channelId);
        
        channel1 = channelService.getChannelById(channel1.getId());
        assertEquals(channel1.getDisabled(), ParamConstants.TRUE);
        
        channelAction.enable(response, channel3.getId());
        
        channel1 = channelService.getChannelById(channel1.getId());
        channel3 = channelService.getChannelById(channel3.getId());
        assertEquals(channel1.getDisabled(), ParamConstants.FALSE);
        assertEquals(channel3.getDisabled(), ParamConstants.FALSE);
        
        channelAction.getOperations(response, channelId);
        
        channelAction.getChannelAll(response);
        
        // 栏目站点删除
        channelAction.delete(response, channel2.getId());
        channelAction.delete(response, siteId);
        
        list = channelDao.getEntities(" from Channel where site.id = ?", siteId);
        assertTrue(list.size() == 0);
        
        assertTrue(TestUtil.printLogs(logService) > 0);
    }
    
}
