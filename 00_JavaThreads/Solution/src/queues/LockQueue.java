import java.util.concurrent.locks.*;

public class LockQueue implements Queue {
    final static int SIZE = 4;
    int head = 0;
    int tail = 0;
    final Elem[] cells = new Elem[SIZE];
    int count = 0;
    long numOfDeq = 0;
    long numOfEnq = 0;

    final ReentrantLock lock = new ReentrantLock();
    final Condition producentCond = lock.newCondition();
    final Condition consumerCond = lock.newCondition();

    public Elem dequeue() {
        Elem ret = null;

        lock.lock();
        try {
            while (count==0) {
                try {
                    consumerCond.await();
                }
                catch (InterruptedException e) {}
            }

            ret = cells[head];
            head = (head + 1) % SIZE;
            count--;
            ++numOfDeq;

            producentCond.signal();
        } finally {
            lock.unlock();
        }

        return ret;
    }

    public void enqueue(Elem e) {

        lock.lock();
        try {
            while(count==SIZE) {
                try {
                    producentCond.await();
                }
                catch (InterruptedException ex) {}
            }

            cells[tail] = e;
            tail = (tail + 1) % SIZE;
            count++;
            ++numOfEnq;

            consumerCond.signal();
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        return("head = "+head+", tail = "+tail + ", numOfEnq = " + numOfEnq + ", numOfDeq = " + numOfDeq);
    }
}
