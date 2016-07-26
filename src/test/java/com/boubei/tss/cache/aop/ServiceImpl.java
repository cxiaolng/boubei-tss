package com.boubei.tss.cache.aop;

import org.springframework.stereotype.Service;

import com.boubei.tss.util.MathUtil;

@Service("xyzService")
public class ServiceImpl implements IService {
	
	public Object f1() {
		
		try { Thread.sleep(10); } catch (InterruptedException e) { }
		
		return Thread.currentThread().getId();
	}
	
	public Object f2() {
		return MathUtil.randomInt(10) * 0;
	}

}
