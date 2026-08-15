package servicehub.ds;

/**
 * Parameterized Binary Search Tree (BST) supporting key-value lookup and tree traversals.
 *
 * @param <K> key type (must implement Comparable)
 * @param <V> value type
 */
public class BinarySearchTree<K extends Comparable<K>, V> {

    private Node<K, V> root;
    private int size;

    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    public void insert(K key) {
        insert(key, null);
    }

    public void insert(K key, V value) {
        if (key == null) return;
        Node<K, V> newNode = new Node<>(key, value);
        if (root == null) {
            root = newNode;
            size++;
            return;
        }

        Node<K, V> current = root;
        while (true) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                if (current.left == null) {
                    current.left = newNode;
                    size++;
                    return;
                }
                current = current.left;
            } else if (cmp > 0) {
                if (current.right == null) {
                    current.right = newNode;
                    size++;
                    return;
                }
                current = current.right;
            } else {
                current.value = value; // Update value if key exists
                return;
            }
        }
    }

    public boolean search(K key) {
        return get(key) != null || containsKey(key);
    }

    public boolean containsKey(K key) {
        if (key == null) return false;
        Node<K, V> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return true;
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    public V get(K key) {
        if (key == null) return null;
        Node<K, V> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    public void delete(K key) {
        if (key == null) return;
        root = deleteRecursive(root, key);
    }

    private Node<K, V> deleteRecursive(Node<K, V> node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = deleteRecursive(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteRecursive(node.right, key);
        } else {
            size--;
            if (node.left == null && node.right == null) return null;
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            Node<K, V> successor = findMinNode(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = deleteRecursive(node.right, successor.key);
            size++; // Offset decrease in recursive call
        }
        return node;
    }

    private Node<K, V> findMinNode(Node<K, V> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public K findMin() {
        if (root == null) throw new IllegalStateException("Tree is empty");
        return findMinNode(root).key;
    }

    public K findMax() {
        if (root == null) throw new IllegalStateException("Tree is empty");
        Node<K, V> current = root;
        while (current.right != null) current = current.right;
        return current.key;
    }

    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node<K, V> node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node + " ");
            inorder(node.right);
        }
    }

    public void preorder() {
        preorder(root);
        System.out.println();
    }

    private void preorder(Node<K, V> node) {
        if (node != null) {
            System.out.print(node + " ");
            preorder(node.left);
            preorder(node.right);
        }
    }

    public void postorder() {
        postorder(root);
        System.out.println();
    }

    private void postorder(Node<K, V> node) {
        if (node != null) {
            postorder(node.left);
            postorder(node.right);
            System.out.print(node + " ");
        }
    }

    public int height() {
        return height(root);
    }

    private int height(Node<K, V> node) {
        if (node == null) return -1;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    public int size() { return size; }
    public boolean isEmpty() { return root == null; }
    public void clear() { root = null; size = 0; }
}
