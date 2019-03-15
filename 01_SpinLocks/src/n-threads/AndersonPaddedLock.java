import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AndersonPaddedLock implements NThreadsLock {

    private PaddedAtomicBoolean[] flags;
    private int nb_threads;
    AtomicInteger next = new AtomicInteger(0);
    ThreadLocal<Integer> mySlot = new ThreadLocal<Integer>();

    public AndersonPaddedLock (int nb_threads) {
        this.nb_threads = nb_threads;

        flags = new PaddedAtomicBoolean[nb_threads];

        flags[0] = new PaddedAtomicBoolean(false);
        for (int i=1; i < nb_threads; i++) {
            flags[i] = new PaddedAtomicBoolean(true);
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

    private class PaddedAtomicBoolean {
        final int PADDING = 7;
        public AtomicBoolean at;
        private AtomicBoolean[] unused;
        public PaddedAtomicBoolean (boolean initialValue) {
            at = new AtomicBoolean(initialValue);
            unused = new AtomicBoolean[PADDING];
            for (int i=0;i<PADDING;i++) {
                unused[i]=new AtomicBoolean(false);
            }
        }
        public boolean get() {
            return at.get();
        }
        public void set(boolean newValue) {
            at.set(newValue);
        }
    }
}
