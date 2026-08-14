package servicehub.ds;

import java.util.ArrayList;
import java.util.List;


public class RedBlackTree<T extends Comparable<T>> {

    static final boolean RED = true;
    static final boolean BLACK = false;

    static class Node<T> {
        T key;
        Node<T> left, right, parent;
        boolean red;
        Node(T key, boolean red, Node<T> nil) {
            this.key = key;
            this.red = red;
            this.left = nil;
            this.right = nil;
            this.parent = nil;
        }
    }

    private final Node<T> NIL = new Node<>(null, BLACK, null);
    private Node<T> root = NIL;
    private int size = 0;

    public RedBlackTree() {
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.parent = NIL;
    }



    public void insert(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        Node<T> y = NIL;
        Node<T> x = root;
        while (x != NIL) {
            y = x;
            int cmp = value.compareTo(x.key);
            if (cmp == 0) return; // duplicate: no-op
            x = cmp < 0 ? x.left : x.right;
        }
        Node<T> z = new Node<>(value, RED, NIL);
        z.parent = y;
        if (y == NIL) root = z;
        else if (value.compareTo(y.key) < 0) y.left = z;
        else y.right = z;
        size++;
        insertFixup(z);
    }

    public boolean contains(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        return find(value) != NIL;
    }

    public void delete(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        Node<T> z = find(value);
        if (z == NIL) return; // not present: no-op
        deleteNode(z);
        size--;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public List<T> inorder() {
        List<T> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }


    Node<T> root() { return root; }
    Node<T> nil() { return NIL; }



    private Node<T> find(T value) {
        Node<T> x = root;
        while (x != NIL) {
            int cmp = value.compareTo(x.key);
            if (cmp == 0) return x;
            x = cmp < 0 ? x.left : x.right;
        }
        return NIL;
    }

    private void inorder(Node<T> node, List<T> out) {
        if (node == NIL) return;
        inorder(node.left, out);
        out.add(node.key);
        inorder(node.right, out);
    }

    private void leftRotate(Node<T> x) {
        Node<T> y = x.right;
        x.right = y.left;
        if (y.left != NIL) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node<T> x) {
        Node<T> y = x.left;
        x.left = y.right;
        if (y.right != NIL) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    private void insertFixup(Node<T> z) {
        while (z.parent.red) {
            if (z.parent == z.parent.parent.left) {
                Node<T> y = z.parent.parent.right; // uncle
                if (y.red) {
                    z.parent.red = BLACK;
                    y.red = BLACK;
                    z.parent.parent.red = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.red = BLACK;
                    z.parent.parent.red = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node<T> y = z.parent.parent.left; // uncle
                if (y.red) {
                    z.parent.red = BLACK;
                    y.red = BLACK;
                    z.parent.parent.red = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.red = BLACK;
                    z.parent.parent.red = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.red = BLACK;
    }

    private void transplant(Node<T> u, Node<T> v) {
        if (u.parent == NIL) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        v.parent = u.parent;
    }

    private Node<T> minimum(Node<T> x) {
        while (x.left != NIL) x = x.left;
        return x;
    }

    private void deleteNode(Node<T> z) {
        Node<T> y = z;
        boolean yOriginalRed = y.red;
        Node<T> x;
        if (z.left == NIL) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NIL) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalRed = y.red;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.red = z.red;
        }
        if (!yOriginalRed) deleteFixup(x);
    }

    private void deleteFixup(Node<T> x) {
        while (x != root && !x.red) {
            if (x == x.parent.left) {
                Node<T> w = x.parent.right;
                if (w.red) {
                    w.red = BLACK;
                    x.parent.red = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (!w.left.red && !w.right.red) {
                    w.red = RED;
                    x = x.parent;
                } else {
                    if (!w.right.red) {
                        w.left.red = BLACK;
                        w.red = RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.red = x.parent.red;
                    x.parent.red = BLACK;
                    w.right.red = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node<T> w = x.parent.left;
                if (w.red) {
                    w.red = BLACK;
                    x.parent.red = RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (!w.right.red && !w.left.red) {
                    w.red = RED;
                    x = x.parent;
                } else {
                    if (!w.left.red) {
                        w.right.red = BLACK;
                        w.red = RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.red = x.parent.red;
                    x.parent.red = BLACK;
                    w.left.red = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.red = BLACK;
    }
}
