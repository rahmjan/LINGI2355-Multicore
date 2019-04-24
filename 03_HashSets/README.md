## Introduction

In this fourth practical assignment we will continue our exploration of the construction of efficient concurrent sets, this time implementing Hashing-based data structures (often called *hash tables*).
We will start with a coarse-grain locking implementation, then follow up with a fine-grain locking implementation using stripped locks, and finally with a lock-free implementation.
Optionally, you can implement a refinable version of the fine-grain locking implementation, for bonus points.
Similarly to the previous assignment, the benchmarking framework, a wrapper for the built-in concurrent HashMap of the Java SDK, and plotting scripts, are provided.

### Running the provided code

It is recommended to get familiar with the provided code first.
This code is very similar to the code of the previous assignment.

Have a look at the following classes:
- [`Benchmark.java`](src/hashsets_benchmark/Benchmark.java) contains the main entry point you will call from the command line.
- [`BenchmarkConfiguration.java`](src/hashsets_benchmark/BenchmarkConfiguration.java) contains the configuration parser and the default values for a test.
- [`Worker.java`](src/hashsets_benchmark/Worker.java) contains the code of the threads that perform operations on the shared set object.
- [`Set.java`](src/hashsets_benchmark/Set.java) is the interface that all hash-based Set implementations have to implement. 
- [`JavaConcurrentHashMapWrapper.java`](src/hashsets_benchmark/JavaConcurrentHashMapWrapper.java) is an implementation of a hash-based Set using Java-provided [concurrent HashMap](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html).

