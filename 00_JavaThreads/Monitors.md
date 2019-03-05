## Using Java built-in synchronization: monitors

Java provides high-level language constructs to synchronize the access to shared data using mutually-exclusive critical sections.
We will learn how to build efficient implementations of mutual exclusion in this course, but first we must know (or remember) how to use existing high-level mechanisms.
They will often act as a reference point for our more scalable algorithms.
We will start with the monitor model that is associated with all Java objects.

### A simple, unsynchronized queue

We will consider a simple queue shared between a group of threads, the consumers, and another group of threads, the producers.
`Producer` threads generate new instances of a class `Elem`, for `Consumer` threads to consume.
Each instance must be consumed only once.
If the number of consumptions equals the number of generations in the execution of our multi-threaded program, the program should terminate will all produced `Elem` objects being consumed and with no remaining object in the queue.

[`Elem`](src/monitors/queues/Elem.java) is an empty class. The interface of our shared queue is as follows:

```java
public interface Queue {
	public Elem dequeue();
	public void enqueue(Elem e);
}
```

The [`Consumer`](src/monitors/queues/Consumer.java) and [`Producer`](src/monitors/queues/Producer.java) class are quite straightforward: each waits a bit of time before starting, and then consumes (resp. produces) a number of elements.
The [`Driver`](src/monitors/queues/Driver.java) creates and launches the producers and consumers and displays the final status of the shared queue at the end of the execution.

A first tentative implementation of the shared queue is provided by the [`UnsyncQueue`](src/monitors/queues/UnsyncQueue.java) class reproduced below:

```java
public class UnsyncQueue implements Queue {
	final static int SIZE = 100 ;
	int head = 0;
	int tail = 0;
	final Elem[] cells = new Elem[SIZE];
	int count = 0;

	public Elem dequeue() {
		while(head==tail) {}
		Elem ret = cells[(head++) % SIZE];
		count--;
		return ret;
	}

	public void enqueue(Elem e) {
		while(count==SIZE) {}
		cells[(tail++) % SIZE] = e;
		count++;
	}

	public String toString() {
		return("head = "+head+", tail = "+tail);
	}
}
```

Two pointers, `head` and `tail` indicate the index of the next available item, and the index of the first empty slot.
This is a FIFO queue.
We can see that the `dequeue()` call performs a busy-waiting loop until an element is available in the shared queue.
Similarly, the `enqueue()` call busy-waits when there is no empty slot available.

:warning: You may have noticed that the code is not using the *traditional* `Math.random()` call to generate pseudo-random numbers, but instead uses `ThreadLocalRandom.current().nextDouble()`.
The reason for this is that the `Math.random()` is itself subject to synchronization and will constitute an unecessary contention point in the application.
You can see more explanations in the [Java documentation](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadLocalRandom.html).

#### Questions

- [ ] Execute the code several times (hint: you can add more producers than consumers if necessary). Is the behavior as expected?
- [ ] Remove the initial waiting time before threads start consuming and producing elements. What is the behavior and how can we explain it?
- [ ] Use a large number of produced and consumed elements and remove all waiting times. What happens?

### Synchronized methods

Java monitors is one (but not the only) way to perform synchronization for accesses to shared objects in Java.
Each object is associated with a *lock*, and with a *condition variable*.
The `synchronized` keyword allows determining regions of code that should execute in mutual exclusion.
Under the monitor model, these regions of code are the access methods for the shared object.

Consider the following shared counter [(available here)](src/monitors/counter/SharedCounter.java):

```java
public class SharedCounter {
	private int counter = 0;
	
	public void inc() { counter++; }
	public void dec() { counter--; }
	public int get() { return counter; }
}
```

This object is not safe since the `++` and `--` operations are not atomic but composed of several individual operations when compiled to the instruction set of the JVM and of the host.
If two threads call the `inc()` method concurrently, we can have the following interleaving of operations:

```
T1: reads counter to register1  (gets 10)
T1: increments register1        (-> 11)
**T1 is preempted**
T2: reads counter to register2  (gets 10)
T2: increments register2        (-> 11)
T2: writes register2 to counter (writes 11)
**T1 resumes ... later**
T1: writes register1 to counter (writes 11)
```

In order to prevent T2 from accessing method `inc()` or `dec()` while T1 is not done executing either of them, we can turn `SharedCounter` into a monitor (class [`MonitorSharedCounter`](src/monitors/counter/MonitorSharedCounter.java)):

```java
public class MonitorSharedCounter {
	private int counter = 0;
	
	public synchronized void inc() { counter++; }
	public synchronized void dec() { counter--; }
	public synchronized int get() { return counter; }
}
```

Entering a `synchronized` method requires acquiring the lock associated with the object.
As long as one thread is currently executing any of the `synchronized` methods for this object, other threads are therefore prevented from executing any of the `synchronized` methods on the same object.

:warning: *There is one lock per object, and not per class or per method: Threads can access concurrently synchronized methods of different objects of the same class, but two threads cannot access concurrently two synchronized methods of the same object.*

#### Question

- [ ] What is the consistency model offered by the SharedCounterMonitor class? Let us now imagine that we remove the `synchronized` keyword from the `get()` method. Does this change the consistency model and how? (you may need to wait for the third lecture to answer this question)

