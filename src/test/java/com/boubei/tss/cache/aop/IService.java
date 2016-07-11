package com.boubei.tss.cache.aop;

public interface IService {
	
	@QueryCached
	@Cached
	Object f1();

}
