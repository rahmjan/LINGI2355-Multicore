public class Driver {
	static final int NB_PROD = 200;
	static final int NB_CONS = 200;
	static final int NB_TESTS = 20;
	
	public static void main(String[] args) {
		Thread[] cons = new Thread[NB_CONS];
		Thread[] prods = new Thread[NB_PROD];
		
		// create the shared queue
//		Queue q = new UnsyncQueue();
		Queue q = new SyncQueue();

		// create the threads and run them
		for (int i=0;i<NB_CONS;i++) {
			cons[i]=new Thread(new Consumer(q,NB_TESTS));
			cons[i].start();
		}
		for (int i=0;i<NB_PROD;i++) {
			prods[i]=new Thread(new Producer(q,NB_TESTS));
			prods[i].start();
		}
		// wait for them to terminate
		for (int i=0;i<NB_PROD;i++) {
			try {
				prods[i].join();
			} catch (InterruptedException e) {}
		}
		for (int i=0;i<NB_CONS;i++) {
			try {
				cons[i].join();
			} catch (InterruptedException e) {}
		}	
		// observe the state of the queue
		System.out.println(q);
	}
}
