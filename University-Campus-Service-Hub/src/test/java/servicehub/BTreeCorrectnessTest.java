package servicehub;

import servicehub.util.BTreeInvariantChecker;
import servicehub.ds.BTree;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("B-Tree Correctness")
class BTreeCorrectnessTest {

    private void assertValid(BTree<?> tree) {
        assertDoesNotThrow(() -> BTreeInvariantChecker.check(tree),
                "B-tree invariants (key-count bounds, sortedness, equal leaf depth) must hold");
    }

    @Nested
    @DisplayName("Normal operations")
    class Normal {

        @Test
        @DisplayName("insert enough keys to force a node split, then search finds everything")
        void insertForcesSplit() {
            BTree<Integer> tree = new BTree<>(2); // t=2: node overflows after 3 keys
            int[] values = {10, 20, 5, 6, 12, 30, 7, 17};
            for (int v : values) tree.insert(v);
            for (int v : values) assertTrue(tree.contains(v));
            assertEquals(values.length, tree.size());
            assertValid(tree);
        }

        @Test
        @DisplayName("delete causing merge/borrow keeps the tree valid")
        void deleteCausesMergeOrBorrow() {
            BTree<Integer> tree = new BTree<>(2);
            int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            for (int v : values) tree.insert(v);
            for (int v : new int[]{6, 5, 12, 1}) {
                tree.delete(v);
                assertValid(tree);
            }
            assertFalse(tree.contains(6));
            assertFalse(tree.contains(5));
            assertTrue(tree.contains(2));
        }

        @Test
        @DisplayName("inorder traversal matches sorted inserted values")
        void inorderIsSorted() {
            BTree<Integer> tree = new BTree<>(3);
            List<Integer> values = new ArrayList<>();
            Random random = new Random(99);
            for (int i = 0; i < 200; i++) values.add(random.nextInt(1000));
            for (int v : values) tree.insert(v);
            List<Integer> expected = new ArrayList<>(new TreeSet<>(values));
            assertEquals(expected, tree.inorder());
        }
    }

    @Nested
    @DisplayName("Boundary conditions")
    class Boundary {

        @Test
        @DisplayName("empty tree reports size 0 and contains nothing")
        void emptyTree() {
            BTree<Integer> tree = new BTree<>(2);
            assertTrue(tree.isEmpty());
            assertEquals(0, tree.size());
            assertFalse(tree.contains(1));
            assertValid(tree);
        }

        @Test
        @DisplayName("root splits exactly when key count exceeds 2t-1")
        void rootSplitsAtCapacity() {
            BTree<Integer> tree = new BTree<>(2); // max 3 keys per node before split
            tree.insert(1);
            tree.insert(2);
            tree.insert(3); // root now full (2t-1 = 3 keys), still one node
            assertValid(tree);
            tree.insert(4); // forces the root to split
            assertValid(tree);
            assertEquals(4, tree.size());
        }

        @Test
        @DisplayName("minimum useful degree t=2 (2-3-4 tree) behaves correctly")
        void minimumDegreeTwo() {
            BTree<Integer> tree = new BTree<>(2);
            for (int i = 1; i <= 50; i++) tree.insert(i);
            assertValid(tree);
            for (int i = 1; i <= 25; i++) tree.delete(i);
            assertValid(tree);
            assertEquals(25, tree.size());
        }

        @Test
        @DisplayName("deleting down to an empty tree keeps invariants valid at every step")
        void deleteToEmpty() {
            BTree<Integer> tree = new BTree<>(2);
            int[] values = {5, 3, 8, 1, 4, 7, 9, 2, 6};
            for (int v : values) tree.insert(v);
            for (int v : values) {
                tree.delete(v);
                assertValid(tree);
            }
            assertTrue(tree.isEmpty());
        }

        @Test
        @DisplayName("deleting a value not present is a safe no-op")
        void deleteMissingValue() {
            BTree<Integer> tree = new BTree<>(2);
            tree.insert(10);
            tree.insert(20);
            assertDoesNotThrow(() -> tree.delete(999));
            assertEquals(2, tree.size());
        }

        @Test
        @DisplayName("inserting a duplicate does not change size")
        void insertDuplicate() {
            BTree<Integer> tree = new BTree<>(2);
            tree.insert(5);
            tree.insert(5);
            assertEquals(1, tree.size());
        }
    }

    @Nested
    @DisplayName("Invalid input handling")
    class InvalidInput {

        @Test
        @DisplayName("constructing with degree < 2 throws")
        void invalidDegreeThrows() {
            assertThrows(IllegalArgumentException.class, () -> new BTree<Integer>(1));
            assertThrows(IllegalArgumentException.class, () -> new BTree<Integer>(0));
            assertThrows(IllegalArgumentException.class, () -> new BTree<Integer>(-3));
        }

        @Test
        @DisplayName("insert(null) throws")
        void insertNull() {
            BTree<Integer> tree = new BTree<>(2);
            assertThrows(IllegalArgumentException.class, () -> tree.insert(null));
        }

        @Test
        @DisplayName("search/contains(null) throws")
        void searchNull() {
            BTree<Integer> tree = new BTree<>(2);
            assertThrows(IllegalArgumentException.class, () -> tree.contains(null));
            assertThrows(IllegalArgumentException.class, () -> tree.search(null));
        }

        @Test
        @DisplayName("delete(null) throws")
        void deleteNull() {
            BTree<Integer> tree = new BTree<>(2);
            assertThrows(IllegalArgumentException.class, () -> tree.delete(null));
        }
    }
}
