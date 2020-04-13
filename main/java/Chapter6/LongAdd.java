package Chapter6;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.LongAdder;

/**
 * 05
 * LongAdder和AtomicLong
 * LongAdder采用分单元累加（类似map reduce）性能更高
 * 继承自Striped64，其中包含
 * 1 累加单元数组，懒加载初始化
 * transient volatile Cell[] cells;
 * 2 基础值，如果没有竞争，则用CAS累加这个域
 * transient volatile long base;
 * 3 在累加单元扩容时用于对数组上锁（CAS自旋锁），置为1表示加锁
 * ansient volatile int cellsBusy;
 */
@Slf4j
public class LongAdd {
    public static void main(String[] args) {
        LongAdder longAdder = new LongAdder();
        longAdder.add(10);
        longAdder.increment();
        log.info(longAdder.toString());
    }
}
