package servicehub.ds;

/**
 * A LIFO (last-in, first-out) stack implemention
 * on top of the project's own {@link SinglyLinkedList}.
 *
 * @param <T> the type of elements held in this stack
 */
public class Stack<T> {

    private final SinglyLinkedList<T> items = new SinglyLinkedList<>();

    public void push(T item) {
        items.addFirst(item);
    }

    public T pop() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot pop from an empty stack");
        }
        return items.removeFirst();
    }

    public T peek() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot peek at an empty stack");
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
