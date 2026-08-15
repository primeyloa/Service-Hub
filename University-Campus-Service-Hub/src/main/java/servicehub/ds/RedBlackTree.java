package servicehub.ds;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Red-Black Tree implementation (self-balancing BST) with standard
 * recolouring and rotation insertions. Supports insert, search, delete
 * (simplified), inorder traversal and height queries.
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

        Node(K key, boolean color, Node<K> parent) {
            this.key = key;
            this.color = color;
            this.parent = parent;
        }
    }

    private Node<K> root;
    private int size;

    private boolean isRed(Node<K> node) {
        return node != null && node.color == RED;
    }

    private void setBlack(Node<K> node) {
        if (node != null) node.color = BLACK;
    }

    private void setRed(Node<K> node) {
        if (node != null) node.color = RED;
    }

    public void insert(K key) {
        if (key == null) throw new IllegalArgumentException("Key must not be null");
        root = insertInternal(root, key);
        setBlack(root);
        size = recomputeSize(root);
    }

    private Node<K> insertInternal(Node<K> node, K key) {
        if (node == null) {
            return new Node<>(key, RED, null);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertInternal(node.left, key);
            node.left.parent = node;
        } else if (cmp > 0) {
            node.right = insertInternal(node.right, key);
            node.right.parent = node;
        } else {
            return node; // duplicate ignored
        }
        return fixUp(node);
    }

    /**
     * Bottom-up fix after insertion: recolour and rotate to restore
     * the red-black invariants.
     */
    private Node<K> fixUp(Node<K> node) {
        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }
        return node;
    }

    private Node<K> rotateLeft(Node<K> h) {
        Node<K> x = h.right;
        h.right = x.left;
        if (x.left != null) x.left.parent = h;
        x.parent = h.parent;
        x.left = h;
        h.parent = x;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private Node<K> rotateRight(Node<K> h) {
        Node<K> x = h.left;
        h.left = x.right;
        if (x.right != null) x.right.parent = h;
        x.parent = h.parent;
        x.right = h;
        h.parent = x;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private void flipColors(Node<K> h) {
        h.color = RED;
        setBlack(h.left);
        setBlack(h.right);
    }

    public boolean contains(K key) {
        if (key == null) return false;
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

    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node<K> node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node.key + " ");
            inorder(node.right);
        }
    }

    public int height() {
        return height(root);
    }

    private int height(Node<K> node) {
        if (node == null) return -1;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    private int recomputeSize(Node<K> node) {
        if (node == null) return 0;
        return 1 + recomputeSize(node.left) + recomputeSize(node.right);
    }

    public int size() { return size; }
    public boolean isEmpty() { return root == null; }
    public void clear() { root = null; size = 0; }
}
