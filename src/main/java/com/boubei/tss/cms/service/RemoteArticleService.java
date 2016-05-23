package com.boubei.tss.cms.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.cms.AttachmentDTO;
import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.cms.dao.IArticleDao;
import com.boubei.tss.cms.dao.IChannelDao;
import com.boubei.tss.cms.entity.Article;
import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.entity.Channel;
import com.boubei.tss.cms.helper.ArticleHelper;
import com.boubei.tss.cms.helper.ArticleQueryCondition;
import com.boubei.tss.cms.helper.HitRateManager;
import com.boubei.tss.cms.job.JobStrategy;
import com.boubei.tss.cms.lucene.executor.IndexExecutorFactory;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.persistence.pagequery.PageInfo;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.web.dispaly.tree.LevelTreeParser;
import com.boubei.tss.framework.web.dispaly.tree.TreeEncoder;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.XMLDocUtil;
import com.boubei.tss.util.XmlUtil;

@Service("RemoteArticleService")
public class RemoteArticleService implements IRemoteArticleService {
    
    protected Logger log = Logger.getLogger(this.getClass());

    @Autowired protected IArticleDao articleDao;
    @Autowired protected IChannelDao channelDao;
 
    public String getArticleListByChannel(Long channelId, int page, int pageSize, boolean isNeedPic) {
        Channel channel = channelDao.getEntity(channelId);
        if(channel == null) {
        	log.error("ID为：" + channelId + " 的栏目不存在！");
            return "<Response><ArticleList><rss><totalPageNum>0</totalPageNum><totalRows>0</totalRows></rss></ArticleList></Response>";
        }
        
        if( !channelDao.checkBrowsePermission(channelId) ) {
            log.error("用户【" + Environment.getUserCode() + "】试图访问没有文章浏览权限的栏目【" + channelId + "】");
            channelId = channelId * -1; // 置反channelId值，使查询不到结果
        }
        
        ArticleQueryCondition condition = new ArticleQueryCondition();
        condition.setChannelId(channelId);
        condition.getPage().setPageNum(page);
        condition.getPage().setPageSize(pageSize);
        condition.setStatus(CMSConstants.XML_STATUS);
        
        PageInfo pageInfo = articleDao.getChannelPageArticleList(condition);
            
        Document doc = org.dom4j.DocumentHelper.createDocument();
        Element channelElement = doc.addElement("rss").addAttribute("version", "2.0");
        
        channelElement.addElement("channelName").setText(channel.getName()); 
        channelElement.addElement("totalPageNum").setText(String.valueOf(pageInfo.getTotalPages()));
        channelElement.addElement("totalRows").setText(String.valueOf(pageInfo.getTotalRows()));
        channelElement.addElement("currentPage").setText(page + "");
        List<?> articleList = pageInfo.getItems();
        if (articleList != null) {
            for (int i = 0; i < articleList.size(); i++) {
                Object[] fields = (Object[]) articleList.get(i);
                Element itemElement = createArticleElement(channelElement, fields);
                
                if(isNeedPic){
                	Long articleId = (Long) fields[0];
                	List<Attachment> attachments = articleDao.getArticleAttachments(articleId);
                    ArticleHelper.addPicListInfo(itemElement, attachments);
                }
            }
        }
        return "<Response><ArticleList>" + channelElement.asXML() + "</ArticleList></Response>";
    }

    public String queryArticlesByChannelIds(String channelIdStr, int page, int pageSize){
        ArticleQueryCondition condition = new ArticleQueryCondition();
        condition.getPage().setPageNum(page);
        condition.getPage().setPageSize(pageSize);
        condition.setStatus(CMSConstants.XML_STATUS);
        
        List<Long> channelIds = new ArrayList<Long>();
        String[] strIdArray = channelIdStr.split(",");
        for( String temp : strIdArray) {
            Long channelId = Long.valueOf(temp);
            if( channelDao.checkBrowsePermission(channelId) ) {
                channelIds.add(channelId);
            }
        }
        condition.setChannelIds(channelIds);
        
        PageInfo pageInfo = articleDao.getArticlesByChannelIds(condition);
        return "<Response><ArticleList>" + createReturnXML(pageInfo, channelIds.get(0)) + "</ArticleList></Response>";
    }
    
