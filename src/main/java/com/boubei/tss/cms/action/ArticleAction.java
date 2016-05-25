package com.boubei.tss.cms.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.helper.ArticleHelper;
import com.boubei.tss.cms.helper.ArticleQueryCondition;
import com.boubei.tss.cms.service.IArticleService;
import com.boubei.tss.cms.service.IRemoteArticleService;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.web.dispaly.grid.GridDataEncoder;
import com.boubei.tss.framework.web.dispaly.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
 
@Controller
@RequestMapping("/auth/article")
public class ArticleAction extends BaseActionSupport {

	@Autowired private IArticleService articleService;
	
	/**
	 * 获取栏目下文章列表
	 */
	@RequestMapping(value = "/list/{channelId}/{page}")
	public void getChannelArticles(HttpServletResponse response, 
			@PathVariable("channelId") Long channelId, @PathVariable("page") int page) {
	    
        PageInfo pageInfo = articleService.getChannelArticles(channelId, page);

        List<Article> articles = new ArrayList<Article>();
        List<?> list = pageInfo.getItems();
        if ( !EasyUtils.isNullOrEmpty(list) ) {
            for ( Object temp : list ) {
                Article article = ArticleHelper.createArticle((Object[]) temp);
                articles.add(article);
            }
        }
		GridDataEncoder gEncoder = new GridDataEncoder(articles, CMSConstants.GRID_ARTICLELIST);

		print(new String[]{"ArticleList", "PageInfo"}, new Object[]{gEncoder, pageInfo});
	} 
	
	/**
	 * 初始化文章新增信息
	 */
	@RequestMapping(value = "/init/{channelId}", method = RequestMethod.GET)
	public void initArticleInfo(HttpServletResponse response, @PathVariable("channelId") Long channelId) {
        Map<String, Object> initMap = new HashMap<String, Object>();
        initMap.put("isTop", ParamConstants.FALSE);
        initMap.put("author", Environment.getUserName()); // 默认作者为登录者，前台可进行修改
        
        // 默认的文章发布日期及过期日期
        initMap.put("issueDate", DateUtil.format(new Date()));
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, 1); // 默认一年后过期
        initMap.put("overdueDate", DateUtil.format(calendar.getTime()));
        
        XFormEncoder articleInfoXForm = new XFormEncoder(CMSConstants.XFORM_ARTICLE, initMap);
 
		GridDataEncoder attachGrid = new GridDataEncoder(new ArrayList<Object>(), CMSConstants.GRID_ATTACHSLIST);
        
