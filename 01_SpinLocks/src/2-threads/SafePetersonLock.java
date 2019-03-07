import java.util.concurrent.atomic.AtomicBoolean;

public class SafePetersonLock implements TwoThreadsLock {
    private AtomicBoolean​[] flag;
//    private volatile int victim ;
    private int victim;

    public SafePetersonLock(int size){
        flag = new AtomicBoolean​[size];
        for (int i=0; i < size; i++) {
            flag[i] = new AtomicBoolean​(false);
        }
    }

    public void lock (int i) {
        // j is the identity of the other thread (by default we have thread 0 and 1)
        int j = 1 - i;

        flag[i].set(true);
        victim = i;
        while (flag[j].get() && victim == i) {} // busy wait
    }

    public void unlock(int i) {
        flag[i].set(false);
    }

}
