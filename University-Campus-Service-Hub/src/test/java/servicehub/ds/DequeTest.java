package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DequeTest {

    @Test
    void startsEmpty() {
        Deque<String> deque = new Deque<>();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }

    @Test
    void addFirstPeekFirst() {
        Deque<String> deque = new Deque<>();
        deque.addFirst("b");
        deque.addFirst("a");
        assertEquals(2, deque.size());
        assertEquals("a", deque.peekFirst());
        assertEquals("b", deque.peekLast());
    }

    @Test
    void addLastPeekLast() {
        Deque<String> deque = new Deque<>();
        deque.addLast("a");
        deque.addLast("b");
        assertEquals("a", deque.peekFirst());
        assertEquals("b", deque.peekLast());
    }

    @Test
    void removeFromBothEnds() {
        Deque<String> deque = new Deque<>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");
        assertEquals("a", deque.removeFirst());
        assertEquals("c", deque.removeLast());
        assertEquals("b", deque.peekFirst());
        assertEquals("b", deque.peekLast());
        assertEquals("b", deque.removeLast());
        assertTrue(deque.isEmpty());
    }

    @Test
    void interleavedAddsPreserveOrder() {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(3);
        deque.addFirst(2);
        deque.addFirst(1);
        deque.addLast(4);
        deque.addLast(5);
        assertEquals(5, deque.size());
        assertEquals(1, deque.removeFirst());
        assertEquals(5, deque.removeLast());
        assertEquals(2, deque.removeFirst());
        assertEquals(4, deque.removeLast());
        assertEquals(3, deque.removeFirst());
        assertTrue(deque.isEmpty());
    }

    @Test
    void removeOnEmptyThrows() {
        Deque<String> deque = new Deque<>();
        assertThrows(IllegalStateException.class, deque::removeFirst);
        assertThrows(IllegalStateException.class, deque::removeLast);
        assertThrows(IllegalStateException.class, deque::peekFirst);
        assertThrows(IllegalStateException.class, deque::peekLast);
    }

    @Test
    void clearEmptiesDeque() {
        Deque<String> deque = new Deque<>();
        deque.addFirst("a");
        deque.addLast("b");
        deque.clear();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertThrows(IllegalStateException.class, deque::removeFirst);
    }

    @Test
    void worksAsBothStackAndQueue() {
        Deque<Integer> deque = new Deque<>();
        deque.addLast(1);
        deque.addLast(2);
        assertEquals(1, deque.removeFirst());
        deque.addFirst(0);
        deque.addLast(3);
        assertEquals(0, deque.removeFirst());
        assertEquals(2, deque.removeFirst());
        assertEquals(3, deque.removeLast());
        assertTrue(deque.isEmpty());
    }

    @Test
    void largeSequenceFromBothEnds() {
        Deque<Integer> deque = new Deque<>();
        for (int i = 0; i < 10_000; i++) {
            deque.addLast(i);
        }
        for (int i = 0; i < 5_000; i++) {
            assertEquals(i, deque.removeFirst());
        }
        for (int i = 9_999; i >= 5_000; i--) {
            assertEquals(i, deque.removeLast());
        }
        assertTrue(deque.isEmpty());
    }

    @Test
    void toArrayReturnsOrderFromFirstToLast() {
        Deque<String> deque = new Deque<>();
        deque.addFirst("b");
        deque.addFirst("a");
        deque.addLast("c");
        Object[] arr = deque.toArray();
        assertEquals(3, arr.length);
        assertEquals("a", arr[0]);
        assertEquals("b", arr[1]);
        assertEquals("c", arr[2]);
    }

    @Test
    void toStringRendersContents() {
        Deque<String> deque = new Deque<>();
        deque.addLast("a");
        deque.addLast("b");
        assertEquals("[a, b]", deque.toString());
    }
}
