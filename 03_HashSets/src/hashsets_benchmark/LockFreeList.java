package hashsets_benchmark;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList {

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
        public boolean sentiel;
        public int value;
        public int key;
        public AtomicMarkableReference<Node> next;
        public Node(int value, int key) {
            this.value = value;
            this.key = key;
            marked = false;
            sentiel = false;
            this.next = new AtomicMarkableReference<Node>(null, false);
        }
    }

    private Node head, tail;

    public LockFreeList() {
        // create head and tail nodes
        head = new Node(0,0);
        tail = new Node(0, Integer.MAX_VALUE);

        // link tail as successor of head node
        while (!head.next.compareAndSet(null, tail, false, false));
    }

    public LockFreeList(LockFreeList parent, int value, int key) {
        // Insert sentiel node
        Node node = new Node(value, key);
        node.sentiel = true;
        if (!parent.addSentielNode(node)){
            System.out.println("ERROR: Sentiel node was not added: " + node.value + "  " + node.key);
        }

        // Set Head
        this.head = node;

        // Set Tail
        this.tail = parent.tail; // is it not used?
    }

    private boolean validate(Node pred, Node curr) {
        return !pred.marked && !curr.marked && pred.next.getReference() == curr;
    }

    private Window find(Node head, int key){
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

                if (curr.key >= key)
                    return new Window(pred, curr);

                pred = curr; curr = succ;
            }
        }
    }

    public boolean add(int value, int key) {
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;

            if (curr.key == key)
                return false;

            Node n = new Node(value, key);
            n.next = new AtomicMarkableReference<Node>(curr, false);

            if (pred.next.compareAndSet(curr, n, false, false))
                return true;
        }
    }

    public boolean remove(int key) {
        boolean b;
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;

            if (curr.key != key)
                return false;

            Node succ = curr.next.getReference();
            b = curr.next.compareAndSet(succ, succ, false, true);
            if (!b)
                continue;

            pred.next.compareAndSet(curr, succ, false, false);
            return true;
        }
    }

    public boolean contains(int key) {
        boolean marked[] = new boolean[1];
        Node curr = this.head;

        while (curr.key <= key) {
            if (key == curr.key)
                break;
            if (curr.sentiel){
                System.out.println("We passed sentiel....!!!");
            }
            curr = curr.next.getReference();
        }

        curr.next.get(marked);
        return (key == curr.key && !marked[0]);
    }

    // Note this method is not thread safe and should not be called concurrently
    public int size() {
        int size = 0;

        Node curr = head;

        while (curr.next.getReference() != tail) {
            size ++;
            curr = curr.next.getReference();
        }

        return size;
    }

    private boolean addSentielNode(Node n) {
        while (true) {
            Window window = find(head, n.key);
            Node pred = window.pred, curr = window.curr;

            if (curr.key == n.key)
                return false;

            n.next = new AtomicMarkableReference<Node>(curr, false);

            if (pred.next.compareAndSet(curr, n, false, false))
                return true;
        }
    }
}
