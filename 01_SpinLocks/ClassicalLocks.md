## Implementing classical locks

In this part you will implement classical locks.
The first exercise requires to correct a bogus implementation of the Peterson lock.
The remaining exercises require to produce correct versions of locks for *n* threads, where *n* the number of threads is known *a priori*.

The details of the algorithms for this section can be found in the slides of lecture 2, and will therefore not be repeated.

### A Safety oracle

The code mentioned in this section is available [here](src/2-threads/).

In order to test the safety of our lock implementations, we will make use of the following *safety oracle*:

```Java
public class SafetyOracle {
	private static volatile int threadInCS = -1 ;
	private static volatile boolean fatal = false;
	
	public static void setFatal (boolean fatal) {
		SafetyOracle.fatal = fatal;
	}
	
	private static void failWith(String s) { 
		System.err.println(s);
		if (fatal) System.exit(-1);
	}
	
	public static void enterCS (int id) {
		if (threadInCS != -1) {
			failWith("Thread "+id+" entering its critical section while thread "+threadInCS+" is still in its CS");
		}
		threadInCS = id;
	}
	
	public static void leaveCS (int id) {
		if (threadInCS != id) {
			failWith("Thread "+id+" is leaving its critical section, but oracle thinks Thread "+threadInCS+" is currently in!");
		} else {
			threadInCS = -1;
		}		
	}
}
```

When a thread enters its critical section, it can call the `enterCS` static method of the safety oracle.
When it leaves it can call the `leaveCS` static method.
The oracle may detect when two threads are concurrently running their critical section.

The oracle is not perfect: it is possible to have two threads in their critical sections (e.g. one just before calling `enterCS` and one just after calling `leaveCS`) that are not detected, or the use of the volatile integer `threadInCS` may introduce a synchronization point that makes an otherwise bogus algorithm [appear correct](http://jargonf.org/wiki/tomber_en_marche) when instrumented for debugging.
But in practice, if the oracle does not raise an alarm for many concurrent attempts to enter critical sections made by many threads, this is a good sign.
If `fatal` is set to `true` then any safety violation will terminate the program, otherwise it outputs an error on STDERR.

:warning: While it is recommended to use the oracle while designing the lock algorithms, **it is necessary to disable it when testing their performance**.
Indeed, the `volatile int` variable `threadInCS` introduces a single point of synchronization between threads that will reduce the performance and bias the comparison between lock algorithms.

### 2-thread lock: Peterson

Consider the following interface for a 2-thread lock:

```Java
public interface TwoThreadsLock {
	public void lock(int i);
	public void unlock(int i);
}
```

The following code shows a bogus implementation of the Peterson lock.

```Java
public class UnsafePetersonLock implements TwoThreadsLock {
	private boolean[] flag = new boolean[2];
	private int victim ;
		
	public void lock (int i) {
		// j is the identity of the other thread (by default we have thread 0 and 1)
		int j = 1 - i;
		
		flag[i] = true;
		victim = i;
		while (flag[j] && victim == i) {} // busy wait
	}
	
	public void unlock(int i) {
		flag[i] = false;
	}

}
```

#### Questions

Provide a single answer for the following two questions.

- [ ] Write a test case using this lock, where two threads of identifiers 0 and 1, try to acquire the lock multiple (e.g. 10,000) times. Use the oracle to test for safety violations.
- [ ] Fix the algorithm and explain the modification. *(link to your code and explanation)*

### n-thread locks: Filter and Bakery

The code mentioned in this section is available [here](src/n-threads/).

Consider the following interface for a n-thread lock:

```Java
public interface NThreadsLock {
	public void lock(int i);
	public void unlock(int i);
}
```

The following wrapper classes will allow us to test our homemade locks against Java's monitors and `ReentrantLock` locks:

- [`MonitorLock.java`](src/n-threads/MonitorLock.java) implements the `NThreadsLock` interface using a monitor and associated condition variable;
- [`ReentrantLockWrapper.java`](src/n-threads/ReentrantLockWrapper.java) encapsulates a `ReentrantLock` lock.

#### Action

In the following, you will need to test locks for a varying number of threads, with a fixed number of critical sections.
The recommended steps in the number of threads on Burattini is (1, 2, 4, 6, 8, 12, 16, 20, 24).
On your own machine, for testing, you can use (1, 2, 4, 6, 8) threads.

- [ ] Implement a test case that allows a number of 1 to 24 threads to run a total of 48.000 critical sections (this is a total, so for 2 threads each thread runs 24.000 critical sections, for 4 threads each thread runs 12.000 critical sections, and so on). Use the provided lock wrapper classes as examples, and output the total time on the standard output. Use the safety oracle to assess the correctness of the test case.

#### Questions

- [ ] Implement the Filter lock and test its correctness. *(link to your code)*
- [ ] Implement the Bakery lock and test its correctness. *(link to your code)*

In the [next section](RMWLocks.md) we will build other locks based on *Read-Modify-Write* operations.
