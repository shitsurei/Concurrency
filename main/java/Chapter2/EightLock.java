package Chapter2;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 02
 * 线程八锁问题
 */
@Slf4j
public class EightLock {
    public static void main(String[] args) {
//        question1();
        question2();
    }

    public static void question1() {
        class Number {
            public synchronized void a() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("1");
            }

            public synchronized void b() {
                log.info("2");
            }
        }
        Number number = new Number();
//        有可能线程1先被调用，也有可能线程2先被调用
//        如果线程1先被调用，则会睡眠1秒再输出1和2
//        此时锁住的是number对象
        new Thread(() -> number.a(), "t1").start();
        new Thread(() -> number.b(), "t2").start();
    }

    public static void question2() {
        Number number = new Number();
//        有可能线程1先被调用，也有可能线程2先被调用
//        如果线程1先被调用，则会睡眠1秒再输出1和2
//        此时锁住的是Number.class对象
        new Thread(() -> number.a(), "t1").start();
        new Thread(() -> number.b(), "t2").start();
    }

    static class Number {
        public static synchronized void a() {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("1");
        }

        public static synchronized void b() {
            log.info("2");
        }
    }
}
