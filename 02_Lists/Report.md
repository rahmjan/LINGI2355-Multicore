- [ ] Implement the four list-based sets and test them extensively using unit tests. *(link to your code)*

     [FineGrainLockedList](./src/lists_benchmark/FineGrainLockedList.java)  
     [OptimisticLockedList](./src/lists_benchmark/OptimisticLockedList.java)  
     [LazyList](./src/lists_benchmark/LazyList.java)  
     [LockFreeList](./src/lists_benchmark/LockFreeList.java)

- [ ] For each configuration, produce a plot that shows the throughput in operations per second (average and standard deviation). Explain the performance and scalability of the different set flavors. *(link to your four plots in PDF, and explanations text for the plots in the [Report.md](Report.md) file)*
 
    ##### First configuration
    [Performance_lists_per_thread](./plots/benchmark-1/performance_lists_per_thread.pdf)  
    [Performance_lists_total](./plots/benchmark-1/performance_lists_total.pdf)  
    
    From the graphs I think you can see expected results for the different flavours. 
    `Lazy` and `Optimistic` also show little deviations for different threads, that should be contributed to `initial traversal`.  
    `Lock-free` should be the best from all of them but maybe `AtomicMarkableReference` is slowing speed of the list. Here it could be interesting to try to run with more operations per thread. This also hold true for other configurations.
    
    ##### Second configuration
    [Performance_lists_per_thread](./plots/benchmark-2/performance_lists_per_thread.pdf)  
    [Performance_lists_total](./plots/benchmark-2/performance_lists_total.pdf)  
    
    Again there is expected results. With more threads we get better throughput.  
    One big difference we can see is `Lazy` for 8 threads. What I can only think is that threads did not need to wait for others. More specifically, the 8 threads were not meeting in the list as much as 12 and more threads.  
    The same `meeting` problem can be also seen in other flavors.
    
    ##### Third configuration
    [Performance_lists_per_thread](./plots/benchmark-3/performance_lists_per_thread.pdf)  
    [Performance_lists_total](./plots/benchmark-3/performance_lists_total.pdf)  
  
    The rest of results is what I more or less expected. Except `Fine-grain` which is better than I tough.