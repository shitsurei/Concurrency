package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 01
 * 多把锁
 * 将锁的力度变细，可以有效提高并发度
 * 【但是如果有线程要同时获取两个锁对象时容易发生死锁】
 */
public class MultiLock {
    public static void main(String[] args) {
        Task task = new Task();
        new Thread(()->{
            try {
//                2020-04-02 19:41:09.170 [t1] INFO  Chapter4.Task - method1
                task.method1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1").start();
        new Thread(()->{
            try {
//                2020-04-02 19:41:09.170 [t2] INFO  Chapter4.Task - method2
                task.method2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2").start();
    }
}
@Slf4j
class Task{
//    执行method1和method2的两个线程同时开始运行，但如果锁住的是this对象，就要等待其中一个执行结果另一个才能开始执行
    final Object lock1 = new Object();
    final Object lock2 = new Object();
    public void method1() throws InterruptedException {
        synchronized (lock1){
            log.info("method1");
            TimeUnit.SECONDS.sleep(2);
        }
    }
    public void method2() throws InterruptedException {
        synchronized (lock2){
            log.info("method2");
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
