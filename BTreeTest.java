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
        BTree tree = new BTree();
        tree.clearTraces();

        boolean found = tree.search(10);
        assertFalse(found, "Key 10 should not be found in empty tree.");

        System.out.println("--- Search Traces for Empty Tree Search ---");
        for (String log : tree.getSearchTrace()) {
            System.out.println("  " + log);
        }
    }

    private static void testRootSplitAndInsertion() {
        System.out.println("\n--- Test Case 2: Root Split on 4th Insertion (T = 2) ---");
        BTree tree = new BTree();
        tree.clearTraces();

        System.out.println("Inserting 10, 20, 30 (Node is now full: [10, 20, 30])");
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        System.out.println("Inserting 40 (Triggers Root Split)");
        tree.insert(40);

        System.out.println("--- Split Traces for Root Split ---");
        for (String log : tree.getSplitTrace()) {
            System.out.println("  " + log);
        }

        // Verify root contains the promoted key
        BTree.BTreeNode root = tree.getRoot();
        assertTrue(root.keyCount == 1, "Root should have exactly 1 key after split");
        assertTrue(root.keys[0] == 20, "Promoted key in root should be 20");
        assertTrue(!root.isLeaf, "Root should no longer be a leaf node");
        assertTrue(root.children[0].keys[0] == 10, "Left child should have key 10");
        assertTrue(root.children[1].keys[0] == 30, "Right child should have keys 30 and 40");
        assertTrue(root.children[1].keys[1] == 40, "Right child should have keys 30 and 40");
    }

    private static void testChildNodeSplit() {
        System.out.println("\n--- Test Case 3: Child Node Split ---");
        BTree tree = new BTree();
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40); // Root split occurred

        tree.clearTraces();
        System.out.println("Current root children: Left=" + tree.getRoot().children[0] + ", Right=" + tree.getRoot().children[1]);
        System.out.println("Inserting 50 (Right child [30, 40] becomes [30, 40, 50] - not yet split)");
        tree.insert(50);
        assertTrue(tree.getRoot().children[1].keyCount == 3, "Right child should have 3 keys now");

        System.out.println("Inserting 60 (Right child is full, triggers child split)");
        tree.insert(60);

        System.out.println("--- Split Traces for Child Split ---");
        for (String log : tree.getSplitTrace()) {
            System.out.println("  " + log);
        }

        BTree.BTreeNode root = tree.getRoot();
        assertTrue(root.keyCount == 2, "Root should have 2 keys now (20 and 40)");
        assertTrue(root.keys[0] == 20 && root.keys[1] == 40, "Root keys should be [20, 40]");
    }

    private static void testSearchExistingAndNonExisting() {
        System.out.println("\n--- Test Case 4: Search Tracing for Existing & Non-Existing Keys ---");
        BTree tree = new BTree();
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(60);

        // Search for existing key
        tree.clearTraces();
        boolean foundExisting = tree.search(50);
        assertTrue(foundExisting, "Key 50 should be found.");
        System.out.println("--- Search Traces for Key 50 (Existing) ---");
        for (String log : tree.getSearchTrace()) {
            System.out.println("  " + log);
        }

        // Search for non-existing key
        tree.clearTraces();
        boolean foundNonExisting = tree.search(25);
        assertFalse(foundNonExisting, "Key 25 should not be found.");
        System.out.println("--- Search Traces for Key 25 (Non-Existing) ---");
        for (String log : tree.getSearchTrace()) {
            System.out.println("  " + log);
        }
    }

    private static void testMultiSplitAndLargeDataset() {
        System.out.println("\n--- Test Case 5: Large Insertion Sequence ---");
        BTree tree = new BTree();
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
