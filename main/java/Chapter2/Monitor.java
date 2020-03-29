package Chapter2;

import lombok.extern.slf4j.Slf4j;

/**
 * 06
 * Java对象头
 * 以32位虚拟机为例
 * 1 普通对象的对象头有8个字节（4个字节的Mark Word和4个字节的Klass Word）
 * Klass Word   存储指向该对象的类模板对应的class对象的指针
 * Mark Word    25bit的hashcode  4bit的年龄分代   1bit的偏向锁状态   2bit的加锁状态
 * 2 数组对象
 * <p>
 * 管程/监视器（Monitor）
 * 1 synchronized锁底层是通过管程来实现的，每一个Java对象都可以关联一个Monitor对象，但只有加锁后才会关联
 * 2 使用synchronized关键字对对象上锁后，对象头的Mark Word就会改变为【30bit的指向Monitor对象的指针和2bit的加锁状态】
 * 3 Monitor的数据结构包括WaitSet、EntryList（指向阻塞队列）和Owner（指向持有锁对象的线程）
 * 4 不同的锁对象关联不同的Monitor对象
 * <p>
 * 线程执行临界区的底层过程：
 * 【从字节码的角度，对synchronized关键编译后会使用助记符monitorenter和monitorexit（成对出现）修改对象的Mark Word，唤醒阻塞队列】
 * 1 线程1执行到临界区之前发现锁对象的对象头中Mark Word已经指向了一个Monitor对象，于是该线程通过指针引用查询Monitor对象的Owner属性是否为空
 * a 为空，当前没有线程持有临界区的锁对象，于是将Owner指针指向自己，开始执行临界区代码
 * b 不为空，当前有线程正在执行临界区代码，于是将自己连接到阻塞队列EntryList尾部，并将线程陷入阻塞状态（block）
 * 2 执行完临界区代码之后，线程1清除Owner标记，并唤醒阻塞队列中的其他线程
 * 3 阻塞队列中的其他线程开始争夺锁对象（具体哪一个线程获取锁对象取决于虚拟机的底层实现）
 */
@Slf4j
public class Monitor {
    public void method() {
        synchronized (this) {
            int a = 1;
        }
    }
}
/**
 * public void method();
 *     descriptor: ()V
 *     flags: ACC_PUBLIC
 *     Code:
 *       stack=2, locals=4, args_size=1
 *          0: aload_0
 *          1: dup
 *          2: astore_1
 *          加锁
 *          3: monitorenter
 *          4: iconst_1
 *          5: istore_2
 *          6: aload_1
 *          释放锁
 *          7: monitorexit
 *          8: goto          16
 *         11: astore_3
 *         12: aload_1
 *         出现异常后释放锁
 *         13: monitorexit
 *         14: aload_3
 *         15: athrow
 *         16: return
 *       异常表
 *       Exception table:
 *          from    to  target type
 *              4     8    11   any
 *             11    14    11   any
 *       LineNumberTable:
 *         line 31: 0
 *         line 32: 4
 *         line 33: 6
 *         line 34: 16
 *       局部变量表
 *       LocalVariableTable:
 *         Start  Length  Slot  Name   Signature
 *             0      17     0  this   LChapter2/Monitor;
 */
