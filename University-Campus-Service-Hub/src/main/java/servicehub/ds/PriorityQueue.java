package servicehub.ds;

public class PriorityQueue {
    private int[] heap;
    private int size;
    private int capacity;

    public PriorityQueue(int initialCapacity) {
        this.capacity = initialCapacity;
        this.size = 0;
        this.heap = new int[initialCapacity];
    }

    // Index helper formulas mapped to a contiguous array
    private int getParentIndex(int childIndex) { return (childIndex - 1) / 2; }
    private int getLeftChildIndex(int parentIndex) { return 2 * parentIndex + 1; }
    private int getRightChildIndex(int parentIndex) { return 2 * parentIndex + 2; }

    private boolean hasParent(int index) { return index > 0; }
    private boolean hasLeftChild(int index) { return getLeftChildIndex(index) < size; }
    private boolean hasRightChild(int index) { return getRightChildIndex(index) < size; }

    private int parent(int index) { return heap[getParentIndex(index)]; }
    private int leftChild(int index) { return heap[getLeftChildIndex(index)]; }
    private int rightChild(int index) { return heap[getRightChildIndex(index)]; }

    private void swap(int indexOne, int indexTwo) {
        int temp = heap[indexOne];
        heap[indexOne] = heap[indexTwo];
        heap[indexTwo] = temp;
    }

    private void ensureExtraCapacity() {
        if (size == capacity) {
            int newCapacity = capacity * 2;
            int[] newheap = new int[newCapacity];
            System.arraycopy(heap, 0, newheap, 0, size);
            heap = newheap;
        }
    }

    // Look at the highest priority element without removing it
    public int peek() {
        if (size == 0) return -1;
        return heap[0];
    }

    // Add an element to the queue
    public void enqueue(int item) {
        ensureExtraCapacity();
        heap[size] = item; // Put at the very end
        size++;
        siftUp();          // Bubble up to restore heap invariant
    }

    // Remove and return the highest priority element
    public int dequeue() {
        if (size == 0) return -1;
        int item = heap[0];
        heap[0] = heap[size - 1]; // Move last element to the root
        size--;
        siftDown();               // Bubble down to restore heap invariant
        return item;
    }

    // Restores heap order by moving a new element upwards
    private void siftUp() {
        int index = size - 1;
        while (hasParent(index) && parent(index) > heap[index]) {
            swap(getParentIndex(index), index);
            index = getParentIndex(index);
        }
    }

    // Restores heap order by moving an element downwards
    private void siftDown() {
        int index = 0;
        while (hasLeftChild(index)) {
            int smallerChildIndex = getLeftChildIndex(index);
            if (hasRightChild(index) && rightChild(index) < leftChild(index)) {
                smallerChildIndex = getRightChildIndex(index);
            }

            if (heap[index] < heap[smallerChildIndex]) {
                break;
            } else {
                swap(index, smallerChildIndex);
            }
            index = smallerChildIndex;
        }
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
