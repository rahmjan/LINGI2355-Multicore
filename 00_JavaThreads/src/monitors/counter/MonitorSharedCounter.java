public class MonitorSharedCounter {
	private int counter = 0;
	
	public synchronized void inc() { counter++; }
	public synchronized void dec() { counter--; }
	public synchronized int get() { return counter; }
}
