package servicehub.ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @Test
    void addAndGet() {
        DynamicArray<String> da = new DynamicArray<>();
        da.add("a");
        da.add("b");
        assertEquals("a", da.get(0));
        assertEquals("b", da.get(1));
        assertEquals(2, da.size());
    }

    @Test
    void growsPastInitialCapacity() {
        DynamicArray<Integer> da = new DynamicArray<>(2);
        for (int i = 0; i < 100; i++) da.add(i);
        assertEquals(100, da.size());
        assertEquals(99, da.get(99));
        assertTrue(da.capacity() >= 100);
    }

    @Test
    void insertAtIndex() {
        DynamicArray<String> da = new DynamicArray<>();
        da.add("a");
        da.add("c");
        da.insert(1, "b");
        assertEquals("b", da.get(1));
        assertEquals("c", da.get(2));
    }

    @Test
    void setReplaces() {
        DynamicArray<Integer> da = new DynamicArray<>();
        da.add(1);
        da.set(0, 42);
        assertEquals(42, da.get(0));
    }

    @Test
    void removeByIndex() {
        DynamicArray<String> da = new DynamicArray<>();
        da.add("a");
        da.add("b");
        da.add("c");
        assertEquals("b", da.remove(1));
        assertEquals(2, da.size());
        assertEquals("a", da.get(0));
        assertEquals("c", da.get(1));
    }

    @Test
    void removeByValue() {
        DynamicArray<String> da = new DynamicArray<>();
        da.add("x");
        da.add("y");
        assertTrue(da.remove("y"));
        assertFalse(da.remove("zzz"));
        assertEquals(1, da.size());
    }

    @Test
    void boundsChecks() {
        DynamicArray<String> da = new DynamicArray<>();
        da.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> da.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> da.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> da.remove(5));
        assertThrows(IndexOutOfBoundsException.class, () -> da.insert(3, "z"));
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray<>(-1));
    }

    @Test
    void clearAndAddAll() {
        DynamicArray<Integer> da = new DynamicArray<>();
        da.add(1);
        da.add(2);
        da.clear();
        assertTrue(da.isEmpty());
        DynamicArray<Integer> other = new DynamicArray<>();
        other.add(3);
        other.add(4);
        da.addAll(other);
        assertEquals(2, da.size());
        assertEquals(3, da.get(0));
        assertEquals(4, da.get(1));
    }

    @Test
    void iteratorWorks() {
        DynamicArray<Integer> da = new DynamicArray<>();
        for (int i = 0; i < 5; i++) da.add(i);
        int expected = 0;
        for (Integer v : da) assertEquals(expected++, v);
        assertEquals(5, expected);
    }

    @Test
    void toStringRenders() {
        DynamicArray<String> da = new DynamicArray<>();
        da.add("a");
        da.add("b");
        assertEquals("[a, b]", da.toString());
    }
}
