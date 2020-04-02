package Chapter3;

import lombok.extern.slf4j.Slf4j;

/**
 * 03
 * wait和notify方法的最佳实践
 * 由于notify方法唤醒线程具有随机性，所以一般在调用wait方法时，通过while语句对执行条件进行判断，如果出现虚假唤醒可以保证再次wait
 * 当要唤醒线程时使用notify方法，唤醒所有等待的线程
 */
@Slf4j
public class WaitNotifyAll {
    static final Object lock = new Object();
    static boolean condition1 = false;
    static boolean condition2 = false;

    public static void main(String[] args) {
        /**
         * 2020-04-02 17:34:38.832 [A] INFO  Chapter3.WaitNotifyAll - is condition1 feed ? false
         * 2020-04-02 17:34:38.837 [A] INFO  Chapter3.WaitNotifyAll - not satisfied , to wait
         * 2020-04-02 17:34:38.837 [B] INFO  Chapter3.WaitNotifyAll - is condition1 feed ? false
         * 2020-04-02 17:34:38.837 [B] INFO  Chapter3.WaitNotifyAll - not satisfied , to wait
         * 2020-04-02 17:34:38.837 [main] INFO  Chapter3.WaitNotifyAll - condition1 is satisfied
         * 2020-04-02 17:34:38.837 [B] INFO  Chapter3.WaitNotifyAll - not satisfied , to wait
         * 2020-04-02 17:34:38.837 [A] INFO  Chapter3.WaitNotifyAll - condition1 is satisfied , working
         */
        new Thread(() -> {
            synchronized (lock) {
                log.info("is condition1 feed ? {}", condition1);
                while (!condition1) {
                    log.info("not satisfied , to wait");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("condition1 is satisfied , working");
            }
        }, "A").start();
        new Thread(() -> {
            synchronized (lock) {
                log.info("is condition1 feed ? {}", condition2);
                while (!condition2) {
                    log.info("not satisfied , to wait");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("condition2 is satisfied , working");
            }
        }, "B").start();

        synchronized (lock) {
            condition1 = true;
            log.info("condition1 is satisfied");
            lock.notifyAll();
        }
    }
}
