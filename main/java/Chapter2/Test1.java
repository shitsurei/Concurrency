package Chapter2;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * 05
 * 习题——卖票
 */
@Slf4j
public class Test1 {
    public static void main(String[] args) {
//        模拟多人买票
        TicketWindow ticketWindow = new TicketWindow(2000);
        List<Thread> customers = new ArrayList<>();
        List<Integer> sells = new Vector<>();
        for (int i = 0; i < 1000; i++) {
//            卖票
            Thread thread = new Thread(() -> {
                int sell = ticketWindow.sell(randomAmount());
                sells.add(sell);
            }, "customer" + i);
            customers.add(thread);
            thread.start();
        }
//        等待线程执行结束
        for(Thread thread : customers) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        统计卖出票数和剩余票数
        log.info("已卖出票数：" + sells.stream().reduce(0,Integer::sum));
        log.info("剩余票数：" + ticketWindow.getCount());
        /**
         * 出现线程同步问题
         * 2020-03-29 18:58:16.397 [main] INFO  Chapter2.Test1 - 已卖出票数：2002
         * 2020-03-29 18:58:16.399 [main] INFO  Chapter2.Test1 - 剩余票数：0
         */
    }

    static Random random = new Random();

    public static int randomAmount() {
        return random.nextInt(5) + 1;
    }
}

/**
 * 售票窗口
 */
class TicketWindow {
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

//    该方法中存在对成员变量的读写操作，需要加锁保证线程安全
    public synchronized int sell(int amount) {
        if (this.count >= amount) {
            this.count -= amount;
            return amount;
        } else
            return 0;
    }
}