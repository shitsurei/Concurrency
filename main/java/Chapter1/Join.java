package Chapter1;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 05
 * join方法
 * 等待线程执行结束，底层是wait方法
 */
@Slf4j
public class Join {
    static int a = 1;

    public static void main(String[] args) {
        Runnable task = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                a = 10;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread t1 = new Thread(task, "t1");
        t1.start();
        try {
//            调用join方法会使得当前线程等待执行join方法的线程结束再运行
//            t1.join();
//            调用带参数的join方法最多会等待线程执行参数时间
            t1.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("a = " + a);
//      调用t1.join(1000)输出  2020-03-28 22:47:35.767 [main] INFO  Chapter1.Join - a = 1
//      调用t1.join()输出  2020-03-28 22:44:21.325 [main] INFO  Chapter1.Join - a = 10
    }
}
