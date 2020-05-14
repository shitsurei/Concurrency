package Chapter9;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 02
 */
@Slf4j
public class FutureTask1 {
    public static void main(String[] args) {
        log.info("start");
        Callable<String> task = () -> {
            TimeUnit.SECONDS.sleep(1);
            return "test";
        };
        FutureTask<String> ans = new FutureTask<>(task);
        new Thread(ans).start();
        try {
            /**
             * 2020-05-14 16:55:27.343 [main] INFO  Chapter9.FutureTask1 - start
             * 此处主线程代码被阻塞，等待FutureTask执行结果
             * 2020-05-14 16:55:28.428 [main] INFO  Chapter9.FutureTask1 - test
             * 2020-05-14 16:55:28.428 [main] INFO  Chapter9.FutureTask1 - end
             */
            log.info(ans.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        log.info("end");
    }
}
