package Chapter8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 04
 * Fork/Join线程池
 * JDK7引入，使用分治的思想，适用于能够进行【任务拆分的CPU密集型运算】
 * 默认会创建和CPU核心数大小相同的线程池
 * 线程池的并发度取决于任务拆分后任务之间的独立性是否足够强
 * JDK8中的Stream流将拆分的工作封装进API内部，减小了开发的难度
 */
@Slf4j
public class ForkJoinThreadPool {
    public static void main(String[] args) {
        cut2();
    }

    public static void cut2() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Integer invoke = forkJoinPool.invoke(new MyTask2(1, 5));
//        2020-04-17 18:16:45.980 [main] INFO  Chapter8.ForkJoinThreadPool - result = 15
        log.info("result = {}", invoke);
    }

    public static void cut1() {
//        默认创建和CPU核心数相同大小的线程池，也可以通过传参自定义
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Integer invoke = forkJoinPool.invoke(new MyTask(5));
//        2020-04-17 17:56:14.401 [main] INFO  Chapter8.ForkJoinThreadPool - result = 15
        log.info("result = {}", invoke);
    }
}

// 计算1~n之间的整数求和
@Slf4j
class MyTask extends RecursiveTask<Integer> {

    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "{" + "n=" + n + '}';
    }

    @Override
//    计算过程类似递归
    protected Integer compute() {
        if (n == 1) {
            log.info("join() {}", n);
            return 1;
        }
        MyTask t1 = new MyTask(n - 1);
//        让一个线程去执行
        t1.fork();
        log.info("fork() {} + {}", n, t1);
//        获取任务结果
        int res = n + t1.join();
        log.info("join() {} + {} = {}", n, t1, res);
        return res;
    }
    /**
     * 创建了4个线程【先拆分再合并】，这种任务的拆分方式会产生导致任务之间产生依赖，并发度不高，可以通过二分来优化
     * 2020-04-17 17:57:12.546 [ForkJoinPool-1-worker-3] INFO  Chapter8.MyTask - fork() 3 + {n=2}           第三步
     * 2020-04-17 17:57:12.546 [ForkJoinPool-1-worker-0] INFO  Chapter8.MyTask - fork() 2 + {n=1}           第四步
     * 2020-04-17 17:57:12.552 [ForkJoinPool-1-worker-3] INFO  Chapter8.MyTask - join() 1                   第五步
     * 2020-04-17 17:57:12.546 [ForkJoinPool-1-worker-2] INFO  Chapter8.MyTask - fork() 4 + {n=3}           第二步
     * 2020-04-17 17:57:12.546 [ForkJoinPool-1-worker-1] INFO  Chapter8.MyTask - fork() 5 + {n=4}           第一步
     * 2020-04-17 17:57:12.552 [ForkJoinPool-1-worker-0] INFO  Chapter8.MyTask - join() 2 + {n=1} = 3       第六步
     * 2020-04-17 17:57:12.552 [ForkJoinPool-1-worker-3] INFO  Chapter8.MyTask - join() 3 + {n=2} = 6       第七步
     * 2020-04-17 17:57:12.552 [ForkJoinPool-1-worker-2] INFO  Chapter8.MyTask - join() 4 + {n=3} = 10      第八步
     * 2020-04-17 17:57:12.552 [ForkJoinPool-1-worker-1] INFO  Chapter8.MyTask - join() 5 + {n=4} = 15      第九步
     */
}

@Slf4j
class MyTask2 extends RecursiveTask<Integer> {

    private int start;
    private int end;

    public MyTask2(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "MyTask2{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    protected Integer compute() {
        if (start == end) {
            log.info("join() {}", start);
            return start;
        }
        if (start == end - 1) {
            log.info("join() {}", start + end);
            return start + end;
        }
        int mid = (start + end) / 2;
        MyTask2 left = new MyTask2(start, mid);
        left.fork();
        MyTask2 right = new MyTask2(mid + 1, end);
        right.fork();
        log.info("fork() {} + {}", left, right);
        int ans = left.join() + right.join();
        log.info("join() {} + {} = {}", left, right, ans);
        return ans;
    }
    /**按二分法分割提高并发度
     * 2020-04-17 18:16:45.974 [ForkJoinPool-1-worker-3] INFO  Chapter8.MyTask2 - join() 9
     * 2020-04-17 18:16:45.973 [ForkJoinPool-1-worker-2] INFO  Chapter8.MyTask2 - fork() MyTask2{start=1, end=2} + MyTask2{start=3, end=3}
     * 2020-04-17 18:16:45.979 [ForkJoinPool-1-worker-3] INFO  Chapter8.MyTask2 - join() 3
     * 2020-04-17 18:16:45.973 [ForkJoinPool-1-worker-0] INFO  Chapter8.MyTask2 - join() 3
     * 2020-04-17 18:16:45.974 [ForkJoinPool-1-worker-1] INFO  Chapter8.MyTask2 - fork() MyTask2{start=1, end=3} + MyTask2{start=4, end=5}
     * 2020-04-17 18:16:45.979 [ForkJoinPool-1-worker-2] INFO  Chapter8.MyTask2 - join() MyTask2{start=1, end=2} + MyTask2{start=3, end=3} = 6
     * 2020-04-17 18:16:45.980 [ForkJoinPool-1-worker-1] INFO  Chapter8.MyTask2 - join() MyTask2{start=1, end=3} + MyTask2{start=4, end=5} = 15
     */
}
