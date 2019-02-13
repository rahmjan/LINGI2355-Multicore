public class SharedCounter {
	private int counter = 0;
	
	public void inc() { counter++; }
	public void dec() { counter--; }
	public int get() { return counter; }
}
