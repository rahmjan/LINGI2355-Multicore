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
##### Locks
##### Environment
##### Testing