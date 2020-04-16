package Chapter7;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 07
 * ThreadPoolExecutor关闭线程池的相关方法
 * 内部实现：
 * 1 将整个线程池锁住（通过获取线程池对象的ReentrantLock主锁实现）
 * 2 进行安全检查，修改线程池状态
 * 3 打断线程（空闲线程或所有线程），回收空闲线程资源
 * 4 尝试终结线程池
 * 5 返回未完成的任务
 */
@Slf4j
public class ThreadPoolAPI3 {
    public static void main(String[] args) throws InterruptedException {
//        testShutdown();
        testShutdownNow();
    }

    public static void testShutdownNow() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        Runnable r1 = () -> {
//            2020-04-16 15:27:14.601 [pool-1-thread-1] INFO  Chapter7.ThreadPoolAPI3 - begin1
//            2020-04-16 15:27:14.602 [pool-1-thread-1] INFO  Chapter7.ThreadPoolAPI3 - finish1
            log.info("begin1");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("finish1");
        };
        Runnable r2 = () -> {
            log.info("begin2");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("finish2");
        };
        log.info("r1 {}",r1);
        log.info("r2 {}",r2);
        threadPool.submit(r1);
        threadPool.submit(r2);
        /**
         * shutdownNow方法会将线程池的状态修改为STOP
         * 这表示 1 线程池不再接受新的任务 2 正在的任务会被打断 3 未执行的任务会返回
         * 【并且次方法不会阻塞调用shutdown方法的主线程】
         */
        List<Runnable> list = threadPool.shutdownNow();
//        2020-04-16 15:27:14.601 [main] INFO  Chapter7.ThreadPoolAPI3 - list r java.util.concurrent.FutureTask@2acf57e3
        log.info("未执行任务 {}",list.get(0));
    }

    public static void testShutdown() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        threadPool.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            2020-04-16 15:00:52.260 [pool-1-thread-1] INFO  Chapter7.ThreadPoolAPI3 - before showdown
            log.info("before showdown");
        });
        /**
         * shutdown方法会将线程池的状态修改为SHUTDOWN
         * 这表示 1 线程池不再接受新的任务 2 但是已提交的任务会执行完成
         * 【并且次方法不会阻塞调用shutdown方法的主线程】
         */
        threadPool.shutdown();
//        awaitTermination方法用于阻塞当前主线程，等待结束后再执行主线程的后序代码
        threadPool.awaitTermination(3, TimeUnit.SECONDS);
//        Exception in thread "main" java.util.concurrent.RejectedExecutionException:执行拒绝策略
//        Task java.util.concurrent.FutureTask@b684286 rejected from java.util.concurrent.
//        ThreadPoolExecutor@880ec60[Shutting down, pool size = 1, active threads = 1, queued tasks = 0, completed tasks = 0]
        threadPool.submit(() -> {
            log.info("after showdown");
        });
//        中断未执行，未中断会立即执行，不阻塞
        log.info("main thread");
    }
}
