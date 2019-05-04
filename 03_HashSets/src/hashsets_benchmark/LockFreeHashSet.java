package hashsets_benchmark;

import hashsets_benchmark.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeHashSet implements Set {
    private LockFreeList[] table;
    private AtomicInteger tableSize;
    private AtomicInteger setSize;
    private AtomicInteger numOfResize;

    public LockFreeHashSet (int numberOfBuckets) {
        table = new LockFreeList[32768];
        table[0] = new LockFreeList();
        tableSize = new AtomicInteger(numberOfBuckets);
        setSize = new AtomicInteger(0);
        numOfResize = new AtomicInteger(0);
    }

    @Override
    public boolean add(int value) {
        int hash = BucketList.hashCode(value);
        int bucket = hash % tableSize.get();
        int key = BucketList.makeOrdinaryKey(value);
        LockFreeList list = getBucketList(bucket);

        if (!list.add(value, key))
            return false;

        resizeCheck();
        setSize.incrementAndGet();
        return true;
    }

    @Override
    public boolean remove(int value) {
        int hash = BucketList.hashCode(value);
        int bucket = hash % tableSize.get();
        int key = BucketList.makeOrdinaryKey(value);
        LockFreeList list = getBucketList(bucket);

        boolean ret = list.remove(key);
        if (ret){
            setSize.decrementAndGet();
        }
        return ret;
    }

    @Override
    public boolean contains(int value) {
        int hash = BucketList.hashCode(value);
        int bucket = hash % tableSize.get();
        int key = BucketList.makeOrdinaryKey(value);
        LockFreeList list = getBucketList(bucket);

        return list.contains(key);
    }

    @Override
    public int size() {
        return setSize.get();
    }

    @Override
    public int getResizesCount() {
        return numOfResize.get();
    }

    private LockFreeList getBucketList(int myBucket){
        if (table[myBucket] == null)
            initializeBucket(myBucket);
        return table[myBucket];
    }

    private void initializeBucket(int bucket) {
        int parent = getParent(bucket);

        if (table[parent] == null)
            initializeBucket(parent);

        int key = BucketList.makeSentinelKey(bucket);
        table[bucket] = new LockFreeList(table[parent], bucket, key);
    }

    private int getParent(int myBucket){
        int parent = tableSize.get();
        do {
            parent = parent >> 1;
        } while (parent > myBucket);
        parent = myBucket - parent;
        return parent;
    }

    private void resizeCheck() {
        if (setSize.get() / tableSize.get() >= 4){ // then resize
            if (table.length >= tableSize.get()*2) {
                if(tableSize.compareAndSet(tableSize.get(), 2 * tableSize.get()))
                    numOfResize.incrementAndGet();
            }
        }
    }
}
