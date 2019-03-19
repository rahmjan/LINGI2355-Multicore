package lists_benchmark;

import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainLockedList implements Set {

	private final ReentrantLock lock;

	private class Node {
		public int value;
		public Node next;
		public Node(int value) {
			this.value = value;
		}
	}
	
	private Node head, tail;

	public CoarseGrainLockedList() {
		lock = new ReentrantLock();
		// create head and tail nodes
		head = new Node(Integer.MIN_VALUE);
		tail = new Node(Integer.MAX_VALUE);
		// link tail as successor of head node
		head.next = tail;
	}

	// this call traverses the list and returns the node that is the predecessor of the given key
	private Node get_pred(int value) {
		Node ret = head;

		while (ret.next.value < value) {
			ret = ret.next ;
		}

		return ret ;
	}

	@Override
	public boolean add(int value) {
		boolean ret = false ;

		lock.lock();
		try {
			Node pred = get_pred(value);

			// if the value is the one we are looking for then the value is already here, otherwise, we can insert it
			if (pred.next.value == value) {
				ret = false;
			} else {
				Node n = new Node(value);
				n.next = pred.next;
				pred.next = n;
				ret = true;
			}
		} finally {
			lock.unlock();
		}

		return ret;
	}

	@Override
	public boolean remove(int value) {
		boolean ret = false ;

		lock.lock();
		try {
			Node pred = get_pred(value);

			// if the key is the one we are looking for, the object is here and can be deleted
			// otherwise it does not exist in the list and we simply return false
			if (pred.next.value != value) {
				ret = false ;
			} else {
				pred.next = pred.next.next;
				ret = true ;
			}

		} finally {
			lock.unlock();
		}
		return ret;
	}

	@Override
	public boolean contains(int value) {
		boolean ret = false ;

		lock.lock();
		try {
			Node pred = get_pred(value);

			// check if the key is here
			ret = (pred.next.value == value);

		} finally {
			lock.unlock();
		}

		return ret;
	}
	
	// Note this method is not thread safe and should not be called concurrently
	@Override
	public int size() {
		int size = 0;

		Node curr = head;
		
		while (curr.next != tail) {
			size ++;
			curr = curr.next;
		}
	
		return size;
	}

}
