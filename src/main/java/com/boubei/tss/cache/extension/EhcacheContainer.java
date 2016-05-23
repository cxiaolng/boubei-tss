/* ==================================================================   
 * Created [2006-12-31] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018 
 * ================================================================== 
 */
package com.boubei.tss.cache.extension;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.boubei.tss.cache.AbstractContainer;
import com.boubei.tss.cache.Cacheable;

/**
 * 使用本类来缓存的对象的key,value必须继承Serializable,这是因为ehcache这么要求.
 * 
 */
public class EhcacheContainer extends AbstractContainer {

	protected Logger log = Logger.getLogger(this.getClass());

	private Cache cache;

	public EhcacheContainer(String name) {
		super(name);
		try {
			cache = CacheManager.getInstance().getCache(name);
			if(cache == null) {
				CacheManager.getInstance().addCache(name);
				cache = CacheManager.getInstance().getCache(name);
			}
		} catch (Exception e) {
			log.error("pool use EhcacheContainer error", e);
		}
	}

	public Cacheable get(Object key) {
		checkKey(key);
		try {
			Element e = cache.get((Serializable) key);
			if (e != null) {
				return (Cacheable) e.getObjectValue();
			}
		} catch (Exception e) {
			log.error("EhcacheContainer-->get(key) error", e);
		}
		return null;
	}

	public Cacheable put(Object key, Cacheable value) {
		checkKey(key);
		checkKey(value);
		Element e = new Element((Serializable) key, (Serializable) value);
		cache.put(e);

		return value;
	}

	public Cacheable remove(Object key) {
		Cacheable item = get(key);
		checkKey(key);
		cache.remove((Serializable) key);
		return item;
	}

	@SuppressWarnings("unchecked")
	public Set<Object> keySet() {
		try {
			return new HashSet<Object>(cache.getKeys());
		} catch (Exception e) {
			log.error("EhcacheContainer-->keySet() error", e);
		}
		return null;
	}

	public void clear() {
		try {
			cache.removeAll();
		} catch (IllegalStateException e) {
			log.error("EhcachePool->clear:IllegalStateException!", e);
		}
	}

	private void checkKey(Object key) {
		if (!(key instanceof Serializable)) {
			throw new RuntimeException("类" + key.getClass() + "没有实现Serializable接口，不能被EHCache缓存！");
		}
	}

	public int size() {
		try {
			return cache.getSize();
		} catch (Exception e) {
			throw new RuntimeException("获取EHCache缓存的大小时失败！", e);
		}
	}

	public Set<Cacheable> valueSet() {
		Set<Cacheable> values = new HashSet<Cacheable>();
		for (Object key : keySet()) {
			values.add( get(key) );
		}
		return values;
	}
}
