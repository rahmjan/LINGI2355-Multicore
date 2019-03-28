package lists_benchmark;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainLockedList implements Set {

    private class Node {
        public int value;
        public Node next;
        public Node(int value) {
            this.value = value;
        }

        private final ReentrantLock lock = new ReentrantLock();
        public void lock(){ lock.lock(); }
        public void unlock(){ lock.unlock(); }
    }

    private Node head, tail;

    public FineGrainLockedList() {
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

        Node pred = null;
        Node next = null;

        try {
            pred = get_pred(value);
            pred.lock();
            next = pred.next;
            next.lock();

            if (pred.next.value == value) {
                ret = false;
            } else {
                Node n = new Node(value);
                n.next = pred.next;
                pred.next = n;
                ret = true;
            }
        } finally {
            pred.unlock();
            next.unlock();
        }

        return ret;
    }

    @Override
    public boolean remove(int value) {
        boolean ret = false ;

        int key = value;
        Node pred = null;
        Node curr = null;

        try {
            pred = this.head;
            pred.lock();
            curr = pred.next;
            curr.lock();

            while (curr.value <= value) {
                if (value == curr.value) {
                    pred.next = curr.next;
                    ret = true;
                }
                pred.unlock();
                pred = curr;
                curr = curr.next;
                curr.lock();
            }
            ret = false;

        } finally {
            curr.unlock();
            pred.unlock();
        }

        return ret;
    }

    @Override
    public boolean contains(int value) {
        boolean ret = false;
        Node pred = null, curr = null;
        // check if the key is here
        try {
            pred = get_pred(value);
            pred.lock();
            curr = pred.next;
            curr.lock();
            ret = (pred.next.value == value);

        } finally {
            curr.unlock();
            pred.unlock();
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
