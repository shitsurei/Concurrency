package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 09
 * 同步模式之顺序控制：
 * 1 wait notify实现
 * 2 ReentrantLock的await和signal方法实现（同1）
 * 3 park和unpark实现
 */
@Slf4j
public class OrderControl {
    private static Object lock = new Object();
    private static boolean t2Run = false;

    public static void main(String[] args) throws InterruptedException {
//        achieve1();
        achieve3();
    }

    public static void achieve3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.info("print 1");
        }, "t1");
        Thread t2 = new Thread(() -> {
            log.info("print 2");
            LockSupport.unpark(t1);
        }, "t2");
        /**
         * 2020-04-12 14:29:13.685 [t2] INFO  Chapter4.OrderControl - print 2
         * 2020-04-12 14:29:13.686 [t1] INFO  Chapter4.OrderControl - print 1
         */
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
    }

    public static void achieve1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (!t2Run) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.info("print 1");
        }, "t1");
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.info("print 2");
                t2Run = true;
                lock.notify();
            }
        }, "t2");
        /**
         * 2020-04-12 13:21:33.645 [t2] INFO  Chapter4.OrderControl - print 2
         * 2020-04-12 13:21:33.648 [t1] INFO  Chapter4.OrderControl - print 1
         */
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
    }
}
