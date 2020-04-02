package Chapter3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 06
 * LockSupport的静态方法
 * park 用于暂停线程运行
 * unpark   用于恢复线程运行
 *
 * 特点：
 * 1 wait和notify必须配合锁对象一起使用，park和unpark不用
 * 2 notify只能随机唤醒一个线程，park和unpark是以线程为单位来阻塞和唤醒线程的，更加精确
 * 3 unpark方法可以在线程暂停之前调用，使得执行到park时失效，wait和notify不能这样操作
 */
@Slf4j
public class ParkUnpark {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("start");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("park");
            LockSupport.park();
            log.info("resume");
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(2);
        log.info("unpark");
        LockSupport.unpark(t1);
        /**
         * 2020-04-02 19:04:20.992 [t1] INFO  Chapter3.ParkUnpark - start
         * 2020-04-02 19:04:21.998 [t1] INFO  Chapter3.ParkUnpark - park
         * 2020-04-02 19:04:22.992 [main] INFO  Chapter3.ParkUnpark - unpark
         * 2020-04-02 19:04:22.992 [t1] INFO  Chapter3.ParkUnpark - resume
         */
    }
}
