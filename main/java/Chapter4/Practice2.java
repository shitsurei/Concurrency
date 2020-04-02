package Chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 03
 * 哲学家就餐问题
 */
public class Practice2 {
    public static void main(String[] args) {
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        new Philosopher("阿基米德", c5, c1).start();
        /**
         * 名称: 阿基米德
         * 状态: Chapter4.Chopstick@6f0e21b6上的BLOCKED, 拥有者: 苏格拉底
         * 总阻止数: 5, 总等待数: 2
         *
         * 堆栈跟踪:
         * Chapter4.Philosopher.run(Practice2.java:44)
         *    - 已锁定 Chapter4.Chopstick@6c5864cb
         *
         * 名称: 苏格拉底
         * 状态: Chapter4.Chopstick@67da07d9上的BLOCKED, 拥有者: 柏拉图
         * 总阻止数: 13, 总等待数: 3
         *
         * 堆栈跟踪:
         * Chapter4.Philosopher.run(Practice2.java:44)
         *    - 已锁定 Chapter4.Chopstick@6f0e21b6
         *
         * 名称: 柏拉图
         * 状态: Chapter4.Chopstick@366fa93上的BLOCKED, 拥有者: 亚里士多德
         * 总阻止数: 4, 总等待数: 2
         *
         * 堆栈跟踪:
         * Chapter4.Philosopher.run(Practice2.java:44)
         *    - 已锁定 Chapter4.Chopstick@67da07d9
         *
         * 名称: 亚里士多德
         * 状态: Chapter4.Chopstick@39bb3457上的BLOCKED, 拥有者: 赫拉克利特
         * 总阻止数: 11, 总等待数: 4
         *
         * 堆栈跟踪:
         * Chapter4.Philosopher.run(Practice2.java:44)
         *    - 已锁定 Chapter4.Chopstick@366fa93
         *
         * 名称: 赫拉克利特
         * 状态: Chapter4.Chopstick@6c5864cb上的BLOCKED, 拥有者: 阿基米德
         * 总阻止数: 7, 总等待数: 2
         *
         * 堆栈跟踪:
         * Chapter4.Philosopher.run(Practice2.java:44)
         *    - 已锁定 Chapter4.Chopstick@39bb3457
         */
    }
}

@Slf4j
class Philosopher extends Thread {
    private Chopstick left;
    private Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while (true) {
//            获取左手筷子
            synchronized (left) {
//            获取右手筷子
                synchronized (right) {
                    eat();
                }
            }
        }
    }

    private void eat() {
        log.info("eating...");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Chopstick {
    private String name;

    public Chopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}