    public String queryArticlesDeeplyByChannelId(Long channelId, int page, int pageSize){
        ArticleQueryCondition condition = new ArticleQueryCondition();
        condition.getPage().setPageNum(page);
        condition.getPage().setPageSize(pageSize);
        condition.setStatus(CMSConstants.XML_STATUS);
        
        List<Channel> subChannels = channelDao.getChildrenById(channelId, CMSConstants.OPERATION_VIEW);
        List<Long> channelIds = new ArrayList<Long>();
        for(Channel temp : subChannels) {
            if( channelDao.checkBrowsePermission(temp.getId()) ) {
                channelIds.add(temp.getId());
            }
        }
        condition.setChannelIds(channelIds);
        
        PageInfo pageInfo = articleDao.getArticlesByChannelIds(condition);
        return "<Response><ArticleList>" + createReturnXML(pageInfo, channelId) + "</ArticleList></Response>";
    }
    
    private String createReturnXML(PageInfo pageInfo, Long channelId){
        Channel channel = channelDao.getEntity(channelId);
        List<?> articleList = pageInfo.getItems();
        
        Document doc = DocumentHelper.createDocument();
        Element channelElement = doc.addElement("rss").addAttribute("version", "2.0");
        
        channelElement.addElement("channelName").setText(channel == null ? "栏目" : channel.getName()); //多个栏目一起查找，取第一个栏目
        channelElement.addElement("totalRows").setText(String.valueOf(pageInfo.getTotalRows()));
        channelElement.addElement("totalPageNum").setText(String.valueOf(pageInfo.getTotalPages()));
        channelElement.addElement("currentPage").setText(String.valueOf(pageInfo.getPageNum()));
        if (articleList != null) {
            for (int i = 0; i < articleList.size(); i++) {
                Object[] fields = (Object[]) articleList.get(i);
                Element itemElement = createArticleElement(channelElement, fields);
                
                Long articleId = (Long) fields[0];
                List<Attachment> attachments = articleDao.getArticleAttachments(articleId);
                ArticleHelper.addPicListInfo(itemElement, attachments);
            }
        }
        return channelElement.asXML();
    }
    
    // fields : a.id, a.title, a.author, a.summary, a.issueDate, a.createTime, a.hitCount, a.isTop, a.commentNum
    private Element createArticleElement(Element channelElement, Object[] fields) {
		return createArticleElement(channelElement, (Long) fields[0],
				(String) fields[1], (String) fields[2], (Date) fields[4],
				(String) fields[3], (Integer) fields[6], (Integer) fields[8]);
    }
    
    private Element createArticleElement(Element channelElement, 
    		Object articleId, String title, String author, Date issueDate, String summary, 
    		int hitCount, int commentNum) {
        
        Element itemElement = channelElement.addElement("item");
        itemElement.addElement("id").setText(EasyUtils.obj2String(articleId));
        itemElement.addElement("title").setText(EasyUtils.obj2String(title));
        itemElement.addElement("author").setText(EasyUtils.obj2String(author));
        itemElement.addElement("issueDate").setText(DateUtil.format(issueDate));
        itemElement.addElement("summary").setText(EasyUtils.obj2String(summary));
        itemElement.addElement("hitCount").setText(EasyUtils.obj2String(hitCount));
        itemElement.addElement("commentNum").setText(EasyUtils.obj2String(commentNum));
        
        return itemElement;
    }
    
    public String getChannelTree4Portlet(Long channelId) {
        List<Channel> list = channelDao.getChildrenById(channelId, CMSConstants.OPERATION_VIEW);
        TreeEncoder encoder = new TreeEncoder(list, new LevelTreeParser());
        encoder.setNeedRootNode(false);
        return encoder.toXml();
    }
    
