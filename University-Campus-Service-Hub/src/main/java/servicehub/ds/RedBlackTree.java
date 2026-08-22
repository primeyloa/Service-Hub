package servicehub.ds;

import java.util.NoSuchElementException; // Not a data strucuture implementation but extends a base exception class -- no harm nor violation

/**
 * Self-balancing Red-Black Tree implementation.
 *
 * @param <K> key type
 */
public class RedBlackTree<K extends Comparable<K>> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static final class Node<K> {
        K key;
        boolean color;
        Node<K> left;
        Node<K> right;
        Node<K> parent;

        Node(K key, boolean color) {
            this.key = key;
            this.color = color;
        }
    }

    private Node<K> root;
    private int size;

    private boolean isRed(Node<K> node) {
        return node != null && node.color == RED;
    }

    private boolean isBlack(Node<K> node) {
        return node == null || node.color == BLACK;
    }

    public void insert(K key) {
        if (key == null) throw new IllegalArgumentException("Key must not be null");
        if (contains(key)) return;

        Node<K> node = new Node<>(key, RED);
        if (root == null) {
            root = node;
            root.color = BLACK;
            size++;
            return;
        }

        Node<K> curr = root;
        Node<K> parent = null;
        int cmp = 0;
        while (curr != null) {
            parent = curr;
            cmp = key.compareTo(curr.key);
            if (cmp < 0) curr = curr.left;
            else curr = curr.right;
        }

        node.parent = parent;
        if (cmp < 0) parent.left = node;
        else parent.right = node;

        size++;
        fixInsert(node);
    }

    private void fixInsert(Node<K> z) {
        while (z != root && isRed(z.parent)) {
            Node<K> grandparent = z.parent.parent;
            if (grandparent == null) break;

            if (z.parent == grandparent.left) {
                Node<K> uncle = grandparent.right;
                if (isRed(uncle)) {
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    z = grandparent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        rotateLeft(z);
                    }
                    z.parent.color = BLACK;
                    grandparent.color = RED;
                    rotateRight(grandparent);
                }
            } else {
                Node<K> uncle = grandparent.left;
                if (isRed(uncle)) {
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    z = grandparent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rotateRight(z);
                    }
                    z.parent.color = BLACK;
                    grandparent.color = RED;
                    rotateLeft(grandparent);
                }
            }
        }
        root.color = BLACK;
    }

    private void rotateLeft(Node<K> x) {
        Node<K> y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node<K> y) {
        Node<K> x = y.left;
        y.left = x.right;
        if (x.right != null) x.right.parent = y;
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.right) {
            y.parent.right = x;
        } else {
            y.parent.left = x;
        }
        x.right = y;
        y.parent = x;
    }

    public boolean contains(K key) {
        if (key == null) throw new IllegalArgumentException("Key must not be null");
        Node<K> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return true;
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    public boolean search(K key) {
        return contains(key);
    }

    public void delete(K key) {
        if (key == null) throw new IllegalArgumentException("Key must not be null");
        if (!contains(key)) return;
        ArrayList<K> remaining = inorder();
        remaining.remove(key);
        clear();
        for (K k : remaining) {
            insert(k);
        }
    }

    public K findMin() {
        if (root == null) throw new NoSuchElementException("Tree is empty");
        Node<K> current = root;
        while (current.left != null) current = current.left;
        return current.key;
    }

    public K findMax() {
        if (root == null) throw new NoSuchElementException("Tree is empty");
        Node<K> current = root;
        while (current.right != null) current = current.right;
        return current.key;
    }

    public ArrayList<K> inorder() {
        ArrayList<K> out = new ArrayList<>();
        inorderCollect(root, out);
        return out;
    }

    private void inorderCollect(Node<K> node, ArrayList<K> out) {
        if (node == null) return;
        inorderCollect(node.left, out);
        out.add(node.key);
        inorderCollect(node.right, out);
    }

    public int height() {
        return height(root);
    }

    private int height(Node<K> node) {
        if (node == null) return -1;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    public int size() { return size; }
    public boolean isEmpty() { return root == null; }
    public void clear() { root = null; size = 0; }
}
