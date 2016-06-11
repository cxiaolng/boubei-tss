package com.boubei.tss.um.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.IOperator;
import com.boubei.tss.framework.sso.context.Context;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.dao.IGroupDao;
import com.boubei.tss.um.dao.IUserDao;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.entity.User;
import com.boubei.tss.um.helper.PasswordRule;
import com.boubei.tss.um.helper.dto.GroupDTO;
import com.boubei.tss.um.helper.dto.OperatorDTO;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.BeanUtil;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.InfoEncoder;
import com.boubei.tss.util.MacrocodeCompiler;
import com.boubei.tss.util.MathUtil;

/**
 * <p>
 * 用户登录系统相关业务逻辑处理接口：
 * <li>根据用户登录名获取用户名及认证方式信息等；
 * <li>根据用户ID获取用户信息；
 * <li>根据用户登录名获取用户信息；
 * </p>
 */
@Service("LoginService")
public class LoginService implements ILoginService {
	
	protected Logger log = Logger.getLogger(this.getClass());

	@Autowired private IUserDao userDao;
	@Autowired private IGroupDao groupDao;
	
	public int checkPwdErrorCount(String loginName) {
		User user = getUserByLoginName(loginName);
		int count = user.getPwdErrorCount();
	    
	    // 离最后一次输错密码已经超过十分钟了，则统计次数重新清零
		Date lastPwdErrorTime = user.getLastPwdErrorTime();
	    if(lastPwdErrorTime == null 
	    		|| System.currentTimeMillis() - lastPwdErrorTime.getTime() > 10*60*1000) {
	    	count = 0;
	    }
	    
		if( count >= 10) {
			throw new BusinessException("您的账号已被锁定，因连续输错密码超过10次，请在10分钟后再尝试登陆。");
		}
		return count;
	}
	
	public void recordPwdErrorCount(String loginName, int currCount) {
		User user = getUserByLoginName(loginName);

		currCount ++;
	    if(currCount >= 10) {
	    	log.info("【" + loginName + "】已连续【" +currCount+ "】次输错密码。");
	    }
	    
	    user.setLastPwdErrorTime(new Date());
    	user.setPwdErrorCount(currCount);
    	userDao.refreshEntity(user);
	}
	
	public Object resetPassword(Long userId, String passwd) {
		User user = userDao.getEntity(userId);
		String token = InfoEncoder.simpleEncode(userId + "_" + passwd, MathUtil.randomInt(12));
        if(user == null)  return token;
        
    	String md5Password = user.encodePassword(passwd);
    	user.setPassword( md5Password );
    	
    	// 计算用户的密码强度，必要的时候强制用户重新设置密码
    	int strengthLevel = PasswordRule.getStrengthLevel(passwd, user.getLoginName());
    	user.setPasswordStrength(strengthLevel);
    	
    	if(Context.isOnline()) {
    		IOperator operator = Context.getIdentityCard().getOperator();
    		operator.getAttributesMap().put("passwordStrength", strengthLevel);
    	}
    	
    	userDao.refreshEntity(user);
    	return token;
	}

	public String[] getLoginInfoByLoginName(String loginName) {
		User user = getUserByLoginName(loginName);
		return new String[] { user.getUserName(), user.getAuthMethod() };
	}
	
	private User getUserByLoginName(String loginName) {
        User user = userDao.getUserByLoginName(loginName);
        if (user == null) {
            throw new BusinessException("此帐号不存在." + loginName);
        } 
        else if (ParamConstants.TRUE.equals(user.getDisabled())) {
            throw new BusinessException("此帐号(" + loginName + ")已被停用");
        } 
        else if (user.getAccountLife() !=  null) {
            if ( new Date().after(user.getAccountLife()) ) {
                throw new BusinessException("此帐号(" + loginName + ")已过期");
            }
        }
        userDao.evict(user);
        return user;
	}

	public OperatorDTO getOperatorDTOByID(Long userId) {
		User user = userDao.getEntity(userId);
		return createOperatorDTO(user);
	}

	public OperatorDTO getOperatorDTOByLoginName(String loginName) {
	    User user = getUserByLoginName(loginName);
	    return createOperatorDTO(user);
	}
	
