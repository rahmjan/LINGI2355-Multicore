package lists_benchmark;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Benchmark {

	private static Set listFactory(String name) {
		if (name.equals("CoarseGrainLockedList")) {
			return new CoarseGrainLockedList();
		} 
		else if (name.equals("FineGrainLockedList")) {
			return new FineGrainLockedList();
		}
		else if (name.equals("OptimisticLockedList")) {
			return new OptimisticLockedList();
		}
//		else if (name.equals("LazyList")) {
//			return new LazyList();
//		}
//		else if (name.equals("LockFreeList")) {
//			return new LockFreeList();
//		} 
		else {
			System.err.println("No implementation of a set with "+name+" class name.");
			System.exit(-1);
			return null; // compiler, shut up.
		}
	}

	private static void printOutput(String s, boolean silent) {
		if (!silent) {
			System.out.println(s);
		}
	}

	private static void performBenchmark(BenchmarkConfiguration config, boolean silent, int[] threadCounts) {

		for (int listFlavorIndex = 0; listFlavorIndex < config.listFlavors.length-1; listFlavorIndex++) {

			for (int scale = 0; scale < threadCounts.length; scale++) {

				printOutput("# Testing "+config.listFlavors[listFlavorIndex]+" with "+threadCounts[scale]+" threads ("+config.samples+" times)", false);

				DescriptiveStatistics opsPerSecond = new DescriptiveStatistics();
				DescriptiveStatistics runningTimeInMs = new DescriptiveStatistics();
				DescriptiveStatistics sizeOfFinaList = new DescriptiveStatistics();

				for (int sample = 0; sample < config.samples; sample++) {	

					int threadCount = threadCounts[scale];
					Thread[] threads = new Thread[threadCount];

					// create a new, empty list
					Set list = listFactory(config.listFlavors[listFlavorIndex]);
					
					// if necessary, populate the list with some initial values
					for (int i=0; i<config.prepopulate; i++) {
						ThreadLocalRandom rand = ThreadLocalRandom.current();
						list.add(rand.nextInt(config.minimum, config.maximum));	
					}

					// create and launch the threads
					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						threads[thread_index] = new Thread(new Worker(config, list, threadCount));
					}

					long start = System.nanoTime();

					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						threads[thread_index].start();
					}

					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						try {
							threads[thread_index].join();
						} catch (InterruptedException e) { }
					}

					double timeInMs = (System.nanoTime()-start)/(1000*1000);
					double operationsPerSeconds = (config.num_operations * threadCount) / timeInMs * 1000;

					runningTimeInMs.addValue(timeInMs);
					opsPerSecond.addValue(operationsPerSeconds);
					sizeOfFinaList.addValue(list.size());
				}
				// calculate statistics over all the runs
				DecimalFormat df = new DecimalFormat("#.##");
				printOutput(
						config.listFlavors[listFlavorIndex]+" "+
								threadCounts[scale]+" "+
								df.format(runningTimeInMs.getMean())+" "+
								df.format(runningTimeInMs.getStandardDeviation())+" "+
								df.format(opsPerSecond.getMean())+" "+
								df.format(opsPerSecond.getStandardDeviation())+" "+
								df.format(sizeOfFinaList.getMean())+" "+
								df.format(sizeOfFinaList.getStandardDeviation()),
								silent);

			}
		}
	}

	public static void main(String[] args) {
		BenchmarkConfiguration config = new BenchmarkConfiguration(args);

		if (config.useWarmupPhase) {
			System.out.println("# Warming up the JIT compiler, be patient...");
			performBenchmark(config, true, config.threadCountsForWarmup);
		}
		System.out.println("# Performing tests...");
		System.out.println("# [list class name] [number of threads] [running time in ms, avg] [same, std. dev.] [ops per second, avg] [same, std. dev] [size of final list, avg] [same, std. dev]");
		performBenchmark(config, false, config.threadCounts);
	}
}
