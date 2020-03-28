package keywords;

public class MyThread extends Thread {
    public MyThread() {
        System.out.println("construct currentThread name = " + Thread.currentThread().getName());
        System.out.println("construct this name = " + this.getName());
    }

    @Override
    public void run() {
        System.out.println("run currentThread name = " + Thread.currentThread().getName());
        System.out.println("run this name = " + this.getName());
    }
}

class Test {
    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.setName("A");
        myThread.start();
    }
}