		print(new String[]{"ArticleInfo", "ArticleContent", "AttachsList"}, 
                new Object[]{articleInfoXForm, "<![CDATA[]]>", attachGrid});
	}
    
	@RequestMapping(value = "/{articleId}", method = RequestMethod.GET)
    public void getArticleInfo(HttpServletResponse response, @PathVariable("articleId") Long articleId) { 
        Article article = articleService.getArticleById(articleId);
        String articleContent = article.getContent();
        Map<String, Object> attributes = article.getAttributes4XForm();
        attributes.remove("Content");
 
        XFormEncoder articleInfoXForm = new XFormEncoder(CMSConstants.XFORM_ARTICLE, attributes);
        
        List<Attachment> attachList = article.getAttachments();
        GridDataEncoder attachGrid = new GridDataEncoder(attachList, CMSConstants.GRID_ATTACHSLIST);
        
        print(new String[]{"ArticleInfo", "ArticleContent", "AttachsList"}, 
                new Object[]{articleInfoXForm, "<![CDATA[" + articleContent + "]]>", attachGrid});
    }
    
	/**
	 * 保存文章。
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void saveArticleInfo(HttpServletResponse response, 
			HttpServletRequest request, Article article) {
		
		String attachList = request.getParameter("attachList");
		String content = request.getParameter("ArticleContent");
		
        String isCommit = request.getParameter("isCommit");
        if( Config.TRUE.equalsIgnoreCase(isCommit) ){
            article.setStatus(CMSConstants.TOPUBLISH_STATUS);
        }
		
        Long channelId = article.getChannel().getId();
        if(article.getId() == null || article.getId() == 0) {
            // 新增的时候上传的附件对象以System.currentTimeMillis(参见CreateAttach类)为主键，此处的"articleId"就是这个值
            String articleIdStr = request.getParameter("articleId");
			Long articleId = EasyUtils.obj2Long(articleIdStr);
	        articleService.createArticle(article, channelId, attachList, articleId); 
	        
	        // 修复文章正文里图片/附件的下载地址
	        if(content != null) {
	        	content = content.replaceAll(articleIdStr, article.getId().toString());
	        }
	    } 
    	article.setContent(content);
        articleService.updateArticle(article, channelId, attachList);
        
	    printSuccessMessage();
	}
 
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteArticle(HttpServletResponse response, @PathVariable("id") Long id) {
	    articleService.deleteArticle(id);
	    printSuccessMessage("删除文章成功");
	}
	
	/**
	 * 移动文章（跨栏目移动）
	 */
	@RequestMapping(value = "/move/{articleId}/{channelId}", method = RequestMethod.POST)
	public void moveArticle(HttpServletResponse response, 
			@PathVariable("articleId") Long articleId, @PathVariable("channelId") Long channelId) {
		
	    articleService.moveArticle(articleId, channelId);
        printSuccessMessage("移动文章成功");
	}

	/**
	 *  文章置顶和取消置顶
	 */
	@RequestMapping(value = "/top/{id}", method = RequestMethod.POST)
	public void doOrUndoTopArticle(HttpServletResponse response, @PathVariable("id") Long id) {
	    articleService.doTopArticle(id);
        printSuccessMessage();
	}

	/**
	 *  搜索文章列表
	 */
	@RequestMapping("/query/{page}")
	public void queryArticles(HttpServletResponse response, @PathVariable("page") int page, ArticleQueryCondition condition) {
        condition.getPage().setPageNum(page);
		
		Object[] data = articleService.searchArticleList(condition);
		GridDataEncoder gEncoder = new GridDataEncoder(data[0], CMSConstants.GRID_ARTICLELIST);
        print(new String[]{"ArticleList", "PageInfo"}, new Object[]{gEncoder, (PageInfo)data[1]});
	}	
	
	
	/************************** CMS对外Action接口，支持RSS。**************************************
	 ************************** 提供供Portlet等外界应用程序读取的文章列表、文章内容等接口。*********************/
 
    @Autowired private IRemoteArticleService remoteService;
 
    /**
     * 获取栏目的文章列表
     */
    @RequestMapping(value = "/list/xml/{channelId}/{page}/{pageSize}/{needPic}", method = RequestMethod.GET)
    public void getArticleListByChannel(HttpServletResponse response, 
    		@PathVariable("channelId") Long channelId, 
    		@PathVariable("page") int page, 
    		@PathVariable("pageSize") int pageSize, 
    		@PathVariable("needPic") boolean needPic) {
    	
        String returnXML = remoteService.getArticleListByChannel(channelId, page, pageSize, needPic);
        print(returnXML);
    }
 
    /**
     * 根据栏目ids，获取这些栏目下的所有文章列表
     */
    @RequestMapping(value = "/channels/{channelIds}/{page}/{pageSize}", method = RequestMethod.GET)
    public void getArticleListByChannels(HttpServletResponse response, 
    		@PathVariable("channelIds") String channelIds, 
    		@PathVariable("page") int page, 
    		@PathVariable("pageSize") int pageSize) {
    	
        if ( EasyUtils.isNullOrEmpty(channelIds) ) {
            throw new BusinessException("栏目IDs为空");
        }
        String returnXML = remoteService.queryArticlesByChannelIds(channelIds, page, pageSize);
        print(returnXML);
    }

    /**
     * 根据栏目id获取文章列表(深度)，取指定栏目以及该栏目下所有子栏目的所有文章列表
     */
    @RequestMapping(value = "/channelDeeply/{channelId}/{page}/{pageSize}", method = RequestMethod.GET)
    public void getArticleListDeeplyByChannel(HttpServletResponse response, 
    		@PathVariable("channelId") Long channelId,
    		@PathVariable("page") int page, 
    		@PathVariable("pageSize") int pageSize) {
    	
        String returnXML = remoteService.queryArticlesDeeplyByChannelId(channelId, page, pageSize);
        print(returnXML);
    }
    
    /**
     * 根据栏目和日期来获取文章列表。
     * 主要用于期刊类需求。
     * @param channelId
     * @param year
     * @param month
     */
    @RequestMapping(value = "/journal/{channelId}/{year}/{month}", method = RequestMethod.GET)
    public void getArticleListByChannelAndTime(HttpServletResponse response, 
    		@PathVariable("channelId") Long channelId, 
    		@PathVariable("year") String year, 
    		@PathVariable("month") String month) {
    	
        String returnXML = remoteService.getArticleListByChannelAndTime(channelId, year, month);
        print(returnXML);
    }
 
    /**
     * 文章的信息展示，并进行相关文章的动态的处理
     */
    @RequestMapping(value = "/xml/{articleId}", method = RequestMethod.GET)
    public void getArticleXmlInfo(HttpServletResponse response, @PathVariable("articleId") Long articleId) {
        String returnXML = remoteService.getArticleXML(articleId);
        if(returnXML.indexOf(("<Response>")) < 0) {
            returnXML = "<Response>" + returnXML + "</Response>";
        }
        print(returnXML);
    }
    
    /**
     * 第三方文章数据导入.
     */
    @RequestMapping(value = "/import/{channelId}", method = RequestMethod.GET)
    public void importArticle(HttpServletResponse response, HttpServletRequest request, 
    		@PathVariable("channelId") Long channelId) {
    	
    	String articleXml = request.getParameter("articleXml");
    	remoteService.importArticle(articleXml, channelId);
    }
    
    /**
     * 获取栏目树为portlet做展示
     */
    @RequestMapping(value = "/channelTree/{channelId}", method = RequestMethod.GET)
    public void getChannelTreeList4Portlet(HttpServletResponse response, @PathVariable("channelId") Long channelId) {
        print("ChannelTree", remoteService.getChannelTree4Portlet(channelId));
    }
    
    /**
     * 全文检索接口。
     * 供门户网站上通过本接口调用全文搜索。
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public void search(HttpServletResponse response, HttpServletRequest request, 
    		Long siteId, int page, int pageSize) {
    	
    	String searchStr = request.getParameter("searchStr"); 
    	try {
            if (searchStr != null) {
                // 处理非法字符
                searchStr = searchStr.replaceAll("]", "").replaceAll("[", ""); // '[' ']'在lucene里为非法字符
                searchStr = searchStr.replaceAll("}", "").replaceAll("{", ""); // '{' '}'在lucene里为非法字符
                searchStr = searchStr.replaceAll(")", "").replaceAll("(", ""); // '(' ')'在lucene里为非法字符
            }
        } catch (Exception e) {
        } 
    	
        String returnXML = remoteService.search(siteId, searchStr, page, pageSize);
        print(returnXML);
    }
    
    /**
     * 添加评论
     */
	@RequestMapping(value = "/{articleId}/comment", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
    public void addComment(HttpServletResponse response, HttpServletRequest request,
    		@PathVariable Long articleId) {
    	
    	Article article = articleService.getArticleById(articleId);
    	String comments = article.getComment();
    	
    	List<Object[]> list;
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {  
   			list = objectMapper.readValue(comments, List.class);  
   	    } 
   		catch (Exception e) {  
   			list = new ArrayList<Object[]>();
   	    } 
    	
    	String comment = request.getParameter("comment"); 
    	String commentTime = DateUtil.formatCare2Second(new Date());
		list.add(new Object[] { Environment.getUserName(), comment, commentTime });
		
		try {
			article.setCommentNum(list.size());
			comments = objectMapper.writeValueAsString(list);
		} catch (Exception e) {  
  	    }  
		article.setComment(comments);
		articleService.updateArticle(article, null, null);
    	
    	printSuccessMessage("评论成功");
    }
	
	@RequestMapping(value = "/{articleId}/comment", method = RequestMethod.GET)
	@ResponseBody
    public List<?> getComment(HttpServletRequest request, @PathVariable Long articleId) {
    	Article article = articleService.getArticleById(articleId);
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {  
    		return objectMapper.readValue(article.getComment(), List.class);  
   	    } catch (Exception e) {  
   			return new ArrayList<Object[]>();
   	    } 
    }
	
	@RequestMapping(value = "/{articleId}/comment/{index}", method = RequestMethod.DELETE)
	@SuppressWarnings("unchecked")
	public void delComment(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Long articleId, @PathVariable int index) {
		
		Article article = articleService.getArticleById(articleId);
    	String comments = article.getComment();
    	
    	List<Object[]> list;
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {  
   			list = objectMapper.readValue(comments, List.class);  
   	    } 
   		catch (Exception e) {  
   			return;
   	    } 
    	
    	if(index < list.size()) {
    		list.remove(index);
    	}
		
		try {
			comments = objectMapper.writeValueAsString(list);
		} catch (Exception e) {  
  	    }  
		article.setComment(comments);
		article.setCommentNum(list.size());
		
		articleService.updateArticle(article, null, null);
    	printSuccessMessage("删除评论成功");
    }
}