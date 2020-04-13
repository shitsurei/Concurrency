package Chapter7.MyThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class BlockingQueue<T> {
    //    任务队列
    private Deque<T> deque = new ArrayDeque<>();
    //    锁
    private ReentrantLock lock = new ReentrantLock();
    //    生产者条件变量
    private Condition fullWaitSet = lock.newCondition();
    //    消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();
    //    容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    //    带超时的阻塞等待
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (deque.isEmpty()) {
//                返回的剩余时间
                long rest = emptyWaitSet.awaitNanos(nanos);
                if (rest <= 0)
                    return null;
            }
            T t = deque.removeFirst();
            fullWaitSet.signalAll();
            return t;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    //    阻塞获取
    public T take() {
        lock.lock();
        try {
            while (deque.isEmpty()) {
                emptyWaitSet.await();
            }
            T t = deque.removeFirst();
            fullWaitSet.signalAll();
            return t;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    //    阻塞添加
    public void put(T task) {
        lock.lock();
        try {
            while (deque.size() == capacity) {
                fullWaitSet.await();
                log.info("等待加入任务队列.... {}", task);
            }
            deque.addLast(task);
            log.info("加入任务队列，{}", task);
            emptyWaitSet.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    //    带超时时间的阻塞添加
    public boolean offer(T task, long timeout, TimeUnit unit) {
        lock.lock();
        try {
            while (deque.size() == capacity) {
                log.info("等待加入任务队列.... {}", task);
                long rest = fullWaitSet.awaitNanos(unit.toNanos(timeout));
                if (rest <= 0)
                    return false;
            }
            deque.addLast(task);
            log.info("加入任务队列，{}", task);
            emptyWaitSet.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return true;
    }

    //    获取大小
    public int size() {
        lock.lock();
        try {
            return capacity;
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
//            判断队列是否已满
            if (deque.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                deque.addLast(task);
                log.info("加入任务队列，{}", task);
                emptyWaitSet.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
