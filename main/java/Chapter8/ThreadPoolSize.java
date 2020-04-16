package Chapter8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * 01
 * 线程池中线程数量的选择：
 * 1 CPU密集型运算：例如数据分析和计算
 * 通常采用CPU核数+1能够实现最优的CPU利用率
 * 加1是保证当前线程由于页缺失故障或其他原因导致暂停时，额外的这个线程就能顶上去，保证CPU时钟周期不被浪费
 * <p>
 * 2 IO密集型运算：例如IO操作，远程RPC调用，数据库操作
 * 线程数 = 核数 * 期望CPU利用率 * （CPU计算时间 + 等待时间） / CPU计算时间
 */
@Slf4j
public class ThreadPoolSize {
    public static void main(String[] args) {
//        任务调度线程池
        ScheduledExecutorService schedulePool = Executors.newScheduledThreadPool(2);
//        testDelay(schedulePool);
        testAround(schedulePool);
    }

    //    scheduleAtFixedRate实现每隔一段时间执行一次任务【注意，任务本身执行时间超过间隔时间时不会造成任务重叠】
    public static void testAround(ScheduledExecutorService schedulePool) {
        log.info("start");
//        schedulePool.scheduleAtFixedRate(() -> {
//            log.info("running");
//        }, 2, 1, TimeUnit.SECONDS);
        /**
         * 2020-04-16 16:30:40.208 [pool-1-thread-1] INFO  Chapter8.ThreadPoolSize - start
         * 2020-04-16 16:30:42.208 [pool-1-thread-1] INFO  Chapter8.ThreadPoolSize - running
         * 2020-04-16 16:30:43.200 [pool-1-thread-1] INFO  Chapter8.ThreadPoolSize - running
         * 2020-04-16 16:30:44.200 [pool-1-thread-2] INFO  Chapter8.ThreadPoolSize - running
         * 2020-04-16 16:30:45.200 [pool-1-thread-1] INFO  Chapter8.ThreadPoolSize - running
         * 2020-04-16 16:30:46.200 [pool-1-thread-1] INFO  Chapter8.ThreadPoolSize - running
         */
        schedulePool.scheduleWithFixedDelay(() -> {
            log.info("running");
            try {
                sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    //        schedule方法实现延时执行
    public static void testDelay(ScheduledExecutorService schedulePool) {
        schedulePool.schedule(() -> {
            log.info("s1");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1, TimeUnit.SECONDS);
        /**
         * 同时执行
         * 2020-04-16 16:24:30.745 [pool-1-thread-2] INFO  Chapter8.ThreadPoolSize - s2
         * 2020-04-16 16:24:30.745 [pool-1-thread-1] INFO  Chapter8.ThreadPoolSize - s1
         */
        schedulePool.schedule(() -> {
            log.info("s2");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1, TimeUnit.SECONDS);
    }
}
