package ds;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference / placeholder Binary Search Tree.
 *
 * REPLACE THIS FILE with your own BST implementation, as long as the
 * class name, package, and method signatures below stay the same
 * (or you update BSTCorrectnessTest.java to match your real signatures).
 *
 * Assumed policy (adjust the test file if yours differs):
 *  - insert(null) throws IllegalArgumentException
 *  - inserting a duplicate value is a no-op (size does not change)
 *  - delete(null) throws IllegalArgumentException
 *  - deleting a value not present is a no-op (no exception)
 */
public class BST<T extends Comparable<T>> {

    private static class Node<T> {
        T key;
        Node<T> left, right;
        Node(T key) { this.key = key; }
    }

    private Node<T> root;
    private int size = 0;

    public void insert(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        root = insert(root, value);
    }

    private Node<T> insert(Node<T> node, T value) {
        if (node == null) {
            size++;
            return new Node<>(value);
        }
        int cmp = value.compareTo(node.key);
        if (cmp < 0) node.left = insert(node.left, value);
        else if (cmp > 0) node.right = insert(node.right, value);
        // cmp == 0: duplicate, ignore
        return node;
    }

    public boolean contains(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        Node<T> cur = root;
        while (cur != null) {
            int cmp = value.compareTo(cur.key);
            if (cmp == 0) return true;
            cur = cmp < 0 ? cur.left : cur.right;
        }
        return false;
    }

    public void delete(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        root = delete(root, value);
    }

    private Node<T> delete(Node<T> node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, value);
        } else if (cmp > 0) {
            node.right = delete(node.right, value);
        } else {
            if (node.left == null) { size--; return node.right; }
            if (node.right == null) { size--; return node.left; }
            Node<T> successor = node.right;
            while (successor.left != null) successor = successor.left;
            node.key = successor.key;
            // successor has no left child, so this recursive call hits a
            // single-child/no-child case above and decrements size exactly once
            node.right = delete(node.right, successor.key);
        }
        return node;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public int height() { return height(root); }

    private int height(Node<T> node) {
        if (node == null) return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /** In-order traversal; used by tests to confirm sortedness. */
    public List<T> inorder() {
        List<T> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    private void inorder(Node<T> node, List<T> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.key);
        inorder(node.right, out);
    }
}
