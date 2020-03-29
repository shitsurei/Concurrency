package Chapter2;

import java.util.ArrayList;

/**
 * 03
 * 变量的线程安全问题：
 * 1 静态变量和实例变量
 * 可能出现线程安全问题
 * 2 局部变量
 * 一般情况下局部变量是线程安全的，因为各自线程操作各自的局部对象内存
 * 【但如果局部变量的引用逃离了方法（即暴露给外部），有可能出现线程不安全问题】
 *
 */
public class ThreadSafe {
    public static void main(String[] args) {
        unSafe2();
    }

    public static void unSafe1() {
        Ts ts = new Ts();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> ts.method1(), "t" + (i + 1)).start();
        }
    }

    public static void unSafe2() {
        Ts2 ts = new Ts2Sub();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> ts.method1(), "t" + (i + 1)).start();
        }
    }
}

class Ts {
    ArrayList list = new ArrayList();

    public void method1() {
        for (int i = 0; i < 20000; i++) {
            method2();
            method3();
        }
    }

    public void method2() {
        list.add("1");
    }

    public void method3() {
//        该行代码报错，原因是ArrayList的底层是数组，因此添加元素时需要先确定新添加元素在数组中的索引
//        考虑这种情况：线程1的add方法还没有执行完，线程的2的add方法就开始执行，此时切回线程1时添加元素的位置是不变的
//        因此线程1的元素覆盖了线程2添加的元素，整个数组中的元素个数比索引位置少，接下来执行remove方法时就可能出现线程安全问题
//        Exception in thread "t2" java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        list.remove(0);
    }
}

class Ts2 {
    public void method1() {
        ArrayList list = new ArrayList();
        for (int i = 0; i < 20000; i++) {
            method2(list);
            method3(list);
        }
    }

    public void method2(ArrayList list) {
        list.add("1");
    }

//    将该方法的访问修饰符修改为private或final可以杜绝子类通过重写该方法，将局部变量暴露给外部线程而出现的线程安全问题
    public void method3(ArrayList list) {
        list.remove(0);
    }
}

class Ts2Sub extends Ts2 {
    @Override
    public void method3(ArrayList list) {
        new Thread(() -> {
//            Exception in thread "t1" Exception in thread "Ts2Sub class" java.lang.ArrayIndexOutOfBoundsException: -1
//            该行代码报错，因为子类继承Ts2之后重写了method3方法，单独创建线程去执行remove方法，将局部变量暴露给了外部，出现线程安全问题
//            因此方法的private和final修饰符可以对方法的线程安全起到保护作用
            list.remove(0);
        }, "Ts2Sub class").start();
    }
}