### Condition variables

Sometimes it is necessary for a thread to halt and wait for a condition to be fulfilled in order to perform an operation on a shared object.
The fulfilment of this condition typically depends on the action of other threads.

Each Java object is associated with a **single** *condition variable* that allows threads to *wait* on a condition, or to *notify* other waiting threads that the condition is met.

The `wait()` call is part of the `Object` class.
It is a blocking call.
When it is called, the thread:
- releases the lock associated with the object;
- is put in a waiting queue associated with the condition variable of this object, and it not scheduled for execution until removed from that queue.

The `notify()` call is also part of the `Object` class.
When it is called, one thread from the condition variable for this object is put in the *Runnable* state and will be considered for execution as soon as the lock associated with the object will be available.
When the thread is scheduled, it obtains the lock to the object and returns from its `wait()` call.

The `notifyAll()` call is similar, excepts that it puts *all* waiting threads in the *Runnable* state.
All these threads will be scheduled for execution as soon as the lock for the object will be available (but obviously only one of them can get the lock at a time).

The following example [(available here)](src/monitors/counter/MonitorSharedPositiveCounter.java) shows how to implement a shared counter that cannot hold negative values:

```java
public class MonitorSharedPositiveCounter {
	private int counter = 0;

	public synchronized void inc() { 
		counter++; 
		notify();
	}
	public synchronized void dec() { 
		if (counter == 0) {
			try { 
				wait(); 
			} catch (InterruptedException e) {}
		}
		counter--; 
	}
	public synchronized int get() { return counter; }
}
```

#### Questions

- [ ] What happens if we use a `notifyAll()` call instead of `notify()` in this example? Is our code still matching the specification? If not, what would be required?
- [ ] Fix the implementation of the shared queue to form a linearizable, safe concurrent object of class `SyncQueue`. Your implementation will be blocking: when there is no element to consume, a consumer thread will be blocked for an unbounded amount of time waiting for a producer thread to produce something, and similarly for producers waiting for a free empty slot.

### Hey you, get in line! FIFO-access (bounded waiting) queue and ThreadLocal variables

The [Java language specification](https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#notify()) tells us that the thread that is selected to be made Runnable by the `notify()` call is selected arbitrarily.
Similarly, we have no control on the order in which threads awaken with a `notifyAll()` call will get the lock and resume from their `wait()` call.

We would like to improve our queue implementation by adding some fairness.
We would like to make our queue support a FIFO-access order (in addition to being a FIFO queue).
In more details, the order in which producers should be allowed to produce must be the same as the order in which they *first* acquired the lock to access the `enqueue()` method.
The same applies for consumers for the `dequeue()` method.

:warning: *The ordering is only between a producer and other producers, or between a consumer and other consumers. There is no ordering requirement between producers and consumers.*

:warning: *The FIFO-access applies both to threads that must wait on the condition variable of the queue object and those who see that a slot/an item is readily available when they access `enqueue()` or `dequeue()`: the latter can not out-pass the former out of mere opportunity.*

In order to implement this behavior, it can be convenient to keep some specific state for each thread, accessed while running a method of the shared queue object.
This can be done using *thread-local* variables, declared as `ThreadLocal<Type>`.
Note that `Type` must be a reference type (e.g. `Integer`) and cannot be a primitive type (e.g. `int`, `double`).
The thread local variable, unique to each thread accessing it from a method of the shared object, is accessed using `set()` and `get()` accessors as shown in this example ([see the full code](src/monitors/threadlocal/CounterWithTL.java)):

```java
public class CounterWithTL {
	private int counter = 0;
	
	private ThreadLocal<Date> tld = new ThreadLocal<Date>();
	
	private void recordLastAccess() {
		tld.set(new Date());
	}
	private void printLastAccess() {
		if (tld.get() == null) {
			System.out.println("This is the first access for "+Thread.currentThread());
		} else {
			System.out.println(Thread.currentThread()+" last access was "+
					(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(tld.get())));
		}
	}
    /* ... */

}
```

#### Question

- [ ] Make a copy of the `SyncQueue` class called `FifoSyncQueue` that implements the FIFO-access fairness guarantee (also called bounded waiting in the lecture). Your implementation will use thread-local variables. Justify the correctness of your construction by providing a proof sketch.

### Synchronized blocks

It is actually possible to use synchronized blocks beyond the specific case of a method of a shared object.
In fact, the use of the `synchronized` keyword in the method signature is *[syntactic sugar](https://en.wikipedia.org/wiki/Syntactic_sugar)*.
This code:

```java
public synchronized void myMethod() {
	// critical section
}
```

is strictly equivalent to:

```java
public void myMethod() {
	synchronized (this) {
		// critical section
	}
}
```

The access to an object can be synchronized using synchronized blocks that explicit the object of which the associated lock must be taken.
Since the `wait()` and `notify/All()` calls are part of the root `Object` class, it is possible to call operations related to the condition variable of an object `o` using:

```java
o.wait(); o.notify(); o.notifyAll();
```

Note that these calls will trigger a runtime exception if called by a thread that is not currently holding the lock to the object, i.e. if they are called from outside a synchronized block for this particular object.
