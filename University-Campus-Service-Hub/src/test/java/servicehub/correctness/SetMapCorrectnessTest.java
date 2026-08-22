package servicehub.correctness;

import servicehub.ds.Map;
import servicehub.ds.Set;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Set/Map Correctness")
class SetMapCorrectnessTest {

    @Nested
    @DisplayName("Set: normal operations")
    class SetNormal {

        @Test
        @DisplayName("add then contains finds every added element")
        void addThenContains() {
            Set<String> set = new Set<>();
            set.add("a");
            set.add("b");
            set.add("c");
            assertTrue(set.contains("a"));
            assertTrue(set.contains("b"));
            assertTrue(set.contains("c"));
            assertEquals(3, set.size());
        }

        @Test
        @DisplayName("remove deletes the element")
        void removeElement() {
            Set<String> set = new Set<>();
            set.add("x");
            set.add("y");
            set.remove("x");
            assertFalse(set.contains("x"));
            assertTrue(set.contains("y"));
            assertEquals(1, set.size());
        }
    }

    @Nested
    @DisplayName("Set: boundary conditions")
    class SetBoundary {

        @Test
        @DisplayName("a new set is empty")
        void newSetIsEmpty() {
            Set<String> set = new Set<>();
            assertTrue(set.isEmpty());
            assertEquals(0, set.size());
        }

        @Test
        @DisplayName("adding a duplicate does not increase size")
        void addDuplicate() {
            Set<String> set = new Set<>();
            set.add("dup");
            set.add("dup");
            assertEquals(1, set.size());
        }

        @Test
        @DisplayName("removing a missing element is a safe no-op")
        void removeMissing() {
            Set<String> set = new Set<>();
            set.add("present");
            assertDoesNotThrow(() -> set.remove("absent"));
            assertEquals(1, set.size());
        }
    }

    @Nested
    @DisplayName("Set: invalid input handling")
    class SetInvalidInput {

        @Test
        @DisplayName("add(null) throws")
        void addNull() {
            Set<String> set = new Set<>();
            assertThrows(IllegalArgumentException.class, () -> set.add(null));
        }

        @Test
        @DisplayName("contains(null) throws")
        void containsNull() {
            Set<String> set = new Set<>();
            assertThrows(IllegalArgumentException.class, () -> set.contains(null));
        }
    }

    @Nested
    @DisplayName("Map: normal operations")
    class MapNormal {

        @Test
        @DisplayName("put then get round-trips")
        void putThenGet() {
            Map<String, Integer> map = new Map<>();
            map.put("one", 1);
            map.put("two", 2);
            assertEquals(1, map.get("one"));
            assertEquals(2, map.get("two"));
            assertEquals(2, map.size());
        }

        @Test
        @DisplayName("put on an existing key updates the value without changing size")
        void updateExistingKey() {
            Map<String, Integer> map = new Map<>();
            map.put("k", 1);
            map.put("k", 99);
            assertEquals(99, map.get("k"));
            assertEquals(1, map.size());
        }
    }

    @Nested
    @DisplayName("Map: boundary conditions")
    class MapBoundary {

        @Test
        @DisplayName("a new map is empty")
        void newMapIsEmpty() {
            Map<String, Integer> map = new Map<>();
            assertTrue(map.isEmpty());
            assertEquals(0, map.size());
            // contains check for missing handled via containsKey returning false
            // get for missing should return null only if we later decide, but now throws for null only;
            // For missing non-null key, returns null
            assertNull(map.get("missing"));
        }

        @Test
        @DisplayName("removing the only entry makes the map empty again")
        void removeLastEntry() {
            Map<String, Integer> map = new Map<>();
            map.put("only", 1);
            map.remove("only");
            assertTrue(map.isEmpty());
        }
    }

    @Nested
    @DisplayName("Map: invalid input handling")
    class MapInvalidInput {

        @Test
        @DisplayName("put(null, value) throws")
        void putNullKey() {
            Map<String, Integer> map = new Map<>();
            assertThrows(IllegalArgumentException.class, () -> map.put(null, 1));
        }

        @Test
        @DisplayName("get(null) throws")
        void getNull() {
            Map<String, Integer> map = new Map<>();
            assertThrows(IllegalArgumentException.class, () -> map.get(null));
        }
    }
}