    public String getArticleXML(Long articleId) {
        Article article = articleDao.getEntity(articleId);
        if(article == null) return "";
        
        Document articleDoc;
        String pubUrl = article.getPubUrl();
        try{
            articleDoc = XMLDocUtil.createDocByAbsolutePath(pubUrl);
        } catch(Exception e){
            String fileContent = FileHelper.readFile(new File(pubUrl), "UTF-8");
            articleDoc = XMLDocUtil.dataXml2Doc(XmlUtil.stripNonValidXMLCharacters(fileContent));
        }
        Element articleElement = articleDoc.getRootElement();
        Element hitRateNode = (Element) articleElement.selectSingleNode("//hitCount");
        hitRateNode.setText(article.getHitCount().toString()); // 更新点击率
        
        Document doc = org.dom4j.DocumentHelper.createDocument();
        Element articleInfoElement = doc.addElement("Response").addElement("ArticleInfo");
        articleInfoElement.addElement("rss").addAttribute("version", "2.0").add(articleElement);

        //添加文章点击率;
        HitRateManager.getInstanse().output(articleId);
        
        return doc.asXML();
    }

    public void importArticle(String articleXml, Long channelId) {
        Document doc = XMLDocUtil.dataXml2Doc(articleXml);
        Element articleNode = (Element) doc.selectSingleNode("//ArticleInfo/Article");
        Article article = new Article();
        BeanUtil.setDataToBean(article, XMLDocUtil.dataNodes2Map(articleNode));
        
        Channel channel = channelDao.getEntity(channelId);
        article.setChannel(channel);
         
        //设置过期时间
        article.setOverdueDate(ArticleHelper.calculateOverDate(channel));
        
        articleDao.saveArticle(article);
    }

    public String search(Long siteId, String searchStr, int page, int pageSize) {
        JobStrategy js = JobStrategy.getIndexStrategy();
        js.site = channelDao.getEntity(siteId);
        
        String indexPath = js.getIndexPath();
        if (!new File(indexPath).exists() || searchStr == null || "".equals(searchStr.trim())) {
            return "<Response><ArticleList><rss version=\"2.0\"><channel/></rss></ArticleList></Response>";
        }
        
        org.dom4j.Document doc = DocumentHelper.createDocument();
        
        // 生成rss格式的xml文件的Head部分
        Element channelElement = doc.addElement("rss").addAttribute("version", "2.0");
        try {
            IndexSearcher searcher = new IndexSearcher(indexPath);
            Query query = IndexExecutorFactory.create(js.executorClass).createIndexQuery(searchStr);
            Hits hits = searcher.search(query, new Sort(new SortField("createTime", SortField.STRING, true))); // 按创建时间排序
            
            // 先遍历一边查询结果集，对其权限进行过滤，将过滤后的结果集放入到一个临时list中。
            List<org.apache.lucene.document.Document> list = new ArrayList<org.apache.lucene.document.Document>();
            for (Iterator<?> it = hits.iterator(); it.hasNext(); ) {
                Hit hit = (Hit) it.next();
                list.add(hit.getDocument());
            }

            int totalRows = list.size();
            int totalPage = totalRows % pageSize != 0 ? totalRows / pageSize + 1 : totalRows / pageSize;

            channelElement.addElement("totalPageNum").setText(String.valueOf(totalPage));
            channelElement.addElement("totalRows").setText(totalRows + "");
            channelElement.addElement("currentPage").setText(page + "");
            for (int i = (page - 1) * pageSize; i < totalRows && i < page * pageSize; i++) {
                org.apache.lucene.document.Document document = list.get(i);
                
                // 生成rss格式的xml文件的搜索出来的内容
                Object articleId = document.get("id");
                Date issueDate = document.get("issueDate") == null ? null : DateUtil.parse(document.get("issueDate"));
                createArticleElement(channelElement, articleId, document.get("title"), document.get("author"), 
                        issueDate, document.get("summary"), 0, 0);
            }
            searcher.close();
        } catch (Exception e) {
            throw new BusinessException("搜索出错!", e);
        } 
        return "<Response><ArticleList>" + channelElement.asXML() + "</ArticleList></Response>";
    }
 
