import java.util.concurrent.locks.*;

public class FifoLockQueue implements Queue {
    final static int SIZE = 4;
    int head = 0;
    int tail = 0;
    final Elem[] cells = new Elem[SIZE];
    int count = 0;
    long numOfDeq = 0;
    long numOfEnq = 0;

    private ThreadLocal<Integer> priorityProducer = new ThreadLocal<Integer>();
    private Integer nextPriorityProducer = 0;
    private Integer nextPriorityProducerGive = 0;
    private ThreadLocal<Integer> priorityConsumer = new ThreadLocal<Integer>();
    private Integer nextPriorityConsumer = 0;
    private Integer nextPriorityConsumerGive = 0;
    private Integer priorityMaxSize = 1000000000;

    final ReentrantLock lock = new ReentrantLock();
    final Condition producentCond = lock.newCondition();
    final Condition consumerCond = lock.newCondition();

    public Elem dequeue() {
        Elem ret = null;
        lock.lock();
        try {
            priorityConsumer.set(nextPriorityConsumerGive);
            nextPriorityConsumerGive = (nextPriorityConsumerGive + 1) % priorityMaxSize;

            while (count==0  || !nextPriorityConsumer.equals(priorityConsumer.get())) {
                try {
                    consumerCond.await();
                }
                catch (InterruptedException e) {}
            }

            ret = cells[head];
            head = (head + 1) % SIZE;
            count--;
            ++numOfDeq;

            nextPriorityConsumer = (nextPriorityConsumer + 1) % priorityMaxSize;

            producentCond.signal();
        } finally {
            lock.unlock();
        }

        return ret;
    }

    public void enqueue(Elem e) {
        lock.lock();
        try {
            priorityProducer.set(nextPriorityProducerGive);
            nextPriorityProducerGive = (nextPriorityProducerGive + 1) % priorityMaxSize;

            while(count==SIZE || !nextPriorityProducer.equals(priorityProducer.get())) {
                try {
                    producentCond.await();
                }
                catch (InterruptedException ex) {}
            }

            cells[tail] = e;
            tail = (tail + 1) % SIZE;
            count++;
            ++numOfEnq;

            nextPriorityProducer = (nextPriorityProducer + 1) % priorityMaxSize;

            consumerCond.signal();
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        return("head = "+head+", tail = "+tail + ", numOfEnq = " + numOfEnq + ", numOfDeq = " + numOfDeq);
    }
}
