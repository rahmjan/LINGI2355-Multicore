package hashsets_benchmark;

public interface Set {
	public boolean add(int value);
	public boolean remove(int value);
	public boolean contains(int value);
	
	// this function can return the number of resize operations that were performed, if it applies to the hashSet implementation.
	// otherwise, it shall return 0.
	public int getResizesCount();
	public int size();
}
