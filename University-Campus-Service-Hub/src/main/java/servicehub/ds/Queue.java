package servicehub.ds;

/**
 * A FIFO (first-in, first-out) queue implementation
 * on top of the project's own {@link SinglyLinkedList}.
 *
 * @param <T> the type of elements held in this queue
 */
public class Queue<T> {

    private final SinglyLinkedList<T> items = new SinglyLinkedList<>();

    public void enqueue(T item) {
        items.addLast(item);
    }

    public T dequeue() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot dequeue from an empty queue");
        }
        return items.removeFirst();
    }

    public T peek() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot peek at an empty queue");
        }
        return items.get(0);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
