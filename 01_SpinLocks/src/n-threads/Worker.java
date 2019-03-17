import java.util.concurrent.ThreadLocalRandom;

public class Worker implements Runnable {

    public static SharedCounter counter = new SharedCounter();
    private static NThreadsLock lock;
    private static final SafetyOracle safe = new SafetyOracle();
    public static volatile long totalWorkingTime = 0;
    public static volatile long totalSynchTime = 0;

    private int workerID;
    private int workToDo;
    private long workingTime = 0;
    private long synchTime = 0;

    public Worker(int i, int j, NThreadsLock inLock){
        workerID = i;
        workToDo = j;
        lock = inLock;
    }

    public void run() {
        long startTime = System.nanoTime();

        // Work
        doWork();

        workingTime += (System.nanoTime() - startTime)/(1000*1000);

        totalWorkingTime += workingTime;
        totalSynchTime += synchTime;
    }

    private void doWork() {
        for (int i = 0; i < workToDo; ++i) {
            // Get lock
            long startTime = System.nanoTime();
            lock.lock(workerID);
            synchTime += (System.nanoTime() - startTime)/(1000*1000);
//            safe.enterCS(workerID);

            // Work in critical section
            for (int j = 0; j < 50; ++j)
            {
                counter.inc();
            }

            // Release lock
//            safe.leaveCS(workerID);
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
                l = new MonitorLock(NB_WORKERS);
                break;

            case 1:
                l = new ReentrantLockWrapper(NB_WORKERS);
                break;

            case 2:
                l = new FilterLock(NB_WORKERS);
                break;

            case 3:
                l = new BakeryLock(NB_WORKERS);
                break;

            case 4:
                l = new TASLock(NB_WORKERS);
                break;

            case 5:
                l = new TTASLock(NB_WORKERS);
                break;

            case 6:
                l = new EBTTASLock(NB_WORKERS);
                break;

            case 7:
                l = new AndersonLock(NB_WORKERS);
                break;

            case 8:
                l = new AndersonPaddedLock(NB_WORKERS);
                break;

            case 9:
                l = new CLHLock(NB_WORKERS);
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

        // print statistic
        System.out.println("Threads:\t" + NB_WORKERS + "\tAll_time:\t" + (Worker.totalWorkingTime/NB_WORKERS) + "\tWorking_time:\t" + ((Worker.totalWorkingTime-Worker.totalSynchTime)/NB_WORKERS) + "\tSynch_time:\t" + (Worker.totalSynchTime/NB_WORKERS) + "\tCounter:\t" + Worker.counter.get() );
    }
}
