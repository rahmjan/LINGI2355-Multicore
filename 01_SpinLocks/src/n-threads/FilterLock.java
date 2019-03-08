import java.util.concurrent.atomic.AtomicInteger;

public class FilterLock implements NThreadsLock {

    private AtomicInteger[] level;
    private AtomicInteger[] victim;
    private int size;

    public FilterLock (int nb_threads) {
        size = nb_threads;

        level = new AtomicInteger[size];
        victim = new AtomicInteger[size];

        for (int i=0; i < size; i++) {
            level[i] = new AtomicInteger(0);
            victim[i] = new AtomicInteger(0);
        }
    }

    public void lock(int in) {
        for (int i = 1; i < size; ++i) {
            level[in].set(i);
            victim[i].set(in);

            for (int k = 0; k < size; k++) {
                while ((k != in) && (level[k].get() >= i && victim[i].get() == in)) {}
            }
        }
    }

    public void unlock(int in) {
        level[in].set(0);
    }
}
