package ray.util.tick.manager;

import ray.util.tick.api.ITickManager;
import ray.util.tick.api.ITickWorker;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class TickManagerBase implements ITickManager {

    private long currentTick = 0;
    private long tps = 0;
    private final long tickRate;
    private final long delay;
    private final List<ITickWorker> tickWorkers;
    private final Timer timer = new Timer();

    protected TickManagerBase(long tickRate, long delay, List<ITickWorker> workers) {
        this.tickRate = tickRate;
        this.delay = delay;
        workers.clear();
        tickWorkers = Collections.synchronizedList(workers);
    }

    protected TickManagerBase(long tickRate, List<ITickWorker> workers) {
        this(tickRate, 0, workers);
    }

    protected TickManagerBase(List<ITickWorker> workers) {
        this(1000, 0, workers);
    }

    @Override
    public synchronized void add(ITickWorker e) {
        tickWorkers.add(e);
    }

    @Override
    public synchronized boolean remove(ITickWorker e) {
        return tickWorkers.remove(e);
    }

    @Override
    public synchronized boolean contains(ITickWorker e) {
        return tickWorkers.contains(e);
    }

    @Override
    public synchronized boolean isEmpty() {
        return tickWorkers.isEmpty();
    }

    @Override
    public synchronized long getCurrentTick() {
        return this.currentTick;
    }

    @Override
    public long getTickRate() {
        return tickRate;
    }

    @Override
    public synchronized long getTps() {
        return this.tps;
    }

    @Override
    public void start() {
        this.timer.schedule(new TickHandler(this, tickRate, delay, 0), delay);
        this.timer.scheduleAtFixedRate(new TpsChecker(), 1000, 1000);
    }

    @Override
    public void stop() {
        this.timer.cancel();
        this.tps = 0;
    }

    protected class TpsChecker extends TimerTask {

        private long oldTick = 0;

        @Override
        public void run() {
            tps = currentTick - oldTick;
            oldTick = currentTick;
        }
    }

    protected class TickHandler extends TimerTask {
        private final TickManagerBase manager;
        private final long tickRate;
        private final long delay;
        private final long currentTick;

        TickHandler(TickManagerBase manager, long tickRate, long delay, long currentTick) {
            this.manager = manager;
            this.tickRate = tickRate;
            this.delay = delay;
            this.currentTick = currentTick;
        }

        @Override
        public void run() {
            manager.currentTick = currentTick;
            synchronized (manager.tickWorkers) {
                for (ITickWorker worker : manager.tickWorkers) {
                    worker.onTick();
                }
            }
            long execTime = System.currentTimeMillis() - this.scheduledExecutionTime();
            long nextTick;
            if (currentTick == Long.MAX_VALUE) {
                nextTick = 0;
            } else {
                nextTick = currentTick + 1;
            }
            manager.timer.schedule(new TickHandler(manager, tickRate, delay, nextTick), tickRate - execTime + delay);
        }
    }
}
