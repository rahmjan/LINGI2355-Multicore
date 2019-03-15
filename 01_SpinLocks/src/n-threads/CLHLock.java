import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CLHLock implements NThreadsLock {

    AtomicReference<QNode> tail = new AtomicReference<>(new QNode(false));
    ThreadLocal<QNode> myNode = new ThreadLocal<QNode>();

    public CLHLock (int nb_threads) {

    }

    public void lock(int i) {
        QNode node = new QNode(true);
        QNode pred = tail.getAndSet(node);
        while (pred.locked.get()) {}
        myNode.set(node);
    }

    public void unlock(int i) {
        QNode node = myNode.get();
        node.locked.set(false);
//        myNode.set(myPred.get());
    }

    private class QNode {
        AtomicBoolean locked;
        public QNode (boolean initialValue) {
            locked = new AtomicBoolean(initialValue);
        }
    }
}
