##### ClassicalLocks

- [ ] Write a test case using this lock, where two threads of identifiers 0 and 1, try to acquire the lock multiple (e.g. 10,000) times. Use the oracle to test for safety violations.
    
    [The code is here.](./src/2-threads/Worker.java)

ER: OK

- [ ] Fix the algorithm and explain the modification. *(link to your code and explanation)*

    [The code is here.](./src/2-threads/SafePetersonLock.java)  
    I made the array of `flag` into `AtomicBoolean​`. This way in every thread `flag` will be synchronized. Or we can use `volatile` for `victim`.

ER: You actually need to do both, or you have no guarantee on the visibility of the writes to the victim field. Less importantly, your code accepts creating a lock for more than 2 threads where it will be incorrect.

- [ ] Implement the Filter lock and test its correctness. *(link to your code)*

    [The code is here.](./src/n-threads/FilterLock.java)  
    Tested with this [Worker](./src/2-threads/Worker.java) and `SafetyOracle` shows no problem.
	
ER: OK

- [ ] Implement the Bakery lock and test its correctness. *(link to your code)*

    [The code is here.](./src/n-threads/BakeryLock.java)  
    Tested with this [Worker](./src/2-threads/Worker.java) and `SafetyOracle` shows no problem.
	
ER: OK

##### RMWLocks

- [ ] Implement in `TASLock.java` a lock based on the use of the *Test and Set* RMW operation for an arbitrary number of threads. Integrate this lock to the test case built in the previous section. *(link to your code)*

    [The code is here.](./src/n-threads/TASLock.java)

ER: OK

- [ ] Implement an evolution of the previous lock (keep both classes!) implementing the test-and-test-and-set strategy. *(link to your code)*
    
    [The code is here.](./src/n-threads/TTASLock.java)

ER: OK

- [ ] Implement an evolution of `TTASLock.java` (keep both classes!) implementing the exponential-backoff strategy. *(link to your code)*

    [The code is here.](./src/n-threads/EBTTASLock.java)

ER: OK

##### BetterLocks

- [ ] Implement the Anderson queue lock without padding. Integrate this lock to the test case built in the previous section. *(link to your code)*
    
    [The code is here.](./src/n-threads/AndersonLock.java)

ER: OK

- [ ] Implement the Anderson queue lock with padding (keep both versions). Integrate this lock to the test case built in the previous section. *(link to your code)*

    [The code is here.](./src/n-threads/AndersonPaddedLock.java)

ER: OK

- [ ] Implement the CLH lock. Integrate this lock to the test case built in the previous section. *(link to your code)*
    
    [The code is here.](./src/n-threads/CLHLock.java)

ER: OK

##### Evaluation

- [ ] Implement and run the benchmark on Burattini. Write a short (20-30 lines) report of your findings: Are the results and performance of the different locks as expected? Are there cases where the performance is not as good as you would expect? Provide a link to your final benchmark implementation. *(link to your code and explanation)*

[Code of the worker.](./src/n-threads/Worker.java)  
[Script of benchmark.](./src/n-threads/benchmark.sh)  
I performed the test under the conditions described [here](./Evaluation.md) and run the benchmark 3-times to ensure I will avoid "JIT".

	ER: you do not avoid JIT, you only avoid its performance uncertainty effects

Here you can see the graph of locks, 3rd-result from the test:
![alt text](./src/n-threads/report_data/graph.png)

Here is the same graph with logarithm scale of time to better see the difference:
![alt text](./src/n-threads/report_data/graph2.png)

The result and performance for the most lock is as expected.  
The EBTTASLock is surprise because in my test is the fastest one.  

ER: it is a bit surprising indeed, but it is supposed to perform well. What is suprising is that CLH is not so good. Could it be that the measurement was done when someone else used the machine?

BakeryLock shows little deflection for 8 threads but it should be only momentarily bud luck because in data for 1st and 2nd results from test it is OK.  
Next is TASLock which is faster for bigger number threads than TTASLock. 

From this test I think it is needed to measure locks for little longer time to get better results. They need to do more "work" operations.

ER: It is a possibility indeed, did you try?

ER: plots are complete and explanations are OK for data available.

ER: final grade is 18/20


