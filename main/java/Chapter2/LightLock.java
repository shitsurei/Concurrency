package Chapter2;

/**
 * 07
 * 轻量级锁
 * 如果对一个对象虽然有多线程访问，但访问的顺序是错开的（也就是没有竞争），可以使用轻量级锁优化
 * 轻量级锁对使用者是透明的，即语法和重量级锁Monitor相同都是synchronized
 * 因此当执行到synchronized代码块时，虚拟机会优先使用轻量级锁，出现竞争时才改用重量级锁
 * 执行流程：
 * 1 创建锁记录（Lock Record）对象，每个线程都会在栈帧中包含一个或多个锁记录的结构，该结构由两部分组成：【cas，用于交换锁记录的Mark Word区域】和【锁对象的地址】
 * a 在锁记录中的对象引用位置存储锁对象的地址（即指针指向锁对象），并尝试用cas（末尾为00，标识轻量级锁）替换锁对象头的Mark Word
 * b 如果锁记录中Mark Word已经被其他线程占有（后两位为00），则交换失败，表明有竞争，进入“锁膨胀阶段”
 * c 如果自己执行了synchronized锁重入，那么再添加一条锁记录作为重入的计数（锁对象引用仍然指向之前的锁对象，cas设为null）
 * 【锁重入计数：线程对同一个锁对象加了几次锁，就有几个锁记录】
 * 2 当执行完加锁代码块退出（即解锁）时
 * a 如果有取值为null的锁记录，就表示有重入，这时要重置锁记录，表示重入数减一
 * b 如果锁记录取值不为null，这时使用cas把Mark Word的值恢复给对象头（成功则解锁成功，失败说明已经上升为重量级锁，进入重量级解锁流程）
 */
public class LightLock {
    static final Object lock = new Object();

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                method1();
            }).start();
        }
    }

    public static void method1() {
//    两个方法同步块，用同一个对象加锁
        synchronized (lock) {
            method2();
        }
    }

    public static void method2() {
        synchronized (lock) {

        }
    }

}
