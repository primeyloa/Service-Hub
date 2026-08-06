package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class QueueTest {

    @Test
    void startsEmpty() {
        Queue<String> queue = new Queue<>();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void enqueueDequeueFollowsFifoOrder() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");
        assertEquals(3, queue.size());
        assertEquals("a", queue.peek());
        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());
        assertEquals("c", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemove() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("x");
        assertEquals("x", queue.peek());
        assertEquals(1, queue.size());
    }

    @Test
    void dequeueOnEmptyThrows() {
        Queue<String> queue = new Queue<>();
        assertThrows(IllegalStateException.class, queue::dequeue);
        assertThrows(IllegalStateException.class, queue::peek);
    }

    @Test
    void clearEmptiesQueue() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("a");
        queue.enqueue("b");
        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void reuseAfterEmptied() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("a");
        queue.dequeue();
        queue.enqueue("b");
        queue.enqueue("c");
        assertEquals("b", queue.dequeue());
        assertEquals("c", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void largeSequencePreservesFifoOrder() {
        Queue<Integer> queue = new Queue<>();
        for (int i = 0; i < 10_000; i++) {
            queue.enqueue(i);
        }
        for (int i = 0; i < 10_000; i++) {
            assertEquals(i, queue.dequeue());
        }
        assertTrue(queue.isEmpty());
    }
}
