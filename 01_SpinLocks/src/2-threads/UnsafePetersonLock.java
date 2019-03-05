public class UnsafePetersonLock implements TwoThreadsLock {
	private boolean[] flag = new boolean[2];
	private int victim ;
		
	public void lock (int i) {
		// j is the identity of the other thread (by default we have thread 0 and 1)
		int j = 1 - i;
		
		flag[i] = true;
		victim = i;
		while (flag[j] && victim == i) {} // busy wait
	}
	
	public void unlock(int i) {
		flag[i] = false;
	}

}