	/* 拷贝User对象的所有属性到OperatorDTO */
    private OperatorDTO createOperatorDTO(User user) {
        OperatorDTO dto = new OperatorDTO();
        
        // 共有的属性直接拷贝
        BeanUtil.copy(dto, user);

        // 其他用户对象特有的属性全部放到DTO的map里面保存
        Map<String, Object> dtoMap = dto.getAttributesMap();
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                dtoMap.put(fieldName, BeanUtil.getPropertyValue(user, fieldName));
            } catch (Exception e) {
            }
        }
        
        return dto;
    }

    @SuppressWarnings("unchecked")
	public List<Object[]> getUserRolesAfterLogin(Long userId) {
        String hql = "select distinct o.id.userId, o.id.roleId from ViewRoleUser o where o.id.userId = ?";
        return (List<Object[]>) userDao.getEntities(hql, userId);
	}

    public List<Long> getRoleIdsByUserId(Long userId) {
        List<Object[]> userRoles = getUserRolesAfterLogin(userId);
        List<Long> roleIds = new ArrayList<Long>();
        for( Object[] urInfo : userRoles ){
            roleIds.add((Long) urInfo[1]);
        }
        return roleIds;
    }
    
	public List<?> getAssistGroupIdsByUserId(Long userId) {
        String hql = "select distinct g.id from Group g, GroupUser gu " +
        		" where g.id = gu.groupId and gu.userId = ? and g.groupType = ?";
        return userDao.getEntities(hql, userId, Group.ASSISTANT_GROUP_TYPE);
	}

	public Object[] getRootGroupByUserId(Long userId) {
		List<?> list = groupDao.getFatherGroupsByUserId(userId);
		if (list.size() == 0) {
			return null;
		}
		Group group = (Group) list.get(0);
		return new Object[] {group.getId(), group.getName()};
	}

	public List<Object[]> getGroupsByUserId(Long userId) {
		List<?> list = groupDao.getFatherGroupsByUserId(userId);
		List<Object[]> result = new ArrayList<Object[]>();
		for (int i = 1; i < list.size() + 1; i++) {
			Group group = (Group) list.get(i - 1);
			result.add(new Object[] {group.getId(), group.getName()});
		}
		return result;
	}

    public List<GroupDTO> getGroupTreeByGroupId(Long groupId) {
        List<Group> groups = groupDao.getChildrenById(groupId);
        
        List<GroupDTO> returnList = new ArrayList<GroupDTO>();
        for( Group group : groups ){
            GroupDTO dto = new GroupDTO();
            dto.setId(group.getId().toString());
            dto.setName(group.getName());
            dto.setParentId(group.getParentId().toString());
            returnList.add(dto);
        }
        
        return returnList;
    }
    
    public List<OperatorDTO> getUsersByGroupId(Long groupId) {
        List<User> users = groupDao.getUsersByGroupId(groupId);
        return translateUserList2DTO(users);
    }
 
    @SuppressWarnings("unchecked")
    public List<OperatorDTO> getUsersByRoleId(Long roleId) {
        String hql = "select distinct u from RoleUser ru, User u" +
                " where ru.id.userId = u.id and ru.id.roleId = ? order by u.id";
       
        List<User> data = (List<User>) groupDao.getEntities( hql, roleId );
        return translateUserList2DTO(data);
    }
    
    private List<OperatorDTO> translateUserList2DTO(List<User> users){
        List<OperatorDTO> returnList = new ArrayList<OperatorDTO>();
        for( User user : users ){
            returnList.add(createOperatorDTO(user));
        }
        return returnList;
    }
    
    public String[] getContactInfos(String receiverStr, boolean justID) {
    	if(receiverStr == null) return null;
    	
    	Map<String, Object> fmDataMap = new HashMap<String, Object>();
		List<Param> macroParams = ParamManager.getComboParam(UMConstants.EMAIL_MACRO);
		if(macroParams != null) {
			for(Param p : macroParams) {
				fmDataMap.put(p.getText(), p.getValue());
			}
		}
		
		receiverStr = MacrocodeCompiler.runLoop(receiverStr, fmDataMap, true);
		String[] receiver = receiverStr.split(",");
		
		// 将登陆账号转换成该用户的邮箱
		Set<String> emails = new HashSet<String>();
		Set<Long> ids = new HashSet<Long>();
		for(int j = 0; j < receiver.length; j++) {
			String temp = receiver[j];
			
			// 判断配置的是否已经是email，如不是，作为loginName处理
			if(temp.endsWith("@tssRole")) {
				List<OperatorDTO> list = getUsersByRoleId(parseID(temp));
				for(OperatorDTO user : list) {
					addUserEmail2List(user, emails, ids);
				}
			} 
			else if(temp.endsWith("@tssGroup")) {
				List<OperatorDTO> list = getUsersByGroupId(parseID(temp));
				for(OperatorDTO user : list) {
					addUserEmail2List(user, emails, ids);
				}
			} 
			else if(temp.indexOf("@") < 0) {
				try {
					OperatorDTO user = getOperatorDTOByLoginName(temp);
					addUserEmail2List(user, emails, ids);
				} 
				catch(Exception e) {
				}
			}
			else if(temp.indexOf("@") > 0 && temp.indexOf(".") > 0) {
				emails.add(temp);
			}
		}
		
		if(justID) {
			return ids.isEmpty() ? new String[]{} : EasyUtils.list2Str(ids).split(",");
		}
		
		receiver = new String[emails.size()];
		receiver = emails.toArray(receiver);
		
		return receiver;
	}
	
	private Long parseID(String temp) {
		try {
			return EasyUtils.obj2Long( temp.split("@")[0] );
		} catch(Exception e) {
			return 0L;
		}
	}
 
	private void addUserEmail2List(OperatorDTO user, Set<String> emails, Set<Long> ids) {
		String email = (String) user.getAttribute("email");
		if( !EasyUtils.isNullOrEmpty(email) ) {
			emails.add( email );
		}
		ids.add(user.getId());
	}
}