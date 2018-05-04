package ray.util.tick.manager;

import java.util.LinkedList;

public class LinkedListTickManager extends TickManagerBase {
    public LinkedListTickManager(long tickRate, long delay) {
        super(tickRate, delay, new LinkedList<>());
    }

    public LinkedListTickManager(long tickRate) {
        super(tickRate, new LinkedList<>());
    }

    public LinkedListTickManager() {
        super(new LinkedList<>());
    }
}
