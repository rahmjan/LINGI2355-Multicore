import java.util.concurrent.atomic.AtomicBoolean;

public class TTASLock implements NThreadsLock {

    private AtomicBoolean state = new AtomicBoolean(false);

    public TTASLock (int nb_threads){}

    public void lock(int i) {
        while (state.get()) {}
        if (!state.getAndSet(true))
            return;
    }

    public void unlock(int i) {
        state.set(false);
    }
}
