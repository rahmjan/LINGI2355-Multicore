## Introduction

In this third practical assignment we will construct four implementations of a concurrent Set, represented as a list in memory.
We will compare their relative performance, together with a coarse-grain-lock implementation.
The benchmarking framework, coarse-grain-lock implementation, and plotting scripts, are provided.

### Running the provided code

It is recommended to get familiar with the provided code first.
This code implements a benchmark suite that can be parameterized in several ways, from the command line.

Have a look at the following classes:
- [`Benchmark.java`](src/lists_benchmark/Benchmark.java) contains the main entry point you will call from the command line.
- [`BenchmarkConfiguration.java`](src/lists_benchmark/BenchmarkConfiguration.java) contains the configuration parser and the default values for a test.
- [`Worker.java`](src/lists_benchmark/Worker.java) contains the code of the threads that perform operations on the shared list object.
- [`Set.java`](src/lists_benchmark/Set.java) is the interface that all list-based Set implementations have to implement. 
- [`CoarseGrainLockedList.java`](src/lists_benchmark/CoarseGrainLockedList.java) is an implementation of a list-based Set using coarse-grain locking.

The `Benchmark` class uses the [Common Math](http://commons.apache.org/proper/commons-math/) library from Apache, available in [`commons-math3-3.6.1.jar`](src/commons-math3-3.6.1.jar).

:warning: The lists in this assignment contain integer values and not *objects* as seen in the generic algorithms seen in class.
The semantics of the set for this practical should be as follows:
- The set only contains integer (`int`) values;
- A value can be present only *once* in the set;
- Values inserted in the set are picked randomly between a *min* and a *max*;
- We assume the min and the max are always greater, resp. smaller, than `Integer.MIN_VALUE` and `Integer.MAX_VALUE`.

You can compile and run the code from the [`src`](src/) folder using:

```
javac -cp .:commons-math3-3.6.1.jar lists_benchmark/*.java
java -cp .:commons-math3-3.6.1.jar lists_benchmark/Benchmark
```

#### Adding a new list implementation

The provided code only contains the coarse-grain-lock list flavor.
In order to add a new list implementation, you must:
- Make sure that it implements the `Set` interface;
- Modify the list factory in [`Benchmark.java`](src/lists_benchmark/Benchmark.java) to support the creation of a list of this type based on its class name;
- Add the class name to the `listFlavors` array in the `BenchmarkConfiguration.java`](src/lists_benchmark/BenchmarkConfiguration.java) configuration class.

#### Changing configuration parameters

The benchmark contains a set of default parameters:
- The list starts with no elements;
- Each thread performs operations for a duration of one second;
- The nature of each operation is selected at random between:
	- **ADD**, with 30% probability;
	- **REMOVE**, with 30% probability;
	- **LOOKUP**, with 40% probability.
- The value for each operation is selected at random in the range [0:10000];
- For each configuration (number of thread, and list flavor), the test is repeated 5 times and the mean and standard deviations are calculated;
- The benchmark uses a warmup phase with 4 threads and all list flavors, in order to trigger Just-in-Time (JIT) compilation before taking time measurements.

This default configuration is used when no parameters are given.
You can also provide different parameters on the command line.
The list, order and types of these parameters are detailed in the [`BenchmarkConfiguration.java`](src/lists_benchmark/BenchmarkConfiguration.java) file.
Note that if you do not provide a list of thread counts, the default value will be used, but for other parameters you must provide a value, even if it is the same as the default.
You can also observe the use of the static `AtomicBoolean` object to control when `Worker` threads start and stop issuing operations to the shared list.

#### Plotting performance

You are provided with two *gnuplot* scripts in the [plots](plots/) folder.
The `plot_total.gp` script plots the total throughput, while the `plot_perThread.gp` plots the throughput per thread.
Each script contains the code for the five flavors of lists you will use, but for now only code for the coarse-grain-lock is uncommented.
You will later uncomment the other lines for newly available list flavors.

The recommendation for using this plot script is as follows:
- create a new folder in `plots/` for each experimental setting (e.g. using as name the parameter string used for launching the experiment as the folder name, replacing spaces with underscores, or using a short configuration name);
- move to this folder;
- copy/paste the complete output of the benchmark in a file called `output.dat`;
- create a symbolic link to the gnuplot scripts available in the parent folder, by using `ln -s ../plot_total.gp .` and `ln -s ../plot_perThread.gp .`;
- call the script by using `gnuplot plot_total.gp; gnuplot plot_perThread.gp`, which will produce two pdfs in this specific folder.

This manipulation allows to plot several runs using different configurations without having to modify the scripts.

You are of course welcome to use your favorite plotting tool if you do not want to use gnuplot.

#### Action

- [ ] Analyze the provided code and plot the performance of the coarse-grain-lock list flavor using the default parameters. 

### Concurrent list flavors

You task in this practical is to implement the four flavors of concurrent list-based sets seen in class under the following files/class names.
Please use the recommended class names: it will make your life easier when using the plotting script.

- `FineGrainLockedList.java`: an implementation of the fine-grain locking strategy, using hand-over-hand locking;
- `OptimisticLockedList.java`: an optimistic version of the fine-grain locking strategy, where not all nodes in the lists are locked when performing the initial traversal;
- `LazyList.java`: the lazy version, where nodes can be marked as virtually deleted in the list before being physically deleted;
- `LockFreeList.java`: the lock-free version.

#### Question

- [ ] Implement the four list-based sets and test them extensively using unit tests. *(link to your code)*

### Test configurations

Once you are convinced of the correctness of your implementations, use Burattini to benchmark the lists using the following configurations.

- On Burattini, use 1 to 24 threads (recommended is: 1, 2, 4, 8, 12, 16, 20, and 24 threads).

All configurations share the following characteristic:

- 3 samples are taken for each (thread count, list flavor) pair;
- each sample is taken over a five-second period.

Note that it is recommended to use smaller scale benchmarks (only a few thread counts, less number of samples, etc.) before running the final, long benchmark.

#### Configuration 1: mixed workload

This is the default configuration (see above).

#### Configuration 2: lookup-dominated workload

In this configuration we want a majority of lookup operations, with a small percentage (5% each) of add and remove:

- Values are chosen in the range [0:10000];
- The list is populated initially by performing 5000 ADD operations;
- 5% of operations are ADD;
- 5% of operations are REMOVE;
- 90% of operations are LOOKUP.

#### Configuration 3: insert-only workload

In this configuration we only perform insertions, and we minimize the risk that inserted values already exist in the set by choosing value from a large range.

- Values are chosen in the range [0:1000000];
- The list is initially empty;
- 100% of operations are ADD.

#### Other configurations

You are welcome, but not required, to test with other configurations of your choice.

#### Question

- [ ] For each configuration, produce a plot that shows the throughput in operations per second (average and standard deviation). Explain the performance and scalability of the different set flavors. *(link to your four plots in PDF, and explanations text for the plots in the [Report.md](Report.md) file)*

### Deliverable

The deliverable will cover the code, the four plots, and the analysis of the four plots.
The deadline is **April 2, 22:00**.
Do not forget to fill in the Moodle assignment with a link to your repository and the identifier of the commit.
As usual we will use the last commit before this date as the final version, unless explicitly told otherwise.
