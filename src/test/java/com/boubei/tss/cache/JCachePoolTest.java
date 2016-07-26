package com.boubei.tss.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.boubei.tss.cache.extension.workqueue.AbstractTask;
import com.boubei.tss.cache.extension.workqueue.OutputRecordsManager;

public class JCachePoolTest {
	
	JCache cache;
			
	@Before
	public void setUp() {
		cache = JCache.getInstance();
	}
	
	/**
	 * 测试简单池
	 */
	@Test
	public void testSimplePool() {
		SimplePool spool = (SimplePool) cache.getPool("SimplePool");
		assertNotNull(spool);
		
		assertEquals(0, spool.size());
		spool.release(true);
		
		assertEquals(0, spool.getFree().size());
		assertEquals(0, spool.getUsing().size());
	}
	
	/**
	 * 测试线程池
	 */
	@Test
	public void testThreadPool() {
		Pool tpool = (Pool) cache.getThreadPool();
		assertNotNull(tpool);
		
		Cacheable threadItem = tpool.checkOut(0);
		assertNotNull(threadItem);
		
		tpool.reload(threadItem);
		tpool.destroyByKey(threadItem.getKey());
		tpool.destroyObject(threadItem);
	}
	
	/**
	 * 测试任务池
	 */
	@Test
	public void testTaskPool() {
		Pool taskpool = cache.getTaskPool();
		taskpool.flush();
		taskpool.init();
		
		Cacheable taskItem = taskpool.checkOut(0);
		assertNotNull(taskItem.getAccessed());
		assertEquals(1, taskItem.getHit());
		assertEquals(false, taskItem.isExpired());
		
		Object key = taskItem.getKey();
		assertNotNull(key);
		assertNotNull(taskItem.getValue());
		
		taskpool.checkIn(taskItem);
		
		// 测试任务接收和执行
		for(int i = 0; i < 20; i++) {
			LogRecorder.getInstanse().output(i);
		}
		try {
			Thread.sleep(3000); // 休息三秒，等待剩余任务被强制执行
		} catch (InterruptedException e) {
		}
		
		// 测试checkOut等待
		for(int i = 0; i < 30; i++) {
			taskpool.checkOut(0);
		}
		
		try {
			taskpool.checkOut(0);
			taskpool.checkOut(0);
		} catch(Exception e) {
			Assert.assertTrue("缓存池【端口扫描任务池】已满，且各缓存项都处于使用状态，需要等待。可考虑重新设置缓存策略！", true);
		}
		
		taskpool.checkIn(taskItem);
		
		taskItem = taskpool.getObject(key);
		taskpool.destroyByKey(key);
		taskItem = taskpool.getObject(key); // 触发reload
		taskpool.putObject(key, taskItem.getValue());
		
		assertTrue(taskpool.getHitRate() > 0);
		taskpool.flush();
		taskpool.init();
	}
	
	static class LogRecorder extends OutputRecordsManager{
		
		private static LogRecorder instance;
	    
	    private LogRecorder(){
	    }
	    
	    public static LogRecorder getInstanse(){
	        if(instance == null) {
	            instance = new LogRecorder();
	        }
	        return instance;
	    }
	    
	    protected int getMaxTime() { 
	    	return 2000;
	    }
 
	    protected void excuteTask(List<Object> logs) {
	    	AbstractTask task = new AbstractTask() {

	    		public void excute() {
					for (Object temp : records) {
				    	System.out.println(temp);
				    }
				}
	    		
	    	};
	        task.fill(logs);

	        tpool.excute(task);
	    }
	}
}
