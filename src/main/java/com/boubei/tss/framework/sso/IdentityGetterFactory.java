package com.boubei.tss.framework.sso;

import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.util.BeanUtil;

/**
 * <p>
 * 身份读取器工厂类
 * </p>
 */
public class IdentityGetterFactory {
    
    /** 用户身份转换器 */
    protected static IdentityGetter getter;

    /**
     * 获取身份转换对象
     * @return
     */
    public static IdentityGetter getInstance() {
        if (getter == null) {
            String configValue = Config.getAttribute(SSOConstants.IDENTITY_TRANSLATOR);
            if (configValue != null) {
                getter = (IdentityGetter) BeanUtil.newInstanceByName(configValue);
            } else {
                throw new BusinessException("当前系统没有定义默认身份读取器，用户不能实现单点登录");
            }
        }
        return getter;
    }
}
