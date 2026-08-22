package servicehub.correctness;

import servicehub.ds.BTree;

public class BTreeTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("B-TREE TESTS AND EXECUTION TRACE LOGS");
        System.out.println("==================================================");

        testEmptyTreeSearch();
        testRootSplitAndInsertion();
        testChildNodeSplit();
        testSearchExistingAndNonExisting();
        testMultiSplitAndLargeDataset();

        System.out.println("\nAll tests completed successfully!");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Test FAILED: " + message);
        }
        System.out.println("[PASS] " + message);
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void testEmptyTreeSearch() {
        System.out.println("\n--- Test Case 1: Search in Empty Tree ---");
        BTree<Integer> tree = new BTree<>(2);
        boolean found = tree.search(10);
        assertFalse(found, "Key 10 should not be found in empty tree.");
    }

    private static void testRootSplitAndInsertion() {
        System.out.println("\n--- Test Case 2: Root Split on 4th Insertion (T = 2) ---");
        BTree<Integer> tree = new BTree<>(2);
        System.out.println("Inserting 10, 20, 30 (Node is now full: [10, 20, 30])");
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        System.out.println("Inserting 40 (Triggers Root Split)");
        tree.insert(40);

        assertTrue(tree.size() == 4, "Tree should have 4 keys after 4 insertions");
        assertTrue(tree.contains(20), "Tree should contain 20");
        assertTrue(tree.contains(10), "Tree should contain 10");
        assertTrue(tree.contains(30), "Tree should contain 30");
        assertTrue(tree.contains(40), "Tree should contain 40");
    }

    private static void testChildNodeSplit() {
        System.out.println("\n--- Test Case 3: Child Node Split ---");
        BTree<Integer> tree = new BTree<>(2);
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);

        System.out.println("Inserting 50 (Right child [30, 40] becomes [30, 40, 50] - not yet split)");
        tree.insert(50);
        assertTrue(tree.size() == 5, "Tree should have 5 keys");
        assertTrue(tree.contains(50), "Should contain 50");

        System.out.println("Inserting 60 (Right child is full, triggers child split)");
        tree.insert(60);
        assertTrue(tree.size() == 6, "Tree should have 6 keys");
        assertTrue(tree.contains(60), "Should contain 60");
        assertTrue(tree.contains(40), "Should contain 40");
    }

    private static void testSearchExistingAndNonExisting() {
        System.out.println("\n--- Test Case 4: Search Tracing for Existing & Non-Existing Keys ---");
        BTree<Integer> tree = new BTree<>(2);
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(60);

        boolean foundExisting = tree.search(50);
        assertTrue(foundExisting, "Key 50 should be found.");

        boolean foundNonExisting = tree.search(25);
        assertFalse(foundNonExisting, "Key 25 should not be found.");
    }

    private static void testMultiSplitAndLargeDataset() {
        System.out.println("\n--- Test Case 5: Large Insertion Sequence ---");
        BTree<Integer> tree = new BTree<>(2);
        int[] keysToInsert = {15, 5, 27, 3, 12, 18, 22, 35, 8, 9, 10, 11};

        for (int key : keysToInsert) {
            tree.insert(key);
        }

        System.out.println("Verifying insertion of all keys:");
        for (int key : keysToInsert) {
            assertTrue(tree.search(key), "Key " + key + " should be successfully found.");
        }
    }
}
