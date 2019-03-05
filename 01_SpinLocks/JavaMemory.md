## The Java memory model

We start by discussing the Java memory model and the use of atomic variables.

We will start by studying an example highlighting the visibility of writes ([also available here](src/impact_volatile/Volatile.java)).

### A simple example

```Java
public class Visibility {
	static boolean stop = false;
	
	public static void main(String[] args) {
		Thread tA, tB;
		
		tA = new Thread(new Runnable() {
			public void run() {
				while (!stop) { }
				System.out.println("Reader stops ");
			}
		});
		
		tB = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
				stop=true;
			}
		});
		
		long start = System.nanoTime();
		tA.start(); tB.start();
		try {
			tA.join(); tB.join();
		} catch (InterruptedException e) {}
		
		System.out.println("Total running time: "+((System.nanoTime()-start)/1000000)+" ms.");
	}
}

```

In this example, thread `tA` spins on reading the value of boolean variable `stop`.
Thread `tB` sleeps for 500 milliseconds and sets the value of `stop` to true.
The expected behavior is obviously that both threads should stop and that the `main` function should display the execution time and exit.

#### Action

- [ ] Try this code (no answer needed).

### Visibility of writes

As we have seen in class, modern CPUs and the Java virtual machine model provide no guarantee (not even sequential consistency) for the visibility of updates to shared variables made by different threads.
The reason is that imposing sequential consistency would be too costly for the general case.
In other words, while thread `tA` will see its own writes in the order in which it issues them, there is no guarantee that it will be the case for writes made by other threads.

Let's imagine that we have two values `a` and `b`, and two threads `tA` and `tB`.
Initially, `a==1` and `b==2`.
`tA` writes `a=3` and `b=4`.
If `tB` reads `a`, then `b`, if can very well read `a==1 && b==4`, which means it does not see the writes from `tA` in the order in which they were issued by `tA`.
The previous example has even shown us that the writes from `tA` might actually *never* be visible to `tB`.

### The `volatile` keyword

In Java, the `volatile` keyword can be used to declare a variable for which the visibility order of writes by different threads actually *matters*.
This is the case of variables used for coordination between independent threads.

A read or a write to a `volatile` variable will trigger the use of appropriate barriers by the JVM.
The nature of the barrier to be used (see lecture) depends on the dependencies that the bytecode interpreter or the Just-in-Time compiler extracts.

From the programmer perspective, it is sufficient to know that a write to a `volatile` variable is *atomic*: The order in which these writes will be visible to other threads is unique and the variable has the semantics of a linearizable register.

#### Action

- [ ] Modify the previous code accordingly and check that the expected behavior is respected.

### Atomic variables

In addition to the `volatile` keyword, the Java standard library provides a number of explicitly-atomic types such as Integers, Booleans, or References.
A complete list can be found in the [atomic package summary](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/package-summary.html).

The behavior of an atomic Integer (class [`AtomicInteger`](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/AtomicInteger.html)) is similar to an `int` variable that is declared `volatile` when it comes to the visibility of writes.
However, such types also come with a number of additional methods that a basic type (such as `int`) does not provide.

Besides these additional capabilities, it is often easier to read code that makes use of atomic types rather than `volatile` variables.
This is the reason this method is favored as a convention by some Java developers.
Note that, unlike for an `int` integer basic type, setting the value of an `AtomicInteger` object requires using the `.set(value)` method and not the assignment `=` operator.
Similarly, you must use the `get()` method to access its value.
There is no *synctatic sugar* allowing to use the assignment and read operators, as you can do with the `Integer` class.

### Atomic arrays

It is important to understand that the following declaration:

```Java
volatile int[] myArray;
```

does **not** declare `myArray` as an array of `volatile int`.
Only the reference to the array is `volatile`.
Its element are *ordinary* integers, with no guarantees on the visibility of the writes that they receive.
This is a common source of bugs!

To use an array of atomic integers, you must declare an array of `AtomicInteger` objects:

```Java
AtomicInteger[] myArray = new AtomicInteger[size];
for (int i=0; i < size; i++) {
	myArray[i] = new AtomicInteger(0);
}
myArray[0].set(1);
```

In the case of integers, it is also possible to use the wrapper class [AtomicIntegerArray](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/AtomicIntegerArray.html), who offers `get()` and `set()` accessors with an index:

```Java
AtomicIntegerArray myArray = new AtomicIntegerArray(size);
myArray.set(0, 1);
```

We can note, however, that this possibility does not exist for other types, and in particular for booleans.
If we want to use an array of atomic booleans we must declare an array of `AtomicBoolean` objects.

#### Action

- [ ] Explore the documentation of the [`AtomicInteger`](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/AtomicInteger.html) class and observe the relation with the atomic RMW (Read-Write-Modify) operations that we have discussed in class.

You are now ready to proceed to the [next part](ClassicalLocks.md) and implement your first locks.
