import java.util.concurrent.ThreadLocalRandom;

public class Worker implements Runnable {

//    private static final TwoThreadsLock lock = new UnsafePetersonLock();
    private static final TwoThreadsLock lock = new SafePetersonLock(2);
    private static final SafetyOracle safe = new SafetyOracle();
    private int workerID;

    public Worker(int i){
        workerID = i;
    }

    public void run() {

        // Wait before start
        try {
            Thread.sleep((int)(ThreadLocalRandom.current().nextDouble()*30));
        } catch (InterruptedException e) {}

        // Work
        doWork();
    }

    private void doWork() {
        for (int i = 0; i < 10000; ++i) {
            // Get lock
            lock.lock(workerID);
            safe.enterCS(workerID);

            // Work in critical section
            try {
                Thread.sleep((int)(ThreadLocalRandom.current().nextDouble()*10));
            } catch (InterruptedException e) {}

            System.out.println("ID: " + workerID + " position: " + i);

            // Release lock
            safe.leaveCS(workerID);
            lock.unlock(workerID);
        }
    }

    public static void main(String[] args) {

        int numOfThreads = 2;
        Thread[] workers = new Thread[numOfThreads];

        // create the threads and run them
        for (int i=0; i < numOfThreads; i++) {
            workers[i] = new Thread(new Worker(i));
            workers[i].start();
        }

        // wait for them to terminate
        for (int i=0; i < numOfThreads; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {}
        }

        System.out.println("END of execution.");
    }
}
