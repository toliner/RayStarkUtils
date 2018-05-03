package ray.util.tick;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TickTimer {
    private boolean isRunning;
    private long tickRate;
    private long minSleep;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TickTimer(long tickRate, long minSleep) {
        if (tickRate < 1) throw new IllegalArgumentException("tickRate must be positive.");
        if (minSleep < 0) throw new IllegalArgumentException("minSleep must not be negative.");

        this.tickRate = tickRate;
        this.minSleep = minSleep;
        isRunning = false;
    }

    public TickTimer(long tickRate) {
        this(tickRate, 0);
    }

    public TickTimer() {
        this(1000, 0);
    }

    public abstract void handle();

    public void start() {
        isRunning = true;
        executor.execute(new Handler());
    }

    public synchronized void stop() {
        isRunning = false;
    }

    public long getTickRate() {
        return this.tickRate;
    }

    private class Handler implements Runnable {
        @Override
        public void run() {
            long oldTime;
            long newTime;
            long sleepTime;

            try {
                while (isRunning) {

                    oldTime = System.currentTimeMillis();
                    handle();
                    newTime = System.currentTimeMillis();

                    sleepTime = tickRate - newTime + oldTime;
                    if (sleepTime < minSleep) continue;
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