    public AttachmentDTO getAttachmentInfo(Long articleId, int seqNo) {
        Attachment attach = articleDao.getAttachment(articleId, seqNo);
        if (attach == null) {
            log.error("数据库中没有相应的附件信息！文章ID：" + articleId + ", 序号：" + seqNo);
            return null;
        }
        
        // 通过文章id获取栏目id
        Article article = attach.getArticle();
        if(article == null) {
        	log.error("附件：" + attach + " 对应的文章为空，可能已被删除了。 articleId = " + articleId);
            return null;
        }
        
		Channel channel = article.getChannel();
		if(channel == null) {
			log.error("附件：" + attach + " 对应的文章所在的栏目为空，可能已被删除了。 articleId = " + articleId);
            return null;
        }
		Channel site = channel.getSite(); 
        
        AttachmentDTO dto = new AttachmentDTO(attach.getType(), attach.getName(), attach.getFileName(), attach.getFileExt(),
                attach.getLocalPath(), new String[]{site.getPath(), site.getImagePath(), site.getDocPath()});
        
        return dto;
    }
    
    public String getArticleListByChannelAndTime(Long channelId, String year, String month) {
        if(channelId == null){
            throw new BusinessException("栏目ID不能为空!");
        }
        if(year == null || month == null){
            throw new BusinessException("年度或月份不能为空!");
        }
        
        Channel channel = channelDao.getEntity(channelId);
        if(channel == null) {
            throw new BusinessException("栏目不存在!");
        }
        Channel site = channel.getSite();
        String publishBaseDir = site.getPath();
       
        month = (month.length() == 1 ? "0" + month : month);
        String publishDir = publishBaseDir + "/" + year + "/" + month;
        List<File> xmlFiles = FileHelper.listFilesByTypeDeeply(".xml", new File(publishDir));
      
        Document doc = org.dom4j.DocumentHelper.createDocument();
        Element channelElement = doc.addElement("rss").addAttribute("version", "2.0");
 
        channelElement.addElement("channelName").setText(channel.getName()); 
        channelElement.addElement("totalPageNum").setText("1");
        channelElement.addElement("totalRows").setText("100");
        channelElement.addElement("currentPage").setText("1");
        for( File xmlFile : xmlFiles ){
            if(xmlFile.getName().startsWith(channelId + "_")){
                Document articleDoc;
                try{
                    articleDoc = XMLDocUtil.createDocByAbsolutePath(xmlFile.getPath());
                } 
                catch(Exception e){
                    String fileContent = FileHelper.readFile(xmlFile, "UTF-8");
                    articleDoc = XMLDocUtil.dataXml2Doc(XmlUtil.stripNonValidXMLCharacters(fileContent));
                }
                
                Node articleNode   = articleDoc.getRootElement();
                Node idNode        = articleNode.selectSingleNode("//id");
                Node titleNode     = articleNode.selectSingleNode("//title");
                Node authorNode    = articleNode.selectSingleNode("//author");
                Node summaryNode   = articleNode.selectSingleNode("//summary");
                Node issueDateNode = articleNode.selectSingleNode("//issueDate");
                
                createArticleElement(channelElement,
                		getText(idNode), 
                		getText(titleNode), 
                		getText(authorNode),
                		DateUtil.parse(getText(issueDateNode)), 
                		getText(summaryNode), 
                        0, 0);
            }
        }
        return "<Response><ArticleList>" + channelElement.asXML() + "</ArticleList></Response>";
    }
    
    private String getText(Node node) {
    	return node == null ? null : node.getText();
    }
}
