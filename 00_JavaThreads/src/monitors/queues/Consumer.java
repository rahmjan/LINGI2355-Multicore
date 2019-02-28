import java.util.concurrent.ThreadLocalRandom;

public class Consumer implements Runnable {
	static final int WORK_TIME = 10;
	final int nb_tests ;
	private Queue q ;
	private long workingTime = 0;
	private long synchTime = 0;

	public Consumer(Queue q) {
		this.q = q;
		nb_tests = 1000;
	}
	
	public Consumer(Queue q, int nb_tests) {
		this.q = q;
		this.nb_tests = nb_tests;
	}
	
	public void run() {
		long startTime = System.nanoTime();
		try {
			Thread.sleep((int)(ThreadLocalRandom.current().nextDouble()*WORK_TIME));
		} catch (InterruptedException e) { }
		workingTime += (System.nanoTime() - startTime)/(1000*1000);

		for (int i=0; i<nb_tests; i++) {
			startTime = System.nanoTime();
			try {
				Thread.sleep((int)(ThreadLocalRandom.current().nextDouble()*WORK_TIME));
			} catch (InterruptedException e) { }
			workingTime += (System.nanoTime() - startTime)/(1000*1000);

			startTime = System.nanoTime();
			q.dequeue();
			synchTime += (System.nanoTime() - startTime)/(1000*1000);
		}

		System.out.println("Consumer Thred_ID: " + Thread.currentThread().getId() + " Working_time: " + workingTime + " Synch_time: " + synchTime);
	}
}
