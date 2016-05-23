package com.boubei.tss.cms.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.cms.AbstractTestSupport;
import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.helper.ArticleQueryCondition;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.test.TestUtil;

/**
 * 文章站点栏目相关模块的单元测试。
 */
public class ArticleModuleTest extends AbstractTestSupport {
 
	@Test
    public void testArticleModule() {
    	// 新建站点
        Channel site = createSite();
        Long siteId = site.getId();
        
        // 新建栏目
        Channel channel1 = super.createChannel("时事评论", site, siteId);
        Channel channel2 = super.createChannel("体育新闻", site, siteId);
        Channel channel3 = super.createChannel("NBA战况", site, channel2.getId());
        Long channelId = channel1.getId();
        
        channel2.setOverdueDate("3");
        channel2.setOverdueDate("4");
        channel3.setOverdueDate("5");
        
        // 开始测试文章模块
		articleAction.initArticleInfo(response, channelId);
        
        Long tempArticleId = System.currentTimeMillis();
        
        Article article = super.createArticle(channel1, tempArticleId);
        Long articleId = article.getId();
        
        articleAction.getArticleInfo(response, articleId);
        
        // 修改文章
        request.addParameter("attachList", "");
		request.addParameter("isCommit", "false");
        articleAction.saveArticleInfo(response, request, article);
        
        List<?> list = getArticlesByChannel(channelId);
        assertNotNull(list);
        assertEquals(1, list.size());
        
        // 置顶、解除置顶
        articleAction.doOrUndoTopArticle(response, articleId);
        assertEquals(article.getIsTop(), ParamConstants.TRUE);
        
        articleAction.doOrUndoTopArticle(response, articleId);
        assertEquals(article.getIsTop(), ParamConstants.FALSE);
        
        articleAction.getChannelArticles(response, channelId, 1);
        
        ArticleQueryCondition condition = new ArticleQueryCondition();
        condition.setTitle("轮回");
        condition.setChannelId(channelId);
		articleAction.queryArticles(response, 1, condition);
		
		condition.setChannelId(channelId);
		condition.setCreateTime(new Date());
		condition.setIsDesc(ParamConstants.TRUE);
		condition.setAuthor("Jon.King");
		condition.setKeyword("历史 轮回");
		condition.setSummary("历史");
		condition.setOrderField("author");
		articleAction.queryArticles(response, 1, condition);
		
		// 移动文章
        articleAction.moveArticle(response, article.getId(), channel3.getId());
        assertEquals(article.getChannel().getId(), channel3.getId());
        
        // 添加/删除/读取评论
        request.addParameter("comment", "very good!");
        articleAction.addComment(response, request, articleId);
        
        request.addParameter("comment", "so bad!");
        articleAction.addComment(response, request, articleId);
        
        List<?> comments = articleAction.getComment(request, articleId);
        Assert.assertTrue(comments.size() == 2);
        
        articleAction.delComment(request, response, articleId, 1);
        
        comments = articleAction.getComment(request, articleId);
        Assert.assertTrue(comments.size() == 1);
       
        // 最后删除文章、栏目、站点
        articleAction.deleteArticle(response, articleId);
        
        deleteSite(siteId);
        
        assertTrue(TestUtil.printLogs(logService) > 0);
    }
}
