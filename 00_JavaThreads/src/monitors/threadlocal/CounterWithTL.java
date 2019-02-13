import java.text.SimpleDateFormat;
import java.util.Date;

public class CounterWithTL {
	private int counter = 0;
	
	private void recordLastAccess() {
		tld.set(new Date());
	}
	private void printLastAccess() {
		if (tld.get() == null) {
			System.out.println("This is first access for "+Thread.currentThread());
		} else {
			System.out.println(Thread.currentThread()+" last access was "+
					(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(tld.get())));
		}
	}
	public synchronized void inc() { 
		printLastAccess(); 
		counter++; 
		recordLastAccess();
	}
	public synchronized void dec() { 
		printLastAccess();
		counter--;
		recordLastAccess();
	}
	public synchronized int get() {
		return counter;
	}
	
	private ThreadLocal<Date> tld = new ThreadLocal<Date>();
	
	public static void main(String[] args) {
		final CounterWithTL counter = new CounterWithTL();
		
		Thread threadA = new Thread(new Runnable() {
			public void run() {
				counter.inc(); counter.dec();
			}
		});
		Thread threadB = new Thread(new Runnable() {
			public void run() {
				counter.dec(); counter.inc();
			}
		});
		threadA.start(); 
		threadB.start();
	}
}
