##### CreatingThreads

- [ ] Run the code above. What do you observe? Is this the *expected* behaviour?

    Every thread counts down but we can not distinguish between them.
    Also the "main" can finish faster then the other threads.  

- [ ] Fix the problem identified by using the appropriate method from the `Thread` class.

    We need to "join" threads back to the "main" [(code available here)](src/queues/FinalCountdowns.java).

    ```java
        for (int i=0; i<8; i++) {
            t[i].join();
        }
        System.out.println("IGNITION!!!");
    ```

- [ ] Modify the code so that each thread counts down exactly 5 seconds and not as fast as it can.

    ```java
        System.out.print(c + "... ");
        Thread.sleep(1000);
    ```

- [ ] You will see that some of the methods you used may throw an [`InterruptedException`](https://docs.oracle.com/javase/7/docs/api/java/lang/InterruptedException.html). What is the purpose of this exception?

    Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity. Occasionally a method may wish to test whether the current thread has been interrupted, and if so, to immediately throw this exception.

- [ ] We cannot distinguish between the outputs of each thread, in particular for the line showing `ready`. Modify the program so that each thread gets an identity (thread number) and uses it for its output.

    We can do it this way:
    ```java
        long thID = Thread.currentThread().getId();
    ```

##### Monitors

- [ ] Execute the code several times (hint: you can add more producers than consumers if necessary). Is the behavior as expected?

    No, there is deadlock. Also the threads using active waiting (while cycle).

- [ ] Remove the initial waiting time before threads start consuming and producing elements. What is the behavior and how can we explain it?

    There is also deadlock or something is still left in the queue. It depend on the access to the counter. For example two threads can access it in the same time but the increment will be only by one insted of two.

- [ ] Use a large number of produced and consumed elements and remove all waiting times. What happens?

    Again deadlock with active waiting.

- [ ] What is the consistency model offered by the SharedCounterMonitor class? Let us now imagine that we remove the `synchronized` keyword from the `get()` method. Does this change the consistency model and how? (you may need to wait for the third lecture to answer this question)

    It should be Mutual Exclusion -> Only one thread can access MonitorSharedCounter object.
    If we removed `synchronized` from `get()` method then threads can read the counter in parallel and the execution of program should be faster because we do not waste time on synchronization for this operation. The function of program will not change. 

- [ ] What happens if we use a `notifyAll()` call instead of `notify()` in this example? Is our code still matching the specification? If not, what would be required?

    All the sleeping threads will wake up and make the decrement. So we can go to negative numbers in the counter.
    We can repair it with `while`:
    
    ```java
        if (counter == 0){} -> change it to:  while (counter == 0){}
    ```  

- [ ] Fix the implementation of the shared queue to form a linearizable, safe concurrent object of class `SyncQueue`. Your implementation will be blocking: when there is no element to consume, a consumer thread will be blocked for an unbounded amount of time waiting for a producer thread to produce something, and similarly for producers waiting for a free empty slot.
- [ ] Make a copy of the `SyncQueue` class called `FifoSyncQueue` that implements the FIFO-access fairness guarantee (also called bounded waiting in the lecture). Your implementation will use thread-local variables. Justify the correctness of your construction by providing a proof sketch.


##### Locks
##### Environment
##### Testing

-- javac *.java ../../../Solution/src/queues/*.java -d ../../../Solution/bin/
