package ray.util.tick;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TickManager {
    private long currentTick = 0;
    private long tps = 0;
    private boolean isTpsChecking;
    private final List<ITickWorker> tickWorkers = new LinkedList<>();
    private final TickTimer timer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TickManager(long tickRate, long minSleepTime) {
        timer = new TickTimer(tickRate, minSleepTime) {
            @Override
            public void handle() {
                synchronized (tickWorkers) {
                    if (!tickWorkers.isEmpty())
                        for (ITickWorker e : tickWorkers)
                            e.onTick();
                }

                if (currentTick == Long.MAX_VALUE) {
                    currentTick = 0;
                    return;
                }
                currentTick++;
            }
        };
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

    public synchronized long getTickRate() {
        return timer.getTickRate();
    }

    public synchronized long getTPS() {
        return this.tps;
    }

    public void start() {
        this.timer.start();
        isTpsChecking = true;
        executor.execute(new TpcChecker());
    }

    public void stop() {
        //この時点でTimerのスレッドは停止するのでスレッドの排他性を気にする必要はない、はず
        this.timer.stop();
        isTpsChecking = false;
        this.tps = 0;
    }

    private class TpcChecker implements Runnable {
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
}
