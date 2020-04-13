package Chapter6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 03
 * 原子数组（修改引用中的值而不是修改引用）
 */
public class AtomicArray {
    public static void main(String[] args) {
        demo(() -> new int[10], array -> array.length, (array, index) -> array[index]++, array -> {
//            存在线程安全问题
//            [994, 994, 992, 991, 994, 994, 994, 994, 995, 994]
            System.out.println(Arrays.toString(array));
        });
//        线程安全
//        [1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000]
        demo(() -> new AtomicIntegerArray(10), array -> array.length(), (array, index) -> array.incrementAndGet(index), System.out::println);
    }

    private static <T> void demo(Supplier<T> arraySup, Function<T, Integer> lenFun, BiConsumer<T, Integer> putCon, Consumer<T> printCon) {
        List<Thread> lt = new ArrayList<>();
        T array = arraySup.get();
        int len = lenFun.apply(array);
        for (int i = 0; i < len; i++) {
//            每个线程对数组操作1000次
            lt.add(new Thread(() -> {
                for (int j = 0; j < 1000; j++)
                    putCon.accept(array, j % len);
            }, "t" + i));
        }
        lt.forEach(Thread::start);
        lt.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        printCon.accept(array);
    }
}
