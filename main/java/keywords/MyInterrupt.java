package keywords;

public class MyInterrupt extends Thread {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println(isInterrupted());
                sleep(1);
                System.out.println(i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("after break");
    }
}

class Test3 {
    public static void main(String[] args){
        Thread t = new Thread(new MyInterrupt());
        t.start();
        t.interrupt();
        System.out.println("over");
    }
}
