import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockWrapper implements NThreadsLock {

	private ReentrantLock lock;
	
	public ReentrantLockWrapper(int nb_threads) {
		lock = new ReentrantLock();
	}
	
	public void lock(int i) {
		lock.lock();
	}

	public void unlock(int i) {
		lock.unlock();
	}

}
