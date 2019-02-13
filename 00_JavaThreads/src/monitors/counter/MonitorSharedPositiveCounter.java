import java.util.concurrent.ThreadLocalRandom;

public class MonitorSharedPositiveCounter {
	private int counter = 0;

	public synchronized void inc() { 
		counter++; 
		notify();
	}
	public synchronized void dec() { 
		if (counter == 0) {
			try { 
				wait(); 
			}
			catch (InterruptedException e) {}
		}
		counter--; 
	}
	public synchronized int get() { return counter; }
	
	public static void main(String[] args) {
		final int NB_THREADS = 1000;
		final int NB_OPS = 1000;
		Thread[] threads = new Thread[NB_THREADS];
		final MonitorSharedPositiveCounter counter = new MonitorSharedPositiveCounter();
		
		// creates a few threads performing random +/- operations
		// note that this code may not terminate if there are more - than + (hence the prob. of 25%/75%)
		for (int i=0;i<NB_THREADS;i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					for (int j=0;j<NB_OPS;j++) {
						if (ThreadLocalRandom.current().nextDouble()<0.25) counter.dec();
						else counter.inc();
					}
				}
			});
			threads[i].start();
		}
		for (int i=0;i<NB_THREADS;i++) {
			try { threads[i].join(); }
			catch (InterruptedException e) {}
		}
		System.out.println("Final value of the counter: "+counter.get());
	}
}
