package Chapter4;

import lombok.extern.slf4j.Slf4j;

/**
 * 10
 * 同步模式之交替输出：
 * 假设有ABC三个线程，各自的功能是打印输出abc，要求三个线程按顺序交替打印若干次
 */
@Slf4j
public class Alternately {
    public static void main(String[] args) {
//        设置初始打印线程为1，循环5次
        Alter alter = new Alter(1, 5);
//            启动三个线程
        new Thread(() -> {
//            每个线程中设置要打印的内容，当前线程序号和下一个线程序号，以此实现交替打印
            alter.print("A", 1, 2);
        }, "t1").start();
        new Thread(() -> {
            alter.print("B", 2, 3);
        }, "t2").start();
        new Thread(() -> {
            alter.print("C", 3, 1);
        }, "t3").start();
        /**
         * 2020-04-12 15:14:55.456 [t1] INFO  Chapter4.Alter - A
         * 2020-04-12 15:14:55.462 [t2] INFO  Chapter4.Alter - B
         * 2020-04-12 15:14:55.462 [t3] INFO  Chapter4.Alter - C
         * 2020-04-12 15:14:55.462 [t1] INFO  Chapter4.Alter - A
         * 2020-04-12 15:14:55.462 [t2] INFO  Chapter4.Alter - B
         * 2020-04-12 15:14:55.462 [t3] INFO  Chapter4.Alter - C
         * 2020-04-12 15:14:55.462 [t1] INFO  Chapter4.Alter - A
         * 2020-04-12 15:14:55.462 [t2] INFO  Chapter4.Alter - B
         * 2020-04-12 15:14:55.463 [t3] INFO  Chapter4.Alter - C
         * 2020-04-12 15:14:55.463 [t1] INFO  Chapter4.Alter - A
         * 2020-04-12 15:14:55.463 [t2] INFO  Chapter4.Alter - B
         * 2020-04-12 15:14:55.463 [t3] INFO  Chapter4.Alter - C
         * 2020-04-12 15:14:55.463 [t1] INFO  Chapter4.Alter - A
         * 2020-04-12 15:14:55.463 [t2] INFO  Chapter4.Alter - B
         * 2020-04-12 15:14:55.463 [t3] INFO  Chapter4.Alter - C
         */
    }
}

@Slf4j
class Alter {
//    当前应该执行的线程索引
    private int current;
//    循环次数
    private int loop;

    public Alter(int current, int loop) {
        this.current = current;
        this.loop = loop;
    }

    /**
     * 传入当前线程索引和要打印的内容以及下一个执行的线程索引
     * @param msg
     * @param index
     * @param next
     */
    public void print(String msg, int index, int next) {
        for (int i = 0; i < loop; i++) {
            synchronized (this) {
//                如果当前线程和传入的索引不相同则等待
                while (index != current) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                相同则打印内容
                log.info(msg);
//                并将当前线程指向下一个索引
                current = next;
//                唤醒其他等待中的线程（此时index参数与下一个线程索引相同的线程就会执行打印操作）
                this.notifyAll();
            }
        }
    }
}