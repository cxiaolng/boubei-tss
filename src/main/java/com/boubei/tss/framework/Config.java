package com.boubei.tss.framework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/** 
 * <p> 应用配置 </p> 
 * 
 *  配置文件默认放在config/resources下的application.properties文件。<br>
 *  支持一个参数对应多个值（多个值之间用“，”号隔开）。
 */
public class Config {
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	/** 配置文件中应用编号属性名：系统获取Code值，如TSS、CMS */
	public static final String APPLICATION_CODE = "application.code";

	/** Spring配置文件， 默认为spring.xml */
	public static final String SPRING_CONTEXT_PATH = "aplication.context";

	/** session过期时间的配置名称 */
	public static final String SESSION_CYCLELIFE_CONFIG = "session.cyclelife";
	
	/** 是否启用定时Job */
	public static final String ENABLE_JOB = "job.enable";
	
    private static ResourceBundle resources = null;
    private static Map<String, Object> propertyMap = new HashMap<String, Object>(); //存放属性 name/value
    
    private static void getBundle(){
        if(resources == null){
            resources = ResourceBundle.getBundle("application", Locale.getDefault());
        }
    }
    
    public static void setProperty(String key, String value) {
    	propertyMap.put(key, value);
    }
    
    /**
     * 获取配置参数
     * @param name
     * @return
     */
    public static String getAttribute(String name){
        String value = (String) propertyMap.get(name);
        if(value == null){
            getBundle();
            if(resources != null){
            	try{
            		value = resources.getString(name);
            	}catch(MissingResourceException exception){
            		return null;
            	}
            }
        }
        return value;
    }

    /**
     * 获取参数Set，适合一个参数有多个值的情况下使用（多个值之间用“,”号隔开）
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getAttributesSet(String name){
        Set<String> set = (Set<String>) propertyMap.get(name);
        if(set == null) {
        	getBundle();
        	propertyMap.put(name, set = new HashSet<String>());
            
            try{
	            String valueStr = resources.getString(name);
	            if(valueStr != null) {
	            	String[] values = valueStr.split(",");
	            	set.addAll(Arrays.asList(values));
	            }
            } 
            catch(MissingResourceException exception){
            	return null;
            }
        }
        return set;
    }
}
