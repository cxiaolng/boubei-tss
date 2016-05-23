/* ==================================================================   
 * Created [2007-1-4] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
 */

package com.boubei.tss.cache;

/**
 * 缓存池监听器。
 */
public class PoolListener implements Listener {

	public void dealwithPoolEvent(PoolEvent poolEvent) {
		Pool pool = poolEvent.getPool();
		if (pool != null) {
			switch (poolEvent.getType()) {
			case PoolEvent.CHECKOUT:
			case PoolEvent.REMOVE:
				/* 移除或者检出缓存项时的事件处理 */
				break;
			case PoolEvent.CHECKIN:
			case PoolEvent.PUT_IN:
				/* 唤醒pool对象休息室中休眠中的线程:
				 * 包括cleaner线程（池为空的时候会休眠），checkout，remove等操作的等待线程。 */
				synchronized (pool) {
					pool.notifyAll();
				}
				checkPoolLimit(pool);
				break;
			case PoolEvent.MAX_POOL_LIMIT_EXCEEDED:
				// 如果缓存池容量已超过极限，则启动移除销毁
				Cacheable item = pool.remove();
				if(item != null) {
					pool.destroyObject( item ); 
				}
				break;
			case PoolEvent.MAX_POOL_LIMIT_REACHED:
				// 如果缓存池容量已经到达极限，则启动缓存清理
				pool.purge(); 
				break;
			case PoolEvent.POOL_RELEASED:
				/* 缓存池被释放后的事件处理 */
				break;
			case PoolEvent.STRATEGY_CHANGED_CYCLELIFE:
				cyclelifeChanged(pool);
				break;
			case PoolEvent.STRATEGY_CHANGED_SIZE_REDUCE:
				checkPoolLimit(pool);
				break;
			case PoolEvent.STRATEGY_CHANGED_RESET:
				pool.release(false);
				pool.init();
				break;
			default:
				throw new RuntimeException("ObjectPoolEvent的type值错误，不存在该类型的事件！");
			}
		}
	}

	private void checkPoolLimit(Pool pool) {
		int maxSize = pool.getCacheStrategy().poolSize;
		
		// 如果maxSize = 0则表示不限制池大小
		if (maxSize > 0) {
			if (pool.size() == maxSize) {
				pool.firePoolEvent(PoolEvent.MAX_POOL_LIMIT_REACHED);
			}
			if (pool.size() > maxSize) {
				pool.firePoolEvent(PoolEvent.MAX_POOL_LIMIT_EXCEEDED);
			}
		}
	}

	/**
	 * <pre>
	 * 缓存策略的一般内容改变，包括对象生命周期值等。
	 * 
	 * 不更新池中已存在缓存项的生命周期，后续新进来的缓存对象将使用新的生命周期值。
	 * </pre>
	 * @param pool
	 */
	private void cyclelifeChanged(Pool pool) {
		/* 如果pool扩展实现了Cleaner接口。
		 * 重新初始化清除器，根据缓存项的生命周期值改变清理间隔时间。*/
		if (pool instanceof Cleaner) {
			((Cleaner) pool).initCleaner();
		}
	}
 
	/*
	 * 所有的ObjectPoolListener实例返回相同的hashCode
	 */
	public int hashCode() {
		return 123456789;
	}
}
