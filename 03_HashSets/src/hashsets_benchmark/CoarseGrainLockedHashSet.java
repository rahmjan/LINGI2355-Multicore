package hashsets_benchmark;

import hashsets_benchmark.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;

public class CoarseGrainLockedHashSet implements Set {
    private final ReentrantLock lock;
    private List[] table;
    private int SetSize = 0;
    private int numOfResize = 0;

    public CoarseGrainLockedHashSet (int numberOfBuckets) {
        lock = new ReentrantLock();
        table = new List[numberOfBuckets];

        for (int i = 0; i < numberOfBuckets; i++) {
            table[i] = new LinkedList<Integer>();
        }
    }

    @Override
    public boolean add(int value) {
        int tabHash = BucketList.hashCode(value) % table.length;

        if (policy()){
            resize();
        }

        lock.lock();
        try {
            if (table[tabHash].contains(value)){
                return false;
            }

            boolean ret = table[tabHash].add(value);
            if (ret){
                SetSize++;
            }
            return ret;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(int value) {
        int tabHash = BucketList.hashCode(value) % table.length;

        lock.lock();
        try {
            boolean ret = table[tabHash].remove((Integer)value);
            if (ret){
                SetSize--;
            }
            return ret;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(int value) {
        int tabHash = BucketList.hashCode(value) % table.length;

        lock.lock();
        try {
            return table[tabHash].contains(value);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        return SetSize;
    }

    @Override
    public int getResizesCount() {
        return numOfResize;
    }

    private boolean policy(){
        return (SetSize / table.length) >= 4;
    }

    private void resize(){
        int oldCapacity = table.length;
        lock.lock();
        try {
            if (oldCapacity != table.length){
                return;
            }

            int newCapacity = oldCapacity*2;
            List[] oldTable = table;
            table = new List[newCapacity];

            for (int i = 0; i < newCapacity; i++) {
                table[i] = new LinkedList<Integer>();
            }

            for (List<Integer> bucket : oldTable){
                for (Integer x : bucket){
                    int tabHash = BucketList.hashCode(x) % table.length;
                    table[tabHash].add(x);
                }
            }

            numOfResize++;
        }
        finally {
            lock.unlock();
        }
    }
}
