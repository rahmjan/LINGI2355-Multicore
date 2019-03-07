## Building a lock with an atomic RMW operation

In this section we will build a simple lock using the *Test and Set* RMW operation, where threads compete for setting a flag and entering their critical section.
We will also build a version of this lock that first tests whether the lock *might be* available by reading the value of the flag before attempting to set it using the *Test and Set* operation.
Finally we will implement a version of this latter lock that uses exponential backoff.

The result of this section should be three new lock implementations:
- `TASLock.java`: the simplest lock using the *Test and Set* operation;
- `TTASLock.java`: implementing the test-and-test-and-set strategy;
- `EBTTASLock.java`: adding exponential backoff to a copy of `TTASLock.java`.

All the work in this section happens [in the n-threads source folder](src/n-threads/).

### Read-Modify-Write operations for Atomic types

In the documentation of the [AtomicBoolean](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/AtomicBoolean.html) class of the Java SDK, you can observe a number of atomic Read-Modify-Write operations.

These operations are automatically mapped to the corresponding atomic instructions of the target platform by the Java interpreter (or the Just-in-Time compiler), if the target Instruction Set supports them.
This is the case for all modern CPUs.

A simple way to implement a lock is having each thread try to overwrite a boolean value (a flag) with `true` using an atomic RMW operation, *Test and Set*.
If the value is already `true`, it means another thread currently owns the lock and is executing its critical section.
If the operation returns `false`, it means the lock was not previously owned and that the value has been atomically set to `true` *together* with the read.
As a result, a single thread can succeed with this operation until the value is set to `false` again.

Note: while the locks we have implemented in the previous section required to know the number of threads beforehand, this is not the case of the lock we build in this section.
You can therefore ignore the parameters to the `lock()` and `unlock()` functions of the `NThreadsLock` interface.

#### Question

- [ ] Implement in `TASLock.java` a lock based on the use of the *Test and Set* RMW operation for an arbitrary number of threads. Integrate this lock to the test case built in the previous section. *(link to your code)*

### Avoiding unnecessary bus traffic

As we have seen in class, when all threads repeatedly issue RMW operations trying to acquire the lock the traffic on the bus explodes and slows down all the threads.
A solution to avoid some of the traffic on the bus is to have each thread first read the value of the flag in order to know if the lock is potentially available: reading `true` means the lock is taken and there is no need to attempt acquiring it with the RMW operation.
Future reads to the flag will happen in the local cache, until this one gets invalidated by the write of value `false` by the thread releasing the lock.

#### Question

- [ ] Implement an evolution of the previous lock (keep both classes!) implementing the test-and-test-and-set strategy. *(link to your code)*

### Exponential backoff

When there is contention for a lock, it makes sometime sense to wait for the lock to become available again rather than spinning and generating ill-fated RMW operations that load the bus and slow down all threads for little progress.
The exponential backoff strategy seen in class implements this behavior.
When a thread notices that the lock is already taken, it goes to sleep for a certain time.
This time is selected randomly in a range, and the max boundary of this range is doubled after every attempt to read the flag that return `true`, up to a maximum obviously.

There are many policies for setting appropriate time ranges, and these depend a lot on the target architecture and CPU model.
In our case, we recommend the following parameters:

- The first sleeping delay is selected in the 0..10 nanoseconds range, after the first failed attempt;
- For every failed attempt after this, the maximal delay (end of the range) is doubled, e.g. the second sleeping delay is randomly selected in the 0..20 nanoseconds range;
- If the range upper boundary gets over 1 millisecond (1000 nanoseconds), it is capped to 1000 nanoseconds.

You should use `ThreadLocalRandom` to generate the random delay.
It generates [less overhead and contention](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadLocalRandom.html) than `Random` in a multithreading context.

The `Thread.sleep()` method allows [sleeping with a precision of nanoseconds](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#sleep-long-int-).
The time that a thread actually waits is *at least* the specified time.
The actual sleeping time can be larger than requested if there is a high contention for the CPU (as you should add the time for the thread to actually get the CPU after the timer expires).
If your code is running solo on Burattini with up to 24 threads the precision should be good enough.

#### Question

- [ ] Implement an evolution of `TTASLock.java` (keep both classes!) implementing the exponential-backoff strategy. *(link to your code)*

The next step is to implement the [advanced locks](BetterLocks.md) that try to avoid the massive contention linked to polling on shared resources.
