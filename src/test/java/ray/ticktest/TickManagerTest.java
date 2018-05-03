package ray.ticktest;

import ray.util.tick.ITickWorker;
import ray.util.tick.TickManager;

public class TickManagerTest {

    public static void main(String[] args) {
        TickManager manager = new TickManager(1000);
        manager.add(new ITickWorker() {
            int i = 0;

            @Override
            public void onTick() {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

