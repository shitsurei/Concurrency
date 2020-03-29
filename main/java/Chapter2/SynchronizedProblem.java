package Chapter2;

import lombok.extern.slf4j.Slf4j;
/**
 * 01
 * 线程同步问题
 * 多个线程对共享资源执行读写操作时，指令交错导致并发问题
 * <p>
 * 临界区和临界资源
 * 使用synchronized关键字来控制进程互斥进入临界区，访问临界资源
 * 用对象锁保证了临界区内代码的【原子性】
 * 底层原理：
 * 1 线程在进入临界区之前会先尝试获取锁，即检查临界区的锁对象是否被占有
 * a 如果被其他线程占有则会陷入阻塞（block状态），注意【即使持有锁对象的线程因时间片用完进入就绪态时也不会释放锁对象】
 * b 如果成功获取锁对象则可以进入临界区执行代码，访问资源
 * 2 执行结束后线程会释放锁对象，并唤醒阻塞中等待该锁对象的线程
 *
 * synchronized关键字可以直接声明要锁住的临界区代码块和锁对象，也可以加载方法声明上
 * synchronized声明实例方法时默认锁住this对象
 * synchronized声明静态方法时默认锁住类的class对象
 */
@Slf4j
public class SynchronizedProblem {
    static int count = 0;
    static final Object lock = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (lock) {
                    /**
                     * 这行代码在虚拟机中会编译成四条指令集：
                     * getstatic    count
                     * iconst_1
                     * iadd
                     * putstatic    count
                     */
                    count++;
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (lock) {
                    /**
                     * 这行代码在虚拟机中会编译成四条指令集：
                     * getstatic    count
                     * iconst_1
                     * isub
                     * putstatic    count
                     */
                    count--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        try {
//            等待t1 t2线程执行结束再输出count结果
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        加锁前每次执行都会出现同步问题
//        2020-03-29 16:19:41.753 [main] INFO  Chapter2.SynchronizedProblem - count = 486
//        2020-03-29 16:35:30.480 [main] INFO  Chapter2.SynchronizedProblem - count = -1028
//        2020-03-29 16:36:28.839 [main] INFO  Chapter2.SynchronizedProblem - count = -1782
        log.info("count = " + count);
//        加锁后不会出现同步问题
//        2020-03-29 16:39:42.952 [main] INFO  Chapter2.SynchronizedProblem - count = 0
//        2020-03-29 16:40:27.977 [main] INFO  Chapter2.SynchronizedProblem - count = 0
    }
}
