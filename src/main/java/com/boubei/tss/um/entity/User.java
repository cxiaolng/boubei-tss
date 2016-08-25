package com.boubei.tss.um.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.boubei.tss.framework.persistence.entityaop.OperateInfo;
import com.boubei.tss.framework.web.dispaly.grid.GridAttributesMap;
import com.boubei.tss.framework.web.dispaly.grid.IGridNode;
import com.boubei.tss.framework.web.dispaly.tree.ITreeNode;
import com.boubei.tss.framework.web.dispaly.tree.TreeAttributesMap;
import com.boubei.tss.framework.web.dispaly.xform.IXForm;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.sso.UMPasswordIdentifier;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.InfoEncoder;

/**
 * 用户域对象
 */
@Entity
@Table(name = "um_user", uniqueConstraints = { 
        @UniqueConstraint(columnNames = { "loginName" })
})
@SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", initialValue = 10000, allocationSize = 10)
public class User extends OperateInfo implements ITreeNode, IGridNode, IXForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "user_sequence")
    private Long   id;              // 用户ID：用户主键id号
    
    @Column(length = 50, nullable = false)  
    private String loginName;       // 登陆系统帐号
    private String userName;        // 姓名
    private String employeeNo;      // 员工编号
    private String sex;             // 性别
    private Date   birthday;        // 出生年月
    private String email;           // 邮件 
    private String telephone;       // 联系电话 
    private String address;         // 地址 
    private String postalCode;      // 邮编 
    private String certificateNo;   // 证件号
    private String certificate;     // 证件种类 :  1：工作证  2：身份证等
    private Date   accountLife;     // 帐户有效期限 ：用户帐户到某个指顶的期限过期
    
    private String  password;         // 密码 
    private String  passwordQuestion; // 密码提示问题 
    private String  passwordAnswer;   // 密码提示答案 
    private Integer passwordStrength; // 密码强度
    
    private String authMethod = UMPasswordIdentifier.class.getName(); // 认证方式,一个实现对应的认证方式的类路径
    
    private String fromUserId;  // 外部应用系统用户的ID (用于【平台用户】对应【其他系统用户】，其值可以是LDAP里的DN字符串)
    
    private Integer disabled = ParamConstants.FALSE; // 帐户状态, 帐户状态(0-停用, 1-启用)
    
    private Integer pwdErrorCount; // 10分钟内密码连续输错次数
    private Date lastPwdErrorTime; // 最后一次密码输错的时间
    
    // 以下值展示的时候用
    @Transient private Long   groupId;         // 用户所在组id
    @Transient private String groupName;       // 用户所在组名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
 
    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer accountState) {
        this.disabled = accountState;
    }
 
    public Date getAccountLife() {
        return accountLife;
    }
 
    public void setAccountLife(Date accountLife) {
        this.accountLife = accountLife;
    }
 
    public String getAddress() {
        return address;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
 
    public String getAuthMethod() {
        return authMethod;
    }
 
    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }
 
    public Date getBirthday() {
        return birthday;
    }
 
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
 
    public String getCertificate() {
        return certificate;
    }
 
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
 
    public String getCertificateNo() {
        return certificateNo;
    }
 
    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }
 
    public String getEmployeeNo() {
        return employeeNo;
    }
 
    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }
 
    public String getLoginName() {
        return loginName;
    }
 
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getFromUserId() {
        return fromUserId;
    }
 
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public String getPasswordAnswer() {
        return passwordAnswer;
    }
 
    public void setPasswordAnswer(String passwordAnswer) {
        this.passwordAnswer = passwordAnswer;
    }
 
    public String getPasswordQuestion() {
        return passwordQuestion;
    }
 
    public void setPasswordQuestion(String passwordQuestion) {
        this.passwordQuestion = passwordQuestion;
    }
    
	public Integer getPasswordStrength() {
		return passwordStrength;
	}

	public void setPasswordStrength(Integer passwordStrength) {
		this.passwordStrength = passwordStrength;
	}
 
    public String getPostalCode() {
        return postalCode;
    }
 
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
 
    public String getSex() {
        return sex;
    }
 
    public void setSex(String sex) {
        this.sex = sex;
    }
 
    public String getTelephone() {
        return telephone;
    }
 
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
 
    public String getUserName() {
        return userName;
    }
 
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String encodePassword(String password) {
    	return encodePasswd(this.getLoginName(), password);
    }
    
    /** 对用户密码进行加密 */
    public static String encodePasswd(String loginName, String password) {
    	return InfoEncoder.string2MD5(loginName + "_" + password);
    }
 
    public GridAttributesMap getAttributes(GridAttributesMap map) {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, properties);
        map.putAll(properties);
        
        map.put("icon", UMConstants.USER_GRID_NODE_ICON + disabled + ".gif");
       
        return map;
    }
 
    public Map<String, Object> getAttributes4XForm() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        BeanUtil.addBeanProperties2Map(this, map);
        
        map.put("birthday", DateUtil.format(birthday));
        map.put("accountLife", DateUtil.format(accountLife));
        
        return map;
    }
 
    public TreeAttributesMap getAttributes() {
        TreeAttributesMap map = new TreeAttributesMap(id, userName);
        map.put("groupId", groupId);
        return map;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
 
    public String toString(){
        return "(ID:" + this.id + ", loginName:" + this.loginName + ")"; 
    }
    
	public Serializable getPK() {
		return this.id;
	}

	public Integer getPwdErrorCount() {
		return pwdErrorCount == null ? 0 : pwdErrorCount;
	}

	public void setPwdErrorCount(Integer pwdErrorCount) {
		this.pwdErrorCount = pwdErrorCount;
	}

	public Date getLastPwdErrorTime() {
		return lastPwdErrorTime;
	}

	public void setLastPwdErrorTime(Date lastPwdErrorTime) {
		this.lastPwdErrorTime = lastPwdErrorTime;
	}
}
