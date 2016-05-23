package com.boubei.tss.cms.service;

import com.boubei.tss.cms.AttachmentDTO;

/** 
 * CMS对外发布的服务接口。
 * 主要有：取（图片）文章列表，取文章内容，创建文章，全文检索等。
 */
public interface IRemoteArticleService {
    
    /**
     * 获取文章(包含附件)列表
     * <p>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
     *  &lt;Response&gt;<br>
     *  &lt;ArticleList&gt;<br>
     *  &lt;rss&gt;<br>
     *      &lt;channelName&gt;最新公告&lt;/channelName&gt;<br>
            &lt;totalPageNum&gt;2&lt;/totalPageNum&gt;<br>
            &lt;totalRows&gt;32&lt;/totalRows&gt;<br>
            &lt;currentPage&gt;1&lt;/currentPage&gt;<br>
            &lt;item&gt;<br>
                &lt;id&gt;21&lt;/id&gt;<br>
                &lt;title&gt;文章标题&lt;/title&gt;<br>
                &lt;author&gt;文章作者&lt;/author&gt;<br>
                &lt;issueDate&gt;2007-06-08&lt;/IssueDate&gt;<br>
                &lt;wzrq&gt;2007-06-18&lt;/wzrq&gt;<br>
                &lt;summary&gt;摘要摘要摘要摘要摘要摘要&lt;/summary&gt;<br>
                &lt;hitcount&gt;122&lt;/hitcount&gt;<br>
                &lt;Attachment type="image"&gt;
                    &lt;url&gt;&lt;![CDATA[http://localhost:8088/cms/download?id=12&seqNo=1]]&gt;&lt;/url&gt;
                &lt;/Attachment&gt;
                &lt;Attachment type="file"&gt;
                    &lt;url&gt;&lt;![CDATA[http://localhost:8088/cms/download?id=12&seqNo=2]]&gt;&lt;/url&gt;
                &lt;/Attachment&gt;
            &lt;/item&gt;<br>
        &lt;/rss&gt;<br>
        &lt;/ArticleList&gt;<br>
        &lt;/Response&gt;<br>
     * </p>
     * </p>
     * 
     * @param channelId
     * @param page
     * @param pageSize
     * @return
     */
    String getArticleListByChannel(Long channelId, int page, int pageSize, boolean isNeedPic);

    /**
     * <p>
     * 获得已发布文章生成的xml信息 
     * &lt;Response&gt;<br>
     * &lt;ArticleInfo&gt;<br>
     * &lt;rss&gt;<br>
     *   &lt;Article&gt;<br>
     *      &lt;id>&lt;![CDATA[1]]>&lt;/id&gt;
     *      &lt;title>&lt;![CDATA[文章标题]]>&lt;/title&gt;<br>
     *      &lt;author>&lt;![CDATA[文章作者]]>&lt;/author&gt;<br>
     *      &lt;keyword>&lt;![CDATA[文章关键字]]>&lt;/keyword&gt;<br>
     *      &lt;status>&lt;![CDATA[-1]]>&lt;/status&gt;<br>
     *      &lt;hitCount>&lt;![CDATA[100]]>&lt;/hitCount&gt;<br>
     *      &lt;channelId>&lt;![CDATA[2]]>&lt;/channelId&gt;<br>
     *      &lt;channelName>&lt;![CDATA[所属栏目]]>&lt;/channelName&gt;<br>
     *      &lt;type>&lt;![CDATA[2]]>&lt;/type&gt;<br>
     *      &lt;typeName>&lt;![CDATA[文章类型]]>&lt;/typeName&gt;<br>
     *      &lt;state>&lt;![CDATA[0]]>&lt;/state&gt;<br>
     *      &lt;userId>&lt;![CDATA[-1]]>&lt;/userId&gt;<br>
     *      &lt;userName>&lt;![CDATA[Admin]]>&lt;/userName&gt;<br>
     *      &lt;creationDate>&lt;![CDATA[2007-06-06]]>&lt;/creationDate&gt;<br>
     *      &lt;modifiedUserId>&lt;![CDATA[-1]]>&lt;/modifiedUserId&gt;<br>
     *      &lt;modifiedUserName>&lt;![CDATA[Admin]]>&lt;/modifiedUserName&gt;<br>
     *      &lt;modifiedDate>&lt;![CDATA[2007-06-08]]>&lt;/modifiedDate&gt;<br>
     *      &lt;issueDate>&lt;![CDATA[2007-06-08]]>&lt;/issueDate&gt;<br>
     *      &lt;wzrq>&lt;![CDATA[2007-06-06]]>&lt;/wzrq&gt;<br>
     *      &lt;summary>&lt;![摘要摘要摘要摘要摘要摘要]]>&lt;/summary&gt;<br>
     *      &lt;content>&lt;![CDATA[正文正文正文正文正文正文正文正文]]>&lt;/content&gt;<br>
     *   &lt;/Article&lt;<br>
     * &lt;/rss&gt;<br>
     * &lt;/ArticleInfo&gt;<br>
     * &lt;/Response&gt;<br>
     * </p>
     * @param articleId
     * @return
     */
    public String getArticleXML(Long articleId);
    
