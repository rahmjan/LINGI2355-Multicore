public class SyncQueue implements Queue {
    final static int SIZE = 4;
    int head = 0;
    int tail = 0;
    final Elem[] cells = new Elem[SIZE];
    int count = 0;
    long numOfDeq = 0;
    long numOfEnq = 0;

    public synchronized Elem dequeue() {
        while (count==0) {
            try {
                wait();
            }
            catch (InterruptedException e) {}
        }

        Elem ret = cells[head];
        head = (head + 1) % SIZE;
        count--;
        ++numOfDeq;

        notifyAll();

        return ret;
    }

    public synchronized void enqueue(Elem e) {
        while(count==SIZE) {
            try {
                wait();
            }
            catch (InterruptedException ex) {}
        }

        cells[tail] = e;
        tail = (tail + 1) % SIZE;
        count++;
        ++numOfEnq;

        notifyAll();
    }

    public String toString() {
        return("head = "+head+", tail = "+tail + ", numOfEnq = " + numOfEnq + ", numOfDeq = " + numOfDeq);
    }
}
