package lists_benchmark;

import java.util.Vector;

public class BenchmarkConfiguration {
	public int num_operations = 1000;
	public boolean num_operations_is_per_thread = true ;
	
	public enum operations {ADD, REMOVE, CONTAINS};
	public double percentageOfAdd = 0.3;
	public double percentageOfRemove = 0.3;
	public double percentageOfContains = 0.4;
	
	public boolean useWarmupPhase = true;

	public int[] threadCountsForWarmup = {4};
//	public int[] threadCounts = {1, 2, 3, 4, 5, 6, 7, 8}; // for pascaline
	public int[] threadCounts = {1, 2, 4, 6, 8, 12, 16, 20, 24}; // for burattini
	
	// uncomment to add new list types
	public String[] listFlavors = {
			"CoarseGrainLockedList",
			"FineGrainLockedList",
			"OptimisticLockedList",
			"LazyList",
			"LockFreeList",
			"null"
	};
	
	public int samples = 10;
	
	public int prepopulate = 0;
	
	public int minimum = 0;
	public int maximum = 10000;

	boolean validate() {
		return (percentageOfAdd+percentageOfRemove+percentageOfContains == 1.0);
	}
	
	public operations getOperation(double coin) {
		if (coin < percentageOfAdd) {
			return operations.ADD;
		} else if ((coin - percentageOfAdd) < percentageOfRemove) {
			return operations.REMOVE;
		} else {
			return operations.CONTAINS;
		}
	}
	
	public BenchmarkConfiguration (String[] args) {
	
		/* the order of parameters is the following:
		 * - minimum of the values inserted in the list (integer)
		 * - maximum of the values inserted in the list (integer)
		 * - number of operations performed (integer)
		 * - number of operations is per thread (PerThread) or in total (InTotal) (string)
		 * - percentage of ADD operations (double)
		 * - percentage of REMOVE operations (double)
		 * - percentage of LOOKUP operations (double)
		 * - number of elements to add before benchmark (integer)
		 * 	(note: the actual number of elements in the list will depend on the success rate of insertions)
		 * - use a JIT warmup phase with 8 threads (boolean) -- recommended
		 * - number of samples for each measurements (integer) -- 10 recommended
		 * - number of threads to use (list of integers) -- e.g. 2 4 8 for three configurations
		 */

		// if there is nothing on the command line, use the default configuration
		if (args.length == 0) {
			System.out.println("# Using default configuration parameters.");
		} else {
			System.out.println("# Using provided configuration parameters.");
			int index = 0;
			minimum = Integer.parseInt(args[index]);
			index++;
			maximum = Integer.parseInt(args[index]);
			index++;
			num_operations = Integer.parseInt(args[index]);
			index++;
			if (args[index].equalsIgnoreCase("PerThread")) {
				num_operations_is_per_thread = true ;
			} else if (args[index].equalsIgnoreCase("InTotal")) {
				num_operations_is_per_thread = false ;
			} else {
				System.err.println("Invalid argument (should be PerThread or InTotal): "+args[index]);
			}
			index++;
			percentageOfAdd = Double.parseDouble(args[index]);
			index++;
			percentageOfRemove = Double.parseDouble(args[index]);
			index++;
			percentageOfContains = Double.parseDouble(args[index]);
			if (!validate()) {
				System.err.println("The percentages of ADD/REMOVE/CONTAINS are not summing up to 1.");
				System.exit(-1);
			}
			index++;
			prepopulate = Integer.parseInt(args[index]);
			index++;
			useWarmupPhase = Boolean.parseBoolean(args[index]);
			index++;
			samples = Integer.parseInt(args[index]);
			index++;
			Vector<Integer> threads = new Vector<Integer>();
			while (index < args.length) {
				threads.add(Integer.parseInt(args[index]));
				index++;
			}
			if (threads.size() == 0) {
				System.err.println("Must provide at least one thread count.");
				System.exit(-1);
			}
			threadCounts = new int[threads.size()];
			for (int i=0; i< threads.size(); i++) {
				threadCounts[i] = threads.get(i);
			}
		}
	}

}
