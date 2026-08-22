package servicehub.ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void putAndGet() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        table.put("two", 2);
        assertEquals(1, table.get("one"));
        assertEquals(2, table.get("two"));
        assertEquals(2, table.size());
    }

    @Test
    void updateExistingKey() {
        HashTable<String, String> table = new HashTable<>();
        table.put("k", "v1");
        table.put("k", "v2");
        assertEquals("v2", table.get("k"));
        assertEquals(1, table.size());
    }

    @Test
    void containsKeyAndGetMissing() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("a", 1);
        assertTrue(table.containsKey("a"));
        assertFalse(table.containsKey("zzz"));
        assertNull(table.get("zzz"));
    }

    @Test
    void removeExistingAndMissing() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("a", 1);
        table.put("b", 2);
        assertEquals(1, table.remove("a"));
        assertNull(table.get("a"));
        assertNull(table.remove("nope"));
        assertEquals(1, table.size());
    }

    @Test
    void collisionsAreHandledByChaining() {
        // bucket size small so collisions are guaranteed
        HashTable<String, Integer> table = new HashTable<>(4);
        table.put("a", 1);
        table.put("b", 2);
        table.put("c", 3);
        table.put("d", 4);
        table.put("e", 5);
        assertEquals(5, table.size());
        for (String k : new String[]{"a", "b", "c", "d", "e"}) {
            assertNotNull(table.get(k));
        }
        assertTrue(table.getCollisionCount() > 0);
    }

    @Test
    void loadFactorCalculated() {
        HashTable<String, Integer> table = new HashTable<>(10);
        for (int i = 0; i < 5; i++) table.put("k" + i, i);
        assertEquals(0.5, table.getLoadFactor(), 1e-9);
    }

    @Test
    void invalidBucketSizeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HashTable<>(0));
        assertThrows(IllegalArgumentException.class, () -> new HashTable<>(-1));
    }

    @Test
    void nullKeyRejected() {
        HashTable<String, Integer> table = new HashTable<>();
        assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
    }

    @Test
    void clearEmptiesEverything() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("a", 1);
        table.put("b", 2);
        table.clear();
        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
        assertNull(table.get("a"));
        assertEquals(0, table.getCollisionCount());
    }

    @Test
    void manyKeysUnderDefaultBuckets() {
        HashTable<Integer, Integer> table = new HashTable<>();
        for (int i = 0; i < 10_000; i++) table.put(i, i);
        for (int i = 0; i < 10_000; i += 97) assertEquals(i, table.get(i));
        assertEquals(10_000, table.size());
    }

    @Test
    void setBuiltOnHashTable() {
        Set<String> set = new Set<>();
        set.add("Balme");
        set.add("CS");
        assertTrue(set.contains("Balme"));
        assertFalse(set.contains("UGMC"));
        assertTrue(set.remove("Balme"));
        assertFalse(set.contains("Balme"));
        assertEquals(1, set.size());
    }
}
