package hashsets_benchmark;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Benchmark {

	private static void printOutput(String s, boolean silent) {
		if (!silent) {
			System.out.println(s);
		}
	}

	private static void performBenchmark(BenchmarkConfiguration config, boolean silent, int[] threadCounts) {

		for (int setFlavorIndex = 0; setFlavorIndex < config.setFlavors.length; setFlavorIndex++) {

			for (int scale = 0; scale < threadCounts.length; scale++) {

				printOutput("# Testing "+config.setFlavors[setFlavorIndex]+" with "+threadCounts[scale]+" threads ("+config.measurementTime+" ms, "+config.samples+" times)", false);

				DescriptiveStatistics opsPerSecond = new DescriptiveStatistics();
				DescriptiveStatistics opsPerThreadPerSecond = new DescriptiveStatistics();
				DescriptiveStatistics sizeOfFinalSet = new DescriptiveStatistics();
				DescriptiveStatistics numberOfResizes = new DescriptiveStatistics();

				for (int sample = 0; sample < config.samples; sample++) {	

					int threadCount = threadCounts[scale];
					Thread[] threads = new Thread[threadCount];

					// create a new, empty set
					Set set = config.setFactory(config.setFlavors[setFlavorIndex]);
					
					// if necessary, populate the list with some initial values
					for (int i=0; i<config.prepopulate; i++) {
						ThreadLocalRandom rand = ThreadLocalRandom.current();
						set.add(rand.nextInt(config.minimum, config.maximum));	
					}

					// create and launch the threads
					Worker[] workers = new Worker[threadCount];
					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						workers[thread_index] = new Worker(config, set, threadCount);
					}
					
					// create the threads, launch them, start operations synchronously, wait for all to complete
					Worker.performOperations.set(false);
					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						threads[thread_index] = new Thread(workers[thread_index]);
					}

					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						threads[thread_index].start();
					}
					
					Worker.performOperations.set(true);
					
					//System.out.println("waiting for "+config.measurementTime+" milliseconds");
					
					// wait for the duration of the experiment
					try {
						Thread.sleep(config.measurementTime);
					} catch (InterruptedException e) { }
					
					//System.out.println("setting perform op to false");
					
					Worker.performOperations.set(false);

					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						try {
							threads[thread_index].join();
						} catch (InterruptedException e) { }
					}

					// collect statistics
					long totalOperations = 0;
					for (int thread_index=0; thread_index < threadCount; thread_index++) {
						totalOperations += workers[thread_index].numberOfPerformedOperations();
					}
					
					double operationsPerSecond = totalOperations / (config.measurementTime / 1000.0);
					double operationsPerThreadPerSecond = operationsPerSecond / threadCount;					
					
					opsPerSecond.addValue(operationsPerSecond);
					opsPerThreadPerSecond.addValue(operationsPerThreadPerSecond);
					sizeOfFinalSet.addValue(set.size());
					numberOfResizes.addValue(set.getResizesCount());
				}
				// calculate statistics over all the runs
				DecimalFormat df = new DecimalFormat("#.##");
				printOutput(
						config.setFlavors[setFlavorIndex]+" "+
								threadCounts[scale]+" "+
								(config.measurementTime/1000)+" "+
								df.format(opsPerSecond.getMean())+" "+
								df.format(opsPerSecond.getStandardDeviation())+" "+
								df.format(opsPerThreadPerSecond.getMean())+" "+
								df.format(opsPerThreadPerSecond.getStandardDeviation())+" "+
								df.format(sizeOfFinalSet.getMean())+" "+
								df.format(sizeOfFinalSet.getStandardDeviation())+" "+
								df.format(numberOfResizes.getMean())+" "+
								df.format(numberOfResizes.getStandardDeviation()),
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
		System.out.println("# [set class name] [number of threads] "+
						"[measurement time in seconds] " +
						"[ops per second, avg] [same, std. dev.] " +
						"[ops per second per thread, avg] [same, std. dev]" +
						"[size of final set, avg] [same, std. dev] "+
						"[number of resizes, avg] [same, std. dev]");
		performBenchmark(config, false, config.threadCounts);
	}
}
