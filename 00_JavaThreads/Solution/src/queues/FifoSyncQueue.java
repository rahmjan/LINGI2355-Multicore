public class FifoSyncQueue implements Queue {
    final static int SIZE = 50 ;
    int head = 0;
    int tail = 0;
    final Elem[] cells = new Elem[SIZE];
    int count = 0;

    private ThreadLocal<Integer> priorityProducer = new ThreadLocal<Integer>();
    private Integer nextPriorityProducer = 0;
    private Integer nextPriorityProducerGive = 0;
    private ThreadLocal<Integer> priorityConsumer = new ThreadLocal<Integer>();
    private Integer nextPriorityConsumer = 0;
    private Integer nextPriorityConsumerGive = 0;
    private Integer priorityMaxSize = 1000000000;

    public synchronized Elem dequeue() {

        priorityConsumer.set(nextPriorityConsumerGive);
        nextPriorityConsumerGive = (nextPriorityConsumerGive + 1) % priorityMaxSize;

        while (head==tail  || !nextPriorityConsumer.equals(priorityConsumer.get())) {

//            System.out.println("Consumer: " + Thread.currentThread() + " " + priorityConsumer.get() + " Head: " + head + " Tail: " + tail);

            try {
                wait();
            }
            catch (InterruptedException e) {}
        }

        Elem ret = cells[head];
        head = (head + 1) % SIZE;
        count--;

        nextPriorityConsumer = (nextPriorityConsumer + 1) % priorityMaxSize;
//        System.out.println("Consumer2: " + Thread.currentThread() + " " + nextPriorityConsumer + " Count: " + count);

        notifyAll();

        return ret;
    }

    public synchronized void enqueue(Elem e) {

        priorityProducer.set(nextPriorityProducerGive);
        nextPriorityProducerGive = (nextPriorityProducerGive + 1) % priorityMaxSize;

        while(count==SIZE || !nextPriorityProducer.equals(priorityProducer.get())) {

//            System.out.println("Producer: " + Thread.currentThread() + " " + priorityProducer.get() + " Count: " + count + " nextPriorityProducer: " + nextPriorityProducer);

            try {
                wait();
            }
            catch (InterruptedException ex) {}
        }

        cells[tail] = e;
        tail = (tail + 1) % SIZE;
        count++;

        nextPriorityProducer = (nextPriorityProducer + 1) % priorityMaxSize;
//        System.out.println("Producer2: " + Thread.currentThread() + " " + nextPriorityProducer);

        notifyAll();
    }

    public String toString() {
        return("head = "+head+", tail = "+tail);
    }
}
