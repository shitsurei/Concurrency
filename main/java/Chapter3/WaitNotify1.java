package Chapter3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 01
 * wait和notify的原理：
 * 1 Monitor对象中除了Owner引用（指向持有当先锁对象的线程），EntryList阻塞队列（因为竞争锁对象失败而陷入阻塞的线程队列）外
 * 还有一个WaitSet的等待集合，其中保存着因为执行过程中因条件不满足而陷入阻塞状态的线程
 * 2 当线程执行过程中调用wait方法时（一般是因为执行条件不满足），当前线程陷入阻塞（WAITING状态），并加入Monitor对象的WaitSet集合中
 * 3 当持有Monitor对象的线程执行notify或notifyAll方法时（一般是等待线程的执行条件满足），等待集合中的线程唤醒
 * 4 唤醒后的线程并不直接获得锁，而是要进入EntryList和其他线程一起竞争锁对象
 * 【执行这两个方法的前提是已经获得了锁对象，即为Owner指向的线程】
 *
 * 不带参数或参数为0的wait方法会无限等待唤醒
 * 带参数的wait方法会等待一定时间后自动唤醒，其他线程也可以对其提前唤醒【此时的阻塞状态为TIMED_WAITING】
 *
 * notify方法会随机唤醒等待集合中的一个线程
 * notifyAll方法会唤醒等待集合中所有的线程
 */
@Slf4j
public class WaitNotify1 {
    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
//        test1();
        test2();
    }

    public static void test2() throws InterruptedException {
        new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("other code");
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("other code");
            }
        }, "t2").start();

        TimeUnit.SECONDS.sleep(3);
        synchronized (lock) {
//            2020-04-01 22:05:14.175 [t1] INFO  Chapter3.WaitNotify1 - other code
            lock.notify();
        }
    }

    public static void test1() {
        try {
//          报错  Exception in thread "main" java.lang.IllegalMonitorStateException
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
