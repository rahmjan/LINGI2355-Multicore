public class SharedCounter {
    private volatile long c = 0;
    public void inc() {
        c++;
    }
    public long get() {
        return c;
    }
}