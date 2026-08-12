/**
 * Custom B-Tree implementation with minimum degree T = 2 (Order 3).
 * Core B-Tree storage and logic is built from scratch without using Java Collections.
 * Tracks node-splits and search comparisons.
 */
public class BTree {
    private static final int T = 2; // Minimum degree T = 2 (Max keys = 3, Max children = 4)
    private static final int MAX_KEYS = 2 * T - 1; // 3
    private static final int MAX_CHILDREN = 2 * T; // 4

    private BTreeNode root;
    private final SimpleList<String> splitTrace;
    private final SimpleList<String> searchTrace;

    public static class BTreeNode {
        public int[] keys;
        public BTreeNode[] children;
        public int keyCount;
        public boolean isLeaf;

        public BTreeNode(boolean isLeaf) {
            this.keys = new int[MAX_KEYS];
            this.children = new BTreeNode[MAX_CHILDREN];
            this.keyCount = 0;
            this.isLeaf = isLeaf;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < keyCount; i++) {
                sb.append(keys[i]);
                if (i < keyCount - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public BTree() {
        this.root = new BTreeNode(true);
        this.splitTrace = new SimpleList<>();
        this.searchTrace = new SimpleList<>();
    }

    public BTreeNode getRoot() {
        return root;
    }

    public SimpleList<String> getSplitTrace() {
        return splitTrace;
    }

    public SimpleList<String> getSearchTrace() {
        return searchTrace;
    }

    public void clearTraces() {
        splitTrace.clear();
        searchTrace.clear();
    }

    /**
     * Searches for a key in the B-Tree.
     * Logs every comparison to the searchTrace.
     */
    public boolean search(int key) {
        return search(root, key);
    }

    private boolean search(BTreeNode node, int key) {
        if (node == null) {
            searchTrace.add(String.format("Reached null node. Key %d not found.", key));
            return false;
        }

        String nodeStr = node.toString();
        searchTrace.add(String.format("Searching in node %s for key %d", nodeStr, key));

        int i = 0;
        while (i < node.keyCount) {
            int currentKey = node.keys[i];
            searchTrace.add(String.format("  Comparing key %d with node key %d at index %d", key, currentKey, i));
            if (key == currentKey) {
                searchTrace.add(String.format("  Key %d found at index %d in node %s", key, i, nodeStr));
                return true;
            } else if (key < currentKey) {
                searchTrace.add(String.format("  Key %d < %d. Stopping linear scan of this node.", key, currentKey));
                break;
            }
            i++;
        }

        if (node.isLeaf) {
            searchTrace.add(String.format("  Node %s is a leaf. Key %d not found in tree.", nodeStr, key));
            return false;
        }

        searchTrace.add(String.format("  Descending to child index %d", i));
        return search(node.children[i], key);
    }

    /**
     * Inserts a key into the B-Tree.
     */
    public void insert(int key) {
        BTreeNode r = root;
        if (r.keyCount == MAX_KEYS) {
            splitTrace.add(String.format("Root %s is full. Performing root split.", r.toString()));
            BTreeNode s = new BTreeNode(false);
            root = s;
            s.children[0] = r;
            splitChild(s, 0, r);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    /**
     * Splits the child y of node x at index i.
     */
    private void splitChild(BTreeNode x, int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.isLeaf);
        // z gets the last T - 1 keys of y (which is 1 key: index 2 of y)
        z.keyCount = T - 1;
        z.keys[0] = y.keys[2];

        // If not leaf, z gets the last T children of y (index 2 and 3 of y)
        if (!y.isLeaf) {
            for (int j = 0; j < T; j++) {
                z.children[j] = y.children[j + T];
                y.children[j + T] = null;
            }
        }

        // y's key count is reduced to T - 1 (which is 1)
        y.keyCount = T - 1;

        // Shift parent children to make room for z
        for (int j = x.keyCount; j >= i + 1; j--) {
            x.children[j + 1] = x.children[j];
        }
        x.children[i + 1] = z;

        // Shift parent keys to make room for y's middle key (index 1)
        for (int j = x.keyCount - 1; j >= i; j--) {
            x.keys[j + 1] = x.keys[j];
        }
        x.keys[i] = y.keys[1];
        x.keyCount++;

        splitTrace.add(String.format(
            "Split child node %s. Middle key %d promoted to parent. Parent becomes %s. Left node: %s, Right node: %s",
            y.toString() + " + promoted key: " + y.keys[1], y.keys[1], x.toString(), y.toString(), z.toString()
        ));
    }

    /**
     * Inserts a key into a non-full BTreeNode.
     */
    private void insertNonFull(BTreeNode x, int key) {
        int i = x.keyCount - 1;

        if (x.isLeaf) {
            // Find position to insert and shift keys
            while (i >= 0 && key < x.keys[i]) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i + 1] = key;
            x.keyCount++;
        } else {
            // Find child to descend into
            while (i >= 0 && key < x.keys[i]) {
                i--;
            }
            i++;
            BTreeNode child = x.children[i];
            if (child.keyCount == MAX_KEYS) {
                splitTrace.add(String.format("Child node at index %d: %s is full. Splitting.", i, child.toString()));
                splitChild(x, i, child);
                if (key > x.keys[i]) {
                    i++;
                }
            }
            insertNonFull(x.children[i], key);
        }
    }
}
