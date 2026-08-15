package servicehub.ds;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {

    @Test
    void startsEmpty() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        assertTrue(pq.isEmpty());
        assertEquals(0, pq.size());
        assertNull(pq.peek());
        assertNull(pq.extract());
    }

    @Test
    void minHeapOrdering() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.insert(30);
        pq.insert(10);
        pq.insert(20);
        pq.insert(5);
        assertEquals(5, pq.peek());
        List<Integer> extracted = new ArrayList<>();
        while (!pq.isEmpty()) extracted.add(pq.extract());
        assertEquals(List.of(5, 10, 20, 30), extracted);
    }

    @Test
    void maxHeapViaComparator() {
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(10, Comparator.reverseOrder());
        pq.insert(1);
        pq.insert(9);
        pq.insert(4);
        assertEquals(9, pq.extract());
        assertEquals(4, pq.extract());
        assertEquals(1, pq.extract());
    }

    @Test
    void handlesDuplicates() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.insert(7);
        pq.insert(7);
        pq.insert(3);
        assertEquals(3, pq.extract());
        assertEquals(7, pq.extract());
        assertEquals(7, pq.extract());
        assertTrue(pq.isEmpty());
    }

    @Test
    void growsBeyondInitialCapacity() {
        PriorityQueue<Integer> pq = new PriorityQueue<>(2);
        for (int i = 100; i >= 1; i--) pq.insert(i);
        assertEquals(100, pq.size());
        assertEquals(1, pq.extract());
        assertEquals(2, pq.extract());
    }

    @Test
    void nullInsertRejected() {
        PriorityQueue<String> pq = new PriorityQueue<>();
        assertThrows(IllegalArgumentException.class, () -> pq.insert(null));
    }

    @Test
    void stringsOrderedNaturally() {
        PriorityQueue<String> pq = new PriorityQueue<>();
        pq.insert("banana");
        pq.insert("apple");
        pq.insert("cherry");
        assertEquals("apple", pq.extract());
        assertEquals("banana", pq.extract());
        assertEquals("cherry", pq.extract());
    }

    @Test
    void largeHeapProducesSortedOutput() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        int n = 10_000;
        for (int i = n; i >= 1; i--) pq.insert(i);
        int prev = Integer.MIN_VALUE;
        while (!pq.isEmpty()) {
            int v = pq.extract();
            assertTrue(v >= prev);
            prev = v;
        }
    }

    @Test
    void clearResetsQueue() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.insert(3);
        pq.insert(1);
        pq.clear();
        assertTrue(pq.isEmpty());
        assertEquals(0, pq.size());
        assertNull(pq.extract());
    }

    @Test
    void serviceRequestsByUrgency() {
        servicehub.model.ServiceRequest low = new servicehub.model.ServiceRequest("R1", "A", "B", "Maint", 1, "t", "d", "NEW");
        servicehub.model.ServiceRequest high = new servicehub.model.ServiceRequest("R2", "A", "B", "Maint", 5, "t", "d", "NEW");
        PriorityQueue<servicehub.model.ServiceRequest> pq = new PriorityQueue<>(
                4, Comparator.comparingInt(servicehub.model.ServiceRequest::getUrgency).reversed());
        pq.insert(low);
        pq.insert(high);
        assertEquals("R2", pq.extract().getRequestId());
        assertEquals("R1", pq.extract().getRequestId());
    }
}
