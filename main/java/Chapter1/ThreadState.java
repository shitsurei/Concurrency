package Chapter1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 03
 * 线程的状态
 *
 * sleep
 * 1 调用sleep会让当前线程从Running转为Timed Waiting状态
 * 2 其他线程可以使用interrupt方法打断正在睡眠的线程，此时sleep方法会抛异常
 * 3 睡眠结束后的线程转为就绪态而非立刻执行
 * 4 建议用TimeUnit的sleep方法，可读性更强
 *
 * yield
 * 1 调用yield方法会使当前线程从Running状态转为Runnable状态
 * 2 具体实现依赖于操作系统的作业调度算法
 *
 * 使用场景：防止while true空转浪费CPU资源，适用于无锁同步的场景
 */
@Slf4j
public class ThreadState {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.info("hello");
            try {
                TimeUnit.SECONDS.sleep(5);
//            等价于Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.warn("wake up...");
            }
        }, "MyThread01");
        log.info(t1.getState().name());//NEW
        t1.start();
        log.info(t1.getState().name());//RUNNABLE

        try {
            TimeUnit.SECONDS.sleep(1);
//            等价于Thread.sleep(1000);
            log.info(t1.getState().name());//TIMED_WAITING
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            唤醒睡眠的线程
        t1.interrupt();
    }
}
