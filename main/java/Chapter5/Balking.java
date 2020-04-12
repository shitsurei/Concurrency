package Chapter5;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 03
 * 犹豫模式（懒加载）
 * 用于一个线程发现另一个线程或本线程已经做了一件相同的事，那么本线程就无需再做了，直接结束返回
 * 应用：实现线程安全的单例
 */
public class Balking {
    public static void main(String[] args) throws InterruptedException {
        Balk balk = new Balk();
        /**
         * 多次调用但只有一次执行
         * 2020-04-12 17:02:44.292 [t1] INFO  Chapter5.Balk - working...
         * 2020-04-12 17:02:46.295 [t1] INFO  Chapter5.Balk - working...
         */
        balk.work();
        balk.work();
        balk.work();
        balk.work();
        TimeUnit.SECONDS.sleep(3);
        balk.stop();
    }
}

@Slf4j
class Balk {
    private volatile boolean stop = false;
    private boolean worked = false;
    private Thread t1;

    public void work() {
        synchronized (this) {
            if (worked)
                return;
            worked = true;
        }
        t1 = new Thread(() -> {
            while (!stop) {
                log.info("working...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1");
        t1.start();
    }

    public void stop() {
        stop = true;
        t1.interrupt();
    }
}
