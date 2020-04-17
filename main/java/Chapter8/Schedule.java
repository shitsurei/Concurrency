package Chapter8;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 02
 * 调度线程池的应用：定时任务
 */
@Slf4j
public class Schedule {
    public static void main(String[] args) {
//        获取目标时间【周五18点，该方法得到的是本周时间，需要判断是否需要改为下周的该时间】
        LocalDateTime taskTime = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.FRIDAY);
//        设置任务的间隔时间
        long period = 1000 * 60 * 60 * 24 * 7;
        scheduleTask(taskTime, period);
    }

    /**
     * 设置定时任务
     *
     * @param taskTime 任务执行的时间
     * @param period   任务间隔周期
     */
    public static void scheduleTask(LocalDateTime taskTime, long period) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
//        获取当前时间
        LocalDateTime now = LocalDateTime.now();
//        判断获取的时间如果在当前时间之前，需要在此基础上再推迟一周
        if (now.compareTo(taskTime) > 0)
            taskTime = taskTime.plusWeeks(1);
//        获取任务执行要延迟的时间
        long initDelay = Duration.between(now, taskTime).toMillis();
        pool.scheduleAtFixedRate(() -> {
            log.info("执行任务中");
        }, initDelay, period, TimeUnit.MILLISECONDS);
    }
}
