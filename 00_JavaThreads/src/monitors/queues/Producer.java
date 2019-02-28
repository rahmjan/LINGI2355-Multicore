import java.util.concurrent.ThreadLocalRandom;

public class Producer implements Runnable {
	static final int WORK_TIME = 50;
	final int nb_tests;
	private Queue q ;

	public Producer(Queue q) {
		this.q = q;
		nb_tests = 1000;
	}

	public Producer(Queue q, int nb_tests) {
		this.q = q;
		this.nb_tests = nb_tests;
	}

	public void run() {
		try {
			Thread.sleep((int)(ThreadLocalRandom.current().nextDouble()*WORK_TIME));
		} catch (InterruptedException e) { }
		for (int i=0; i<nb_tests; i++) {
			try {
				Thread.sleep((int)(ThreadLocalRandom.current().nextDouble()*WORK_TIME));
			} catch (InterruptedException e) { }
			q.enqueue(new Elem());
		}
	}
}
