package lists_benchmark;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList implements Set {

    private class Window {
        Node pred;
        Node curr;
        Window(Node pred, Node curr) {
            this.pred = pred;
            this.curr = curr;
        }
    }

    private class Node {
        public boolean marked;
        public int value;
        public AtomicMarkableReference<Node> next;
        public Node(int value) {
            this.value = value;
            marked = false;
            this.next = new AtomicMarkableReference<Node>(null, false);
        }

        private final ReentrantLock lock = new ReentrantLock();
        public void lock(){ lock.lock(); }
        public void unlock(){ lock.unlock(); }
    }

    private Node head, tail;

    public LockFreeList() {
        // create head and tail nodes
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);

        // link tail as successor of head node
        while (!head.next.compareAndSet(null, tail, false, false));
    }

    // this call traverses the list and returns the node that is the predecessor of the given key
    private Node get_pred(int value) {
        Node ret = head;

        while (ret.next.getReference().value < value) {
            ret = ret.next.getReference() ;
        }

        return ret ;
    }

    private boolean validate(Node pred, Node curr) {
        return !pred.marked && !curr.marked && pred.next.getReference() == curr;
    }

    private Window find(Node head, int value){
        Node pred, curr, succ;
        boolean[] marked = { false }; boolean b;

        retry: while (true) {
            pred = head;
            curr = pred.next.getReference();

            while (true) {
                succ = curr.next.get(marked);

                while (marked[0]) {
                    b = pred.next.compareAndSet(curr, succ, false, false);
                    if (!b) continue retry;
                    curr = succ;
                    succ = curr.next.get(marked);
                }

				// ER: why not simply use >= ?
                if ((curr.value == value && curr.value == value)
                        || curr.value > value)
                    return new Window(pred, curr);

                pred = curr; curr = succ;
            }
        }
    }

    @Override
    public boolean add(int value) {
        while (true) {
            Window window = find(head, value);
            Node pred = window.pred, curr = window.curr;

            if (curr.value == value)
                return false;

            Node n = new Node(value);
            n.next = new AtomicMarkableReference<Node>(curr, false);

            if (pred.next.compareAndSet(curr, n, false, false))
                return true;
        }
    }

    @Override
    public boolean remove(int value) {
        boolean b;
        while (true) {
            Window window = find(head, value);
            Node pred = window.pred, curr = window.curr;

            if (curr.value != value)
                return false;

            Node succ = curr.next.getReference();
            b = curr.next.compareAndSet(succ, succ, false, true);
            if (!b)
                continue;

            pred.next.compareAndSet(curr, succ, false, false);
            return true;
        }
    }

    @Override
    public boolean contains(int value) {
        boolean marked[] = new boolean[1];
        Node curr = this.head;

        while (curr.value <= value) {
            if (value == curr.value)
                break;
            curr = curr.next.getReference();
        }

        curr.next.get(marked);
        return (value == curr.value && !marked[0]);
    }

    // Note this method is not thread safe and should not be called concurrently
    @Override
    public int size() {
        int size = 0;

        Node curr = head;

        while (curr.next.getReference() != tail) {
            size ++;
            curr = curr.next.getReference();
        }

        return size;
    }

}
