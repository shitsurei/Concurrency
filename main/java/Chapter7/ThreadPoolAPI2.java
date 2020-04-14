package Chapter7;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * 06
 * ThreadPoolExecutor执行任务的相关方法
 */
@Slf4j
public class ThreadPoolAPI2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
//        执行任务方法，传入参数为Runnable类型，无返回参数
        threadPool.execute(() -> {
            log.info("1");
        });
//        执行任务，传入参数为Callable类型，通过【保护性暂停模式】在主线程中接收返回值
//        Future相等于生成的中间对象，用于两个线程之间通信
        Future<String> future = threadPool.submit(() -> {
            Thread.sleep(1000);
//            返回值返回时会唤醒阻塞中的主线程
            return "ok";
        });
//        2020-04-14 18:21:35.775 [main] INFO  Chapter7.ThreadPoolAPI2 - ok
        log.info(future.get());

        List<Callable<String>> list = new Vector<>();
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            list.add(() -> {
                /**
                 * 三个线程交替执行
                 * 2020-04-14 19:10:02.908 [pool-1-thread-1] INFO  Chapter7.ThreadPoolAPI2 - 0
                 * 2020-04-14 19:10:02.908 [pool-1-thread-3] INFO  Chapter7.ThreadPoolAPI2 - 2
                 * 2020-04-14 19:10:02.908 [pool-1-thread-2] INFO  Chapter7.ThreadPoolAPI2 - 1
                 * 2020-04-14 19:10:02.911 [pool-1-thread-2] INFO  Chapter7.ThreadPoolAPI2 - 3
                 * 2020-04-14 19:10:02.911 [pool-1-thread-1] INFO  Chapter7.ThreadPoolAPI2 - 4
                 */
                log.info(String.valueOf(finalI));
                return "ok" + finalI;
            });
        }
//        传入任务集合，返回返回值集合
        List<Future<String>> res = threadPool.invokeAll(list);
//        传入任务集合，返回线程最先得到结果的返回值，【其他线程不再继续执行】
//        String s = threadPool.invokeAny(list);
        res.forEach(t -> {
            try {
                /**
                 * 2020-04-14 19:05:56.647 [main] INFO  Chapter7.ThreadPoolAPI2 - ok0
                 * 2020-04-14 19:05:56.647 [main] INFO  Chapter7.ThreadPoolAPI2 - ok1
                 * 2020-04-14 19:05:56.647 [main] INFO  Chapter7.ThreadPoolAPI2 - ok2
                 * 2020-04-14 19:05:56.647 [main] INFO  Chapter7.ThreadPoolAPI2 - ok3
                 * 2020-04-14 19:05:56.647 [main] INFO  Chapter7.ThreadPoolAPI2 - ok4
                 */
                log.info(t.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
