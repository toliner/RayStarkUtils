package ray.util.tick;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TickManager {
    private long currentTick = 0;
    private long tps = 0;
    private boolean isTpsChecking;
    private final long tickRate;
    private final long delay;
    private final List<ITickWorker> tickWorkers = new LinkedList<>();
    private final Timer timer = new Timer();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TickManager(long tickRate, long delay) {
        this.tickRate = tickRate;
        this.delay = delay;
    }

    public TickManager(long tickRate) {
        this(tickRate, 0);
    }

    public TickManager() {
        this(1000, 0);
    }


    public synchronized void add(ITickWorker e) {
        tickWorkers.add(e);
    }

    public synchronized boolean remove(ITickWorker e) {
        return tickWorkers.remove(e);
    }

    public synchronized boolean contains(ITickWorker e) {
        return tickWorkers.contains(e);
    }

    public synchronized boolean isEmpty() {
        return tickWorkers.isEmpty();
    }

    public synchronized long getCurrentTick() {
        return this.currentTick;
    }

    public long getTickRate() {
        return tickRate;
    }

    public synchronized long getTPS() {
        return this.tps;
    }

    public void start() {
        this.timer.schedule(new TickHandler(this, tickRate, delay, 0), delay);
        isTpsChecking = true;
        executor.execute(new TpsChecker());
    }

    public void stop() {
        //この時点でTimerのスレッドは停止するのでスレッドの排他性を気にする必要はない、はず
        this.timer.cancel();
        isTpsChecking = false;
        this.tps = 0;
    }

    private class TpsChecker implements Runnable {
        @Override
        public void run() {
            try {
                long oldTick = currentTick;
                while (isTpsChecking) {
                    Thread.sleep(1000);
                    tps = currentTick - oldTick;
                    if (tps < 0) tps += Long.MAX_VALUE;
                    oldTick = currentTick;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class TickHandler extends TimerTask {
        private final TickManager manager;
        private final long tickRate;
        private final long delay;
        private final long currentTick;

        TickHandler(TickManager manager, long tickRate, long delay, long currentTick) {
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
