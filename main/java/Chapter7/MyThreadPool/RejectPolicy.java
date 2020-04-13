package Chapter7.MyThreadPool;


public interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}
