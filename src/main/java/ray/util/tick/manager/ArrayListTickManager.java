package ray.util.tick.manager;

import java.util.ArrayList;

public class ArrayListTickManager extends TickManagerBase {
    public ArrayListTickManager(long tickRate, long delay) {
        super(tickRate, delay, new ArrayList<>());
    }

    public ArrayListTickManager(long tickRate) {
        super(tickRate, new ArrayList<>());
    }

    public ArrayListTickManager() {
        super(new ArrayList<>());
    }
}
