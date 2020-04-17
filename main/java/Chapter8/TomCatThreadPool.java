package Chapter8;

/**
 * 03
 * Tomcat线程池：
 * Tomcat分为两大部分：连接器（Connector）和容器（Container）
 * 连接器部分就使用到了线程池，分为几个组件：
 * 1 LimitLatch 作为请求入口起到限流的作用，可以控制最大连接个数，类似JUC中的Semaphore
 * 2 Acceptor   单个线程不断自旋查看是否有新的连接，【只负责接收新的socket连接】，当请求没有超过最大连接个数时就会进入Acceptor
 * 3 Poller     只负责监听socket channel是否有可读的IO事件，一旦有可读信息就将其封装成一个任务对象（socketProcessor），提交给Executor线程池处理
 * 4 Executor   线程池，其中的工作线程最终负责处理请求
 * 【不同的线程处理不同的任务，合理分工实现高并发】
 */
public class TomCatThreadPool {
    /**
     * Tomcat线程池扩展了Java原生的ThreadPoolExecutor，行为稍有不同
     * 1 如果总线程数达到线程池数量的最大值，不会立即执行拒绝策略（抛异常），而是会再次尝试将任务放入队列，如果再次失败则抛异常
     * 2 实现方式为在拒绝策略的catch块中获取
     */
    /**
     * 相关配置：
     * 1 Connector配置
     * acceptorThreadCount  默认值为1    表示Acceptor线程池数量（只接受连接不处理，因此1个线程足够）
     * pollerThreadCount    默认值为1    表示Poller线程池数量（使用IO多路复用的技术，一个线程足够）
     * minSpareThread       默认值为10   表示核心线程数量
     * maxThread            默认值为200  表示最大线程数量
     * executor             -            引用配置，指明了引用的Executor配置后其优先级要高于以上的配置
     * 2 Executor配置
     * threadPriority       默认值为5                   线程优先级
     * daemon               默认值true                  Tomcat中的线程是守护线程
     * minSpareThread       默认值为25                  表示核心线程数量
     * maxThread            默认值为200                 表示最大线程数量
     * maxIdleTime          默认值60000                 线程生存时间，单位是ms，即默认值1分钟
     * maxQueueSize         默认值Integer.MAX_VALUE     队列长度（Tomcat的线程池在核心线程满载时优先赋给急救线程处理，达到最大线程后再往队列中添加）
     * prestartminSpareThread   默认false               表示核心线程是否在服务器启动时创建，默认懒加载
     */
}
