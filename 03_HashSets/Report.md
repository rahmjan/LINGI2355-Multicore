- [ ] Implement the four hash-based sets and test them extensively using unit tests.

    [CoarseGrainLockedHashSet](./src/hashsets_benchmark/CoarseGrainLockedHashSet.java) 

ER: OK
 
[FineGrainLockedHashSet](./src/hashsets_benchmark/FineGrainLockedHashSet.java): Same implementation like in slides of the presentation.  

ER: OK

[FineGrainLockedHashSetWithLocks](./src/hashsets_benchmark/FineGrainLockedHashSetWithLocks.java) - Implementation using  Read/Write lock.  

ER: Not required, implementation OK.

[LockFreeHashSet](./src/hashsets_benchmark/LockFreeHashSet.java)  

ER: OK

[Unit tests](./src/hashsets_benchmark/UnitTests.java)  

ER: OK (note that this does not test concurrent accesses however)

- [ ] For each configuration, produce a plot that shows the throughput in operations per second (average and standard deviation). Explain the performance and scalability of the different set flavors, and the potential impact of resizings.

##### First configuration
[Graph of 1-configuration](./plots/benchmark-1/performance_hashSets.pdf)   
The results are as expected. `Lock-free` and `Java Conc. HM` with more threads are better and better. And the rest of HashSets are stagnating because of the limited number of locks.  
Little surprise is `Fine lock Read/Write` which is even worse than `Coarse lock`. And the only reason I can think is the implementation of "Read/Write Lock" that is slowing the Set.

ER: results as expected. It would have been interesting to dig a bit more and understand why the RW lock is less performant. Is this a fairness issue for writes? One way to know this would have been to monitor the time it takes to get the lock from the moment we enter the add method and the moment we get the write lock.

##### Second configuration
[Graph of 2-configuration](./plots/benchmark-2/performance_hashSets.pdf)  
The results are the same as in the previous configuration. Throughput is better but that is result of bigger percentage of contains operation.

ER: OK

##### Third configuration
[Graph of 3-configuration](./plots/benchmark-3/performance_hashSets.pdf)  
Configuration with only the ADD operation, the results are important for comparision with next configuration.

ER: OK, but do you have an explanation why the Java-provided data structure is so much better?

##### Fourth configuration
[Graph of 4-configuration](./plots/benchmark-4/performance_hashSets.pdf)  
Here we can see the influence of resizing if we compare with the graph from previous configuration.
Except `Coarse lock`, each of the Sets behave much more better because we do not need to wait for the resize.
Another reason why `Coarse lock` is left behind is because of initial bigger number of locks.  
For `Lock-free` there is minimal change but that is expected result. The resizing should have very minimal influence on this Set.

ER: OK

ER: final grade is 19/20, congratulations.
