package ray.ticktest;

import ray.util.tick.ITickWorker;
import ray.util.tick.TickManager;

public class TPSTest {
    private static void main(String[] args) {
        TickManager manager = new TickManager(20);
        manager.add(new ITickWorker() {
            long oldTick = manager.getCurrentTick();

            @Override
            public void onTick() {
                if (20 * (manager.getCurrentTick() - oldTick) < 1000) return;

                System.out.println(manager.getTPS());
                oldTick = manager.getCurrentTick();
            }
        });

        try {
            manager.start();
            Thread.sleep(10000);
            manager.stop();
            long tps = manager.getTPS();
            assert tps == 0 : "current tps:" + tps;
            Thread.sleep(3000);
            manager.start();
            Thread.sleep(10000);
            manager.stop();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
