package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 11
 * 利用ReentrantLock实现线程交替执行
 */
@Slf4j
public class Alternately2 {
    public static void main(String[] args) throws InterruptedException {
        Alter2 alter2 = new Alter2(5);
//        设置不同的条件变量，通过唤醒不同等带队列中的线程来控制下一次要执行的操作
        Condition A = alter2.newCondition();
        Condition B = alter2.newCondition();
        Condition C = alter2.newCondition();
        new Thread(() -> {
//            设置要打印的信息，当前线程的等待区和执行过后要进入的等待区
            alter2.print("a", A, B);
        }, "A").start();
        new Thread(() -> {
            alter2.print("b", B, C);
        }, "B").start();
        new Thread(() -> {
            alter2.print("c", C, A);
        }, "C").start();

        TimeUnit.SECONDS.sleep(1);
        alter2.lock();
        try {
            log.info("开始执行");
//            唤醒初始执行任务的等待区线程
            A.signal();
        } finally {
            alter2.unlock();
        }
        /**
         * 2020-04-12 15:29:15.935 [main] INFO  Chapter4.Alternately2 - 开始执行
         * 2020-04-12 15:29:15.953 [A] INFO  Chapter4.Alter2 - a
         * 2020-04-12 15:29:15.953 [B] INFO  Chapter4.Alter2 - b
         * 2020-04-12 15:29:15.953 [C] INFO  Chapter4.Alter2 - c
         * 2020-04-12 15:29:15.953 [A] INFO  Chapter4.Alter2 - a
         * 2020-04-12 15:29:15.956 [B] INFO  Chapter4.Alter2 - b
         * 2020-04-12 15:29:15.956 [C] INFO  Chapter4.Alter2 - c
         * 2020-04-12 15:29:15.956 [A] INFO  Chapter4.Alter2 - a
         * 2020-04-12 15:29:15.957 [B] INFO  Chapter4.Alter2 - b
         * 2020-04-12 15:29:15.957 [C] INFO  Chapter4.Alter2 - c
         * 2020-04-12 15:29:15.959 [A] INFO  Chapter4.Alter2 - a
         * 2020-04-12 15:29:15.959 [B] INFO  Chapter4.Alter2 - b
         * 2020-04-12 15:29:15.959 [C] INFO  Chapter4.Alter2 - c
         * 2020-04-12 15:29:15.960 [A] INFO  Chapter4.Alter2 - a
         * 2020-04-12 15:29:15.960 [B] INFO  Chapter4.Alter2 - b
         * 2020-04-12 15:29:15.961 [C] INFO  Chapter4.Alter2 - c
         */
    }
}

@Slf4j
class Alter2 extends ReentrantLock {
    private int loop;

    public Alter2(int loop) {
        this.loop = loop;
    }

    public void print(String msg, Condition curr, Condition next) {
        for (int i = 0; i < loop; i++) {
            this.lock();
            try {
                curr.await();
                log.info(msg);
                next.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.unlock();
            }
        }
    }
}
