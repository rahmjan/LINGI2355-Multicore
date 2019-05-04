- [ ] Implement the four hash-based sets and test them extensively using unit tests. *(link to your code)*

[CoarseGrainLockedHashSet](./src/hashsets_benchmark/CoarseGrainLockedHashSet.java)  
[FineGrainLockedHashSet](./src/hashsets_benchmark/FineGrainLockedHashSet.java)  
[FineGrainLockedHashSetWithLocks](./src/hashsets_benchmark/FineGrainLockedHashSetWithLocks.java)  
[LockFreeHashSet](./src/hashsets_benchmark/LockFreeHashSet.java)  - 262144  

[Unit tests](./src/hashsets_benchmark/UnitTests.java)

- [ ] For each configuration, produce a plot that shows the throughput in operations per second (average and standard deviation). Explain the performance and scalability of the different set flavors, and the potential impact of resizings. *(link to your four plots in PDF, and explanations of the plots in the [Report.md](Report.md) file)*


Below you can see the graphs of throughput in operations per second:  
[Graph of 1-benchmark](./plots/benchmark-1/performance_hashSets.pdf)    
[Graph of 2-benchmark](./plots/benchmark-2/performance_hashSets.pdf)  
[Graph of 3-benchmark](./plots/benchmark-3/performance_hashSets.pdf)  
[Graph of 4-benchmark](./plots/benchmark-4/performance_hashSets.pdf)  
