import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ThreadLocalRandom;

public class EBTTASLock implements NThreadsLock {

    private AtomicBoolean state = new AtomicBoolean(false);
    private Integer MAX_DELAY = 1000;
    private ThreadLocal<Integer> DELAY;

    public EBTTASLock (int nb_threads){
        int time = (int)(ThreadLocalRandom.current().nextDouble()*10);
        DELAY = new ThreadLocal<Integer>();
    }

    public void lock(int i) {
        while (true) {
            while (state.get()) {}

            if (!state.getAndSet(true))
                return;

            try{
                Thread.sleep(0, DELAY.get());
            }
            catch(InterruptedException e){}

            if (DELAY.get() < MAX_DELAY)
                DELAY.set(2*DELAY.get());

            if (DELAY.get() > MAX_DELAY)
                DELAY.set(MAX_DELAY);
        }
    }

    public void unlock(int i) {
        state.set(false);
    }
}
