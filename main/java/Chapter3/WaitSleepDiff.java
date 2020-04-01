package Chapter3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 02
 * wait和sleep方法的区别：
 * 1 sleep是Thread类的静态方法，wait是每个对象都有的实例方法
 * 2 sleep不需要获取锁对象，可以任意场景使用，wait方法需要先获取锁对象才能调用（即需要配合synchronized关键字一起使用）
 * 3 sleep方法在阻塞过程中不会释放锁对象，但wait进入阻塞会释放锁对象，不影响其他线程访问
 * 4 sleep只能使线程阻塞为TIMED_WAITING状态，wait可以使线程进入TIMED_WAITING阻塞或者WAITING阻塞
 */
@Slf4j
public class WaitSleepDiff {
    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                log.info("获得锁");
                try {
//                    2020-04-01 22:17:58.144 [t1] INFO  Chapter3.WaitSleepDiff - 获得锁
//                    2020-04-01 22:17:59.143 [main] INFO  Chapter3.WaitSleepDiff - 获取锁
                    lock.wait();
//                    2020-04-01 22:17:28.453 [t1] INFO  Chapter3.WaitSleepDiff - 获得锁
//                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
//        2020-04-01 22:21:41.605 [main] INFO  Chapter3.WaitSleepDiff - WAITING
        log.info(t1.getState().name());
        TimeUnit.SECONDS.sleep(2);
        synchronized (lock) {
            log.info("获取锁");
        }
    }
}
