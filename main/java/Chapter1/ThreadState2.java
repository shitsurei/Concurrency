package Chapter1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 07
 * Java中线程的六种状态
 * NEW：线程已经创建，但未启动
 * RUNNABLE：运行中（也可能出现了操作系统层面的就绪态和阻塞态，其中阻塞态指执行IO操作）
 * TIMED_WAITING：有确定时限的等待，例如睡眠状态
 * WAITING：无确定时限的等待，例如等待其他线程同步（执行不带参数的join方法）
 * BLOCKED：应为获取不到要上锁的资源陷入阻塞
 * TERMINATED：已运行结束
 */
@Slf4j
public class ThreadState2 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
        }, "t1");
        Thread t2 = new Thread(() -> {
            synchronized (ThreadState2.class) {
                while (true) {

                }
            }
        }, "t2");
        t2.start();
        Thread t3 = new Thread(() -> {
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t3");
        t3.start();
        Thread t4 = new Thread(() -> {
            try {
                t3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t4");
        t4.start();
        Thread t5 = new Thread(() -> {
            synchronized (ThreadState2.class) {
            }
        }, "t5");
        t5.start();
        Thread t6 = new Thread(() -> {
        }, "t6");
        t6.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//      2020-03-29 15:42:53.374 [main] INFO  Chapter1.ThreadState2 - t1 state : NEW
        log.info("t1 state : " + t1.getState().name());
//      2020-03-29 15:42:53.377 [main] INFO  Chapter1.ThreadState2 - t2 state : RUNNABLE
        log.info("t2 state : " + t2.getState().name());
//      2020-03-29 15:42:53.378 [main] INFO  Chapter1.ThreadState2 - t3 state : TIMED_WAITING
        log.info("t3 state : " + t3.getState().name());
//      2020-03-29 15:42:53.378 [main] INFO  Chapter1.ThreadState2 - t4 state : WAITING
        log.info("t4 state : " + t4.getState().name());
//      2020-03-29 15:42:53.378 [main] INFO  Chapter1.ThreadState2 - t5 state : BLOCKED
        log.info("t5 state : " + t5.getState().name());
//      2020-03-29 15:48:52.155 [main] INFO  Chapter1.ThreadState2 - t6 state : TERMINATED
        log.info("t6 state : " + t6.getState().name());
    }
}
