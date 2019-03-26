package lists_benchmark;

import java.util.concurrent.ThreadLocalRandom;

import lists_benchmark.BenchmarkConfiguration.operations;

public class Worker implements Runnable {
	// used to keep some stats about how well the operations went
	private class OperationsStatistics {
		long addSuccess = 0;
		long addFailure = 0;
		long removeSuccess = 0;
		long removeFailure = 0;
		long lookupSuccess = 0;
		long lookupFailure = 0;
	}

	final BenchmarkConfiguration config;
	final Set list;
	final OperationsStatistics stats;
	final int num_threads;

	public Worker(BenchmarkConfiguration config, Set list, int num_threads) {
		this.config = config;
		this.list = list;
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

		// determine the number of operations for this worker run
		int num_operations = config.num_operations;
		if (!config.num_operations_is_per_thread) {
			num_operations /= num_threads;
		}
		
		// the worker generates random add, remove and deletes in the provided set
		for (int i = 0; i < num_operations; i++) {
			// start by deciding the nature of the operation
			double coin = rand.nextDouble();
			BenchmarkConfiguration.operations op = config.getOperation(coin);

			// randomly choose an integer to add/delete/remove
			int value = rand.nextInt(config.minimum, config.maximum);			

			// perform the operation
			if (op == operations.ADD) {
				if (list.add(value)) {
					stats.addSuccess++;
					//System.out.println("adding value "+value+" (success)");
				} else {
					stats.addFailure++;
					//System.out.println("adding value "+value+" (failure)");
				}				
			} else if (op == operations.REMOVE) {
				if (list.remove(value)) {
					stats.removeSuccess++;
					//System.out.println("removing value "+value+" (success)");
				} else {
					stats.removeFailure++;
					//System.out.println("removing value "+value+" (failure)");
				}
			} else {
				// assume this is a lookup
				if (list.contains(value)) {
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
