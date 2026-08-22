package servicehub.ds;

/**
 * Generic Map implemented on top of a Binary Search Tree (key-value pairs).
 *
 * @param <K> key type (must implement Comparable)
 * @param <V> value type
 */
public class Map<K extends Comparable<K>, V> {

    private MapEntry<K, V> root;
    private int size;

    public Map() {
        root = null;
        size = 0;
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Null keys are not allowed");
        MapEntry<K, V> newEntry = new MapEntry<>(key, value);
        if (root == null) {
            root = newEntry;
            size++;
            return;
        }
        MapEntry<K, V> current = root;
        while (true) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                current.value = value; // update existing
                return;
            }
            if (cmp < 0) {
                if (current.left == null) {
                    current.left = newEntry;
                    size++;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = newEntry;
                    size++;
                    return;
                }
                current = current.right;
            }
        }
    }

    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("Null keys are not allowed");
        MapEntry<K, V> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("Null keys are not allowed");
        MapEntry<K, V> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return true;
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("Null keys are not allowed");
        V removed = get(key);
        root = removeRecursive(root, key);
        return removed;
    }

    private MapEntry<K, V> removeRecursive(MapEntry<K, V> node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeRecursive(node.left, key);
        } else if (cmp > 0) {
            node.right = removeRecursive(node.right, key);
        } else {
            size--;
            if (node.left == null && node.right == null) return null;
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            MapEntry<K, V> successor = findMin(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = removeRecursive(node.right, successor.key);
            size++;
        }
        return node;
    }

    private MapEntry<K, V> findMin(MapEntry<K, V> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public K findMin() {
        if (root == null) throw new IllegalStateException("Map is empty");
        return findMin(root).key;
    }

    public void inorder() {
        System.out.print("Map (inorder): ");
        inorder(root);
        System.out.println();
    }

    private void inorder(MapEntry<K, V> node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node + "  ");
            inorder(node.right);
        }
    }

    public int size() { return size; }
    public boolean isEmpty() { return root == null; }
    public void clear() { root = null; size = 0; }
}
