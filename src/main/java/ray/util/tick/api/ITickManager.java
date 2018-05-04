package ray.util.tick.api;

public interface ITickManager {
    void add(ITickWorker worker);
    boolean remove(ITickWorker worker);
    boolean contains(ITickWorker worker);
    boolean isEmpty();
    long getCurrentTick();
    long getTickRate();
    long getTps();
    void start();
    void stop();
}
