- [ ] Implement the four hash-based sets and test them extensively using unit tests.

    [CoarseGrainLockedHashSet](./src/hashsets_benchmark/CoarseGrainLockedHashSet.java)  
    [FineGrainLockedHashSet](./src/hashsets_benchmark/FineGrainLockedHashSet.java) - Same implementation like in slides of the presentation.  
    [FineGrainLockedHashSetWithLocks](./src/hashsets_benchmark/FineGrainLockedHashSetWithLocks.java) - Implementation using  Read/Write lock.  
    [LockFreeHashSet](./src/hashsets_benchmark/LockFreeHashSet.java)  

    [Unit tests](./src/hashsets_benchmark/UnitTests.java)  

- [ ] For each configuration, produce a plot that shows the throughput in operations per second (average and standard deviation). Explain the performance and scalability of the different set flavors, and the potential impact of resizings.

##### First configuration
[Graph of 1-configuration](./plots/benchmark-1/performance_hashSets.pdf)   
The results are as expected. `Lock-free` and `Java Conc. HM` with more threads are better and better. And the rest of HashSets are stagnating because of the limited number of locks.  
Little surprise is `Fine lock Read/Write` which is even worse than `Coarse lock`. And the only reason I can think is the implementation of "Read/Write Lock" that is slowing the Set.

##### Second configuration
[Graph of 2-configuration](./plots/benchmark-2/performance_hashSets.pdf)  
The results are the same as in the previous configuration.

##### Third configuration
[Graph of 3-configuration](./plots/benchmark-3/performance_hashSets.pdf)  
With only the ADD operation, the result are again the same.

##### Fourth configuration
[Graph of 4-configuration](./plots/benchmark-4/performance_hashSets.pdf)  
Here we can see the influence of resizing if we compare with the graph from previous configuration.
Except `Coarse lock` and `Lock-free`, each of the Sets behave much more better because we do not need to wait for the resize.
Another reason why `Coarse lock` is left behind is because of bigger initial number of lock.  
For `Lock-free` there is no change but that is expected result. The resizing should have very minimal influence on this Set.