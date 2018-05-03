package ray.util.tick;

import java.util.LinkedList;
import java.util.List;

public class TickManager {
    private long currentTick;
    private long tps;

    private List<ITickControlled> tickControlledList;

    private TickTimer timer;

    private boolean TPSChecking;

    public TickManager(long tickRate, long minSleepTime){
        timer = new TickTimer(tickRate, minSleepTime){
            @Override
            public void handle(){
                if(!tickControlledList.isEmpty())
                    for(ITickControlled e : tickControlledList)
                        e.done();

                if(currentTick == Long.MAX_VALUE){
                    currentTick = 0;
                    return;
                }
                currentTick++;
            }
        };

        this.currentTick = 0;
        this.tps = 0;
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
    public boolean isEmpty(){ return tickControlledList.isEmpty(); }

    public long getCurrentTick(){ return this.currentTick; }

    public long getTickRate(){ return timer.getTickRate(); }

    public long getTPS(){ return this.tps; }

    public void start(){
        this.timer.start();
        TPSChecking = true;
        new Thread(() -> {
            try{
                long oldTick = currentTick;
                while(TPSChecking){
                    Thread.sleep(1000);
                    tps = currentTick - oldTick;
                    if(tps < 0) tps += Long.MAX_VALUE;
                    oldTick = currentTick;
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }).start();
    }

    public void stop(){
        this.timer.stop();
        TPSChecking = false;
        this.tps = 0;
    }

}
