package Chapter1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 06
 * interrupt方法，用于打断线程
 * 1 打断处于阻塞中（sleep、wait、join）的线程
 * 2 打断正常运行的线程
 * 3 打断park线程
 * 静态方法Thread.interrupted()和非静态方法isInterrupted()类似，都能获取打断标记，但是静态方法执行结束会会将打断标记重置为false
 * <p>
 * 两阶段终止模式：
 * 想要停止线程的最佳实践为在外部执行interrupt方法改变打断标志位，在线程内部通过判断标志位的改变来结束线程的执行
 * 注意对于睡眠状态的线程打断后需要在catch块中重置打断标志位为true
 *
 * 不推荐使用的方法，容易破坏同步代码块，造成对象的锁得不到释放，产生死锁：
 * stop()       强行终止线程
 * suspend()    挂起线程
 * resume()     恢复线程运行
 */
@Slf4j
public class Interrupt {
    public static void main(String[] args) {
//        interruptRunning();
//        interruptTimeWait();
        interruptPark();
    }

    /**
     * 打断正在运行的线程
     */
    public static void interruptRunning() {
        Thread t1 = new Thread(() -> {
            while (true) {
//                打断运行中的线程不会导致线程停止，需要在线程执行内部对打断标志位做判断
                if (Thread.currentThread().isInterrupted()) {
//                    建议使用这种方式打断线程，这样可以在线程内部执行中断前的部分操作
                    log.info("finally code");
                    break;
                }
            }
        }, "t1");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        打断运行中的线程，打断标志位会变为true
        t1.interrupt();
    }

    /**
     * 打断处于阻塞状态的线程
     */
    public static void interruptTimeWait() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted())
                    break;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
//                    睡眠状态的线程被打断后会将打断标志位重置为false，此时如果要进行结束线程的判断，需要在catch块中重置标志位为true
//                    否则不执行该行，打断异常会抛出但不会进入终止线程判断，因为标志位被重置为false
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }, "t1");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
//        打断睡眠状态中的线程，会在抛出异常后重置线程的打断状态，因此打断后输出打断标志位为false
        log.info("interrupt signal : " + t1.isInterrupted());
//      输出  2020-03-28 22:57:48.489 [main] INFO  Chapter1.Interrupt - interrupt signal : false
    }

    /**
     * 打断park线程
     */
    public static void interruptPark() {
        Thread t1 = new Thread(() -> {
            log.info("park");
//            执行park方法会判断当前的打断标志
//            如果为false会导致线程直接阻塞，对其进行打断可以使线程继续向下执行
            LockSupport.park();
            log.info("unpark");
//            2020-03-28 23:47:06.439 [t1] INFO  Chapter1.Interrupt - interrupt signal : true
            log.info("interrupt signal : " + Thread.currentThread().isInterrupted());
//            如果为true线程会继续向下执行，因此想多次使用park线程可以使用静态方法interrupted()重置打断标志位
            Thread.interrupted();
            LockSupport.park();
        }, "t1");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
    }
}
