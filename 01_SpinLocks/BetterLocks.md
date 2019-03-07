## Architecture-aware locks

In this final section we will implement the two advanced locks seen in class.
These locks are bus-friendly as they do not require all threads spinning on the same shared value.
They avoid a burst of RMW operations following an invalidation, and implement a FIFO-entry / bounded waiting fairness guarantee.

The result of this section should be three new lock implementations:
- `AndersonLock.java`: a lock based on Anderson's principle of an array of boolean flags used as a queue;
- `AndersonPaddedLock.java`: a version of the former that addresses a false sharing issue;
- `CLHLock`: a lock based on Craig, Landin and Hagersten's principle of single-use node objects to implement a queue of threads acquiring the lock in turn.

As in the previous section, all the work in this section happens [in the n-threads source folder](src/n-threads/).

### Anderson's queue lock

The principles and pseudocode of the Anderson's queue lock are provided in the lecture's slides.

Your implementation will need to make use of a `ThreadLocal` field, in order for a thread to "remember" what was its slot between the `lock()` and `unlock()` methods.
If you do not remember how to use `ThreadLocal` fields, refer to the previous practical for a detailed explanation and an example.

Another important aspect is that the use of a shared array between all threads will lead to false sharing if we do not use proper padding.
As demonstrated in class, padding allows to dispatch independently-accessed variables in different cache lines, as having them in the same cache line would cause invalidations of otherwise-unshared data.
Java does not offer as much control over data placement as low-level languages such as C.
But a simple trick to make sure that each flag in the array is on a different cache line than the other flag is to add a bunch of unused variables between each boolean, as in the following example:

```Java
public class PaddedAndersonLock implements NThreadsLock {

	private class PaddedAtomicBoolean {
		final int PADDING = 7;
		public AtomicBoolean at;
		private AtomicBoolean[] unused;  
		public PaddedAtomicBoolean (boolean initialValue) {
			at = new AtomicBoolean(initialValue);
			unused = new AtomicBoolean[PADDING];
			for (int i=0;i<PADDING;i++) {
				unused[i]=new AtomicBoolean(false);
			}
		}
		public boolean get() {
			return at.get();
		}
		public void set(boolean newValue) {
			at.set(newValue);
		}
	}
	
	PaddedAtomicBoolean[] flags;
```

The cache line size on the Pascaline and Burattini architectures is 64 Bytes.
Java object are generally aligned to an 8-byte granularity in memory, and since AtomicBoolean uses less than that (the state is actually encoded as an `int` field), its size can be estimated to 8 Bytes.
8 atomic booleans will therefore fill an entire cache line and avoid false sharing.

#### Question

- [ ] Implement the Anderson queue lock without padding. Integrate this lock to the test case built in the previous section. *(link to your code)*
- [ ] Implement the Anderson queue lock with padding (keep both versions). Integrate this lock to the test case built in the previous section. *(link to your code)*

### Craig, Landin and Hagersten (CLH) lock

The principles and pseudocode of the CLH lock are provided in the lecture slides.

It can be useful for implementing the CLH lock to declare an inner private class as part of the CLHLock class, instead of a public class that is not used elsewhere:

```Java
public class CLHLock implements NThreadsLock {
	
	private class QNode {
		AtomicBoolean locked;
		public QNode (boolean initialValue) {
			locked = new AtomicBoolean(initialValue);
		}
	}
	
	AtomicReference<QNode> tail = new AtomicReference<>(new QNode(false));
	
	(...)
	
}
```

Because we limit the scope of use of the `QNode` class to the `CLHLock` class, it is acceptable to access the `locked` field of a `QNode` as a public field, something that would be considered bad practice with a public class.

#### Question

- [ ] Implement the CLH lock. Integrate this lock to the test case built in the previous section. *(link to your code)*

We are now ready to proceed to the final step of this practical, the [benchmarking of the different locks](Evaluation.md).
