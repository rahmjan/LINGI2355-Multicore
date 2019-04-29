package hashsets_benchmark;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import hashsets_benchmark.BenchmarkConfiguration.operations;

public class Worker implements Runnable {
	// static boolean used to control the duration of the test
	public static AtomicBoolean performOperations = new AtomicBoolean(false) ;
	
	// used to keep some stats about how well the operations went
	private class OperationsStatistics {
		long addSuccess = 0;
		long addFailure = 0;
		long removeSuccess = 0;
		long removeFailure = 0;
		long lookupSuccess = 0;
		long lookupFailure = 0;
		protected long successes () {
			return (addSuccess + removeSuccess + lookupSuccess);
		}
		protected long failures() {
			return (addFailure + removeFailure + lookupFailure);
		}
	}

	final BenchmarkConfiguration config;
	final Set set;
	final OperationsStatistics stats;
	final int num_threads;

	public long numberOfPerformedOperations () {
		return (stats.successes()+stats.failures());
	}
	
	public Worker(BenchmarkConfiguration config, Set set, int num_threads) {
		this.config = config;
		this.set = set;
		stats = new OperationsStatistics();
		this.num_threads = num_threads;
	}

	@Override
	public String toString() {
		// print the collected statistics
		return "ADD "+stats.addSuccess+":"+stats.addFailure+
			" REMOVE "+stats.removeSuccess+":"+stats.removeFailure+
			" LOOKUP "+stats.lookupSuccess+":"+stats.lookupFailure;
	}
	
	@Override
	public void run() {
		ThreadLocalRandom rand = ThreadLocalRandom.current();

		// wait for the order to start running
		while(!performOperations.get()) {}
		
		// the worker generates random add, remove and deletes in the provided set, until instructed to stop
		while(performOperations.get()) {
			// start by deciding the nature of the operation
			double coin = rand.nextDouble();
			BenchmarkConfiguration.operations op = config.getOperation(coin);

			// randomly choose an integer to add/delete/remove
			int value = rand.nextInt(config.minimum, config.maximum);			

			// perform the operation
			if (op == operations.ADD) {
				if (set.add(value)) {
					stats.addSuccess++;
					//System.out.println("adding value "+value+" (success)");
				} else {
					stats.addFailure++;
					//System.out.println("adding value "+value+" (failure)");
				}				
			} else if (op == operations.REMOVE) {
				if (set.remove(value)) {
					stats.removeSuccess++;
					//System.out.println("removing value "+value+" (success)");
				} else {
					stats.removeFailure++;
					//System.out.println("removing value "+value+" (failure)");
				}
			} else {
				// assume this is a lookup
				if (set.contains(value)) {
					stats.lookupSuccess++;
					//System.out.println("lookup value "+value+" (success)");
				} else {
					stats.lookupFailure++;
					//System.out.println("lookup value "+value+" (failure)");
				}
			}
		}
	}
}
