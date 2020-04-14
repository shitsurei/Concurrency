package Chapter7.MyThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * 线程池
 */
public class ThreadPool {
    //    任务队列
    private BlockingQueue<Runnable> taskQueue;
    //    线程集合
    private HashSet<Worker> workers = new HashSet<>();
    //    核心线程数
    private int coreSize;
    //    拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;
    //    获取任务的超时时间（超时时间内如果任务队列中获取不到任务，则该线程对象自动回收）
    private long timeout;
    private TimeUnit unit;

    public ThreadPool(int coreSize, long timeout, TimeUnit unit, int capacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.unit = unit;
        this.rejectPolicy = rejectPolicy;
        this.taskQueue = new BlockingQueue<>(capacity);
    }

    /**
     * 线程的包装类
     */
    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
//            当task任务不为空，执行任务
            while (task != null) {
                try {
                    log.info("正在执行任务,{}", task);
                    task.run();
                } finally {
//            当task执行完毕，从任务队列获取正在等待的新任务执行
//                    策略1：take方法为无超时时间的等待从任务队列中获取新任务
//                    task = taskQueue.take();
//                    策略2：poll方法为有超时时间的等待从任务队列中获取新任务
                    task = taskQueue.poll(timeout, unit);
                }
            }
//            超时时间内没有从任务队列中获取任务，释放该线程
            synchronized (workers) {
                log.info("worker被移除,{}", this);
                workers.remove(this);
            }
        }
    }

    //    线程池执行执行任务
    public void execute(Runnable task) {
//        线程集合为HashSet，需要保证线程安全
        synchronized (workers) {
//        当任务数没有超过coreSize时
            if (workers.size() < coreSize) {
//        直接新建Worker对象交给其执行
                Worker worker = new Worker(task);
                log.info("新增worker，{},{}", worker, task);
//                将新创建的线程对象加入线程集合，后面复用
                workers.add(worker);
//                启动线程
                worker.start();
            } else {
//        当任务数超过coreSize时暂存入任务队列等待线程空闲时执行，同时传入拒绝策略，处理当任务队列已满时的处理逻辑
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }
}
