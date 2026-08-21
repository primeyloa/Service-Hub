package servicehub.ds;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A parameterized dynamic array list implementation.
 * Backed by a plain Object array that grows when full.
 *
 * @param <T> the type of elements held in this list
 */
public class ArrayList<T> implements Iterable<T> {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] data;
    private int size;

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must not be negative");
        }
        this.data = new Object[initialCapacity];
        this.size = 0;
    }

    public ArrayList(Collection<? extends T> c) {
        if (c == null) throw new IllegalArgumentException("Collection must not be null");
        this.data = new Object[Math.max(DEFAULT_CAPACITY, c.size())];
        this.size = 0;
        for (T item : c) add(item);
    }

    public ArrayList(Iterable<? extends T> iterable) {
        this(DEFAULT_CAPACITY);
        if (iterable != null) {
            for (T item : iterable) add(item);
        }
    }

    public void add(T item) {
        ensureCapacity(size + 1);
        data[size++] = item;
    }

    public void add(int index, T item) {
        checkIndexForAdd(index);
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = item;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    @SuppressWarnings("unchecked")
    public T set(int index, T item) {
        checkIndex(index);
        T old = (T) data[index];
        data[index] = item;
        return old;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }
        data[--size] = null;
        return removed;
    }

    public boolean remove(T item) {
        int index = indexOf(item);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(T item) {
        return indexOf(item) >= 0;
    }

    public int indexOf(T item) {
        for (int i = 0; i < size; i++) {
            if (equalsNullSafe(data[i], item)) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<T> subList(int fromIndex, int toIndex) {
        checkIndex(fromIndex);
        checkIndex(toIndex - 1);
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        ArrayList<T> sublist = new ArrayList<>(toIndex - fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            sublist.add(get(i));
        }
        return sublist;
    }

    public void addAll(ArrayList<? extends T> c) {
        if (c == null) return;
        for (T item : c) {
            add(item);
        }
    }

    public void addAll(Collection<? extends T> c) {
        if (c == null) return;
        for (T item : c) add(item);
    }

    public void addAll(Iterable<? extends T> iterable) {
        if (iterable == null) return;
        for (T item : iterable) add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof ArrayList) {
            ArrayList<?> other = (ArrayList<?>) o;
            if (size != other.size) return false;
            for (int i = 0; i < size; i++) {
                if (!equalsNullSafe(data[i], other.data[i])) return false;
            }
            return true;
        }
        if (o instanceof java.util.List) {
            java.util.List<?> other = (java.util.List<?>) o;
            if (size != other.size()) return false;
            for (int i = 0; i < size; i++) {
                if (!equalsNullSafe(data[i], other.get(i))) return false;
            }
            return true;
        }
        if (o instanceof Iterable) {
            java.util.Iterator<?> it = ((Iterable<?>) o).iterator();
            for (int i = 0; i < size; i++) {
                if (!it.hasNext()) return false;
                if (!equalsNullSafe(data[i], it.next())) return false;
            }
            return !it.hasNext();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < size; i++) {
            h = 31 * h + (data[i] == null ? 0 : data[i].hashCode());
        }
        return h;
    }

    public Object[] toArray() {
        Object[] copy = new Object[size];
        System.arraycopy(data, 0, copy, 0, size);
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(data[i]);
        }
        return sb.append("]").toString();
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCapacity = Math.max(minCapacity, data.length * 2);
            if (newCapacity == 0) {
                newCapacity = DEFAULT_CAPACITY;
            }
            Object[] grown = new Object[newCapacity];
            System.arraycopy(data, 0, grown, 0, size);
            data = grown;
        }
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

    @Override
    public Iterator<T> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements available.");
            }
            return (T) data[currentIndex++];
        }
    }
}
