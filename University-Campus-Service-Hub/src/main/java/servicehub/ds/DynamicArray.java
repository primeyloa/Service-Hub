package servicehub.ds;


public class DynamicArray<T>  {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public DynamicArray() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public DynamicArray(int capacity) {
        this.elements = new Object[capacity];
        this.size = 0;
    }

    public void add(T element) {
        ensureCapacity();
        elements[size++] = element;
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

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (size >= elements.length) {
            int newCapacity = elements.length * 2;
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
}
