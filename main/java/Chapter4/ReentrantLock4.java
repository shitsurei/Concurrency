package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 08
 * ReentrantLock的条件变量类比于synchronized中的wait set等待集合
 * 不同点：synchronized监视器中只有一块wait set空间，无法对不同的等待条件做划分，执行notifyAll方法会产生虚假唤醒现象
 * ReentrantLock支持多条件变量
 * <p>
 * 执行流程：
 * await之前需要先获取锁对象
 * await执行后会释放锁，进入conditionObject等待
 * await线程被唤醒（或打断，或超时）会重新竞争ReentrantLock锁
 * 竞争到锁后执行之后的代码
 */
@Slf4j
public class ReentrantLock4 {
    private static ReentrantLock room = new ReentrantLock();
    private static boolean cigar = false;
    private static Condition waitCigar = room.newCondition();
    private static boolean takeout = false;
    private static Condition waitTakeout = room.newCondition();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
//            1 先获取锁对象（同时在finally块中释放锁对象）
            room.lock();
            try {
                log.info("有烟吗?{}", cigar);
                while (!cigar) {
//                    2 进入waitCigar的等待区等待
                    waitCigar.await();
                }
                log.info("有烟吗?{}", cigar);
                if (cigar)
                    log.info("有烟了，开始干活");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                room.unlock();
            }
        }, "小南");
        Thread t2 = new Thread(() -> {
            room.lock();
            try {
                log.info("有外卖吗?{}", takeout);
                while (!takeout) {
                    waitTakeout.await();
                }
                log.info("有外卖吗?{}", takeout);
                if (takeout)
                    log.info("有外卖了，开始干活");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                room.unlock();
            }
        }, "小女");
        Thread t3 = new Thread(() -> {
            room.lock();
            try {
//                3 修改共享资源
                cigar = true;
//                4 唤醒waitCigar中的线程（因为只有一个，所以调用signal方法即可）
                waitCigar.signal();
            } finally {
                room.unlock();
            }
        }, "送烟的");
        Thread t4 = new Thread(() -> {
            room.lock();
            try {
                takeout = true;
                waitTakeout.signal();
            } finally {
                room.unlock();
            }
        }, "送外卖的");
        /**
         * 2020-04-12 00:00:20.215 [小南] INFO  Chapter4.ReentrantLock4 - 有烟吗?false
         * 2020-04-12 00:00:20.218 [小女] INFO  Chapter4.ReentrantLock4 - 有外卖吗?false
         * 2020-04-12 00:00:22.213 [小南] INFO  Chapter4.ReentrantLock4 - 有烟吗?true
         * 2020-04-12 00:00:22.215 [小南] INFO  Chapter4.ReentrantLock4 - 有烟了，开始干活
         * 2020-04-12 00:00:24.214 [小女] INFO  Chapter4.ReentrantLock4 - 有外卖吗?true
         * 2020-04-12 00:00:24.215 [小女] INFO  Chapter4.ReentrantLock4 - 有外卖了，开始干活
         */
        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(2);
        t3.start();
        TimeUnit.SECONDS.sleep(2);
        t4.start();
    }
}
