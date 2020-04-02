package Chapter3;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 04
 * 同步模式【结果一旦产生好就会被消费掉】之保护性暂停（Guarded Suspension）
 * 用于一个线程等待另一个线程的执行结果
 * 要点：
 * 1 有一个线程要等待另一个线程的执行结果，就将这两个线程关联到同一个Guarded Object上
 * 2 如果有结果不断从一个线程到两一个线程，就可以使用消息队列（生产者，消费者模式）
 * 3 JDK中，join方法和Future类的实现就采用的此模式
 */
@Slf4j
public class GuardedSuspend {
    /**
     * 2020-04-02 17:56:54.070 [t2] INFO  Chapter3.GuardedSuspend - 执行下载中……
     * 2020-04-02 17:56:54.180 [t1] INFO  Chapter3.GuardedSuspend - <!DOCTYPE html>
     * 2020-04-02 17:56:54.180 [t1] INFO  Chapter3.GuardedSuspend - <!--STATUS OK--><html>……
     */
    public static void main(String[] args) {
        GuardedObject object = new GuardedObject();
//        线程1等待线程2下载结果
        new Thread(() -> {
            try {
                /**
                 * 2020-04-02 18:03:37.505 [t2] INFO  Chapter3.GuardedSuspend - 执行下载中……
                 * Exception in thread "t1" java.lang.NullPointerException
                 * 	at Chapter3.GuardedSuspend.lambda$main$0(GuardedSuspend.java:35)
                 * 	at java.lang.Thread.run(Thread.java:748)
                 * 2020-04-02 18:03:37.606 [t2] INFO  Chapter3.GuardedSuspend - 下载完成
                 */
                List<String> lines = (List<String>) object.getResponse(10);
                if (lines != null)
                    for (String e : lines)
                        log.info(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        new Thread(() -> {
            try {
                log.info("执行下载中……");
                HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://www.baidu.com").openConnection();
                List<String> lines = new LinkedList<>();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    lines.add(line);
                object.setResponse(lines);
                log.info("下载完成");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}

class GuardedObject {
    //    中间结果
    private Object response;

    //    获取结果方法，执行时会对this对象上锁
    public synchronized Object getResponse() throws InterruptedException {
        while (this.response == null)
            wait();
        return this.response;
    }

    //    带超时参数的获取结果方法
    public synchronized Object getResponse(long timeOut) throws InterruptedException {
        long begin = System.currentTimeMillis();
        while (this.response == null) {
            long passTime = System.currentTimeMillis() - begin;
            if (passTime >= timeOut)
                break;
//            出现虚假唤醒时应该重置等待时间
            wait(timeOut - passTime);
        }
        return this.response;
    }

    //    生成结果方法，执行后会对调用this锁对象中等待的线程
    public synchronized void setResponse(Object response) {
        this.response = response;
        notifyAll();
    }
}
