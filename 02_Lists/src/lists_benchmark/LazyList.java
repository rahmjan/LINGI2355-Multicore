package lists_benchmark;

import java.util.concurrent.locks.ReentrantLock;

public class LazyList implements Set {

    private class Node {
        public boolean marked;
        public int value;
        public Node next;
        public Node(int value) {
            this.value = value;
            marked = false;
        }

        private final ReentrantLock lock = new ReentrantLock();
        public void lock(){ lock.lock(); }
        public void unlock(){ lock.unlock(); }
    }

    private Node head, tail;

    public LazyList() {
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
        return !pred.marked && !curr.marked && pred.next == curr;
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
                        curr.marked = true;
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
        Node curr = this.head;

        while (curr.value <= value) {
            if (value == curr.value)
                break;
            curr = curr.next;
        }

        return value == curr.value && !curr.marked;
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
