package lists_benchmark;

import java.util.concurrent.locks.ReentrantLock;

public class OptimisticLockedList implements Set {

    private final ReentrantLock lock;

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

    public OptimisticLockedList() {
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

    private boolean validate(Node pred, Node curr) {
        Node node = this.head;
        while (node.value <= pred.value) {
            if (node == pred)
                return pred.next == curr;
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean add(int value) {
        while (true) {
            Node pred = this.head;
            Node next = pred.next;

            while (next.value <= value) {
                if (next.value == value){
                    return false;
                }
                pred = next;
                next = next.next;
            }

            try {
                pred.lock();
                next.lock();

                if (validate(pred, next)) {
                    Node n = new Node(value);
                    n.next = pred.next;
                    pred.next = n;
                    return true;
                }
            } finally {
                pred.unlock();
                next.unlock();
            }
        }
    }

    @Override
    public boolean remove(int value) {
        while (true) {
            Node pred = this.head;
            Node curr = pred.next;

            while (curr.value <= value) {
                if (curr.value == value){
                    break;
                }
                pred = curr;
                curr = curr.next;
            }

            try {
                pred.lock();
                curr.lock();

                if (validate(pred, curr)) {
                    if (curr.value == value) {
                        pred.next = curr.next;
                        return true;
                    } else
                        return false;
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
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
