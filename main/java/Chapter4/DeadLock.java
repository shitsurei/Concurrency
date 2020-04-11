package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 02
 * 死锁
 * 查询死锁的工具：
 * 1 jstack 命令行工具
 * 2 jconsole gui工具
 *
 * 解决方案：
 * 1 锁排序：每个线程都按相同的顺序加锁
 * 缺点：有可能出现饥饿现象
 */
@Slf4j
public class DeadLock {
    static final Object lock1 = new Object();
    static final Object lock2 = new Object();

    /**
     * 2020-04-02 19:48:58.254 [t2] INFO  Chapter4.DeadLock - try get lock2
     * 2020-04-02 19:48:58.254 [t1] INFO  Chapter4.DeadLock - try get lock1
     * 2020-04-02 19:48:58.259 [t1] INFO  Chapter4.DeadLock - get lock1
     * 2020-04-02 19:48:58.259 [t2] INFO  Chapter4.DeadLock - get lock2
     * 2020-04-02 19:48:59.260 [t1] INFO  Chapter4.DeadLock - try get lock2
     * 2020-04-02 19:49:00.261 [t2] INFO  Chapter4.DeadLock - try get lock1
     */
    public static void main(String[] args) {
        new Thread(() -> {
            log.info("try get lock1");
            synchronized (lock1) {
                log.info("get lock1");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("try get lock2");
                synchronized (lock2) {
                    log.info("get lock2");
                }
            }
        }, "t1").start();
        new Thread(() -> {
            log.info("try get lock2");
            synchronized (lock2) {
                log.info("get lock2");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("try get lock1");
                synchronized (lock1) {
                    log.info("get lock1");
                }
            }
        }, "t2").start();
    }
}
/**
 * ---首先找到当前发生死锁的java线程pid
 * C:\Users\张国荣>jps
 * 22304 RemoteMavenServer36
 * 10532
 * 18276 Launcher
 * 7124 Jps
 * 22956 KotlinCompileDaemon
 * 23804 DeadLock
 *
 * ---使用jstack命令查询死锁
 * C:\Users\张国荣>jstack 23804
 * 2020-04-02 19:49:43
 * Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.201-b09 mixed mode):
 * …………
 * ---检测到一个死锁，t1 t2线程，43行和28行
 * Found one Java-level deadlock:
 * =============================
 * "t2":
 *   waiting to lock monitor 0x000000000385b748 (object 0x00000000d7359b88, a java.lang.Object),
 *   which is held by "t1"
 * "t1":
 *   waiting to lock monitor 0x000000000385d848 (object 0x00000000d7359b98, a java.lang.Object),
 *   which is held by "t2"
 *
 * Java stack information for the threads listed above:
 * ===================================================
 * "t2":
 *         at Chapter4.DeadLock.lambda$main$1(DeadLock.java:43)
 *         - waiting to lock <0x00000000d7359b88> (a java.lang.Object)
 *         - locked <0x00000000d7359b98> (a java.lang.Object)
 *         at Chapter4.DeadLock$$Lambda$2/1854778591.run(Unknown Source)
 *         at java.lang.Thread.run(Thread.java:748)
 * "t1":
 *         at Chapter4.DeadLock.lambda$main$0(DeadLock.java:28)
 *         - waiting to lock <0x00000000d7359b98> (a java.lang.Object)
 *         - locked <0x00000000d7359b88> (a java.lang.Object)
 *         at Chapter4.DeadLock$$Lambda$1/1784662007.run(Unknown Source)
 *         at java.lang.Thread.run(Thread.java:748)
 *
 * Found 1 deadlock.
 */
