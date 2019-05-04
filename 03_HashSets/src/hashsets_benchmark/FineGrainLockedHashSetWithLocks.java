package hashsets_benchmark;

import hashsets_benchmark.Set;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;

public class FineGrainLockedHashSetWithLocks implements Set {
    private ReentrantReadWriteLock[] lock;
    private List[] table;
    private AtomicInteger setSize;
    private int numOfResize = 0;

    public FineGrainLockedHashSetWithLocks (int numberOfBuckets) {
        table = new List[numberOfBuckets];
        lock = new ReentrantReadWriteLock[numberOfBuckets];
        setSize = new AtomicInteger(0);

        for (int i = 0; i < numberOfBuckets; i++) {
            lock[i] = new ReentrantReadWriteLock();
            table[i] = new LinkedList<Integer>();
        }
    }

    @Override
    public boolean add(int value) {
        boolean ret = false;
        int keyHash = BucketList.hashCode(value) % lock.length;

        lock[keyHash].writeLock().lock();
        try{
            int tabHash = BucketList.hashCode(value) % table.length;

            if (!table[tabHash].contains(value)){
                table[tabHash].add(value);
                ret = true;
                setSize.incrementAndGet();
            }
        }
        finally {
            lock[keyHash].writeLock().unlock();
        }

        if (policy()){
            resize();
        }

        return ret;
    }

    @Override
    public boolean remove(int value) {
        int keyHash = BucketList.hashCode(value) % lock.length;

        lock[keyHash].writeLock().lock();
        try{
            int tabHash = BucketList.hashCode(value) % table.length;
            boolean ret = table[tabHash].remove((Integer)value);
            if (ret){
                setSize.decrementAndGet();
            }
            return ret;
        }
        finally {
            lock[keyHash].writeLock().unlock();
        }
    }

    @Override
    public boolean contains(int value) {
        int keyHash = BucketList.hashCode(value) % lock.length;

        lock[keyHash].readLock().lock();
        try{
            int tabHash = BucketList.hashCode(value) % table.length;
            return table[tabHash].contains(value);
        }
        finally {
            lock[keyHash].readLock().unlock();
        }
    }

    @Override
    public int size() {
        return setSize.get();
    }

    @Override
    public int getResizesCount() {
        return numOfResize;
    }

    private boolean policy(){
        return (this.size() / table.length) >= 4;
    }

    private void resize(int depth, List[] oldTab) {
        lock[depth].writeLock().lock();
        try{
            if (depth == 0 && oldTab != table)
                return;

            int next = depth + 1;
            if (next < lock.length)
                resize(next, oldTab);
            else
                sequentialResize();
        }
        finally {
            lock[depth].writeLock().unlock();
        }
    }

    private void resize() { resize(0, table); }

    private void sequentialResize() {
        int newCapacity =  table.length*2;
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
}
