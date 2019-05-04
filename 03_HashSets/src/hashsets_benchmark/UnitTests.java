package hashsets_benchmark;

import org.junit.*;

public class UnitTests {
    private int starting_buckets = 4;

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
        System.out.println("CoarseGrainLockedHashSet:");
        Set set = new CoarseGrainLockedHashSet(starting_buckets);
        defTest(set);
    }

    private void u_FineGrainLockedHashSet(){
        System.out.println("FineGrainLockedHashSet:");
        Set set = new FineGrainLockedHashSet(starting_buckets);
        defTest(set);
    }

    private void u_FineGrainLockedHashSetWithLocks(){
        System.out.println("FineGrainLockedHashSetWithLocks:");
        Set set = new FineGrainLockedHashSetWithLocks(starting_buckets);
        defTest(set);
    }

    private void u_LockFreeHashSet(){
        System.out.println("LockFreeHashSet:");
        Set set = new LockFreeHashSet(starting_buckets);
        defTest(set);
    }

    private void defTest(Set set){

    }
}
