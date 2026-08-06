package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SinglyLinkedListTest {

    @Test
    void startsEmpty() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addFirstPrepends() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.addFirst("b");
        list.addFirst("a");
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
    }

    @Test
    void addLastAppends() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.addLast("a");
        list.addLast("b");
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
    }

    @Test
    void addAtIndexInsertsInMiddle() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("c");
        list.add(1, "b");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
        list.add(3, "d");
        assertEquals("d", list.get(3));
        list.add(0, "z");
        assertEquals("z", list.get(0));
    }

    @Test
    void removeFirstAndLast() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals("a", list.removeFirst());
        assertEquals("c", list.removeLast());
        assertEquals(1, list.size());
        assertEquals("b", list.get(0));
        assertFalse(list.isEmpty());
        assertEquals("b", list.removeFirst());
        assertTrue(list.isEmpty());
    }

    @Test
    void removeFirstOnEmptyThrows() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        assertThrows(IllegalStateException.class, list::removeFirst);
        assertThrows(IllegalStateException.class, list::removeLast);
    }

    @Test
    void removeByIndex() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals("b", list.remove(1));
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("c", list.get(1));
        assertEquals("c", list.remove(1));
        assertEquals("a", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void removeByValue() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        list.add("b");
        list.add("c");
        assertTrue(list.remove("b"));
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
        assertFalse(list.remove("zzz"));
    }

    @Test
    void removeByValueUpdatesTail() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        assertTrue(list.remove("b"));
        assertEquals(1, list.size());
        assertEquals("a", list.removeLast());
        assertTrue(list.isEmpty());
    }

    @Test
    void setReplacesValue() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        assertEquals("b", list.set(1, "B"));
        assertEquals("B", list.get(1));
    }

    @Test
    void indexOutOfBoundsThrows() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "x"));
    }

    @Test
    void containsSupportsNull() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add(null);
        list.add("x");
        assertTrue(list.contains(null));
        assertTrue(list.contains("x"));
        assertFalse(list.contains("y"));
    }

    @Test
    void clearEmptiesList() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertThrows(IllegalStateException.class, list::removeFirst);
    }

    @Test
    void toArrayReturnsInOrder() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        Object[] arr = list.toArray();
        assertEquals("a", arr[0]);
        assertEquals("b", arr[1]);
    }

    @Test
    void largeAppendWorks() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        for (int i = 0; i < 10_000; i++) {
            list.add(i);
        }
        assertEquals(10_000, list.size());
        assertEquals(9_999, list.get(9_999));
        for (int i = 0; i < 10_000; i++) {
            assertEquals(i, list.removeFirst());
        }
        assertTrue(list.isEmpty());
    }

    @Test
    void toStringRendersContents() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("a");
        list.add("b");
        assertEquals("[a, b]", list.toString());
    }
}
