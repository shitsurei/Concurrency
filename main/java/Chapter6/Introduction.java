package Chapter6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 01
 * 无锁并发
 * 1 CAS结合volatile
 * 2 原子整数，原子引用，原子累加器
 * 3 Unsafe
 */
public class Introduction {
    public static void main(String[] args) {
        Account account = new AccountUnsafe(10000);
        /**
         * 不安全的线程实现
         * 余额：3610
         * 用时：161 ms
         */
        account.demo();
        /**
         * 用synchronized同步代码块安全实现
         * 余额：0
         * 用时：102 ms
         */
        account = new AccountSafe1(10000);
        account.demo();
        /**
         * 用原子整数和CAS的无锁安全实现
         * 余额：0
         * 用时：121 ms
         */
        account = new AccountCAS(10000);
        account.demo();
    }
}

class AccountCAS implements Account {
    /**
     * AtomicInteger中维护的value值通过volatile修饰保证了其【可见性】
     * private volatile int value;
     * CAS必须配合volatile才能保证比较时是与主存中的最新值比较
     */
    private AtomicInteger balance;

    public AccountCAS(int balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        while (true){
//            获取余额最新值
            int prev = balance.get();
//            要修改的余额
            int next = prev - amount;
//            将修改后的余额同步到主存中（CAS有可能失败，即主存中的共享变量已经被其他线程修改，因此失败后需要重新循环获取最新结果）
//            CAS属于CPU指令操作（底层X86架构下的lock cmpxchg指令），可以保证原子性
            if (balance.compareAndSet(prev,next)) {
                break;
            }
        }
    }
}

class AccountSafe1 implements Account {

    private Integer balance;

    public AccountSafe1(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        synchronized (this) {
            return balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        synchronized (this) {
            balance -= amount;
        }
    }
}

class AccountUnsafe implements Account {

    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        return balance;
    }

    @Override
    public void withdraw(Integer amount) {
        balance -= amount;
    }
}

interface Account {
    //    获取余额
    Integer getBalance();

    //    取款
    void withdraw(Integer amount);

    default void demo() {
        List<Thread> lt = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            lt.add(new Thread(() -> {
                withdraw(10);
            }, "t" + (i + 1)));
        }
        long start = System.currentTimeMillis();
        lt.forEach(Thread::start);
        lt.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("余额：" + getBalance());
        System.out.println("用时：" + (end - start) + " ms");
    }
}
