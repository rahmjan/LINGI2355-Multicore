## Measuring the performance of our Queues implementations

When reaching this point you should have the following FIFO Queue implementations ready for testing:

- `SyncQueue`: monitor-based, non FIFO-entry;
- `FifoSyncQueue`: monitor-based, FIFO-entry;
- `LockQueue`: Reentrant-lock based with multiple condition variables, non FIFO-entry;
- `FifoLockQueue`: Reentrant-lock based with multiple condition variables, FIFO-entry.

The suggested scenario for testing the performance of all queues is as follows:

- The number of producer threads vary from 1 to 24, using increments 1, 2, 4, 6, 8, 10, 12, 16, 20, 24;
- The number of consumer threads is the same as the number of producer threads;
- The total number of produced/consumed elements is 96000, split over the number of threads;
- The queue size is 4 elements.

Feel free to use other values and explore larger number of threads, different queue sizes etc.
These should be command line parameters to some `Benchmark` class, and not hard-coded in your code.

The behavior of the producer and consumer threads will emulate real work done to produce and consume elements:

- Each thread starts with a random waiting time *t*;
- Between each calls to `enqueue()` or `dequeue()`, each thread waits a random time *t*.

Do not forget to use `ThreadLocalRandom.current().nextDouble()` and not `Math.random()` in order to avoid unnecessary overhead and contention.

You goal is to try with different ranges for *t* (e.g. random from 5ms to 10ms) and evaluate the total time taken for useful work vs. the time taken for synchronization.
For this, you will need to log the *actual* waiting times taken for the emulated work by each thread and the total time.
The difference is the time spent on synchronization.

:warning: As detailed [previously](Environment.md) you should collect statistics for each thread in memory and outputs it at the end of the program execution.

- [ ] Provide in your report Markdown file, a short report of your findings about which is the better algorithm and why, and in which conditions the use of the four kinds of synchronization makes sense. You should present your results as a 2D plot, generated using the tool of your choice (e.g., gnuplot), with the necessary text explanations. Knowing how to present results in a plot and explain them based on algorithmic arguments will be key for the rest of the practicals.
