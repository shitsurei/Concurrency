package Chapter6;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 04
 * 字段更新器
 * 【注意被字段更新器修改的变量必须是volatile修饰的，且不能是private属性】
 */
@Slf4j
public class FieldUpdate {
    public static void main(String[] args) {
        Student student = new Student();
        AtomicReferenceFieldUpdater updater = AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
        if (updater.compareAndSet(student, null, "张三")) {
            log.info("修改成功");
        }
        /**
         * 2020-04-13 16:23:35.534 [main] INFO  Chapter6.FieldUpdate - 修改成功
         * 2020-04-13 16:23:35.537 [main] INFO  Chapter6.FieldUpdate - Student{name='张三'}
         */
        log.info(student.toString());
    }
}

class Student {
    volatile String name;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }
}
