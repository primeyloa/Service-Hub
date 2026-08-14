import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A custom generic list implementation built from the ground up.
 * Supports basic list operations and custom iterator for for-each loops.
 */
public class SimpleList<E> implements Iterable<E> {
    private Object[] elements;
    private int size;

    public SimpleList() {
        this.elements = new Object[10];
        this.size = 0;
    }

    public void add(E element) {
        ensureCapacity();
        elements[size++] = element;
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        @SuppressWarnings("unchecked")
        E value = (E) elements[index];
        return value;
    }

    public int size() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length * 2;
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new SimpleListIterator();
    }

    private class SimpleListIterator implements Iterator<E> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E) elements[currentIndex++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }
}
