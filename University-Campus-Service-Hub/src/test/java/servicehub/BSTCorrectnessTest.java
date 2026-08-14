package correctness;

import ds.BST;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BST Correctness")
class BSTCorrectnessTest {

    private BST<Integer> tree;

    @BeforeEach
    void setUp() { tree = new BST<>(); }

    @Nested
    @DisplayName("Normal operations")
    class Normal {

        @Test
        @DisplayName("insert then contains finds every inserted value")
        void insertThenContains() {
            int[] values = {50, 30, 70, 20, 40, 60, 80};
            for (int v : values) tree.insert(v);
            for (int v : values) assertTrue(tree.contains(v), "expected tree to contain " + v);
            assertEquals(values.length, tree.size());
        }

        @Test
        @DisplayName("inorder traversal is sorted")
        void inorderIsSorted() {
            int[] values = {50, 30, 70, 20, 40, 60, 80, 10, 25};
            for (int v : values) tree.insert(v);
            List<Integer> expected = new ArrayList<>();
            for (int v : values) expected.add(v);
            Collections.sort(expected);
            assertEquals(expected, tree.inorder());
        }

        @Test
        @DisplayName("deleting a leaf node removes only that value")
        void deleteLeaf() {
            for (int v : new int[]{50, 30, 70}) tree.insert(v);
            tree.delete(30);
            assertFalse(tree.contains(30));
            assertTrue(tree.contains(50));
            assertTrue(tree.contains(70));
            assertEquals(2, tree.size());
        }

        @Test
        @DisplayName("deleting a node with one child re-links correctly")
        void deleteNodeWithOneChild() {
            for (int v : new int[]{50, 30, 20}) tree.insert(v); // 30 has only a left child
            tree.delete(30);
            assertFalse(tree.contains(30));
            assertTrue(tree.contains(20));
            assertEquals(List.of(20, 50), tree.inorder());
        }

        @Test
        @DisplayName("deleting a node with two children preserves sorted order")
        void deleteNodeWithTwoChildren() {
            int[] values = {50, 30, 70, 20, 40, 60, 80};
            for (int v : values) tree.insert(v);
            tree.delete(30); // has two children: 20 and 40
            assertFalse(tree.contains(30));
            List<Integer> expected = new ArrayList<>();
            for (int v : values) if (v != 30) expected.add(v);
            Collections.sort(expected);
            assertEquals(expected, tree.inorder());
            assertEquals(expected.size(), tree.size());
        }
    }

    @Nested
    @DisplayName("Boundary conditions")
    class Boundary {

        @Test
        @DisplayName("a new tree is empty")
        void newTreeIsEmpty() {
            assertTrue(tree.isEmpty());
            assertEquals(0, tree.size());
            assertFalse(tree.contains(1));
        }

        @Test
        @DisplayName("delete on an empty tree is a safe no-op")
        void deleteOnEmptyTree() {
            assertDoesNotThrow(() -> tree.delete(42));
            assertTrue(tree.isEmpty());
        }

        @Test
        @DisplayName("single-node tree: insert then delete returns to empty")
        void singleNodeLifecycle() {
            tree.insert(5);
            assertEquals(1, tree.size());
            tree.delete(5);
            assertTrue(tree.isEmpty());
            assertFalse(tree.contains(5));
        }

        @Test
        @DisplayName("inserting a duplicate does not change size")
        void insertDuplicate() {
            tree.insert(10);
            tree.insert(10);
            assertEquals(1, tree.size());
        }

        @Test
        @DisplayName("deleting a value not present leaves the tree unchanged")
        void deleteMissingValue() {
            tree.insert(10);
            tree.insert(20);
            tree.delete(999);
            assertEquals(2, tree.size());
            assertTrue(tree.contains(10));
            assertTrue(tree.contains(20));
        }

        @Test
        @DisplayName("handles Integer.MIN_VALUE and MAX_VALUE")
        void extremeValues() {
            tree.insert(Integer.MIN_VALUE);
            tree.insert(Integer.MAX_VALUE);
            tree.insert(0);
            assertTrue(tree.contains(Integer.MIN_VALUE));
            assertTrue(tree.contains(Integer.MAX_VALUE));
            assertEquals(List.of(Integer.MIN_VALUE, 0, Integer.MAX_VALUE), tree.inorder());
        }

        @Test
        @DisplayName("a fully skewed (sorted-order insert) tree stays correct")
        void skewedInsertStress() {
            for (int i = 0; i < 1000; i++) tree.insert(i);
            assertEquals(1000, tree.size());
            for (int i = 0; i < 1000; i += 137) assertTrue(tree.contains(i));
            for (int i = 0; i < 1000; i += 2) tree.delete(i);
            assertEquals(500, tree.size());
            List<Integer> expected = new ArrayList<>();
            for (int i = 1; i < 1000; i += 2) expected.add(i);
            assertEquals(expected, tree.inorder());
        }
    }

    @Nested
    @DisplayName("Invalid input handling")
    class InvalidInput {

        @Test
        @DisplayName("insert(null) throws")
        void insertNull() {
            assertThrows(IllegalArgumentException.class, () -> tree.insert(null));
        }

        @Test
        @DisplayName("contains(null) throws")
        void containsNull() {
            assertThrows(IllegalArgumentException.class, () -> tree.contains(null));
        }

        @Test
        @DisplayName("delete(null) throws")
        void deleteNull() {
            assertThrows(IllegalArgumentException.class, () -> tree.delete(null));
        }
    }
}
