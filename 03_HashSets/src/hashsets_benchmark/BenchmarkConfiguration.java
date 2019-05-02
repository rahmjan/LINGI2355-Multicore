package hashsets_benchmark;

import java.util.Vector;

public class BenchmarkConfiguration {
	// this is a new parameter compared to the listSet benchmark:
	// it is the number of starting buckets used by the hashSet implementations.
	// If a hashSet does not support resizing, this will remain the number of buckets in the structure.
	// Note: the HashMap provided by Java does not take the number of buckets but the expected size as parameter.
	//       The wrapper takes this into account already.
	public int starting_buckets = 4;
	
	public enum operations {ADD, REMOVE, CONTAINS};
	public int percentageOfAdd = 30;
	public int percentageOfRemove = 30;
	public int percentageOfContains = 40;
	public boolean useWarmupPhase = true;
	public int prepopulate = 2000;
	public int minimum = 0;
	public int maximum = 10000;
	public int measurementTime = 3000; // in milliseconds
	public int samples = 3;
	
	public int[] threadCountsForWarmup = {4};
	public int[] threadCounts = {1, 2, 4, 6, 8, 12, 16, 20, 24}; // for burattini
	private String threadCountsToString () {
		String ret = "";
		int ind = 0;
		while (ind < threadCounts.length) {
			ret=ret+threadCounts[ind];
			if (ind < threadCounts.length - 1) {
				ret=ret+", ";
			}
			ind++;
		}
		return ret;
	}
		
	// uncomment to add new list types
	public String[] setFlavors= {
//			"JavaConcurrentHashMapWrapper",
			"CoarseGrainLockedHashSet",
//			"FineGrainLockedHashSet",
//			"RefinableFineGrainLockedHashSet",
//			"LockFreeHashSet",
			"null"
	};
	
	public Set setFactory(String name) {
		if (name.equals("JavaConcurrentHashMapWrapper")) {
			return new JavaConcurrentHashMapWrapper(starting_buckets);
		} 
		else if (name.equals("CoarseGrainLockedHashSet")) {
			return new CoarseGrainLockedHashSet(starting_buckets);
		}
		else if (name.equals("FineGrainLockedHashSet")) {
			return new FineGrainLockedHashSet(starting_buckets);
		}
//		else if (name.equals("RefinableFineGrainLockedHashSet")) {
//			return new RefinableFineGrainLockedHashSet(starting_buckets);
//		}
		else if (name.equals("LockFreeHashSet")) {
			return new LockFreeHashSet(starting_buckets);
		}
		else {
			System.err.println("No implementation of a set with "+name+" class name.");
			System.exit(-1);
			return null; // compiler, shut up.
		}
	}
	
	boolean validate() {
		return (percentageOfAdd+percentageOfRemove+percentageOfContains == 100);
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
		 * - starting size (number of buckets) for the hashset (integer)
		 * - minimum of the values inserted in the set (integer)
		 * - maximum of the values inserted in the set (integer)
		 * - percentage of ADD operations (0-100 integer)
		 * - percentage of REMOVE operations (0-100 integer)
		 * - percentage of LOOKUP operations (0-100 integer)
		 * - number of elements to add before benchmark (integer) 
		 * 	(note: the actual number of elements in the set will depend on the success rate of insertions)
		 * - use a JIT warmup phase with 8 threads (boolean) -- recommended
		 * - duration of the measurement (integer, milliseconds) -- 
		 * - number of samples for each measurements (integer) -- 5 recommended
		 * - number of threads to use (list of integers) -- e.g. 2 4 8 for three configurations
		 *  (note: if the number of threads is not provided, the default of 1 2 4 6 8 12 16 20 24 will be used)
		 */
		
		// if there is nothing on the command line, use the default configuration
		if (args.length == 0) {
			System.out.println("# Using default configuration parameters.");
		} else {
			System.out.println("# Using provided configuration parameters.");
			int index = 0;
			starting_buckets = Integer.parseInt(args[index]);
			index++;
			minimum = Integer.parseInt(args[index]);
			index++;
			maximum = Integer.parseInt(args[index]);
			index++;
			percentageOfAdd = Integer.parseInt(args[index]);
			index++;
			percentageOfRemove = Integer.parseInt(args[index]);
			index++;
			percentageOfContains = Integer.parseInt(args[index]);
			if (!validate()) {
				System.err.println("The percentages of ADD/REMOVE/CONTAINS are not summing up to 100%.");
				System.exit(-1);
			}
			index++;
			prepopulate = Integer.parseInt(args[index]);
			index++;
			useWarmupPhase = Boolean.parseBoolean(args[index]);
			index++;
			measurementTime = Integer.parseInt(args[index]);
			index++;
			samples = Integer.parseInt(args[index]);
			index++;
			Vector<Integer> threads = new Vector<Integer>();
			while (index < args.length) {
				threads.add(Integer.parseInt(args[index]));
				index++;
			}
			if (threads.size() == 0) {
				System.out.println("Using standard thread counts: "+threadCountsToString());
			} else {
				threadCounts = new int[threads.size()];
				for (int i=0; i< threads.size(); i++) {
					threadCounts[i] = threads.get(i);
				}
			}
		}
	}

}
