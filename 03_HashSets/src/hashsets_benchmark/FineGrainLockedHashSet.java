package hashsets_benchmark;

import hashsets_benchmark.Set;
import java.util.*;

public class FineGrainLockedHashSet implements Set {
    private Object[] lock;
    private List[] table;
    private int[] sizeOfList; // size of bucket
    private int numOfResize = 0;

    public FineGrainLockedHashSet (int numberOfBuckets) {
        table = new List[numberOfBuckets];
        lock = new Object[numberOfBuckets];
        sizeOfList = new int[numberOfBuckets];

        for (int i = 0; i < numberOfBuckets; i++) {
            lock[i] = new Object();
            table[i] = new LinkedList<Integer>();
            sizeOfList[i] = 0;
        }
    }

    @Override
    public boolean add(int value) {
        int tabHash = BucketList.hashCode(value) % table.length;
        int keyHash = BucketList.hashCode(value) % lock.length;

        if (policy()){
            resize();
        }

        synchronized (lock[keyHash]) {
            if (table[tabHash].contains(value)){
                return false;
            }

            boolean ret = table[tabHash].add(value);
            if (ret){
                sizeOfList[tabHash]++;
            }
            return ret;
        }
    }

    @Override
    public boolean remove(int value) {
        int tabHash = BucketList.hashCode(value) % table.length;
        int keyHash = BucketList.hashCode(value) % lock.length;

        synchronized (lock[keyHash]) {
            boolean ret = table[tabHash].remove((Integer)value);
            if (ret){
                sizeOfList[tabHash]--;
            }
            return ret;
        }
    }

    @Override
    public boolean contains(int value) {
        int tabHash = BucketList.hashCode(value) % table.length;
        int keyHash = BucketList.hashCode(value) % lock.length;

        synchronized (lock[keyHash]) {
            return table[tabHash].contains(value);
        }
    }

    @Override
    public int size() {
        int numOfElements = 0;
        for (int i = 0; i < table.length; i++)
        {
            numOfElements += sizeOfList[i];
        }
        return numOfElements;
    }

    @Override
    public int getResizesCount() {
        return numOfResize;
    }

    private boolean policy(){
        return (this.size() / table.length) >= 4;
    }

    private void resize(int depth, List[] oldTab) {
        synchronized (lock[depth]) {
            if (depth == 0 && oldTab != table)
                return;

            int next = depth + 1;
            if (next < lock.length)
                resize(next, oldTab);
            else
                sequentialResize();
        }
    }

    private void resize() { resize(0, table); }

    private void sequentialResize() {
        int newCapacity =  table.length*2;
        List[] oldTable = table;
        table = new List[newCapacity];
        sizeOfList = new int[newCapacity];

        for (int i = 0; i < newCapacity; i++) {
            table[i] = new LinkedList<Integer>();
        }

        for (List<Integer> bucket : oldTable){
            for (Integer x : bucket){
                int tabHash = BucketList.hashCode(x) % table.length;
                table[tabHash].add(x);
                sizeOfList[tabHash]++;
            }
        }

        numOfResize++;
    }
}
