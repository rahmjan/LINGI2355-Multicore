import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class BakeryLock implements NThreadsLock {

    private AtomicInteger[] label;
    private AtomicBoolean[] flag;
    private int size;

    public BakeryLock (int nb_threads) {
        size = nb_threads;

        label = new AtomicInteger[size];
        flag = new AtomicBoolean[size];

        for (int i=0; i < size; i++) {
            label[i] = new AtomicInteger(0);
            flag[i] = new AtomicBoolean(false);
        }
    }

    public void lock(int in) {
        flag[in].set(true);
        label[in].set(findMaximumElement() + 1);
        for (int k = 0; k < size; k++) {
            while ((k != in) && flag[k].get() && ((label[k].get() < label[in].get()) ||
                    ((label[k].get() == label[in].get()) && k < in))) { }
        }
    }

    public void unlock(int in) {
        flag[in].set(false);
    }

    private int findMaximumElement() {
        int maxValue = Integer.MIN_VALUE;
        for (AtomicInteger element : label) {
            if (element.get() > maxValue) {
                maxValue = element.get();
            }
        }
        return maxValue;
    }
}
