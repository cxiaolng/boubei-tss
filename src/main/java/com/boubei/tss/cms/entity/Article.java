package com.boubei.tss.cms.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.boubei.tss.cms.CMSConstants;
import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.dispaly.grid.GridAttributesMap;
import com.boubei.tss.framework.web.dispaly.grid.IGridNode;
import com.boubei.tss.framework.web.dispaly.xform.IXForm;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.DateUtil;

/** 
 * <p>文章Article实体对象</p>
 */
@Entity
@Table(name = "cms_article")
@SequenceGenerator(name = "article_sequence", sequenceName = "article_sequence", initialValue = 1, allocationSize = 10)
public class Article extends OperateInfo implements IGridNode, IXForm {
    
	public static final String[] IGNORE_PROPERTIES = new String[] { "id", "status", "hitCount", "issueDate" };
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "article_sequence")
    private Long   id;			//文章编号 PK
	
	@Column(nullable = false)
    private String title;		//标题
    private String subtitle;	//副标题
    private String keyword;		//关键字
    private String author;		//作者
    private String summary;		//摘要 
    
    @Column(length = 4000)
    private String  content = ""; //正文内容  
    
    @Lob    
    private String  comment;	  // 评论  
    private int commentNum = 0;   // 评论次数
    
    private Date    overdueDate;  // 过期时间
    private Date    issueDate;	  // 发布日期	开始为空,只有审核和发布阶段可以改
    private String  pubUrl;       // 发布路径
    
    private Integer hitCount = 0; //点击率
	
    private Integer status = CMSConstants.START_STATUS;	   // 文章的状态 1：编辑中 2：待发布 3：已发布生成xml 4：过期 
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Channel channel;      // 文章所属栏目
    
    private Integer isTop = ParamConstants.FALSE;   // 文章是否置顶
    
    @Transient 
    List<Attachment> attachments = new ArrayList<Attachment>();  // 存放文章附件列表
 
    public String getAuthor() {
        return author;
    }
 
    public void setAuthor(String author) {
        this.author = author;
    }
 
    public String getContent() {
        return content;
    }
 
    public void setContent(String content) {
        this.content = content != null ? content : "";
    }
 
    public Integer getHitCount() {
        return hitCount;
    }
 
    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Date getIssueDate() {
        return issueDate;
    }
 
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
 
    public String getKeyword() {
        return keyword;
    }
 
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getStatus() {
        return status;
    }
 
    public void setStatus(Integer status) {
        this.status = status;
    }
 
    public String getSubtitle() {
        return subtitle;
    }
 
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
 
    public String getSummary() {
        return summary;
    }
 
    public void setSummary(String summary) {
        this.summary = summary;
    }
 
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }
 
	public String getPubUrl() {
		return pubUrl;
	}
 
	public void setPubUrl(String pubUrl) {
		this.pubUrl = pubUrl;
	}
 
	public Date getOverdueDate() {
		return overdueDate;
	}
 
	public void setOverdueDate(Date overdueDate) {
		this.overdueDate = overdueDate;
	}
    
    public GridAttributesMap getAttributes(GridAttributesMap map) {
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, attributes, "channel");
        map.putAll(attributes);
        map.put("channel.id", channel.getId());
        map.put("channel.name", channel.getName());
        
        boolean overdue = overdueDate != null && overdueDate.before(new Date());
        map.put("icon", "images/article_" + (overdue ? 1 : 0) + ".gif");
        
        return map;
    }

    public Map<String, Object> getAttributes4XForm() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, map, "channel");
        map.put("id", id);
        map.put("channel.id", channel.getId());
        
        map.put("createTime", DateUtil.format(createTime));
        if(overdueDate != null) {
        	map.put("overdueDate", DateUtil.format(overdueDate));
        }
        if(issueDate != null) {
        	map.put("issueDate", DateUtil.format(issueDate));
        }
        
        return map;
    }
 
    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    public String toString(){
        return "(id:" + this.id + ", title:" + this.title + ")"; 
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public Serializable getPK() {
		return this.id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
}