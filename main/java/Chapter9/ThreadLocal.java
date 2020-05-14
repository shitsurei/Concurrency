package Chapter9;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 01
 * ThreadLocal类位于lang包下，可以用于申请和存储线程私有的内存空间及变量
 */
@Slf4j
public class ThreadLocal {
    private static java.lang.ThreadLocal<String> threadLocal = new java.lang.ThreadLocal<>();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                threadLocal.set("t1:" + i);
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(threadLocal.get());
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                threadLocal.set("t2:" + i);
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(threadLocal.get());
            }
        });
        t1.start();
        t2.start();
        for (int i = 0; i < 50; i++) {
            threadLocal.set("main:" + i);
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(threadLocal.get());
        }
    }
}
