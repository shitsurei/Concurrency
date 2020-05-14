package Chapter9;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 03
 * 获取当前运行的线程数
 */
@Slf4j
public class ThreadGroup1 {
    public static void main(String[] args) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        log.info("curT:" + group.activeCount());
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        for (Thread t : threads)
            log.info("cur t:" + t.getName());
        /**
         * 2020-05-14 17:34:36.588 [main] INFO  Chapter9.ThreadGroup1 - curT:2
         * 2020-05-14 17:34:36.594 [main] INFO  Chapter9.ThreadGroup1 - cur t:main
         * 2020-05-14 17:34:36.594 [main] INFO  Chapter9.ThreadGroup1 - cur t:Monitor Ctrl-Break
         */


        while (group.getParent() != null) {
            group = group.getParent();
        }
        log.info("topT:" + group.activeCount());
        threads = new Thread[group.activeCount()];
//        将活跃的线程引用复制到数组
        group.enumerate(threads);
        for (Thread t : threads)
            log.info("top t:" + t.getName());
        /**
         * 2020-05-14 17:34:36.594 [main] INFO  Chapter9.ThreadGroup1 - topT:6
         * 2020-05-14 17:34:36.594 [main] INFO  Chapter9.ThreadGroup1 - top t:Reference Handler
         * 2020-05-14 17:34:36.595 [main] INFO  Chapter9.ThreadGroup1 - top t:Finalizer
         * 2020-05-14 17:34:36.595 [main] INFO  Chapter9.ThreadGroup1 - top t:Signal Dispatcher
         * 2020-05-14 17:34:36.595 [main] INFO  Chapter9.ThreadGroup1 - top t:Attach Listener
         * 2020-05-14 17:34:36.595 [main] INFO  Chapter9.ThreadGroup1 - top t:main
         * 2020-05-14 17:34:36.595 [main] INFO  Chapter9.ThreadGroup1 - top t:Monitor Ctrl-Break
         */
    }
}
