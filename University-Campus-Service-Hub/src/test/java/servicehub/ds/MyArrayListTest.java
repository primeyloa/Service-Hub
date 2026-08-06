package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MyArrayListTest {

    @Test
    void startsEmpty() {
        MyArrayList<String> list = new MyArrayList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addAppendsAndGrowsPastInitialCapacity() {
        MyArrayList<Integer> list = new MyArrayList<>(2);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        assertEquals(100, list.size());
        assertEquals(0, list.get(0));
        assertEquals(99, list.get(99));
        assertFalse(list.isEmpty());
    }

    @Test
    void addAtIndexInsertsAndShifts() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("c");
        list.add(1, "b");
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    void addAtIndexOutOfBoundsThrows() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "x"));
    }

    @Test
    void getOutOfBoundsThrows() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void setReplacesAndReturnsOldValue() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("old");
        String previous = list.set(0, "new");
        assertEquals("old", previous);
        assertEquals("new", list.get(0));
    }

    @Test
    void removeByIndexShiftsAndShrinks() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals("b", list.remove(1));
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("c", list.get(1));
    }

    @Test
    void removeByValueRemovesFirstOccurrence() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.add("a");
        assertTrue(list.remove("a"));
        assertEquals(2, list.size());
        assertEquals("b", list.get(0));
        assertEquals("a", list.get(1));
        assertFalse(list.remove("zzz"));
        assertEquals(2, list.size());
    }

    @Test
    void supportsNullElements() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add(null);
        list.add("x");
        assertTrue(list.contains(null));
        assertTrue(list.remove(null));
        assertEquals(1, list.size());
    }

    @Test
    void containsAndIndexOf() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("one");
        list.add("two");
        assertTrue(list.contains("two"));
        assertFalse(list.contains("three"));
        assertEquals(1, list.indexOf("two"));
        assertEquals(-1, list.indexOf("three"));
    }

    @Test
    void clearEmptiesTheList() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    void toArrayReturnsSnapshotInOrder() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        Object[] arr = list.toArray();
        assertEquals(2, arr.length);
        assertEquals("a", arr[0]);
        assertEquals("b", arr[1]);
        list.add("c");
        assertEquals(2, arr.length);
    }

    @Test
    void toStringRendersContents() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        assertEquals("[a, b]", list.toString());
    }

    @Test
    void invalidCapacityRejected() {
        assertThrows(IllegalArgumentException.class, () -> new MyArrayList<>(-1));
    }

    @Test
    void zeroCapacityListStillGrows() {
        MyArrayList<String> list = new MyArrayList<>(0);
        list.add("a");
        assertEquals(1, list.size());
        assertEquals("a", list.get(0));
    }
}
