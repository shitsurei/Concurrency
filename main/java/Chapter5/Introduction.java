package Chapter5;

import lombok.extern.slf4j.Slf4j;

/**
 * 01
 * JMM（java内存模型）
 * 定义了主存、工作内存抽象概念，底层对应着CPU寄存器、缓存、硬件内存、CPU指令优化
 * 主存：所有线程共享的数据
 * 工作内存：线程私有的数据
 * <p>
 * 体现：
 * 1 原子性：保证指令不会受到线程上下文切换的影响
 * 2 可见性：保证指令不会受到CPU缓存的影响
 * 3 顺序性：保证指令不会受到CPU指令优化的影响
 */
@Slf4j
public class Introduction {
//    volatile关键字会使主存中变量修改后立即同步到工作内存中（synchronized也可以，但是开销更大）
    private volatile static boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
//            由于该变量被频繁访问，JIT编译器会将run变量从主存中读取到工作内存（高速缓存）
            while (run) {

            }
        }, "t1").start();
        Thread.sleep(1000);
        log.info("停止线程");
//        执行后线程并未停止
        run = false;
    }
}
