import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AndersonLock implements NThreadsLock {

    private AtomicBoolean[] flags;
    private int nb_threads;
    AtomicInteger next = new AtomicInteger(0);
    ThreadLocal<Integer> mySlot = new ThreadLocal<Integer>();

    public AndersonLock (int nb_threads) {
        this.nb_threads = nb_threads;

        flags = new AtomicBoolean​[nb_threads];

        flags[0] = new AtomicBoolean​(false);
        for (int i=1; i < nb_threads; i++) {
            flags[i] = new AtomicBoolean​(true);
        }
    }

    public void lock(int i) {
        int slot = next.getAndIncrement();
        while (flags[slot % nb_threads].get()) {}
        flags[slot % nb_threads].set(true);
        mySlot.set(slot);
    }

    public void unlock(int i) {
        int slot = mySlot.get();
        flags[(slot + 1) % nb_threads].set(false);
    }
}
