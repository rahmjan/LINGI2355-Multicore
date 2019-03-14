import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ThreadLocalRandom;

public class EBTTASLock implements NThreadsLock {

    private AtomicBoolean state = new AtomicBoolean(false);
    private Integer MAX_DELAY = 1000;
    private ThreadLocal<Integer> DELAY = new ThreadLocal<Integer>();

    public EBTTASLock (int nb_threads){ }

    public void lock(int i) {
        int time = (int)(ThreadLocalRandom.current().nextDouble()*10);
        DELAY.set(time);

        while (true) {
            while (state.get()) {}

            if (!state.getAndSet(true))
                return;

            try{
                Thread.sleep(0, DELAY.get().intValue());
            }
            catch(InterruptedException e){}

            if (DELAY.get().compareTo(MAX_DELAY) < 0)
                DELAY.set(2*DELAY.get());

            if (DELAY.get().compareTo(MAX_DELAY) > 0)
                DELAY.set(MAX_DELAY);
        }
    }

    public void unlock(int i) {
        state.set(false);
    }
}
