package servicehub.ds;

/**
 * A fixed-capacity circular queue implementation.
 * Elements are stored in a plain Object array; the front index wraps around
 * so that slots freed by dequeueing can be reused without shifting.
 *
 * @param <T> the type of elements held in this queue
 */
public class CircularQueue<T> {

    private final Object[] data;
    private int front;
    private int size;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.data = new Object[capacity];
        this.front = 0;
        this.size = 0;
    }

    public void enqueue(T item) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full (capacity " + data.length + ")");
        }
        int index = (front + size) % data.length;
        data[index] = item;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot dequeue from an empty queue");
        }
        T item = (T) data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        size--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot peek at an empty queue");
        }
        return (T) data[front];
    }

    public int capacity() {
        return data.length;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == data.length;
    }

    public void clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = null;
        }
        front = 0;
        size = 0;
    }

    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = data[(front + i) % data.length];
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(data[(front + i) % data.length]);
        }
        return sb.append("]").toString();
    }
}
