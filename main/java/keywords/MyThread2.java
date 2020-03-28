package keywords;

public class MyThread2 extends Thread {
    public MyThread2() {
        System.out.println("construct currentThread name = " + Thread.currentThread().getName());
        System.out.println("construct currentThread live = " + Thread.currentThread().isAlive());
        System.out.println("construct this name = " + this.getName());
        System.out.println("construct this live = " + this.isAlive());
    }

    @Override
    public void run() {
        System.out.println("run currentThread name = " + Thread.currentThread().getName());
        System.out.println("run currentThread live = " + Thread.currentThread().isAlive());
        System.out.println("run this name = " + this.getName());
        System.out.println("run this live = " + this.isAlive());
    }
}

class Test2 {
    public static void main(String[] args) {
        MyThread2 myThread2 = new MyThread2();
        Thread t = new Thread(myThread2);
        t.setName("t");
        t.start();
    }
}
