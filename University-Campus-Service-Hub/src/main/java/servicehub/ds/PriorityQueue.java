package servicehub.ds;

import java.util.Comparator;

/**
 * A generic Binary Heap Priority Queue implementation.
 * Supports both custom Comparator and natural Comparable ordering.
 *
 * @param <T> element type
 */
public class PriorityQueue<T> {
    private Object[] heap;
    private int size;
    private int capacity;
    private final Comparator<T> comparator;

    @SuppressWarnings("unchecked")
    public PriorityQueue(int initialCapacity, Comparator<T> comparator) {
        if (initialCapacity <= 0) {
            initialCapacity = 10;
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.heap = new Object[initialCapacity];
        this.comparator = comparator;
    }

    public PriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public PriorityQueue() {
        this(10, null);
    }

    private int getParentIndex(int childIndex) { return (childIndex - 1) / 2; }
    private int getLeftChildIndex(int parentIndex) { return 2 * parentIndex + 1; }
    private int getRightChildIndex(int parentIndex) { return 2 * parentIndex + 2; }

    private boolean hasParent(int index) { return index > 0; }
    private boolean hasLeftChild(int index) { return getLeftChildIndex(index) < size; }
    private boolean hasRightChild(int index) { return getRightChildIndex(index) < size; }

    @SuppressWarnings("unchecked")
    private T parent(int index) { return (T) heap[getParentIndex(index)]; }
    @SuppressWarnings("unchecked")
    private T leftChild(int index) { return (T) heap[getLeftChildIndex(index)]; }
    @SuppressWarnings("unchecked")
    private T rightChild(int index) { return (T) heap[getRightChildIndex(index)]; }

    private void swap(int indexOne, int indexTwo) {
        Object temp = heap[indexOne];
        heap[indexOne] = heap[indexTwo];
        heap[indexTwo] = temp;
    }

    private void ensureCapacity() {
        if (size >= capacity) {
            capacity *= 2;
            Object[] newHeap = new Object[capacity];
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<T>) a).compareTo(b);
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) return null;
        return (T) heap[0];
    }

    public void enqueue(T item) {
        insert(item);
    }

    public void insert(T item) {
        if (item == null) throw new IllegalArgumentException("Cannot insert null into PriorityQueue");
        ensureCapacity();
        heap[size] = item;
        size++;
        siftUp();
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        return extract();
    }

    public T extractMin() {
        return extract();
    }

    public T extractMax() {
        return extract();
    }

    @SuppressWarnings("unchecked")
    public T extract() {
        if (size == 0) return null;
        T item = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        siftDown();
        return item;
    }

    @SuppressWarnings("unchecked")
    private void siftUp() {
        int index = size - 1;
        while (hasParent(index) && compare((T) heap[index], parent(index)) < 0) {
            swap(getParentIndex(index), index);
            index = getParentIndex(index);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown() {
        int index = 0;
        while (hasLeftChild(index)) {
            int smallerChildIndex = getLeftChildIndex(index);
            if (hasRightChild(index) && compare(rightChild(index), leftChild(index)) < 0) {
                smallerChildIndex = getRightChildIndex(index);
            }

            if (compare((T) heap[index], (T) heap[smallerChildIndex]) <= 0) {
                break;
            } else {
                swap(index, smallerChildIndex);
            }
            index = smallerChildIndex;
        }
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public void clear() {
        for (int i = 0; i < size; i++) heap[i] = null;
        size = 0;
    }
}
