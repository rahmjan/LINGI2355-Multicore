package hashsets_benchmark;

import static org.junit.Assert.*;
import java.util.concurrent.ThreadLocalRandom;

public class UnitTests {
    private final int starting_buckets = 4;

    public UnitTests(){}

    public void do_unit_tests(){
        System.out.println("Unit tests - Start!!");

        u_CoarseGrainLockedHashSet();
        u_FineGrainLockedHashSet();
        u_FineGrainLockedHashSetWithLocks();
        u_LockFreeHashSet();

        System.out.println("Unit tests - End!!");
        return;
    }

    private void u_CoarseGrainLockedHashSet(){
        Set set = new CoarseGrainLockedHashSet(starting_buckets);
        defTest(set);
        defTestConcurrent(set);
    }

    private void u_FineGrainLockedHashSet(){
        Set set = new FineGrainLockedHashSet(starting_buckets);
        defTest(set);
        defTestConcurrent(set);
    }

    private void u_FineGrainLockedHashSetWithLocks(){
        Set set = new FineGrainLockedHashSetWithLocks(starting_buckets);
        defTest(set);
        defTestConcurrent(set);
    }

    private void u_LockFreeHashSet(){
        Set set = new LockFreeHashSet(starting_buckets);
        defTest(set);
        defTestConcurrent(set);
    }

    private void defTest(Set set){
        assertEquals(0, set.size());

        assertTrue(set.add(16));
        assertTrue(set.add(4));
        assertTrue(set.add(9));
        assertTrue(set.add(7));
        assertTrue(set.add(5));

        assertTrue(set.contains(4));
        assertFalse(set.contains(0));
        assertFalse(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(3));
        assertTrue(set.contains(5));

        assertEquals(5, set.size());

        assertFalse(set.remove(0));
        assertFalse(set.remove(1));
        assertFalse(set.remove(2));
        assertTrue(set.remove(5));
        assertTrue(set.remove(9));

        assertEquals(3, set.size());

        assertTrue(set.remove(7));
        assertTrue(set.remove(4));
        assertTrue(set.remove(16));

        assertEquals(0, set.size());
    }

    private void defTestConcurrent(Set set){
        assertEquals(0, set.size());
        int NB_WORKERS = 4;

        startJob(NB_WORKERS, 0, set);
        assertEquals(2000000, set.size());

        startJob(NB_WORKERS, 1, set);

        startJob(NB_WORKERS, 2, set);
        assertEquals(0, set.size());
    }

    private void startJob(int NB_WORKERS, int job, Set set){
        Thread[] workers = new Thread[NB_WORKERS];
        for (int i=0; i < NB_WORKERS; i++) {
            workers[i] = new Thread(new Worker(set, job));
            workers[i].start();
        }

        for (int i=0; i < NB_WORKERS; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {}
        }
    }

    private class Worker implements Runnable {
        private Set set;
        private int job;

        public Worker(Set set, int job){this.set = set; this.job = job;}

        public void run() {
            switch (job){
                default:
                case 0:
                    job0();
                    break;

                case 1:
                    job1();
                    break;

                case 2:
                    job2();
                    break;
            }
        }

        private void job0(){
            for (int j = 0; j < 2000000; ++j)
            {
                set.add(j);
            }
        }

        private void job1(){
            for (int j = 0; j < 2000000; ++j)
            {
                assertTrue(set.contains(j));
            }
        }

        private void job2(){
            for (int j = 0; j < 2000000; ++j)
            {
                set.remove(j);
            }
        }
    }
}
