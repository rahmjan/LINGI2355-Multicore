public class UnsyncQueue implements Queue {
	final static int SIZE = 100 ;
	int head = 0;
	int tail = 0;
	final Elem[] cells = new Elem[SIZE];
	int count = 0;

	public Elem dequeue() {
		while(head==tail) {}
		Elem ret = cells[(head++) % SIZE];
		count--;
		return ret;
	}

	public void enqueue(Elem e) {
		while(count==SIZE) {}
		cells[(tail++) % SIZE] = e;
		count++;
	}

	public String toString() {
		return("head = "+head+", tail = "+tail);
	}
}
