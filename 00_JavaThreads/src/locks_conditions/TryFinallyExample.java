import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class TryFinallyExample {
	final ReentrantLock lock = new ReentrantLock();
	
	final private class DivideByZeroException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public void dangerousMethod (int min_val) throws DivideByZeroException {
		lock.lock();
		int i = 5; 
		do {
			i--;
			if (i==0) throw new DivideByZeroException();
			System.out.println("10 divided by "+i+" is "+(10/i));
		} while (i > min_val);
		lock.unlock();
	}
	
	public void safeMethod (int min_val) throws DivideByZeroException {
		lock.lock();
		try {
			int i = 5; 
			do {
				i--;
				if (i==0) throw new DivideByZeroException();
				System.out.println("10 divided by "+i+" is "+(10/i));
			} while (i > min_val);
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) {
		final int NUM_THREADS = 100 ;
		Thread[] threads = new Thread[NUM_THREADS];
		TryFinallyExample shared_object = new TryFinallyExample();
		for (int i=0;i<NUM_THREADS;i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					int min_val = (int)(ThreadLocalRandom.current().nextDouble()*10);
					try {
						shared_object.dangerousMethod(min_val);
						// replace by:
						// shared_object.safeMethod(min_val);
					} catch (DivideByZeroException e) {
						System.out.println("Ooops min val was too small");
					}
				}
			});
			threads[i].start();
		}
		for (int i=0;i<NUM_THREADS;i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) { }
		}
		System.out.println("the end");
	}
}
