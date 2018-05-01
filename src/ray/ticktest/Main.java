package ray.ticktest;

import ray.tick.ITickControlled;
import ray.tick.TickManager;

public class Main {
    public static void main(String[] args){
        TickManager manager = new TickManager(500);
        manager.add(new TickTest());

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

    private static class TickTest implements ITickControlled {
        int i=0;
        @Override
        public void done(){
            System.out.println(i++);
        }
    }
}

