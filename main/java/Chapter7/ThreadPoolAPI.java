package Chapter7;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 05
 * JDK提供的线程池实现
 * ExecutorService接口：线程池的基本接口
 * ScheduledExecutorService接口：带有任务调度功能的线程池的扩展接口
 * ThreadPoolExecutor类：基础实现类
 * ScheduledThreadPoolExecutor类：带有任务调度功能的扩展实现类
 * <p>
 * 1 线程池状态：
 * ThreadPoolExecutor类使用int整数的高3位表示线程状态，低29位表示线程数量
 * （用一个数保存的好处是只需要进行一次CAS操作就可以改变数量信息和状态信息）
 * RUNNING      111     接受新任务       处理阻塞队列中的任务
 * SHUTDOWN     000     不接受新任务     处理阻塞队列中的任务
 * STOP         001     不接受新任务     中断正在执行的任务，抛弃阻塞队列中的任务
 * TIDYING      010     任务全部执行完毕，活动线程为0，即将进入关闭的状态
 * TERMINATED   011     终结状态
 * 【注意】RUNNING状态数字最小，因为int最高位为1表示负数
 * 2 JDK提供的拒绝策略：
 * RejectExecutionHandler接口，基本接口
 * AbortPolicy实现类：抛出异常，默认策略
 * CallerRunsPolicy实现类：让调用者自己去执行任务
 * DiscardPolicy实现类：放弃本次任务
 * DiscardOldestPolicy实现类：放弃队列中最早等待的任务，将当前任务加入队列
 * 3 Dubbo的实现：
 * 抛出异常之前会记录日志，并且dump线程栈信息，方便定位问题
 * 4 Netty的实现：
 * 创建一个新的线程来执行任务【不推荐】
 * 5 ActiveMQ的实现：
 * 带超时等待（60s）尝试放入队列
 * 6 PinPoint的实现：
 * 责任链模式，按一定顺序尝试执行多种拒绝策略
 */
@Slf4j
public class ThreadPoolAPI {
    public static void main(String[] args) throws InterruptedException {
        /**
         * 自定义线程池类在添加任务时的触发机制依次为
         * 【核心线程执行--核心线程不足时-->任务队列等待--任务队列已满时-->急救线程执行--急救线程不足时-->采取拒绝策略】
         * 【注意，急救线程的触发前提是采用有限容量的任务队列，如数组，如果是链式的则任务队列不会到达上限】
         * public ThreadPoolExecutor(
         *      int corePoolSize,       核心线程数目（最多长期保留的线程数，核心线程在执行完线程任务后不回收）
         *      int maximumPoolSize,    最大线程数目（包括核心线程和急救线程，急救线程在执行完任务后回收资源）
         *      long keepAliveTime,     急救线程的最长生存时间和时间单位
         *      TimeUnit unit,
         *      BlockingQueue<Runnable> workQueue,      任务队列
         *      ThreadFactory threadFactory,            线程工厂
         *      RejectedExecutionHandler handler)       拒绝策略
         */
//        固定线程个数的线程池（无急救线程），等待队列为链式实现
        ExecutorService fixed = Executors.newFixedThreadPool(2);
//        for (int i = 0; i < 5; i++) {
//            int finalI = i;
//            fixed.execute(() -> {
//                log.info(String.valueOf(finalI));
//            });
//        }
//        带缓冲功能的线程池（只创建60s的救济线程），可以创建Integer的上限个急救线程，使用SynchronousQueue作为等待队列
//        适用于任务数比较密集，但每个任务执行时间较短的场景
        ExecutorService cache = Executors.newCachedThreadPool();
//        单线程的线程池，任务队列无上限，保证整个任务串行执行
//        使用FinalizableDelegatedExecutorService对线程池的实现类做了包装，限制了对部分方法的访问
//        优点在当线程应为某个任务出错失败时，构造器会为我们创建一个新的线程执行后续任务
        ExecutorService single = Executors.newSingleThreadExecutor();
        /**
         * 2020-04-14 18:07:30.583 [pool-3-thread-1] INFO  Chapter7.ThreadPoolAPI - 1 1
         * 2020-04-14 18:07:30.587 [pool-3-thread-2] INFO  Chapter7.ThreadPoolAPI - 2
         * 2020-04-14 18:07:30.587 [pool-3-thread-2] INFO  Chapter7.ThreadPoolAPI - 3
         * Exception in thread "pool-3-thread-1" java.lang.ArithmeticException: / by zero
         */
        single.execute(() -> {
            log.info("1 1");
            int i = 1 / 0;
            log.info("1 2");
        });
        single.execute(() -> {
            log.info("2");
        });
        single.execute(() -> {
            log.info("3");
        });
    }

    /**
     * 2020-04-14 17:41:06.150 [t1] INFO  Chapter7.ThreadPoolAPI - putting 1
     * 2020-04-14 17:41:07.156 [t2] INFO  Chapter7.ThreadPoolAPI - taking t1
     * 2020-04-14 17:41:07.156 [t1] INFO  Chapter7.ThreadPoolAPI - putted 1
     * 2020-04-14 17:41:07.156 [t2] INFO  Chapter7.ThreadPoolAPI - took t1 1
     * 2020-04-14 17:41:07.161 [t2] INFO  Chapter7.ThreadPoolAPI - taking t2
     * 2020-04-14 17:41:08.162 [t3] INFO  Chapter7.ThreadPoolAPI - putting 2
     * 2020-04-14 17:41:08.163 [t3] INFO  Chapter7.ThreadPoolAPI - putted 2
     * 2020-04-14 17:41:08.163 [t2] INFO  Chapter7.ThreadPoolAPI - took t2 2
     */
    public static void testSynQueue() throws InterruptedException {
//        容量无上限，实现方式为只有当有线程空闲时才允许任务放入等带队列
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
        new Thread(() -> {
            log.info("putting 1");
            try {
                synchronousQueue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("putted 1");
        }, "t1").start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            try {
                log.info("taking t1");
                Integer t1 = synchronousQueue.take();
                log.info("took t1 {}", t1);
                log.info("taking t2");
                Integer t2 = synchronousQueue.take();
                log.info("took t2 {}", t2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            log.info("putting 2");
            try {
                synchronousQueue.put(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("putted 2");
        }, "t3").start();
    }
}
