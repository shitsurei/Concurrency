package Chapter7.MyThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
/**
 * 阻塞队列
 * 作为外部线程使用线程池中的线程对象执行任务和线程池中的线程对象消费任务之间的缓冲组件
 */
public class BlockingQueue<T> {
    //    任务队列
    private Deque<T> taskQueue = new ArrayDeque<>();
    //    锁（保证多个线程在【从队列中获取任务（队列头获取）】或【向队列中添加任务（队列尾添加）】时不会产生线程不安全的问题）
    private ReentrantLock lock = new ReentrantLock();
    //    生产者条件变量（当任务队列已满时，添加任务的线程需要陷入阻塞，等待任务队列被消费后再唤醒添加）
    private Condition fullWaitSet = lock.newCondition();
    //    消费者条件变量（当任务队列为空时，获取任务的线程需要陷入阻塞，等待任务队列添加新任务后再唤醒获取）
    private Condition emptyWaitSet = lock.newCondition();
    //    任务队列最大容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    //    线程【带超时】的获取任务，任务队列为空时线程阻塞等待，等待超时后返回null
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (taskQueue.isEmpty()) {
//                返回的剩余时间（等待时间-经过时间）
                long rest = emptyWaitSet.awaitNanos(nanos);
                if (rest <= 0)
                    return null;
            }
            T t = taskQueue.removeFirst();
            fullWaitSet.signalAll();
            return t;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    //    线程获取任务，任务队列为空时线程阻塞等待
    public T take() {
        lock.lock();
        try {
            while (taskQueue.isEmpty()) {
                emptyWaitSet.await();
            }
            T t = taskQueue.removeFirst();
            fullWaitSet.signalAll();
            return t;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    //    线程添加任务，任务队列已满时线程阻塞等待
    public void put(T task) {
        lock.lock();
        try {
            while (taskQueue.size() == capacity) {
                fullWaitSet.await();
                log.info("等待加入任务队列.... {}", task);
            }
            taskQueue.addLast(task);
            log.info("加入任务队列，{}", task);
            emptyWaitSet.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    //    线程【带超时】的添加任务，任务队列已满时线程阻塞等待，等待超时后返回false表示添加失败，添加成功返回true
    public boolean offer(T task, long timeout, TimeUnit unit) {
        lock.lock();
        try {
            while (taskQueue.size() == capacity) {
                log.info("等待加入任务队列.... {}", task);
                long rest = fullWaitSet.awaitNanos(unit.toNanos(timeout));
                if (rest <= 0)
                    return false;
            }
            taskQueue.addLast(task);
            log.info("加入任务队列，{}", task);
            emptyWaitSet.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return true;
    }

    //    线程【可自定义拒绝策略】的添加任务，任务队列已满时，执行传入的解决策略
    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
//            判断队列是否已满
            if (taskQueue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                taskQueue.addLast(task);
                log.info("加入任务队列，{}", task);
                emptyWaitSet.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    //    获取队列中等待任务的数量
    public int size() {
        lock.lock();
        try {
            return capacity;
        } finally {
            lock.unlock();
        }
    }
}
