package Chapter7;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 08
 * 工作线程模式，通过有限的工作线程轮流处理无限多的任务，也可以归类为分工模式
 * 【不同的任务类型应该使用不同的线程池，这样能够避免饥饿，并且提升效率】
 * 固定大小的线程池会有饥饿现象（此处的饥饿现象是指线程不足导致的饥饿）
 */
@Slf4j
public class WorkThread {
    private static final List<String> MENU = Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁", "烤鸡翅");
    private static Random random = new Random();

    private static String cooking() {
        return MENU.get(random.nextInt(MENU.size()));
    }

    private static void work2() {
        ExecutorService cookPool = Executors.newFixedThreadPool(1);
        ExecutorService witterPool = Executors.newFixedThreadPool(1);
        Runnable r = () -> {
            log.info("点菜中……");
            FutureTask<String> food = (FutureTask<String>) cookPool.submit(() -> {
                log.info("做菜中……");
                return cooking();
            });
            try {
                log.info("上菜 {}", food.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        };
//        两个线程正常工作
        witterPool.execute(r);
        witterPool.execute(r);
        /**
         * 2020-04-16 15:54:40.875 [pool-2-thread-1] INFO  Chapter7.WorkThread - 点菜中……
         * 2020-04-16 15:54:40.879 [pool-1-thread-1] INFO  Chapter7.WorkThread - 做菜中……
         * 2020-04-16 15:54:40.880 [pool-2-thread-1] INFO  Chapter7.WorkThread - 上菜 地三鲜
         * 2020-04-16 15:54:40.882 [pool-2-thread-1] INFO  Chapter7.WorkThread - 点菜中……
         * 2020-04-16 15:54:40.882 [pool-1-thread-1] INFO  Chapter7.WorkThread - 做菜中……
         * 2020-04-16 15:54:40.882 [pool-2-thread-1] INFO  Chapter7.WorkThread - 上菜 烤鸡翅
         */
    }

    /**
     * 同一个线程池中的线程处理不同任务时一旦线程不足，就可能出现饥饿现象
     */
    private static void work1() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Runnable r = () -> {
            log.info("点菜中……");
            FutureTask<String> food = (FutureTask<String>) pool.submit(() -> {
                log.info("做菜中……");
                return cooking();
            });
            try {
                log.info("上菜 {}", food.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        };
//        两个线程同时陷入阻塞
//        2020-04-16 15:50:54.495 [pool-1-thread-1] INFO  Chapter7.WorkThread - 点菜中……
        pool.execute(r);
//        2020-04-16 15:50:54.495 [pool-1-thread-2] INFO  Chapter7.WorkThread - 点菜中……
        pool.execute(r);
    }

    public static void main(String[] args) {
//        work1();
        work2();
    }
}
