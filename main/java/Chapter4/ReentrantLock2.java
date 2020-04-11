package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 06
 * ReentrantLock
 * 设置延时等待被其他线程持有的锁对象
 */
@Slf4j
public class ReentrantLock2 {
    private static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) {
//        设置超时等待
        Thread t1 = new Thread(() -> {
            log.info("尝试获取锁");
            /**
             * 不带参数的调用，会立即判断
             * 2020-04-11 21:51:27.532 [t1] INFO  Chapter4.ReentrantLock2 - 尝试获取锁
             * 2020-04-11 21:51:27.535 [t1] INFO  Chapter4.ReentrantLock2 - 获取不到锁
             * 带时间参数的调用，会等待一段时间后判断
             * 2020-04-11 21:54:24.792 [t1] INFO  Chapter4.ReentrantLock2 - 尝试获取锁
             * 2020-04-11 21:54:25.797 [t1] INFO  Chapter4.ReentrantLock2 - 获取不到锁
             */
            try {
//                可以设置带时间参数的tryLock方法，即当前线程会尝试获取锁对象一定时间
                if (!reentrantLock.tryLock(1, TimeUnit.SECONDS)) {
                    log.info("获取不到锁");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("获取不到锁");
                return;
            }
            try {
                /**
                 * 主线程在等待时间内释放了锁，该线程成功获取到
                 * 2020-04-11 21:56:45.253 [t1] INFO  Chapter4.ReentrantLock2 - 尝试获取锁
                 * 2020-04-11 21:56:45.258 [t1] INFO  Chapter4.ReentrantLock2 - 获取到了锁
                 */
                log.info("获取到了锁");
            } finally {
                reentrantLock.unlock();
            }
        }, "t1");
        reentrantLock.lock();
        t1.start();
        reentrantLock.unlock();
    }
}
