package Chapter3;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * 05
 * 异步模式【消息的生产和消费之间有延迟】之生产者消费者模式
 * 要点：
 * 1 保护性暂停模式中的协调同步线程是一一对应的
 * 2 有时我们需要协调产品提供者和使用者线程的数量，消息队列可以用来平衡生产者和消费者线程资源
 * 3 消息队列是有容量的，满时不会加入数据，空时不会消耗数据
 * 4 JDK中的各种阻塞队列采用的就是这种模式
 */
@Slf4j
public class ProducerConsumer {
    /**
     * 2020-04-02 18:58:53.653 [consumer0] INFO  Chapter3.MessageQueue - empty queue , can not consume
     * 2020-04-02 18:58:53.657 [producer0] INFO  Chapter3.ProducerConsumer - put message Message{id=0, value=message0}
     * 2020-04-02 18:58:53.657 [producer2] INFO  Chapter3.ProducerConsumer - put message Message{id=2, value=message2}
     * 2020-04-02 18:58:53.657 [producer3] INFO  Chapter3.ProducerConsumer - put message Message{id=3, value=message3}
     * 2020-04-02 18:58:53.657 [producer1] INFO  Chapter3.ProducerConsumer - put message Message{id=1, value=message1}
     * 2020-04-02 18:58:53.658 [producer5] INFO  Chapter3.MessageQueue - full queue , can not produce
     * 2020-04-02 18:58:53.658 [producer4] INFO  Chapter3.ProducerConsumer - put message Message{id=4, value=message4}
     * 2020-04-02 18:58:53.658 [producer6] INFO  Chapter3.MessageQueue - full queue , can not produce
     * 2020-04-02 18:58:53.659 [producer8] INFO  Chapter3.MessageQueue - full queue , can not produce
     * 2020-04-02 18:58:53.659 [producer7] INFO  Chapter3.MessageQueue - full queue , can not produce
     * 2020-04-02 18:58:53.663 [producer9] INFO  Chapter3.MessageQueue - full queue , can not produce
     * 2020-04-02 18:58:53.668 [consumer9] INFO  Chapter3.ProducerConsumer - get message : Message{id=0, value=message0}
     * 2020-04-02 18:58:53.671 [consumer8] INFO  Chapter3.ProducerConsumer - get message : Message{id=1, value=message1}
     * 2020-04-02 18:58:53.672 [consumer7] INFO  Chapter3.ProducerConsumer - get message : Message{id=2, value=message2}
     * 2020-04-02 18:58:53.674 [consumer6] INFO  Chapter3.ProducerConsumer - get message : Message{id=3, value=message3}
     * 2020-04-02 18:58:53.676 [consumer4] INFO  Chapter3.MessageQueue - empty queue , can not consume
     * 2020-04-02 18:58:53.674 [consumer5] INFO  Chapter3.ProducerConsumer - get message : Message{id=4, value=message4}
     * 2020-04-02 18:58:53.678 [consumer3] INFO  Chapter3.MessageQueue - empty queue , can not consume
     * 2020-04-02 18:58:53.678 [consumer2] INFO  Chapter3.MessageQueue - empty queue , can not consume
     * 2020-04-02 18:58:53.678 [consumer1] INFO  Chapter3.MessageQueue - empty queue , can not consume
     * 2020-04-02 18:58:53.678 [producer9] INFO  Chapter3.ProducerConsumer - put message Message{id=9, value=message9}
     * 2020-04-02 18:58:53.678 [producer7] INFO  Chapter3.ProducerConsumer - put message Message{id=7, value=message7}
     * 2020-04-02 18:58:53.679 [producer8] INFO  Chapter3.ProducerConsumer - put message Message{id=8, value=message8}
     * 2020-04-02 18:58:53.679 [producer6] INFO  Chapter3.ProducerConsumer - put message Message{id=6, value=message6}
     * 2020-04-02 18:58:53.679 [producer5] INFO  Chapter3.ProducerConsumer - put message Message{id=5, value=message5}
     * 2020-04-02 18:58:53.679 [consumer0] INFO  Chapter3.ProducerConsumer - get message : Message{id=9, value=message9}
     * 2020-04-02 18:58:53.679 [consumer1] INFO  Chapter3.ProducerConsumer - get message : Message{id=7, value=message7}
     * 2020-04-02 18:58:53.679 [consumer3] INFO  Chapter3.ProducerConsumer - get message : Message{id=6, value=message6}
     * 2020-04-02 18:58:53.679 [consumer4] INFO  Chapter3.ProducerConsumer - get message : Message{id=5, value=message5}
     * 2020-04-02 18:58:53.679 [consumer2] INFO  Chapter3.ProducerConsumer - get message : Message{id=8, value=message8}
     */
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(5);
        for (int i = 0; i < 10; i++) {
//            lambda表达式要求代码块中引用的外部变量必须是不被修改的，因此要用新的变量存放每次更新的i
            int finalI = i;
            new Thread(() -> {
                try {
                    Message m = new Message(finalI, "message" + finalI);
                    queue.putMessage(m);
                    log.info("put message " + m);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "producer" + i).start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    Message message = queue.getMessage();
                    log.info("get message : " + message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "consumer" + i).start();
        }
    }
}

@Slf4j
// 线程间通信的消息队列
class MessageQueue {
    //    消息队列集合
    private LinkedList<Message> list = new LinkedList<>();
    //    消息队列容量
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    public Message getMessage() throws InterruptedException {
        synchronized (list) {
            while (list.isEmpty()) {
                log.info("empty queue , can not consume");
                list.wait();
            }
//            消费消息
            Message message = list.removeFirst();
//            唤醒生产者线程
            list.notifyAll();
            return message;
        }
    }

    public void putMessage(Message message) throws InterruptedException {
        synchronized (list) {
            while (list.size() == capacity) {
                log.info("full queue , can not produce");
                list.wait();
            }
//            添加消息
            list.addLast(message);
//            唤醒消费者线程
            list.notifyAll();
        }
    }
}

final class Message {
    private int id;
    private Object value;

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}