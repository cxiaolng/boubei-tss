package com.boubei.tss.cache;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.boubei.tss.cache.extension.threadpool.IThreadPool;

/**
 * 测试池机制。<br/>
 * 包括ReusablePool的checkout，checkIn等操作，
 * 以及ThreadPool的机制（包括worker池和work队列）
 * 
 */
public class JCacheFunctionTest {
    
    protected Logger log = Logger.getLogger(JCacheFunctionTest.class);
    
    private Pool apool;
    private IThreadPool tpool;
    private int portNum = 28;
    Long cyclelife;
    
    @Before
    public void setUp() {
        JCache cache = JCache.getInstance();
        
        try { // 休眠6s，等待池初始化结束
            Thread.sleep(6000);
            apool = cache.getTaskPool();
            tpool = cache.getThreadPool();
            cyclelife = apool.getCacheStrategy().cyclelife;
        } 
        catch (InterruptedException e) {
            log.error(e);
        }
    }

    @Test
    public void testPool() throws Exception {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        
        for (int port = 1 ; port <= portNum ; port++) {
            scanPort(port);
            
            if( port % 7 == 0) {
                Thread.sleep(1*1000); 
                log.debug(tpool);
                log.debug(apool);
            }
        }
        log.debug("工作队列填充完毕！工填充了 " + portNum + "个任务。");
        
        // 主线程休眠，等待全部端口被扫描完
        while( ScannerTask.finishedNum < portNum) {
            Thread.sleep(10*1000); 
        }
        
        // 休眠，等待池中对象都过期后
        Thread.sleep(cyclelife + 1000); 
        
        // 测试checkIn事件是否重新唤醒apool池的cleaner线程
        scanPort(80);
        
        // 休眠，等待scanPort 80 端口任务完成
        Thread.sleep(3*1000); 
    }
    
    private void scanPort(int port) {
        Cacheable o = apool.checkOut(100);
        ScannerTask task = (ScannerTask) o.getValue();
        task.port = port;
        
        o.update(task);
        tpool.excute(apool, o);
    }
}
