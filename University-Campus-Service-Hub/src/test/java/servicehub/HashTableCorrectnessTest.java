package correctness;

import ds.HashTable;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Hash Table Correctness")
class HashTableCorrectnessTest {

    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() { table = new HashTable<>(); }

    /** A key whose hashCode() is fixed, so every instance collides in the same bucket. */
    static class ConstantHashKey {
        final String label;
        ConstantHashKey(String label) { this.label = label; }
        @Override public int hashCode() { return 42; }
        @Override public boolean equals(Object o) {
            return o instanceof ConstantHashKey && ((ConstantHashKey) o).label.equals(label);
        }
    }

    static class NegativeHashKey {
        final String label;
        NegativeHashKey(String label) { this.label = label; }
        @Override public int hashCode() { return Integer.MIN_VALUE; }
        @Override public boolean equals(Object o) {
            return o instanceof NegativeHashKey && ((NegativeHashKey) o).label.equals(label);
        }
    }

    @Nested
    @DisplayName("Normal operations")
    class Normal {

        @Test
        @DisplayName("put then get round-trips for multiple keys")
        void putThenGet() {
            table.put("apple", 1);
            table.put("banana", 2);
            table.put("cherry", 3);
            assertEquals(1, table.get("apple"));
            assertEquals(2, table.get("banana"));
            assertEquals(3, table.get("cherry"));
            assertEquals(3, table.size());
        }

        @Test
        @DisplayName("put on an existing key overwrites the value without changing size")
        void putOverwritesExistingKey() {
            table.put("key", 1);
            table.put("key", 2);
            assertEquals(2, table.get("key"));
            assertEquals(1, table.size());
        }

        @Test
        @DisplayName("remove deletes the key and updates containsKey/size")
        void removeExistingKey() {
            table.put("a", 1);
            table.put("b", 2);
            table.remove("a");
            assertFalse(table.containsKey("a"));
            assertNull(table.get("a"));
            assertTrue(table.containsKey("b"));
            assertEquals(1, table.size());
        }
    }

    @Nested
    @DisplayName("Boundary conditions")
    class Boundary {

        @Test
        @DisplayName("a new table is empty")
        void newTableIsEmpty() {
            assertTrue(table.isEmpty());
            assertEquals(0, table.size());
            assertNull(table.get("missing"));
            assertFalse(table.containsKey("missing"));
        }

        @Test
        @DisplayName("removing the last element makes the table empty again")
        void removeLastElement() {
            table.put("only", 1);
            table.remove("only");
            assertTrue(table.isEmpty());
        }

        @Test
        @DisplayName("removing a key that isn't present is a safe no-op")
        void removeMissingKey() {
            table.put("a", 1);
            assertDoesNotThrow(() -> table.remove("nonexistent"));
            assertEquals(1, table.size());
        }

        @Test
        @DisplayName("colliding keys (same hash bucket) are all still retrievable")
        void collisionHandling() {
            HashTable<ConstantHashKey, String> t = new HashTable<>();
            ConstantHashKey k1 = new ConstantHashKey("k1");
            ConstantHashKey k2 = new ConstantHashKey("k2");
            ConstantHashKey k3 = new ConstantHashKey("k3");
            t.put(k1, "v1");
            t.put(k2, "v2");
            t.put(k3, "v3");
            assertEquals("v1", t.get(k1));
            assertEquals("v2", t.get(k2));
            assertEquals("v3", t.get(k3));
            assertEquals(3, t.size());
        }

        @Test
        @DisplayName("a negative hashCode() does not crash bucket indexing")
        void negativeHashCodeHandled() {
            HashTable<NegativeHashKey, String> t = new HashTable<>();
            NegativeHashKey key = new NegativeHashKey("k");
            assertDoesNotThrow(() -> t.put(key, "value"));
            assertEquals("value", t.get(key));
        }

        @Test
        @DisplayName("inserting past the load-factor threshold triggers a resize; all keys survive")
        void resizePreservesAllKeys() {
            for (int i = 0; i < 200; i++) table.put("key" + i, i);
            for (int i = 0; i < 200; i++) assertEquals(i, table.get("key" + i));
            assertEquals(200, table.size());
        }
    }

    @Nested
    @DisplayName("Invalid input handling")
    class InvalidInput {

        @Test
        @DisplayName("put(null, value) throws")
        void putNullKey() {
            assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
        }

        @Test
        @DisplayName("get(null) throws")
        void getNullKey() {
            assertThrows(IllegalArgumentException.class, () -> table.get(null));
        }

        @Test
        @DisplayName("remove(null) throws")
        void removeNullKey() {
            assertThrows(IllegalArgumentException.class, () -> table.remove(null));
        }

        @Test
        @DisplayName("containsKey(null) throws")
        void containsNullKey() {
            assertThrows(IllegalArgumentException.class, () -> table.containsKey(null));
        }
    }
}
