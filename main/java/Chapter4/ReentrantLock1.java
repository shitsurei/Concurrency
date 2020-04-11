package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 05
 * juc包下的ReentrantLock
 * 相比于synchronized的增强功能：
 * 1 可中断（lockInterruptibly方法实现）
 * 2 可设置尝试在一定时间内获取锁对象（带参数的tryLock方法实现）
 * 3 可以设置为公平锁，即先进先出，相对的synchronized阻塞队列中唤醒的线程和新来的线程同时争抢锁（通过带参数的构造方法实现）
 * 4 支持多个条件变量（synchronized只有一个wait set）
 * 相同点：都支持可重入
 */
@Slf4j
public class ReentrantLock1 {
    private static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
//        method1();

        /**
         * 可打断特性
         */
        Thread t1 = new Thread(() -> {
            try {
                log.info("尝试获得锁");
//                1 如果没有竞争，此方法就会获取lock对象锁
                /**
                 * 2020-04-11 21:40:33.008 [t1] INFO  Chapter4.ReentrantLock1 - 尝试获得锁
                 * 2020-04-11 21:40:33.015 [t1] INFO  Chapter4.ReentrantLock1 - 获取到锁了
                 */
//                2 如果有竞争，就会进入阻塞队列，但是可以被其他线程的interrupt方法打断
                /**
                 * 【注意：lock方法不可打断，需要执行lockInterruptibly方法】
                 * 2020-04-11 21:42:24.678 [t1] INFO  Chapter4.ReentrantLock1 - 尝试获得锁
                 * 2020-04-11 21:42:26.679 [main] INFO  Chapter4.ReentrantLock1 - 打断t1线程
                 * 2020-04-11 21:42:26.684 [t1] INFO  Chapter4.ReentrantLock1 - 没有获得锁
                 */
                reentrantLock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("没有获得锁");
                return;
            }
            try {
                log.info("获取到锁了");
            } finally {
                reentrantLock.unlock();
            }
        }, "t1");
        t1.start();
        reentrantLock.lock();
        TimeUnit.SECONDS.sleep(2);
        log.info("打断t1线程");
        t1.interrupt();
    }

    /**
     * 可重入特性：
     * 2020-04-11 21:35:58.287 [main] INFO  Chapter4.ReentrantLock1 - enter method1
     * 2020-04-11 21:35:58.287 [main] INFO  Chapter4.ReentrantLock1 - enter method2
     */
    public static void method1() {
//        1 加锁
        reentrantLock.lock();
        try {
//            2 临界区
            log.info("enter method1");
            method2();
        } finally {
//            3 释放锁
            reentrantLock.unlock();
        }
    }

    public static void method2() {
        reentrantLock.lock();
        try {
            log.info("enter method2");
        } finally {
            reentrantLock.unlock();
        }
    }
}
