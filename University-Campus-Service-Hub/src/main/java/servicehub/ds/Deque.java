package servicehub.ds;

/**
 * A double-ended queue (deque) implementation
 * using a doubly linked list. Elements can be added and removed from both ends
 * in constant time.
 *
 * @param <T> the type of elements held in this deque
 */
public class Deque<T> {

    private static final class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void addFirst(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    public void addLast(T item) {
        Node<T> node = new Node<>(item);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public T removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot remove from an empty deque");
        }
        T removed = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }
        size--;
        return removed;
    }

    public T removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot remove from an empty deque");
        }
        T removed = tail.data;
        tail = tail.prev;
        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }
        size--;
        return removed;
    }

    public T peekFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot peek at an empty deque");
        }
        return head.data;
    }

    public T peekLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot peek at an empty deque");
        }
        return tail.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public Object[] toArray() {
        Object[] result = new Object[size];
        Node<T> current = head;
        for (int i = 0; i < size; i++) {
            result[i] = current.data;
            current = current.next;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            if (current != head) {
                sb.append(", ");
            }
            sb.append(current.data);
            current = current.next;
        }
        return sb.append("]").toString();
    }
}
