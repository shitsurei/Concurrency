package Chapter4;

import lombok.extern.slf4j.Slf4j;

/**
 * 04
 * 活锁
 * 两个线程的运行导致不断修改对方的终止条件
 * 解决方法：
 * 增加随机的睡眠时间
 */
@Slf4j
public class LiveLock {
    static volatile int a = 10;

    public static void main(String[] args) {
        new Thread(() -> {
            while (a > 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a--;
                log.info(a + "");
            }
        }, "t1").start();
        new Thread(() -> {
            while (a < 20) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a++;
                log.info(a + "");
            }
        }, "t2").start();
    }
}
