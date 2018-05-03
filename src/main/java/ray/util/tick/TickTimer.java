package ray.util.tick;

public abstract class TickTimer {
    private long tickRate;
    private long minSleep;

    private boolean timerStarted;

    public TickTimer(long tickRate, long minSleep) {
        if (tickRate < 0) throw new IllegalArgumentException("tickRate must not be negative.");
        if (minSleep < 0) throw new IllegalArgumentException("minSleep must not be negative.");

        this.tickRate = tickRate;
        this.minSleep = minSleep;
        timerStarted = false;
    }

    public TickTimer(long tickRate) {
        this(tickRate, 0);
    }

    public TickTimer() {
        this(1000, 0);
    }

    public abstract void handle();

    public void start() {
        timerStarted = true;
        new Thread(() -> {
            long oldTime;
            long newTime;
            long sleepTime;

            try {
                while (timerStarted) {

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
        }).start();
    }

    public void stop() {
        timerStarted = false;
    }

    public long getTickRate() {
        return this.tickRate;
    }

}
