package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PriorityQueueTest {

    @Test
    void startsEmpty() {
        PriorityQueue queue = new PriorityQueue();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void returnsLowestDistanceFirst() {
        PriorityQueue queue = new PriorityQueue();
        queue.add(3, 30);
        queue.add(1, 10);
        queue.add(2, 20);

        PriorityQueue.VertexDistance first = queue.poll();
        assertEquals(1, first.getVertex());
        assertEquals(10, first.getDistance());

        PriorityQueue.VertexDistance second = queue.poll();
        assertEquals(2, second.getVertex());
        assertEquals(20, second.getDistance());

        PriorityQueue.VertexDistance third = queue.poll();
        assertEquals(3, third.getVertex());
        assertEquals(30, third.getDistance());

        assertTrue(queue.isEmpty());
    }

    @Test
    void breaksTiesByVertex() {
        PriorityQueue queue = new PriorityQueue();
        queue.add(5, 10);
        queue.add(2, 10);
        queue.add(7, 10);

        assertEquals(2, queue.poll().getVertex());
        assertEquals(5, queue.poll().getVertex());
        assertEquals(7, queue.poll().getVertex());
    }

    @Test
    void peekDoesNotRemove() {
        PriorityQueue queue = new PriorityQueue();
        queue.add(8, 12);

        PriorityQueue.VertexDistance peeked = queue.peek();
        assertEquals(8, peeked.getVertex());
        assertEquals(12, peeked.getDistance());
        assertEquals(1, queue.size());
    }

    @Test
    void emptyPollThrows() {
        PriorityQueue queue = new PriorityQueue();
        assertThrows(IllegalStateException.class, queue::poll);
        assertThrows(IllegalStateException.class, queue::peek);
    }
}
