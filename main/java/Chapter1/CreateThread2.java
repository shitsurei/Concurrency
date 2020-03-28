package Chapter1;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 02
 * Thread和Runnable的关系：
 * Thread是线程，Runnable是任务
 */
@Slf4j
public class CreateThread2 {
    public static void main(String[] args) {
//      lambda表达式简化创建线程
        Thread t = new Thread(() -> log.info("hello"), "MyThread001");
        t.start();

//        方式3 FutureTask对象可以将线程执行的结果通过get方法返回主线程
        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("running...");
                Thread.sleep(1000);
                return 100;
            }
        });
        Thread t2 = new Thread(task, "MyThread002");
        t2.start();
        try {
//            task.get()执行该方法的线程（即主线程）会一直阻塞，直到得到线程执行完的返回结果
            log.info(t2.getName() + " result: " + task.get());
//            输出  2020-03-28 17:00:30.121 [main] INFO  Chapter1.CreateThread2 - MyThread002 result: 100
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
