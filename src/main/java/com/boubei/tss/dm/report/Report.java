package com.boubei.tss.dm.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.report.permission.ReportResource;
import com.boubei.tss.framework.persistence.entityaop.IDecodable;
import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.dispaly.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.dispaly.xform.IXForm;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.permission.IResource;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;

@Entity
@Table(name = "dm_report")
@SequenceGenerator(name = "report_sequence", sequenceName = "report_sequence", initialValue = 1, allocationSize = 10)
public class Report extends OperateInfo implements IXForm, IDecodable, IResource {
    
	public static final int TYPE0 = 0;  // 报表分组
	public static final int TYPE1 = 1;  // 业务报表
    
	public static final Long DEFAULT_PARENT_ID = 0L;
    
    // 资源类型： 报表
    public static final String RESOURCE_TYPE = "D1"; 
    
    // 报表资源操作ID
    public static final String OPERATION_VIEW    = "1"; // 查看
    public static final String OPERATION_EDIT    = "2"; // 维护
    public static final String OPERATION_DELETE  = "3"; // 删除
    public static final String OPERATION_DISABLE = "4"; // 停用启用
    
    public Report() { }
    
    public Report(Long groupId, String groupName, Long parentId) {
    	this.id = groupId;
    	this.name = groupName;
    	this.type = TYPE0;
    	this.parentId = parentId == null ? DEFAULT_PARENT_ID : parentId;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "report_sequence")
    private Long    id;         // 主键
    
    @Column(length = 100, nullable = false)
    private String  name;       // 展示名称
    
    @Lob
    private String  script;     // SQL
    
    @Column(length = 2000)  
    private String  param;      // 参数配置
    
    private String  datasource; // 单独为报表指定数据源
    private String  displayUri; // 用来展示当前报表的模板页面的路径
    
    @Column(nullable = false)
    private Integer type;  // 种类  0：报表分组 1: 业务报表
    
    @Column(length = 2000)
    private String  remark; 
    
    private Long    parentId;  // 父节点
    private Integer seqNo;    // 排序号
    private String  decode;  // 层码，要求唯一
    private Integer levelNo;// 层次值
    
    private Integer disabled = ParamConstants.TRUE; // 停用/启用标记。默认为停用，开发完成后再启用
    private Integer needLog  = ParamConstants.TRUE; // 记录修改日志
    
    public Integer getNeedLog() {
		return needLog;
	}

	public void setNeedLog(Integer needLog) {
		this.needLog = needLog;
	}

	public String toString() {
        return "【" + this.id + ", " + this.name + ", " + this.creatorName + ", " + this.updatorName + "】";
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getParam() {
        return param;
    }
    public void setParam(String param) {
        this.param = param;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public Integer getSeqNo() {
        return seqNo;
    }
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }
    public String getDecode() {
        return decode;
    }
    public void setDecode(String decode) {
        this.decode = decode;
    }
    public Integer getLevelNo() {
        return levelNo;
    }
    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
    }
    public Integer getDisabled() {
        return disabled;
    }
    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getScript() {
        return script;
    }
    public void setScript(String script) {
        this.script = script;
    }
    public String getDatasource() {
        return DMConstants.getDS(datasource);
    }
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(id, name);;
       
        String icon_path;
        if ( isGroup() ) {
            icon_path = "images/reportGroup.gif";
        } 
        else {
            icon_path = "images/report_" + disabled + ".gif";
        } 
        map.put("icon", icon_path);
        map.put("parentId", parentId);
        map.put("disabled", disabled);
        map.put("type", type);
        if(TYPE1 == type) {
            map.put("param", param);
            map.put("displayUri", displayUri);
            map.put("hasScript", EasyUtils.isNullOrEmpty(this.script) ? "false" : "true" );
        }
        
        if( this.id.longValue() < 0 || (this.levelNo < 2 && isActive()) ) {
        	map.put("_open", "true");
        }
 
        return map;
    }

    public Map<String, Object> getAttributes4XForm() {
        Map<String, Object> map = new HashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, map);
        return map;
    }

    public Class<?> getParentClass() {
        if(this.parentId.equals(DEFAULT_PARENT_ID)) {
            return ReportResource.class;
        }
        return this.getClass();
    }
    
    public boolean isGroup() {
    	return TYPE0 == this.type;
    }
    
    public boolean isActive() {
    	return !ParamConstants.TRUE.equals(this.getDisabled());
    }

	public String getResourceType() {
		return RESOURCE_TYPE;
	}

	public String getDisplayUri() {
		return displayUri;
	}

	public void setDisplayUri(String displayUri) {
		this.displayUri = displayUri;
	}
	
	public Serializable getPK() {
		return this.getId();
	}
}
