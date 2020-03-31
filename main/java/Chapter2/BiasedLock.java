package Chapter2;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * 09
 * 偏向锁
 * JDK6引入对轻量级锁做进一步优化，减少锁重入过程中CAS操作尝试替换锁对象对象头Mark Word的系统开销
 * 执行流程：
 * 1 第一次上锁时执行CAS操作将线程ID设置到锁对象头的Mark Word中
 * 2 之后每次只要检查锁对象头中的Mark Word，只要线程ID是自己就表示没有竞争，该对象就归属线程所有
 * <p>
 * 偏向锁状态：
 * 未加锁的对象头：25bit保留     31bit hash码    1bit保留   1bitGC年龄    1bit偏向锁状态（0）    2bit加锁状态（01表示未加锁）
 * 加锁对象头：    54bit线程id   2bit epoch      1bit保留   1bitGC年龄    1bit偏向锁状态（1）    2bit加锁状态（01表示未加锁）
 * <p>
 * 如果开启了偏向锁，对象创建后其Mark Word值为0x05，即最后三位是101，此时他的ThreadID、epoch、age都为0
 * 【对象创建后偏向锁是默认开启的，但开启有延迟，如果想让程序启动时立即生效，需要添加JVM参数-XX:BiasedLockingStartupDelay=0来禁用延迟】
 * 【使用JVM参数-XX:-UseBiasedLocking可以禁用偏向锁，因此会优先使用轻量级锁】
 */
@Slf4j
public class BiasedLock {
    public static void main(String[] args) {
        System.out.println(Integer.toHexString(Object.class.hashCode()));
        System.out.println(ClassLayout.parseInstance(Object.class).toPrintable());
        System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
    }

    public static void jolAnalysis() {
        /**
         * jol包可以对对象内部字节码进行分析，这里输出的字节码属于小端模式
         * 2020-03-31 20:16:51.983 [main] INFO  Chapter2.BiasedLock - java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
//
        log.info(ClassLayout.parseInstance(new Object()).toPrintable());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /**
         * 2020-03-31 20:16:56.014 [main] INFO  Chapter2.BiasedLock - java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        log.info(ClassLayout.parseInstance(new Object()).toPrintable());

        Object o = new Object();
        /**
         * 2020-03-31 20:16:56.014 [main] INFO  Chapter2.BiasedLock - java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        log.info(ClassLayout.parseInstance(o).toPrintable());
        synchronized (o) {
            /**
             * 2020-03-31 20:16:56.014 [main] INFO  Chapter2.BiasedLock - java.lang.Object object internals:
             *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
             *       0     4        (object header)                           05 38 6e 02 (00000101 00111000 01101110 00000010) (40777733)
             *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
             *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
             *      12     4        (loss due to the next object alignment)
             * Instance size: 16 bytes
             * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
             */
            log.info(ClassLayout.parseInstance(o).toPrintable());
        }
        /**
         * 2020-03-31 20:16:56.014 [main] INFO  Chapter2.BiasedLock - java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           05 38 6e 02 (00000101 00111000 01101110 00000010) (40777733)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        log.info(ClassLayout.parseInstance(o).toPrintable());
        o.hashCode();
        /**
         * 2020-03-31 20:16:56.014 [main] INFO  Chapter2.BiasedLock - java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           01 6a 1b bd (00000001 01101010 00011011 10111101) (-1122276863)
         *       4     4        (object header)                           08 00 00 00 (00001000 00000000 00000000 00000000) (8)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        log.info(ClassLayout.parseInstance(o).toPrintable());
    }
}
