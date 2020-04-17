package Chapter8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static java.lang.Thread.sleep;

/**
 * 05
 * AQS（抽象队列同步器）：阻塞式锁和相关同步工具的框架
 * 特点：
 * 1 用state属性来表示资源的状态（分独占模式和共享模式），子类需要定义如何维护这个状态，控制如何获取锁和释放锁（通过CAS方式修改状态）
 * 2 提供了基于FIFO的等待队列，类似于Monitor的EntryList
 * 3 支持多个条件变量来实现等待、唤醒机制（通过park/unpark实现），类似于Monitor的WaitSet
 */
@Slf4j
public class AQS {
    public static void main(String[] args) {
        /**
         * 线程公用锁的情况
         * 2020-04-17 18:43:56.047 [t1] INFO  Chapter8.AQS - locking
         * 2020-04-17 18:43:57.053 [t1] INFO  Chapter8.AQS - unlocking
         * 2020-04-17 18:43:57.054 [t2] INFO  Chapter8.AQS - locking
         * 2020-04-17 18:43:57.054 [t2] INFO  Chapter8.AQS - unlocking
         */
        MyLock lock = new MyLock();
        new Thread(() -> {
            lock.lock();
            try {
                log.info("locking");
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.info("unlocking");
                lock.unlock();
            }
        }, "t1").start();
        new Thread(() -> {
            lock.lock();
            try {
                log.info("locking");
            } finally {
                log.info("unlocking");
                lock.unlock();
            }
        }, "t2").start();
    }
}

//自实现的【不可重入锁】
class MyLock implements Lock {

    //    独占锁   同步器类
    class MySync extends AbstractQueuedSynchronizer {
        @Override
//        尝试获得锁
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)) {
//                成功表示已经加锁，设置Owner为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
//        解锁
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
//            state是volatile的，将其执行放在后面可以保证之前操作的可见性
            setState(0);
            return true;
        }

        @Override
//        是否独占锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }
    }

    private MySync sync = new MySync();

    @Override
//    加锁方法，加锁失败则会进入等带队列等待
    public void lock() {
        sync.acquire(1);
    }

    @Override
//    可打断的加锁方法
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
//    尝试一次加锁
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
//    带超时的尝试加锁
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
//    解锁
    public void unlock() {
        sync.release(1);
    }

    @Override
//    新建条件变量
    public Condition newCondition() {
        return sync.newCondition();
    }
}