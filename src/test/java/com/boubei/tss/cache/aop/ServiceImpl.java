package com.boubei.tss.cache.aop;

import org.springframework.stereotype.Service;

@Service("xyzService")
public class ServiceImpl implements IService {
	
	public Object f1() {
		
		try { Thread.sleep(10); } catch (InterruptedException e) { }
		
		return Thread.currentThread().getId();
	}

}
