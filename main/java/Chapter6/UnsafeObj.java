package Chapter6;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 06
 * Unsafe对象
 * 直接操作内存和线程的对象，单例
 * juc包下的多数类都是调用该类的方法
 *
 * 【getDeclaredField】方法才可以获取私有的成员变量
 */
@Slf4j
public class UnsafeObj {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
//        必须通过反射调用
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
//        静态成员变量不需要传递对象，参数设置null
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        Teacher teacher = new Teacher();
//        获得域的偏移地址
        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));
//        执行CAS操作
//        比较并交换整型
        if (unsafe.compareAndSwapInt(teacher, idOffset, 0, 1)) {
            log.info("id设置成功");
        }
//        比较并交换Object类型
        if (unsafe.compareAndSwapObject(teacher, nameOffset, null, "张三")) {
            log.info("姓名设置成功");
        }
//        Teacher{id=1, name='张三'}
        log.info(teacher.toString());
    }
}

class Teacher {
    volatile int id;
    volatile String name;

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
