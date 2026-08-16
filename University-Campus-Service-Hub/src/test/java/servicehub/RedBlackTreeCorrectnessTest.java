package servicehub;

import servicehub.util.RBInvariantChecker;
import servicehub.ds.RedBlackTree;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Red-Black Tree Correctness")
class RedBlackTreeCorrectnessTest {

    private RedBlackTree<Integer> tree;

    @BeforeEach
    void setUp() { tree = new RedBlackTree<>(); }

    private void assertValidRBTree() {
        assertDoesNotThrow(() -> RBInvariantChecker.check(tree),
                "red-black invariants (root black / no red-red / equal black-height) must hold");
    }

    @Nested
    @DisplayName("Normal operations")
    class Normal {

        @Test
        @DisplayName("insert keeps BST order and RB invariants")
        void insertMaintainsInvariants() {
            int[] values = {10, 20, 30, 15, 25, 5, 1};
            for (int v : values) {
                tree.insert(v);
                assertValidRBTree();
            }
            List<Integer> expected = new ArrayList<>();
            for (int v : values) expected.add(v);
            Collections.sort(expected);
            assertEquals(expected, tree.inorder());
        }

        @Test
        @DisplayName("delete keeps BST order and RB invariants")
        void deleteMaintainsInvariants() {
            int[] values = {10, 20, 30, 40, 50, 25, 5, 15, 1, 60};
            for (int v : values) tree.insert(v);
            int[] toDelete = {25, 10, 60, 1};
            for (int v : toDelete) {
                tree.delete(v);
                assertValidRBTree();
            }
            assertFalse(tree.contains(25));
            assertFalse(tree.contains(10));
        }

        @Test
        @DisplayName("randomized insert/delete sequence maintains invariants throughout")
        void randomizedSequenceMaintainsInvariants() {
            Random random = new Random(1234); // fixed seed: reproducible
            TreeSet<Integer> reference = new TreeSet<>();
            for (int i = 0; i < 500; i++) {
                int v = random.nextInt(1000);
                if (random.nextDouble() < 0.65) {
                    tree.insert(v);
                    reference.add(v);
                } else {
                    tree.delete(v);
                    reference.remove(v);
                }
                if (i % 25 == 0) assertValidRBTree(); // check periodically for speed
            }
            assertValidRBTree();
            assertEquals(reference.size(), tree.size());
            assertEquals(new ArrayList<>(reference), tree.inorder());
        }
    }

    @Nested
    @DisplayName("Boundary conditions")
    class Boundary {

        @Test
        @DisplayName("empty tree is valid and reports size 0")
        void emptyTreeIsValid() {
            assertTrue(tree.isEmpty());
            assertEquals(0, tree.size());
            assertValidRBTree();
        }

        @Test
        @DisplayName("single insert leaves root black")
        void singleInsertRootIsBlack() {
            tree.insert(42);
            assertValidRBTree(); // checker itself asserts root is black
            assertEquals(1, tree.size());
        }

        @Test
        @DisplayName("ascending insert sequence forces rotations, invariants still hold")
        void ascendingInsertForcesRotation() {
            for (int i = 1; i <= 20; i++) {
                tree.insert(i);
                assertValidRBTree();
            }
        }

        @Test
        @DisplayName("descending insert sequence forces rotations, invariants still hold")
        void descendingInsertForcesRotation() {
            for (int i = 20; i >= 1; i--) {
                tree.insert(i);
                assertValidRBTree();
            }
        }

        @Test
        @DisplayName("deleting down to an empty tree keeps invariants valid at every step")
        void deleteToEmpty() {
            int[] values = {8, 4, 12, 2, 6, 10, 14, 1, 3, 5, 7};
            for (int v : values) tree.insert(v);
            for (int v : values) {
                tree.delete(v);
                assertValidRBTree();
            }
            assertTrue(tree.isEmpty());
        }

        @Test
        @DisplayName("deleting a value not present is a safe no-op")
        void deleteMissingValue() {
            tree.insert(5);
            tree.insert(10);
            assertDoesNotThrow(() -> tree.delete(999));
            assertEquals(2, tree.size());
        }

        @Test
        @DisplayName("inserting a duplicate does not change size")
        void insertDuplicate() {
            tree.insert(7);
            tree.insert(7);
            assertEquals(1, tree.size());
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