The `Benchmark` class uses the [Common Math](http://commons.apache.org/proper/commons-math/) library from Apache, available in [`commons-math3-3.6.1.jar`](src/commons-math3-3.6.1.jar).

:warning: As for the previous practical assignment on lists, the hash sets in this assignment contain integer values and not *objects* as seen in the generic algorithms seen in class.
The semantics of the set for this practical are the same as for the previous:
- The set only contains integer (`int`) values;
- A value can be present only *once* in the set;
- Values inserted in the set are picked randomly between a *min* and a *max*;
- We assume the min and the max are always greater, resp. smaller, than `Integer.MIN_VALUE / 2` and `Integer.MAX_VALUE / 2`.

You can compile and run the code from the [`src`](src/) folder using:

```
javac -cp .:commons-math3-3.6.1.jar hashsets_benchmark/*.java
java -cp .:commons-math3-3.6.1.jar hashsets_benchmark/Benchmark
```

#### Adding a new hash set implementation

The provided code only contains a wrapper to the Java-provided `HashMap` implementation.
In order to add a new hash set implementation, you must:
- Make sure that it implements the `Set` interface;
- Modify the hash set factory in [`Benchmark.java`](src/hashsets_benchmark/Benchmark.java) to support the creation of a hash set of this type based on its class name;
- Add the class name to the `setFlavors` array in the [`BenchmarkConfiguration.java`](src/hashsets_benchmark/BenchmarkConfiguration.java) configuration class.

:clap: While the practical is conceived to be self-contained (and can be realized without doing the one on list-based sets), it is possible to merge the two and compare the performance of list-based sets with those of hash-based sets, by continuing in your previous project.
This will not grant additional points, but it may make sense in your particular configuration, e.g. if you modified the benchmark classes to add new features.

#### Changing configuration parameters

The benchmark contains a set of default parameters.
These differ from the ones used for list-based sets:

- The hash-based sets use 4 buckets initially;
- The set starts with 2000 elements;
- All threads send operations as fast as they can for a duration of 5 seconds;
- The nature of each operation is selected at random between:
	- **ADD**, with 30% probability;
	- **REMOVE**, with 30% probability;
	- **CONTAINS**, with 40% probability.
- The value for each operation is selected at random in the range [0:10000];
- For each configuration (number of thread, and set flavor), the test is repeated 5 times and the mean and standard deviations are calculated;
- The benchmark uses a warmup phase with 4 threads and all set flavors, in order to trigger Just-in-Time (JIT) compilation before taking time measurements.

This default configuration is used when no parameters are given.
You can also provide different parameters on the command line.
The order and types of these parameters are detailed in the [`BenchmarkConfiguration.java`](src/hashsets_benchmark/BenchmarkConfiguration.java) file.

:exclamation: There is a new parameter at the very beginning of the list of parameters, `starting_buckets`.
It is the number of buckets that your hash-based sets use initially.
If an implementation does not support resizing, then the linked list associated with each bucket can grow pretty large.
The `HashMap` class supports resizing, but the Java documentation recommends providing the estimated number of elements in the set to the constructor.
By default, this number is 4 times the number of buckets used with the other implementations, as the recommended policy is to resize when the average number of elements per buckets reaches 4 or more.

#### Plotting performance

You are provided with a *gnuplot* script in the [plots](plots/) folder.
It is used exactly as for the previous practical, henceforth instructions are not repeated here.

Again, you are free to use your own favorite plotting tool if you prefer.

#### Action

- [ ] Analyze the provided code and plot the performance of the HashMap-wrapper set flavor using the default parameters. 

### Concurrent hash-based set flavors

You are provided with one class:

- `JavaConcurrentHashMapWrapper.java`: the provided wrapper to the java HashMap.

You task in this practical is to implement three flavors of concurrent hash-based sets seen in class under the following files/class names.
Please use the recommended class names: it will make your life easier when using the plotting script.
You will end up with the following new 3 classes:

- `CoarseGrainLockedHashSet.java`: a coarse-grain locking approach where a lock is taken for the entire set for any operation;
- `FineGrainLockedHashSet.java`: a fine-grain locking approach where a fixed set of locks is used to control concurrent accesses to set of buckets. As the number of buckets increase due to resizing, the locks are stripped and cover a growing number of entries;
- `LockFreeHashSet.java`: the lock-free version.

Additionally, if you decide to perform the bonus task, you will also have the following class:

- `RefinableFineGrainLockedHashSet.java`: a fine-grain locking approach where the number of locks is the same as the number of buckets, and is adapted dynamically during the resizing.

For the `FineGrainLockedHashSet.java` implementation:

- you can use the [Read/Write lock class](`FineGrainLockedHashSet.java`) provided by the Java SDK.
- the use of optimistic synchronization for `contains()` operations is optional. If you use it, mention it in your report.

For the `LockFreeHashSet.java` implementation:

- the skeleton of the [`BucketList.java`](src/hashsets_benchmark/BucketList.java) class provides already some useful functions for the generation of normal and sentinel keys.

#### Resizing policy and mechanism

It is a requirement that your hash-based set implementations support dynamic resizing, but only for growing (adding more buckets).
It can make sense to first code and test the classes without resizing, and add resizing in a second phase.
Resizing is typically triggered during a add operation based on the state of the list.

You are free to implement any resizing policy.
A suggestion is to resize when the average number of items per bucket is equal to or greater than 4.

It is important however to ensure that two nodes concurrently adding a value, do not both decide to resize and end up creating 4 times more buckets instead of just 2 times.
:wink: Note that the code shown in class may not completely fulfill this requirement ...

#### Question

- [ ] Implement the four hash-based sets and test them extensively using unit tests. *(link to your code)*

#### Bonus question

This question can provide up to **4 bonus points**.
If the bonus points get you a grade over 20/20 :sunglasses: then the points will be dispatched to the other practicals (but the average for all practicals will be capped to 20).

Realizing this question will require you to consult Chapter 13 of the reference book.
Using the [SST library search page](https://bib.uclouvain.be/opac/ucl/en), lookup *"The art of multiprocessor programming"*.
Make sure you use the revised reprint.
The book is available as an electronic resource, but a copy is also available there for consulting or borrowing.

- [ ] Implement the *Refinable* version of the fine-grain locking approach.

### Test configurations

Once you are convinced of the correctness of your implementations, use Burattini to test the hash sets using the standard thread counts (1, 2, 4, 6, 8, 12, 16, 20, and 24 threads), 3 seconds measurement time (or more) and 3 samples (or more).

#### Configuration 1: mixed workload

This is the default configuration.

#### Configuration 2: read dominated workload

This is the default configuration modified to have 90% CONTAINS, 9% ADD and 1% REMOVE operations (as )

#### Configuration 3: resizing performance

In this configuration we want to only add elements and trigger resizing operations.
Make sure your classes implement the `getResizesCount()` method from the `Set` interface to get statistics on resizings.

- Values are chosen in the range [0:100000];
- The set is initially empty;
- 100% of operations are ADD;
- The initial number of buckets is 4.

#### Configuration 4: starting with more buckets

This is the same configuration as previous, but now, you should start with the number of buckets that is optimal to avoid resizing.
This number depends on your resizing policy, and the final size of the set (based on the result of experiment 3).
Use the results to comment on the impact of resizing operations on performance (if any).

#### Other configurations

You are welcome, but not required, to test with other configurations of your choice.

#### Question

- [ ] For each configuration, produce a plot that shows the throughput in operations per second (average and standard deviation). Explain the performance and scalability of the different set flavors, and the potential impact of resizings. *(link to your four plots in PDF, and explanations of the plots in the [Report.md](Report.md) file)*

### Deliverable

The deliverable will cover the code, the four plots, and the four plots analysis.
The deadline is **May 7, 22:00**.
As usual we will use the last commit before this date as the final version, unless explicitly told otherwise.
