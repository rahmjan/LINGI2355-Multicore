package hashsets_benchmark;

import java.util.concurrent.ConcurrentHashMap;
import hashsets_benchmark.Set;

public class LockFreeHashSet implements Set {

    private ConcurrentHashMap<Integer, Integer> set ;
    // note that the second field will always be the same as the key.

    public LockFreeHashSet () {
        set = new ConcurrentHashMap<Integer, Integer>();
    }

    public LockFreeHashSet (int numberOfBuckets) {
        // Note: the argument is actually the number of buckets, so we use it to
        // compute an approximate number of elements (with 4 elements per bucket).
        int expectedSize = 4*numberOfBuckets;
        set = new ConcurrentHashMap<Integer, Integer>(expectedSize);
    }

    @Override
    public boolean add(int value) {
        return (set.putIfAbsent(value, value) != null);
    }

    @Override
    public boolean remove(int value) {
        return (set.remove(value) != null);
    }

    @Override
    public boolean contains(int value) {
        return (set.contains(value));
    }

    @Override
    public int size() {
        return (set.size());
    }

    @Override
    public int getResizesCount() {
        return 0; // Unknown
    }
}
