package Chapter1;

import lombok.extern.slf4j.Slf4j;

/**
 * 04
 * 线程优先级
 * 默认有三种状态min（数值为1）、norm（数值为5）、max（数值为10）
 * 线程优先级会提示调度器优先调度该线程，但是这仅仅是一个提示，调度器可以忽略
 * CPU越繁忙，优先级作用越明显（yield也类似）
 */
@Slf4j
public class ThreadPriority {
    public static void main(String[] args) {
        Runnable task1 = () -> {
            int count = 0;
            while (true) {
//                由于t1线程每次循环都先让渡自己的执行权限，因此t1线程增长速度落后于t2
                /**
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787531
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787532
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787533
                 * 2020-03-28 18:59:54.525 [MyThread1] INFO  Chapter1.ThreadPriority - times:13376
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787534
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787535
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787536
                 * 2020-03-28 18:59:54.525 [MyThread1] INFO  Chapter1.ThreadPriority - times:13377
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787537
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787538
                 * 2020-03-28 18:59:54.525 [MyThread1] INFO  Chapter1.ThreadPriority - times:13378
                 * 2020-03-28 18:59:54.525 [MyThread2] INFO  Chapter1.ThreadPriority - times:787539
                 */
//                Thread.yield();
                log.info("times:" + count++);
            }
        };
        Runnable task2 = () -> {
            int count = 0;
            while (true)
                log.info("times:" + count++);
        };
        Thread t1 = new Thread(task1, "MyThread1");
        Thread t2 = new Thread(task2, "MyThread2");
//        经试验优先级设置比yield的作用更强
        t1.setPriority(Thread.MAX_PRIORITY);
        t2.setPriority(Thread.MIN_PRIORITY);
        t1.start();
        t2.start();
    }
}
