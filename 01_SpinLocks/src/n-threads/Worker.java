import java.util.concurrent.ThreadLocalRandom;

public class Worker implements Runnable {

    private static NThreadsLock lock;
    private static final SafetyOracle safe = new SafetyOracle();

    private int workerID;
    private int workToDo;

    public Worker(int i, int j, NThreadsLock inLock){
        workerID = i;
        workToDo = j;
        lock = inLock;
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
        for (int i = 0; i < workToDo; ++i) {
            // Get lock
            lock.lock(workerID);
            safe.enterCS(workerID);

            // Work in critical section
            try {
                Thread.sleep((int)(ThreadLocalRandom.current().nextDouble()*10));
            } catch (InterruptedException e) {}

//            System.out.println("ID: " + workerID + " position: " + i);

            // Release lock
            safe.leaveCS(workerID);
            lock.unlock(workerID);
        }
    }

    public static void main(String[] args) {

        final int NB_WORKERS  = Integer.parseInt(args[1]);
        final int NB_TESTS = Integer.parseInt(args[2]) / NB_WORKERS;

        // create the lock
        NThreadsLock l = null;
        switch(Integer.parseInt(args[0])){
            case 0:
            default:
                l = new FilterLock(NB_WORKERS);
                break;

            case 1:
                l = new BakeryLock(NB_WORKERS);
                break;
        }

        Thread[] workers = new Thread[NB_WORKERS];

        // create the threads and run them
        for (int i=0; i < NB_WORKERS; i++) {
            workers[i] = new Thread(new Worker(i, NB_TESTS, l));
            workers[i].start();
        }

        // wait for them to terminate
        for (int i=0; i < NB_WORKERS; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {}
        }

        System.out.println("END of execution.");
    }
}
