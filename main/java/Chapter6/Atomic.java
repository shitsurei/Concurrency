package Chapter6;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 02
 * 原子整数
 * AtomicInteger
 * AtomicBoolean
 * AtomicLong
 * 原子引用
 * AtomicReference（只能通过引用值是否相同来判断引用是否被其他线程修改）
 * AtomicMarkableReference（版本号追踪引用被线程修改的次数）
 * AtomicStampedReference（只用一个boolean变量标记是否有线程对引用做了修改）
 */
@Slf4j
public class Atomic {
    public static void main(String[] args) {
//        atomicInteger();
//        atomicReference();
        atomicStampReference();
    }

    public static void atomicInteger() {
        AtomicInteger integer = new AtomicInteger(0);
//        CAS方法，传入期望值和更新值
        integer.compareAndSet(3, 4);
//        等价于++i（保证自增和取值的原子性）
        System.out.println(integer.incrementAndGet());//1
//        等价于i++（保证自增和取值的原子性）
        System.out.println(integer.getAndIncrement());//1
//        等价于先复制再自加2
        System.out.println(integer.getAndAdd(2));//2
//        等价于i+=2
        System.out.println(integer.addAndGet(2));//6
//        可以传入lambda表达式执行复杂运算，保证原子性
        System.out.println(integer.getAndUpdate(x -> x * 2 + 5));//6
        System.out.println(integer.updateAndGet(x -> x * 2 + 5));//39
    }

    public static void atomicReference() {
        AtomicReference<String> reference = new AtomicReference<>("A");
        String ref = reference.get();
        /**
         * 2020-04-13 15:42:19.284 [t1] INFO  Chapter6.Atomic - 修改成功
         * 2020-04-13 15:42:19.287 [t1] INFO  Chapter6.Atomic - C
         * 2020-04-13 15:42:20.287 [t2] INFO  Chapter6.Atomic - 修改成功
         * 2020-04-13 15:42:20.287 [t2] INFO  Chapter6.Atomic - A
         */
        other(reference);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//       AtomicReference 原子引用无法感知到其他线程对引用值是否做过修改，只是通过判断引用值是否变化来判断进行修改操作
        if (reference.compareAndSet(ref, "B")) {
            log.info("修改成功");
        }
        /**
         * 2020-04-13 15:42:22.287 [main] INFO  Chapter6.Atomic - 修改成功
         * 2020-04-13 15:42:22.287 [main] INFO  Chapter6.Atomic - B
         */
        log.info(reference.get());
    }

    public static void other(AtomicReference<String> reference) {
        new Thread(() -> {
            if (reference.compareAndSet(reference.get(), "C")) {
                log.info("修改成功");
            }
            log.info(reference.get());
        }, "t1").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            if (reference.compareAndSet(reference.get(), "A")) {
                log.info("修改成功");
            }
            log.info(reference.get());
        }, "t2").start();
    }

    public static void atomicStampReference() {
        AtomicStampedReference<String> reference = new AtomicStampedReference<>("A", 0);
        String ref = reference.getReference();
        int stamp = reference.getStamp();
        /**
         * 2020-04-13 15:55:48.423 [main] INFO  Chapter6.Atomic - 修改成功
         * 2020-04-13 15:55:48.426 [main] INFO  Chapter6.Atomic - C
         * 2020-04-13 15:55:49.430 [main] INFO  Chapter6.Atomic - 修改成功
         * 2020-04-13 15:55:49.430 [main] INFO  Chapter6.Atomic - A
         */
        otherStamp(reference);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//       AtomicStampedReference 通过版本号的追踪可以感知到线程对引用值做过几次修改
        if (reference.compareAndSet(ref, "B", stamp, stamp + 1))
            log.info("修改成功");
        else
            log.info("修改失败");
        /**
         * 2020-04-13 15:55:51.433 [main] INFO  Chapter6.Atomic - 修改失败
         * 2020-04-13 15:55:51.433 [main] INFO  Chapter6.Atomic - A
         */
        log.info(reference.getReference());
    }

    public static void otherStamp(AtomicStampedReference<String> reference) {
        if (reference.compareAndSet(reference.getReference(), "C", reference.getStamp(), reference.getStamp() + 1))
            log.info("修改成功");
        else
            log.info("修改失败");
        log.info(reference.getReference());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (reference.compareAndSet(reference.getReference(), "A", reference.getStamp(), reference.getStamp() + 1))
            log.info("修改成功");
        else
            log.info("修改失败");
        log.info(reference.getReference());
    }
}
