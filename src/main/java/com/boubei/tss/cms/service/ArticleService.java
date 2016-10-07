package com.boubei.tss.cms.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.dao.IArticleDao;
import com.boubei.tss.cms.dao.IChannelDao;
import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.helper.ArticleHelper;
import com.boubei.tss.cms.helper.ArticleQueryCondition;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
 
@Service("ArticleService")
public class ArticleService implements IArticleService {
    
    protected final Logger log = Logger.getLogger(getClass());

	@Autowired private IArticleDao articleDao;
	@Autowired private IChannelDao channelDao;
 
    public Article getArticleById(Long articleId) {
        Article article = getArticleOnly(articleId);
        
        List<Attachment> attachments = articleDao.getArticleAttachments(articleId);
        article.getAttachments().addAll(attachments);
        return article;
    }
    
    public Attachment processFile(File file, Long articleId, Long channelId, int type, String kidName) {
        Channel site = channelDao.getEntity(channelId).getSite();
        String siteRootPath =  ArticleHelper.getAttachmentPath(site, type);
        File siteRootDir = new File(siteRootPath);
        
        // 将附件从上传临时目录剪切到站点指定的附件目录里
        String fileName = FileHelper.copyFile(siteRootDir, file); 
		String fileSuffix = FileHelper.getFileSuffix(fileName);
		
		// file指向剪切后的地址
		file = new File(siteRootPath + "/" + fileName); 
		
		// 保存附件信息对象
		Attachment attachment = new Attachment();
		attachment.setSeqNo(articleDao.getAttachmentIndex(articleId));
		attachment.setName(kidName);
		attachment.setType(type);
		attachment.setFileName(FileHelper.getFileNameNoSuffix(fileName));
		attachment.setFileExt(fileSuffix);
		attachment.setUrl(CMSConstants.DOWNLOAD_SERVLET_URL);
		attachment.setArticleId(articleId);
		
		String filepath = file.getPath();

		/* 缩略图质量太差
		if (attachment.isImage()) { // 如果是图片，则为其制作缩略图
			try {
				filepath = new ImageProcessor(filepath).resize(0.68);
			} catch (Exception e) {
				log.error("制作附近图片的缩略图失败。", e);
				filepath = file.getPath(); // 如果缩略失败，则还是采用原图片
			}
		}
		*/

		String year = new SimpleDateFormat("yyyy").format(new Date());
		int index = filepath.indexOf(year);
		attachment.setLocalPath(filepath.substring(index).replaceAll("\\\\", "/"));
		attachment.setUploadDate(new Date());
		
		articleDao.createObject(attachment);

		return attachment;
	}
    
	public void createArticle(Article article, Long channelId, String attachList, Long tempArticleId) {
		Channel channel = channelDao.getEntity(channelId);
		article.setChannel(channel);
 
		// set over date
		Date calculateOverDate = ArticleHelper.calculateOverDate(channel);
        if( calculateOverDate != null ) {
			article.setOverdueDate(calculateOverDate);
        }
		articleDao.saveArticle(article);
        
		// 处理附件
        List<String> attachSeqNos = new LinkedList<String>();
		if ( !EasyUtils.isNullOrEmpty(attachList) ) {			
            StringTokenizer st = new StringTokenizer(attachList, ",");
            while (st.hasMoreTokens()) {
                attachSeqNos.add(st.nextToken());
            }
        }
		
		List<Attachment> attachments = articleDao.getArticleAttachments(tempArticleId); // 后台查找的新建文章时上传的附件列表
        for ( Attachment attachment : attachments ) {
			Integer seqNo = attachment.getSeqNo();
            if (attachSeqNos.contains(seqNo.toString())) { // 判断附件在文章保存时是否还存在
				attachment.setArticleId(article.getId());
				attachment.setSeqNo(seqNo);
				articleDao.update(attachment);
			}
            else { // 删除附件
				String[] uploadName = ArticleHelper.getAttachUploadPath(channel.getSite(), attachment);
                new File(uploadName[0]).delete();
                articleDao.delete(attachment);
			}
		}
	}
 
    public void updateArticle(Article article, Long channelId, String attachList) {
    	
        articleDao.update(article);
        
        if(channelId == null) return; // 不关心附件的增删
        
        // 处理附件, attachList为剩余的附件列表
        List<String> attachSeqNos = new LinkedList<String>();
        if ( !EasyUtils.isNullOrEmpty(attachList) ) {
            StringTokenizer st = new StringTokenizer(attachList, ",");
            while (st.hasMoreTokens()) {
                attachSeqNos.add(st.nextToken());
            }
        }
        
        List<Attachment> attachments = articleDao.getArticleAttachments(article.getId());
        for ( Attachment attachment : attachments ) {
            if (attachSeqNos.contains(attachment.getSeqNo().toString())) {
               continue;
            }
            
            // 删除附件
            articleDao.delete(attachment);
            Channel site = channelDao.getSiteByChannel(channelId);
            String path = site.getPath() + "/" + site.getAttanchmentPath(attachment) + "/" + attachment.getLocalPath();
            new File(path).delete();
        }
    }

    public Article deleteArticle(Long articleId) {
        Article article = getArticleOnly(articleId);
        articleDao.deleteArticle(article);
        
        return article;
	}
    
    private Article getArticleOnly(Long articleId){
        Article article = articleDao.getEntity(articleId);
        if (article == null) {
            throw new NullPointerException("未找到文章！");
        }
        
        return article;
    }
 
    public void moveArticle(Long articleId, Long channelId) {
        Article article = articleDao.getEntity(articleId);
        Channel channel = channelDao.getEntity(channelId);
        if(channel.isSiteRoot()) {
        	throw new BusinessException("文章不能移动到站点目录下，请重新选择一个目标栏目");
        }
        article.setChannel(channel);
        
        articleDao.update(article);
	}
 
	public Article doTopArticle(Long articleId) {
	    Article article = getArticleOnly(articleId);
	    article.setIsTop(article.getIsTop() == 0 ? 1 : 0);
		articleDao.update(article);
		
		return article;
	}
 
    public PageInfo getChannelArticles(Long channelId, Integer page, String...orderBy) {
       if( !channelDao.checkBrowsePermission(channelId) ) {
            log.error("用户【" + Environment.getUserCode() + "】试图访问没有文章浏览权限的栏目【" + channelId + "】");
            return new PageInfo();
        }
        return articleDao.getPageList(channelId, page, orderBy);
    }

	public Object[] searchArticleList(ArticleQueryCondition condition) {
		// 将用户有浏览权限的选定栏目下子栏目ID列表存入临时表中
		Long channelId = condition.getChannelId();
        List<Channel> children = channelDao.getChildrenById(channelId, CMSConstants.OPERATION_VIEW);
		channelDao.insertEntityIds2TempTable(children);
		condition.setChannelId(null);
 
		PageInfo page = articleDao.getSearchArticlePageList(condition);
        List<?> list = page.getItems();
        
		List<Article> articleList = new ArrayList<Article>();
		if ( !EasyUtils.isNullOrEmpty(list) ) {
			for ( Object temp : list ) {
                Article article = ArticleHelper.createArticle((Object[]) temp);
				articleList.add(article);
			}
		}
		return new Object[]{articleList, page};
	}
}