    /**
     * <p>
     *  第三方文章数据导入，文章内容信息的XML格式如下：<br>
     *  &lt;ArticleInfo&gt;<br>
     *      &lt;Article&gt;<br>
     *      &lt;title>&lt;![CDATA[文章一]]>&lt;/title&gt;<br>
     *      &lt;author>&lt;![CDATA[斯蒂芬]]>&lt;/author&gt;<br>
     *      &lt;keyword>&lt;![CDATA[公认的]]>&lt;/keyword&gt;<br>
     *      &lt;status>&lt;![CDATA[-1]]>&lt;/status&gt;<br>
     *      &lt;type>&lt;![CDATA[2]]>&lt;/type&gt;<br>
     *      &lt;typeName>&lt;![CDATA[报表]]>&lt;/typeName&gt;<br>
     *      &lt;wzrq>&lt;![CDATA[2007-06-06]]>&lt;/wzrq&gt;<br>
     *      &lt;content>&lt;![CDATA[正文正文正文正文正文正文正文正文]]>&lt;/content&gt;<br>
     *      &lt;/Article&lt;<br>
     * &lt;/ArticleInfo&gt;<br>
     * </p>
     * @param articleXml
     */
    void importArticle(String articleXml, Long channelId);

    /**
     * 根据栏目ids，获取这些栏目下的所有文章列表。
     * 注：因为本方法调用dao时里需要往临时表temp里写入数据，所以方法名不能以get开头
     * 
     * @param channelIds
     * @param page
     * @param pageSize
     * @return 
     *        返回数据格式同getArticleListXMLByChannel方法
     */
    String queryArticlesByChannelIds(String channelIds, int page, int pageSize);

    /**
     * 根据栏目id获取文章列表(深度)，取指定栏目以及该栏目下所有子栏目的所有文章列表
     * 
     * @param channelId
     * @param page
     * @param pageSize
     * @return 
     *        返回数据格式同getArticleListXMLByChannel方法
     */
    String queryArticlesDeeplyByChannelId(Long channelId, int page, int pageSize);
    
    /**
     * 获取栏目树，用以显示“当前位置”等地方
     * @param channelId
     * @return
     */
    String getChannelTree4Portlet(Long channelId);
    
    /**
     * 全文检索service接口。
     * 供门户网站上通过本接口调用全文搜索。
     * <p>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
     *  &lt;Response&gt;<br>
     *  &lt;ArticleList&gt;<br>
     *  &lt;rss&gt;<br>
            &lt;totalPageNum&gt;2&lt;/totalPageNum&gt;<br>
            &lt;totalRows&gt;32&lt;/totalRows&gt;<br>
            &lt;currentPage&gt;1&lt;/currentPage&gt;<br>
            &lt;item&gt;<br>
                &lt;id&gt;21&lt;/id&gt;<br>
                &lt;title&gt;文章标题&lt;/title&gt;<br>
                &lt;author&gt;文章作者&lt;/author&gt;<br>
                &lt;issueDate&gt;2007-06-08&lt;/IssueDate&gt;<br>
                &lt;wzrq&gt;2007-06-18&lt;/wzrq&gt;<br>
                &lt;summary&gt;摘要摘要摘要摘要摘要摘要&lt;/summary&gt;<br>
                &lt;hitcount&gt;122&lt;/hitcount&gt;<br>
            &lt;/item&gt;<br>
        &lt;/rss&gt;<br>
        &lt;/ArticleList&gt;<br>
        &lt;/Response&gt;<br>
     * </p>
     * 
     * @param siteId
     * @param searchStr
     * @param pageNum
     * @param pageSize
     * @return
     */
    String search(Long siteId, String searchStr, int pageNum, int pageSize);
    
    /**
     * 获取附件信息。附件下载时候使用，由DownloadServlet调用。
     * @param articleId
     * @param seqNo
     * @return
     */
    AttachmentDTO getAttachmentInfo(Long articleId, int seqNo);
    
    /**
     * 根据栏目和日期来获取文章列表。
     * 主要用于期刊类需求。
     * <p>
     *  &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
     *  &lt;Response&gt;<br>
     *  &lt;ArticleList&gt;<br>
     *  &lt;rss&gt;<br>
     *      &lt;channelName&gt;最新公告&lt;/channelName&gt;<br>
            &lt;totalPageNum&gt;2&lt;/totalPageNum&gt;<br>
            &lt;totalRows&gt;32&lt;/totalRows&gt;<br>
            &lt;currentPage&gt;1&lt;/currentPage&gt;<br>
            &lt;item&gt;<br>
                &lt;id&gt;21&lt;/id&gt;<br>
                &lt;title&gt;文章标题&lt;/title&gt;<br>
                &lt;author&gt;文章作者&lt;/author&gt;<br>
                &lt;issueDate&gt;2007-06-08&lt;/IssueDate&gt;<br>
                &lt;wzrq&gt;2007-06-18&lt;/wzrq&gt;<br>
                &lt;summary&gt;摘要摘要摘要摘要摘要摘要&lt;/summary&gt;<br>
                &lt;hitcount&gt;122&lt;/hitcount&gt;<br>
            &lt;/item&gt;<br>
        &lt;/rss&gt;<br>
        &lt;/ArticleList&gt;<br>
        &lt;/Response&gt;<br>
     * </p>
     * @param channelId
     * @param year
     * @param month
     * @return
     */
    String getArticleListByChannelAndTime(Long channelId, String year, String month);
}

