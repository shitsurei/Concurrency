package Chapter5;

import lombok.extern.slf4j.Slf4j;

/**
 * 02 volatile优化两阶段终止模式
 */
@Slf4j
public class TwoStepStop {
    private volatile static boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (stop) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("working...");
            }
        }, "t1").start();

        /**
         * 2020-04-12 16:50:10.945 [t1] INFO  Chapter5.TwoStepStop - working...
         * 2020-04-12 16:50:11.947 [t1] INFO  Chapter5.TwoStepStop - working...
         * 2020-04-12 16:50:12.949 [t1] INFO  Chapter5.TwoStepStop - working...
         */
        Thread.sleep(3000);
        stop = true;
    }
}
