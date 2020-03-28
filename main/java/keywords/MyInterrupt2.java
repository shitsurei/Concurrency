package keywords;

public class MyInterrupt2 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
//            isInterrupted不改变打断状态，无法用于判断
//            if (isInterrupted()){
//                System.out.println("isInterrupted");
//                return;
//            }
            if (Thread.interrupted()) {
                System.out.println("interrupted");
                return;
            }
            System.out.println(i);
        }
        System.out.println("after break");
    }
}

class Test4 {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new MyInterrupt2());
        t.start();
        Thread.sleep(1);
        t.interrupt();
        System.out.println("over");
    }
}
