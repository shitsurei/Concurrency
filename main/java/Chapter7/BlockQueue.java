package Chapter7;

import Chapter7.MyThreadPool.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 04
 * 自定义线程池
 */
@Slf4j
public class BlockQueue {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(1, 1000, TimeUnit.MILLISECONDS, 1, (queue, task) -> {
//            拒绝策略1：死等
//            queue.put(task);
//            拒绝策略2：带超时的等待
//            if (!queue.offer(task, 500, TimeUnit.MILLISECONDS)) {
//                2020-04-13 22:24:14.267 [main] INFO  Chapter7.BlockQueue - 任务等待超时，未执行 Chapter7.BlockQueue$$Lambda$2/885951223@67b64c45
//                log.info("任务等待超时，未执行 {}", task);
//            }
//            拒绝策略3：直接放弃任务执行
//            2020-04-13 22:25:42.973 [main] INFO  Chapter7.BlockQueue - 阻塞队列已满，放弃执行 Chapter7.BlockQueue$$Lambda$2/885951223@67b64c45
//            log.info("阻塞队列已满，放弃执行 {}",task);
//            拒绝策略4：抛出异常，剩余任务不执行
            /**
             * 2020-04-13 22:27:15.372 [main] INFO  Chapter7.ThreadPool - 新增worker，Thread[Thread-0,5,main],Chapter7.BlockQueue$$Lambda$2/885951223@19dfb72a
             * 2020-04-13 22:27:15.378 [main] INFO  Chapter7.BlockingQueue - 加入任务队列，Chapter7.BlockQueue$$Lambda$2/885951223@3796751b
             * 2020-04-13 22:27:15.379 [Thread-0] INFO  Chapter7.ThreadPool - 正在执行任务,Chapter7.BlockQueue$$Lambda$2/885951223@19dfb72a
             * Exception in thread "main" java.lang.RuntimeException: 任务失败 Chapter7.BlockQueue$$Lambda$2/885951223@67b64c45
             * 	at Chapter7.BlockQueue.lambda$main$0(BlockQueue.java:31)
             * 	at Chapter7.BlockingQueue.tryPut(BlockQueue.java:228)
             * 	at Chapter7.ThreadPool.execute(BlockQueue.java:111)
             * 	at Chapter7.BlockQueue.main(BlockQueue.java:35)
             * 2020-04-13 22:27:16.384 [Thread-0] INFO  Chapter7.BlockQueue - 0
             * 2020-04-13 22:27:16.384 [Thread-0] INFO  Chapter7.ThreadPool - 正在执行任务,Chapter7.BlockQueue$$Lambda$2/885951223@3796751b
             * 2020-04-13 22:27:17.387 [Thread-0] INFO  Chapter7.BlockQueue - 1
             * 2020-04-13 22:27:18.392 [Thread-0] INFO  Chapter7.ThreadPool - worker被移除,Thread[Thread-0,5,main]
             */
//            throw new RuntimeException("任务失败 " + task);
//            拒绝策略5：主线程自己调用任务
            /**
             * 任务2 3由主线程自己执行
             * 2020-04-13 22:28:34.747 [main] INFO  Chapter7.ThreadPool - 新增worker，Thread[Thread-0,5,main],Chapter7.BlockQueue$$Lambda$2/885951223@19dfb72a
             * 2020-04-13 22:28:34.752 [main] INFO  Chapter7.BlockingQueue - 加入任务队列，Chapter7.BlockQueue$$Lambda$2/885951223@3796751b
             * 2020-04-13 22:28:34.752 [Thread-0] INFO  Chapter7.ThreadPool - 正在执行任务,Chapter7.BlockQueue$$Lambda$2/885951223@19dfb72a
             * 2020-04-13 22:28:35.756 [main] INFO  Chapter7.BlockQueue - 2
             * 2020-04-13 22:28:35.756 [Thread-0] INFO  Chapter7.BlockQueue - 0
             * 2020-04-13 22:28:36.760 [main] INFO  Chapter7.BlockQueue - 3
             * 2020-04-13 22:28:36.760 [Thread-0] INFO  Chapter7.ThreadPool - 正在执行任务,Chapter7.BlockQueue$$Lambda$2/885951223@3796751b
             * 2020-04-13 22:28:36.760 [main] INFO  Chapter7.BlockingQueue - 加入任务队列，Chapter7.BlockQueue$$Lambda$2/885951223@67b64c45
             * 2020-04-13 22:28:37.768 [Thread-0] INFO  Chapter7.BlockQueue - 1
             * 2020-04-13 22:28:37.768 [Thread-0] INFO  Chapter7.ThreadPool - 正在执行任务,Chapter7.BlockQueue$$Lambda$2/885951223@67b64c45
             * 2020-04-13 22:28:38.771 [Thread-0] INFO  Chapter7.BlockQueue - 4
             * 2020-04-13 22:28:39.772 [Thread-0] INFO  Chapter7.ThreadPool - worker被移除,Thread[Thread-0,5,main]
             */
            task.run();
        });
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("{}", finalI);
            });
        }
    }
}