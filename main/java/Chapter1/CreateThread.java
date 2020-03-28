package Chapter1;

import lombok.extern.slf4j.Slf4j;

/**
 * 01
 * 创建线程的方式
 */
@Slf4j
public class CreateThread {
    public static void main(String[] args) {
//        方式1 匿名内部类方式创建Thread子类（同理可以继承Thread类，重写run方法）
        Thread t = new Thread("MyThread001"){
            @Override
            public void run() {
//                要执行的任务
                log.info("hello");
//              输出  2020-03-28 16:41:18.916 [MyThread001] INFO  Chapter1.CreateThread - hello
            }
        };
//        启动线程，注意该方法不会导致线程的run方法立即执行，需要等待CPU分配给该线程运行的时间片（即不可控）
//        且start方法只能调用一次，多次调用会报错
        t.start();

//        方式2 匿名内部类方式创建匿名类对象实现Runnable接口，重写run方法，将该对象传入Thread类
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("world");
//                输出  2020-03-28 16:47:51.373 [MyThread002] INFO  Chapter1.CreateThread - world
            }
        };
        Thread t2 = new Thread(runnable,"MyThread002");
        t2.start();
    }
}
