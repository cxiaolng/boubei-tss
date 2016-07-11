package com.boubei.tss.cache.extension;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.boubei.tss.cache.CacheStrategy;
import com.boubei.tss.cache.Cleaner;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.modules.param.ParamListener;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.modules.param.ParamService;
import com.boubei.tss.util.BeanUtil;

/**
 * 结合Param模块来配置实现缓存池
 */
public class PCache implements ParamListener {
	
	static Logger log = Logger.getLogger(PCache.class);
 
	// 在Global.setContext 里触发
	public void afterChange(Param param) {
		ParamService paramService = ParamManager.getService();
		
		// 为第一次初始化，由ParamServiceImpl初始化完成后触发
		if(param == null) {
			List<Param> list = paramService.getParamsByParentCode(CacheHelper.CACHE_PARAM);
			if(list != null) {
				for(Param item : list) {
					if( ParamConstants.FALSE.equals(item.getDisabled()) ){
						rebuildCache(item.getCode(), item.getValue());
					}
		    	}
			}
	    	return;
		}
		
		if(paramService.getParam(param.getId()) == null) return; // 如果是删除了参数，则什么都不做
		
		Long parentId = param.getParentId();
		if(parentId == null) return;
		
		try {
			Param parent = paramService.getParam(parentId);
			if( parent != null && CacheHelper.CACHE_PARAM.equals(parent.getCode()) ){
				String cacheCode   = param.getCode();
	    		String cacheConfig = param.getValue();
	    		rebuildCache(cacheCode, cacheConfig);
			}
		} catch(Exception e) { 
			log.error("rebuildCache 出错了，" + param.getCode(), e);
		}
	}
    
    @SuppressWarnings("unchecked")
    public static void rebuildCache(String cacheCode, String newConfig) {
    	Map<String, String> attrs;
    	try {  
  			ObjectMapper objectMapper = new ObjectMapper();
  			attrs = objectMapper.readValue(newConfig, Map.class);
		} 
    	catch (Exception e) {  
			log.error("CACHE_PARAM【" + cacheCode + "】的参数配置有误。\n" + newConfig, e);
			return;
  	    } 
    	
    	Pool pool = JCache.pools.get(cacheCode);
		if(pool != null) { // 如pool已存在，则只刷新其策略
			CacheStrategy strategy = pool.getCacheStrategy();
			
			// 如果改变了缓存池生命周期，则需要重新初始化cleaner
			long oldCyclelife = strategy.getCyclelife();
			BeanUtil.setDataToBean(strategy, attrs);
			long cyclelife = strategy.getCyclelife();
			
			if( oldCyclelife != cyclelife && pool instanceof Cleaner) {
				pool.flush();
				( (Cleaner) pool).initCleaner();
			}
		}
		else {
			CacheStrategy strategy = new CacheStrategy();
			BeanUtil.setDataToBean(strategy, attrs);
			JCache.pools.put(cacheCode, strategy.getPoolInstance()); // 新建
		}
    }
}
