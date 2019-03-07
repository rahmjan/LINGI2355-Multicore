## Locks evaluation

We will now proceed to evaluating the performance of the various locks we have built.
We will only consider the n-threads locks.
You should have the following 10 n-thread lock classes available:

- [`MonitorLock.java`](src/n-threads/MonitorLock.java) -- provided
- [`ReentrantLockWrapper.java`](src/n-threads/ReentrantLockWrapper.java) -- provided
- `FilterLock.java`
- `BakeryLock.java`
- `TASLock.java`
- `TTASLock.java`
- `EBTTASLock.java`
- `AndersonLock.java`
- `AndersonPaddedLock.java`
- `CLHLock.java`

### A lock factory

In order to make their evaluation simpler, you can use a lock factory as follows:

```Java
private static NThreadsLock getLockInstanceByClassName(String lockClassName, int threadCount) {
	if (lockClassName.equals("MonitorLock")) {
		return new MonitorLock(threadCount);
	} else if (lockClassName.equals("...")) {
		return new ...
	} else if ... // add other lock classes here	 
	} else {
		System.err.println("Invalid class name "+lockClassName);
		System.exit(-1);
		return null; // remove compiler warning
	}
}

List<String> locksImplementationsToTest = new ArrayList<String>(Arrays.asList(
	"MonitorLock",
	"ReentrantLockWrapper",
	"FilterLock",
	... // add the locks you would like to test
));

for (String s : locksImplementationsToTest) {
	testLock(s, true);
}
```

### Test conditions and output

When in a critical section, threads should update several 50 times a counter, instance of the following class and shared by all threads:

```Java
public class SharedCounter {
	private volatile long c = 0;
	public void inc() {
		c++;
	}
	public long get() {
		return c;
	}
}
```

The tests must be performed under the following conditions:

- There must be no wait outside of critical sections, i.e. threads are continuously attempting to enter their critical sections when they are not currently executing it.
- When in their critical sections, threads are incrementing 50 times the shared counter.
- The recommended number of critical sections is 48.000 over all threads, and a time resolution for the output in milliseconds.
- The benchmark program must test for a number of threads ranging from 1 to 24 on the Burattini machine. The recommended steps are (1, 2, 4, 6, 8, 12, 16, 20, 24).
- You should disable the use of the `SafetyOracle` oracle.

The tests measure the time taken for the *n* threads to perform the total 48.000 critical sections.
Each point of information is therefore a triplet (lock name, number of threads, time in milliseconds).
The tests should output data that is easily parsable by a plotting script (e.g. using gnuplot or R).
Another solution is to output CSV (comma-separated-values) that can be easily pasted into a spreadsheet (Google sheets, Microsoft Excel, etc.).

It is important to get meaningful results to take into account the impact of the JVM's Just-in-Time compiler to collect meaningful results, as detailed in the following section.

### Avoiding JIT-based measurement errors

The simplest way to avoid performance deviations due to Just-in-time compilation is to exercise the JVM by running the methods that are in the critical path (the path whose time is measured) sufficiently many times to trigger their compilation, before measuring their running time.
The Oracle JVM JIT thresholds are rather complicated to understand but a safe practice is to run the methods 10.000 times or more to be on the safe side.

In our case, the simplest solution is to run the full benchmark twice in a row: once to exercise the JIT compiler, and another time to collect measurements.

#### Actions

- [ ] If you do not know what a Just-in-Time (JIT) compiler is, read the corresponding [Wikipedia page](https://en.wikipedia.org/wiki/Just-in-time_compilation) introduction and overview sections.

### Running the tests

You can now modify your test case to perform a complete exploration of the parameters space.
With 1 to 24 threads (9 configurations), and 10 lock implementations, your benchmark should output 90 triplets.
Synchronize with the other students to run your benchmark alone on Burattini.

Benchmarking is a complicated matter, in particular in Java.
The results we will get here will give us an idea of the trends but will not always be conclusive: they depend on so many factors, including but not limited to, the triggering of garbage collection by the JVM, concurrently-running processes, optimizations made by the JVM, etc.
It may be necessary to run the test another time if the results for one of the lock implementations seems too far off (e.g. if they take many seconds for one triplet, except for `FilterLock` which is expected to be bad for large thread count always).
In case of doubt, do not hesitate to compare your results with those of other students.

#### Question

- [ ] Implement and run the benchmark on Burattini. Write a short (20-30 lines) report of your findings: Are the results and performance of the different locks as expected? Are there cases where the performance is not as good as you would expect? Provide a link to your final benchmark implementation. *(link to your code and explanation)*
