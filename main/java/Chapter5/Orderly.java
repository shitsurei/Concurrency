package Chapter5;

/**
 * 04
 * 有序性
 * <p>
 * 指令重排序优化：在不改变执行结果的情况下，通过重排序和组合来实现指令级并行
 * CPU层面，每条指令的执行可以分为5个阶段：
 * 1 取指令    instruction fetch
 * 2 指令译码  instruction decode
 * 3 指令执行  execute
 * 4 内存访问  memory access
 * 5 数据写回  register write back
 * 现在CPU支持多级指令流水线，即同时执行以上5个步骤，称为五级指令流水线
 * 本质上流水线技术不能缩短单条指令的执行时间，但是变相提高了指令的吞吐率
 * <p>
 * 多线程环境下，指令重排序可能导致程序执行出现错误，需要volatile关键字通过写屏障禁止指令重排
 * volatile底层原理：内存屏障
 * 1 对volatile变量的写指令之“后”会加入写屏障：写屏障保证在该屏障之前的对共享变量的改动，都同步到主存当中
 * 2 对volatile变量的读指令之“前”会加入读屏障：读屏障保证在该屏障之后对共享变量的读取，加载的是主存中的最新数据（而不是工作内存中的数据）
 * 但是不能解决指令交错产生的问题：
 * 1 写屏障只能保证之后的读操作可以读到主存中的最新结果，但指令交错下读操作有可能跑到写操作前面去
 * 2 有序性的保证只是保证本线程内相关代码不被重排序（即volatile不能保证原子性）
 */
public class Orderly {
    public static void main(String[] args) {

    }

    private static volatile Orderly singleton;

    private Orderly() {
    }

    //    double-check-locking单例模式
    public static Orderly getSingleton() {
//        其他线程可以在外部访问单例对象
        if (singleton == null) {
//            【注意，synchronized可以保证原子性，可见性和有序性，前提是共享变量完全由synchronized块管理】
            synchronized (Orderly.class) {
                if (singleton == null)
                    singleton = new Orderly();
            }
        }
        return singleton;
    }
    /**
     * 指令码：
     *   public static Chapter5.Orderly getSingleton();
     *     Code:
     *        0: getstatic     #2                  // Field singleton:LChapter5/Orderly;
     *        3: ifnonnull     37
     *        6: ldc           #3                  // class Chapter5/Orderly
     *        8: dup
     *        9: astore_0
     *       10: monitorenter
     *       11: getstatic     #2                  // Field singleton:LChapter5/Orderly;
     *       14: ifnonnull     27
     *       ===============================================================================
     *       这4行代码有可能发生指令重排，21和24的执行顺序有可能被被重排，即先对单例引用赋值，再去执行对象初始化
     *       如果单例变量先指向了内存地址，那么有可能其他线程在执行外层的if判断时发现引用不为null，但此时对象还未初始化完成
     *       17: new           #3                  // class Chapter5/Orderly
     *       20: dup
     *       21: invokespecial #4                  // Method "<init>":()V
     *       24: putstatic     #2                  // Field singleton:LChapter5/Orderly;
     *       ------------------------加入写屏障，保证初始化方法不会跳到赋值操作之后执行------------------------->
     *       ===============================================================================
     *       27: aload_0
     *       28: monitorexit
     *       29: goto          37
     *       32: astore_1
     *       33: aload_0
     *       34: monitorexit
     *       35: aload_1
     *       36: athrow
     *       37: getstatic     #2                  // Field singleton:LChapter5/Orderly;
     *       40: areturn
     *     Exception table:
     *        from    to  target type
     *           11    29    32   any
     *           32    35    32   any
     */
}
