- [ ] Implement the four list-based sets and test them extensively using unit tests. *(link to your code)*

     [FineGrainLockedList](./src/lists_benchmark/FineGrainLockedList.java)  
	 
ER: Your implementation of the fine grain locked list is incorrect. It does not implement the hand-over-hand locking and traverses the list without taking locks. It takes the locks as for the optimistic version, but it does not retraverse to check that the nodes are still available. You can therefore add an element between two removed nodes! (see lecture for details)
	 
     [OptimisticLockedList](./src/lists_benchmark/OptimisticLockedList.java)  
	 

ER: in general correct but you have an error (see comment in your code) where you reply that an element is already present without taking the locks and validating. This can break linearizablity, and you can respond incorrectly since you do not validate (e.g. if the node you presume already exist is actually linked from removed nodes itself).

     [LazyList](./src/lists_benchmark/LazyList.java)  
	 
ER: ok but same problem in the add as previously.

     [LockFreeList](./src/lists_benchmark/LockFreeList.java)

ER: there is dead code in your implementation (the marked field for the Node class, the validate call, etc.); You should avoid this. I also added a comment to the code. Otherwise OK.

ER: where are the unit tests?

- [ ] For each configuration, produce a plot that shows the throughput in operations per second (average and standard deviation). Explain the performance and scalability of the different set flavors. *(link to your four plots in PDF, and explanations text for the plots in the [Report.md](Report.md) file)*
 
##### First configuration
[Performance_lists_per_thread](./plots/benchmark-1/performance_lists_per_thread.pdf)  
[Performance_lists_total](./plots/benchmark-1/performance_lists_total.pdf)  

From the graphs I think you can see expected results for the different flavours. 
`Lazy` and `Optimistic` also show little deviations for different threads, that should be contributed to `initial traversal`.  
`Lock-free` should be the best from all of them but maybe `AtomicMarkableReference` is slowing speed of the list. Here it could be interesting to try to run with more operations per thread. This also hold true for other configurations.

ER: The performance list total is plotting the wrong column of the file, you could have pinpointed this. I am very surprised by the performance of your buggy fine grain list implementation. It should produce inconsistent behavior but go faster than the optimistic version. Did you use another version? The rest of the behaviors is indeed as expected.

##### Second configuration
[Performance_lists_per_thread](./plots/benchmark-2/performance_lists_per_thread.pdf)  
[Performance_lists_total](./plots/benchmark-2/performance_lists_total.pdf)  

Again there is expected results. With more threads we get better throughput.  
One big difference we can see is `Lazy` for 8 threads. What I can only think is that threads did not need to wait for others. More specifically, the 8 threads were not meeting in the list as much as 12 and more threads.  
The same `meeting` problem can be also seen in other flavors.

ER: same remark for the total tput. For the tput per thread, what you have plotted is actually the total one (you could have guessed that increasing the throughput per thread when adding more threads and therefore more contention was not making a lot of sense). I am again very surprised by the results you have for the fine grain locked list and I am unsure you used your own version (or at least the one I see on this commit). I am not sure to understand the discussion about the meeting problem.

##### Third configuration
[Performance_lists_per_thread](./plots/benchmark-3/performance_lists_per_thread.pdf)  
[Performance_lists_total](./plots/benchmark-3/performance_lists_total.pdf)  

The rest of results is what I more or less expected. Except `Fine-grain` which is better than I tough.

ER: here the result of your fine grain bugged implementation is consistent with the expectation.

ER: final grade is 11/20
