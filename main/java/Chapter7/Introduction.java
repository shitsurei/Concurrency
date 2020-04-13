package Chapter7;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * 01
 * 不可变对象
 * 例如String类
 * 保护性拷贝，避免共享，保证线程安全
 */
@Slf4j
public class Introduction {
    public static void main(String[] args) {
//        dateTest();
        dateTimeFormat();
    }

    public static void dateTimeFormat() {
//        不可变的线程安全日期对象
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                log.info(dtf.parse("1998-03-22").toString());
            }, "t" + i).start();
        }
    }

    public static void dateTest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                /**
                 * 并发访问中会出现的各种问题
                 * Exception in thread "t12" Exception in thread "t14" java.lang.NumberFormatException: multiple points
                 * Exception in thread "t29" java.lang.NumberFormatException: empty String
                 * Exception in thread "t13" java.lang.ArrayIndexOutOfBoundsException: -1
                 * Exception in thread "t95" java.lang.NumberFormatException: For input string: "303.E2303E2"
                 */
//                try {
//                    log.info(sdf.parse("1998-03-22").toString());
//                } catch (ParseException e) {
//                    log.error(e.getMessage());
//                }
                /**
                 * 可以通过synchronized同步代码块解决，但性能较低
                 */
                synchronized (sdf) {
                    try {
                        log.info(sdf.parse("1998-03-22").toString());
                    } catch (ParseException e) {
                        log.error(e.getMessage());
                    }
                }
            }, "t" + i).start();
        }
    }
}
