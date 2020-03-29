package Chapter2;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * 04
 * 常见的线程安全类
 * 1 String（不可变量是线程安全的，字符串创建后是不可变的）
 * 【这也解释了String类被设计成final的原因，就是为了保证类中的方法不被子类继承后重写，从而破坏线程安全，这体现了设计模式中的“闭合原则”】
 * 2 包装类
 * 3 StringBuilder
 * 4 Random
 * 5 Vector 线程安全的list实现
 * 6 HashTable 线程安全的Map实现
 * 7 juc包下的类
 * <p>
 * 这里的线程安全是指，多个线程调用他们同一个实例方法时是线程安全的，即【他们每个方法是原子的】
 * 【但是多个方法的组合不是原子的】
 */
@Slf4j
public class SafeThreadClass {
    public static void main(String[] args) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int num = 200000;
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < num; i++) {
                if (map.get(i) == null)
                    map.put(i, i);
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < num; i++) {
                if (map.get(i) != null)
                    map.remove(i);
            }
        }, "t2");
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        2020-03-29 18:39:18.510 [main] INFO  Chapter2.SafeThreadClass - map size = 199915
        log.info("map size = " + map.size());
    }
}
