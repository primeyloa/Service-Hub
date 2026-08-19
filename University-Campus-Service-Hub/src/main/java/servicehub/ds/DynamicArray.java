package servicehub.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Parameterized Dynamic Array data structure supporting array resizing and iteration.
 *
 * @param <T> element type
 */
public class DynamicArray<T> implements Iterable<T> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
        this.elements = new Object[capacity];
        this.size = 0;
    }

    public void add(T element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    public void insert(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    public void addAll(DynamicArray<? extends T> other) {
        if (other == null) return;
        for (T item : other) {
            add(item);
        }
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];
    }

    public void set(int index, T element) {
        checkIndex(index);
        elements[index] = element;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
        return removed;
    }

    public boolean remove(T item) {
        for (int i = 0; i < size; i++) {
            if (elements[i] == null ? item == null : elements[i].equals(item)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(T item) {
        for (int i = 0; i < size; i++) {
            if (elements[i] == null ? item == null : elements[i].equals(item)) return true;
        }
        return false;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    public int capacity() {
        return elements.length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(minCapacity, elements.length * 2);
            if (newCapacity == 0) newCapacity = DEFAULT_CAPACITY;
            Object[] newArray = new Object[newCapacity];
            System.arraycopy(elements, 0, newArray, 0, size);
            elements = newArray;
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size;
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException("No more elements in DynamicArray");
                return (T) elements[current++];
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append("]").toString();
    }
}
