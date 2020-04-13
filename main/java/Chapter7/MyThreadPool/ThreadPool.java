package Chapter7.MyThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPool {
    //    任务队列
    private BlockingQueue<Runnable> taskQueue;
    //    线程集合
    private HashSet<Worker> workers = new HashSet<>();
    //    核心线程数
    private int coreSize;
    //    获取任务的超时时间
    private long timeout;
    private TimeUnit unit;
    //    拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit unit, int capacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.unit = unit;
        this.rejectPolicy = rejectPolicy;
        this.taskQueue = new BlockingQueue<>(capacity);
    }

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
//            当task执行完毕，从任务队列获取任务并执行
//                    策略1：take方法为无超时时间的等待从任务队列中获取新任务
//                    task = taskQueue.take();
//                    策略2：poll方法为有超时时间的等待从任务队列中获取新任务
                    task = taskQueue.poll(timeout, unit);
                }
            }
            synchronized (workers) {
                log.info("worker被移除,{}", this);
                workers.remove(this);
            }
        }
    }

    public void execute(Runnable task) {
        synchronized (workers) {
//        当任务数没有超过coreSize时，直接交给worker对象执行，超过时加入任务队列缓存
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.info("新增worker，{},{}", worker, task);
                worker.start();
                workers.add(worker);
            } else {
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }
}
