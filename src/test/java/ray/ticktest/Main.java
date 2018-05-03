package ray.ticktest;

import ray.util.tick.ITickControlled;
import ray.util.tick.TickManager;

public class Main {
    public static void main(String[] args){
        testTPS();
    }

    private static void testTickManager(){
        TickManager manager = new TickManager(1000);
        manager.add(new ITickControlled() {
            int i=0;
            @Override
            public void done(){
                System.out.println(i++);
            }
        });

        try {
            manager.start();
            Thread.sleep(5000);
            manager.stop();
            Thread.sleep(3000);
            manager.start();
            Thread.sleep(5000);
            manager.stop();

        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private static void testTPS(){
        TickManager manager = new TickManager(20);
        manager.add(new ITickControlled() {
            long oldTick = manager.getCurrentTick();
            @Override
            public void done() {
                if(20 * (manager.getCurrentTick()-oldTick) < 1000) return;

                System.out.println(manager.getTPS());
                oldTick = manager.getCurrentTick();
            }
        });

        try {
            manager.start();
            Thread.sleep(10000);
            manager.stop();
            System.out.println("TPS: " + manager.getTPS() + "This TPS must be 0.");
            Thread.sleep(3000);
            manager.start();
            Thread.sleep(10000);
            manager.stop();

        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}

