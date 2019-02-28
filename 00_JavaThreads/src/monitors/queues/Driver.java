public class Driver {

	public static void main(String[] args) {
		final int NB_PROD  = Integer.parseInt(args[1]);
		final int NB_CONS  = Integer.parseInt(args[2]);
		final int NB_TESTS = Integer.parseInt(args[3]);

		Thread[] cons = new Thread[NB_CONS];
		Thread[] prods = new Thread[NB_PROD];

		System.out.println("\nNum of threads: " + NB_PROD);
		
		// create the shared queue
		Queue q = null;
		switch(Integer.parseInt(args[0])){
			case 0:
			default:
				q = new UnsyncQueue();
				break;

			case 1:
				q = new SyncQueue();
				break;

			case 2:
				q = new FifoSyncQueue();
				break;

			case 3:
				q = new LockQueue();
				break;

			case 4:
				q = new FifoLockQueue();
				break;
		}

		// create the threads and run them
		for (int i=0;i<NB_CONS;i++) {
			cons[i]=new Thread(new Consumer(q,NB_TESTS/NB_PROD));
			cons[i].start();
		}
		for (int i=0;i<NB_PROD;i++) {
			prods[i]=new Thread(new Producer(q,NB_TESTS/NB_CONS));
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
