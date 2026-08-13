package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MyArrayListTest {

    @Test
    void startsEmpty() {
        ArrayList<String> list = new ArrayList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addAppendsAndGrowsPastInitialCapacity() {
        ArrayList<Integer> list = new ArrayList<>(2);
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
        ArrayList<String> list = new ArrayList<>();
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
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "x"));
    }

    @Test
    void getOutOfBoundsThrows() {
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void setReplacesAndReturnsOldValue() {
        ArrayList<String> list = new ArrayList<>();
        list.add("old");
        String previous = list.set(0, "new");
        assertEquals("old", previous);
        assertEquals("new", list.get(0));
    }

    @Test
    void removeByIndexShiftsAndShrinks() {
        ArrayList<String> list = new ArrayList<>();
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
        ArrayList<String> list = new ArrayList<>();
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
        ArrayList<String> list = new ArrayList<>();
        list.add(null);
        list.add("x");
        assertTrue(list.contains(null));
        assertTrue(list.remove(null));
        assertEquals(1, list.size());
    }

    @Test
    void containsAndIndexOf() {
        ArrayList<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        assertTrue(list.contains("two"));
        assertFalse(list.contains("three"));
        assertEquals(1, list.indexOf("two"));
        assertEquals(-1, list.indexOf("three"));
    }

    @Test
    void clearEmptiesTheList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    void toArrayReturnsSnapshotInOrder() {
        ArrayList<String> list = new ArrayList<>();
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
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        assertEquals("[a, b]", list.toString());
    }

    @Test
    void invalidCapacityRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayList<>(-1));
    }

    @Test
    void zeroCapacityListStillGrows() {
        ArrayList<String> list = new ArrayList<>(0);
        list.add("a");
        assertEquals(1, list.size());
        assertEquals("a", list.get(0));
    }
}
