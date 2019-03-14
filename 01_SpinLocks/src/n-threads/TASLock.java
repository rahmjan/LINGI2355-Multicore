import java.util.concurrent.atomic.AtomicBoolean;

public class TASLock implements NThreadsLock {

    private AtomicBoolean state = new AtomicBoolean(false);

    public TASLock (int nb_threads){}

    public void lock(int i) {
        while (state.getAndSet(true)) {}
    }

    public void unlock(int i) {
        state.set(false);
    }
}
