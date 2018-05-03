package ray.util.tick;

import java.util.LinkedList;
import java.util.List;

public class TickManager {
    private long currentTick;

    private List<ITickControlled> tickControlledList;

    private TickTimer timer;

    public TickManager(long tickRate, long minSleepTime){
        timer = new TickTimer(tickRate, minSleepTime){
            @Override
            public void handle(){
                if(!tickControlledList.isEmpty())
                    for(ITickControlled e : tickControlledList)
                        e.done();

                currentTick++;
            }
        };

        this.currentTick = Long.MIN_VALUE;
        tickControlledList = new LinkedList<>();
    }

    public TickManager(long tickRate){
        this(tickRate, 0);
    }

    public TickManager(){
        this(1000, 0);
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
    public long getTickRate(){ return timer.getTickRate(); }

    public void start(){
        timer.start();
   }

    public void stop(){
        timer.stop();
    }

}
