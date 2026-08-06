package servicehub.ds;

/**
 * A singly linked list implementation.
 * Each node holds one element and a reference to the next node.
 *
 * @param <T> the type of elements held in this list
 */
public class SinglyLinkedList<T> {

    private static final class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void add(T item) {
        addLast(item);
    }

    public void add(int index, T item) {
        checkIndexForAdd(index);
        if (index == 0) {
            addFirst(item);
            return;
        }
        if (index == size) {
            addLast(item);
            return;
        }
        Node<T> prev = nodeAt(index - 1);
        Node<T> node = new Node<>(item);
        node.next = prev.next;
        prev.next = node;
        size++;
    }

    public void addFirst(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
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
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public T removeFirst() {
        if (head == null) {
            throw new IllegalStateException("Cannot remove from an empty list");
        }
        T removed = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return removed;
    }

    public T removeLast() {
        if (head == null) {
            throw new IllegalStateException("Cannot remove from an empty list");
        }
        if (head == tail) {
            T removed = head.data;
            head = null;
            tail = null;
            size--;
            return removed;
        }
        Node<T> current = head;
        while (current.next != tail) {
            current = current.next;
        }
        T removed = tail.data;
        current.next = null;
        tail = current;
        size--;
        return removed;
    }

    public T remove(int index) {
        checkIndex(index);
        if (index == 0) {
            return removeFirst();
        }
        Node<T> prev = nodeAt(index - 1);
        Node<T> target = prev.next;
        prev.next = target.next;
        if (target == tail) {
            tail = prev;
        }
        size--;
        return target.data;
    }

    public boolean remove(T item) {
        Node<T> prev = null;
        Node<T> current = head;
        while (current != null) {
            if (equalsNullSafe(current.data, item)) {
                if (prev == null) {
                    head = current.next;
                    if (head == null) {
                        tail = null;
                    }
                } else {
                    prev.next = current.next;
                    if (current == tail) {
                        tail = prev;
                    }
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    public T get(int index) {
        checkIndex(index);
        return nodeAt(index).data;
    }

    public T set(int index, T item) {
        checkIndex(index);
        Node<T> node = nodeAt(index);
        T old = node.data;
        node.data = item;
        return old;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(T item) {
        Node<T> current = head;
        while (current != null) {
            if (equalsNullSafe(current.data, item)) {
                return true;
            }
            current = current.next;
        }
        return false;
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

    private Node<T> nodeAt(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }

    private static boolean equalsNullSafe(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
