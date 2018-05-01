package ray.tick;

import java.util.LinkedList;
import java.util.List;

public class TickManager {
    private long currentTick;

    private long tickRate; //milliseconds.

    private List<ITickControlled> tickControlledList;
    private boolean tickRunning;
    private Runnable callElements = () -> {
            long oldTime;
            long newTime;
            long sleepTime;

            try {
                while (tickRunning) {
                    oldTime = System.currentTimeMillis();
                    if(!tickControlledList.isEmpty())
                        for (ITickControlled e : tickControlledList)
                            e.done();
                    newTime = System.currentTimeMillis();

                    sleepTime = tickRate - (newTime - oldTime);
                    if(sleepTime>0) Thread.sleep(sleepTime);

                    currentTick++;
                }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
    };

    public TickManager(int tickRate){
        this.currentTick = 0;
        this.tickRate = tickRate;
        tickRunning = false;
        tickControlledList = new LinkedList<>();
    }

    public TickManager(){
        this(1000);
    }

    public void add(ITickControlled e){
        tickControlledList.add(e);
    }

    public boolean remove(ITickControlled e){
        return tickControlledList.remove(e);
    }

    public boolean isContain(ITickControlled e){
        return tickControlledList.contains(e);
    }

    public long getCurrentTick(){ return this.currentTick; }
    public long getTickRate(){ return this.tickRate; }

    public void start(){
        tickRunning = true;
        new Thread(callElements).start();
    }

    public void stop(){
        tickRunning = false;
    }

}
