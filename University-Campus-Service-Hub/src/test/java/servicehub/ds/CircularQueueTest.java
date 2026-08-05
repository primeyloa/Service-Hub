package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CircularQueueTest {

    @Test
    void invalidCapacityRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<>(-1));
    }

    @Test
    void startsEmptyAndNotFull() {
        CircularQueue<String> queue = new CircularQueue<>(3);
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertEquals(0, queue.size());
        assertEquals(3, queue.capacity());
    }

    @Test
    void enqueueDequeueInFifoOrder() {
        CircularQueue<String> queue = new CircularQueue<>(3);
        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");
        assertTrue(queue.isFull());
        assertFalse(queue.isEmpty());
        assertEquals("a", queue.peek());
        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());
        assertEquals("c", queue.dequeue());
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
    }

    @Test
    void wrapsAroundAndReusesFreedSlots() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        queue.enqueue(4);
        queue.enqueue(5);
        assertEquals(3, queue.dequeue());
        assertEquals(4, queue.dequeue());
        assertEquals(5, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void enqueueWhenFullThrows() {
        CircularQueue<String> queue = new CircularQueue<>(1);
        queue.enqueue("a");
        assertThrows(IllegalStateException.class, () -> queue.enqueue("b"));
    }

    @Test
    void dequeueWhenEmptyThrows() {
        CircularQueue<String> queue = new CircularQueue<>(2);
        assertThrows(IllegalStateException.class, queue::dequeue);
        assertThrows(IllegalStateException.class, queue::peek);
    }

    @Test
    void clearResetsQueue() {
        CircularQueue<String> queue = new CircularQueue<>(2);
        queue.enqueue("a");
        queue.enqueue("b");
        queue.clear();
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertEquals(0, queue.size());
    }

    @Test
    void fullQueueCanAcceptMoreAfterClear() {
        CircularQueue<String> queue = new CircularQueue<>(2);
        queue.enqueue("a");
        queue.enqueue("b");
        queue.clear();
        queue.enqueue("c");
        queue.enqueue("d");
        assertTrue(queue.isFull());
        assertEquals("c", queue.dequeue());
        assertEquals("d", queue.dequeue());
    }

    @Test
    void toArrayReturnsOrderFromFront() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.dequeue();
        queue.enqueue(4);
        Object[] arr = queue.toArray();
        assertEquals(3, arr.length);
        assertEquals(2, arr[0]);
        assertEquals(3, arr[1]);
        assertEquals(4, arr[2]);
    }

    @Test
    void largeWraparoundSequence() {
        CircularQueue<Integer> queue = new CircularQueue<>(64);
        for (int round = 0; round < 10; round++) {
            for (int i = 0; i < 64; i++) {
                queue.enqueue(i);
            }
            for (int i = 0; i < 64; i++) {
                assertEquals(i, queue.dequeue());
            }
            assertTrue(queue.isEmpty());
        }
    }
}
