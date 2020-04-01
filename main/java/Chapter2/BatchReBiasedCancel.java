package Chapter2;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 10
 * 批量重偏向：
 * 如果有多个线程访问锁对象，但没有发生竞争，此时已经偏向线程1的锁对象仍有机会重新偏向线程2，【重偏向会重置对象的线程ID】
 * 当撤销偏向锁阈值超过20次后，jvm会这样觉得，我是不是偏向错了呢，于是会在给这些对象加锁时重新偏向至加锁线程
 * <p>
 * 批量撤销：
 * 当撤销偏向锁阈值超过40次后，jvm会这样这个类的对象竞争很激烈，根本就不该偏向。
 * 【整个类的所有对象都会变为不可偏向的，新建的对象也是不可偏向的】
 * <p>
 * 锁消除优化：
 * 对JIT即时编译器来说，对于执行中的热点代码（循环等）会进行优化
 * 如果JIT分析得到某个加锁过程是没有必要的，即锁对象并没有给方法外的线程共享，就会去掉加锁过程
 * 【锁消除优化默认是开启的，可以通过JVM参数-XX:-EliminateLocks关闭】
 * <p>
 * 锁粗化：
 * 对相同对象多次加锁，导致线程发生多次重入，可以使用锁粗化方式来优化，这不同于之前讲的细分锁的粒度
 */
@Slf4j
public class BatchReBiasedCancel {
    static Thread t1, t2, t3;

    public static void main(String[] args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        Vector<Object> list = new Vector<>();
        t1 = new Thread(() -> {
            for (int i = 0; i < 39; i++) {
                Object o = new Object();
                list.add(o);
                synchronized (o) {

                }
            }
            LockSupport.unpark(t2);
        }, "t1");
        t2 = new Thread(() -> {
            LockSupport.park();
            for (int i = 0; i < 39; i++) {
                Object o = new Object();
                list.add(o);
                synchronized (o) {
//                    if (i == 19 || i == 18)
//                        log.info(i + "---" + ClassLayout.parseInstance(o).toPrintable());
                    /**
                     * 执行了20次，触发批量重偏向，导致线程ID更换
                     * 2020-04-01 21:31:27.107 [t2] INFO  Chapter2.BatchReBiasedCancel - 18---java.lang.Object object internals:
                     *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
                     *       0     4        (object header)                           10 f1 7b 1a (00010000 11110001 01111011 00011010) (444330256)
                     *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
                     *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
                     *      12     4        (loss due to the next object alignment)
                     * Instance size: 16 bytes
                     * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
                     *
                     * 2020-04-01 21:31:27.112 [t2] INFO  Chapter2.BatchReBiasedCancel - 19---java.lang.Object object internals:
                     *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
                     *       0     4        (object header)                           05 a0 f3 19 (00000101 10100000 11110011 00011001) (435396613)
                     *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
                     *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
                     *      12     4        (loss due to the next object alignment)
                     * Instance size: 16 bytes
                     * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
                     */
                }
            }
            LockSupport.unpark(t3);
        }, "t2");
        t3 = new Thread(() -> {
            LockSupport.park();
            for (int i = 0; i < 39; i++) {
                Object o = new Object();
                list.add(o);
                synchronized (o) {

                }
            }
        }, "t3");
        t1.start();
        t2.start();
        t3.start();
        t3.join();
        /**
         * 因为锁对象竞争激烈导致的偏向锁批量撤销，新产生的对象不再使用偏向锁
         * 2020-04-01 21:26:54.721 [main] INFO  Chapter2.BatchReBiasedCancel - java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        log.info(ClassLayout.parseInstance(new Object()).toPrintable());
    }
}
