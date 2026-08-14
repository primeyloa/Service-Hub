package ds;

import servicehub.ds.Graph;

/
public class BTree<T extends Comparable<T>> {

    private final int t;
    private Node root;
    private int size = 0;

    private class Node {
        List<T> keys = new ArrayList<>();
        List<Node> children = new ArrayList<>();
        boolean leaf = true;
    }

    public BTree(int minimumDegree) {
        if (minimumDegree < 2) {
            throw new IllegalArgumentException("minimum degree must be >= 2");
        }
        this.t = minimumDegree;
        this.root = new Node();
    }



    public void insert(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        if (contains(value)) return; // duplicate: no-op

        Node r = root;
        if (r.keys.size() == 2 * t - 1) {
            Node newRoot = new Node();
            newRoot.leaf = false;
            newRoot.children.add(r);
            splitChild(newRoot, 0);
            root = newRoot;
            insertNonFull(root, value);
        } else {
            insertNonFull(r, value);
        }
        size++;
    }

    public boolean contains(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        return search(root, value) != null;
    }


    public boolean search(T value) {
        return contains(value);
    }

    public void delete(T value) {
        if (value == null) throw new IllegalArgumentException("value must not be null");
        if (!contains(value)) return; // not present: no-op
        deleteFromNode(root, value);
        if (root.keys.isEmpty() && !root.leaf) {
            root = root.children.get(0);
        }
        size--;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public List<T> inorder() {
        List<T> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }



    private Node search(Node node, T value) {
        int i = 0;
        while (i < node.keys.size() && value.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && value.compareTo(node.keys.get(i)) == 0) return node;
        if (node.leaf) return null;
        return search(node.children.get(i), value);
    }

    private void inorder(Node node, List<T> out) {
        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.leaf) inorder(node.children.get(i), out);
            out.add(node.keys.get(i));
        }
        if (!node.leaf) inorder(node.children.get(node.keys.size()), out);
    }


    private void splitChild(Node x, int i) {
        Node y = x.children.get(i);
        Node z = new Node();
        z.leaf = y.leaf;

        T median = y.keys.get(t - 1);

        z.keys.addAll(y.keys.subList(t, y.keys.size()));
        List<T> newYKeys = new ArrayList<>(y.keys.subList(0, t - 1));

        if (!y.leaf) {
            z.children.addAll(y.children.subList(t, y.children.size()));
            List<Node> newYChildren = new ArrayList<>(y.children.subList(0, t));
            y.children.clear();
            y.children.addAll(newYChildren);
        }

        y.keys.clear();
        y.keys.addAll(newYKeys);

        x.children.add(i + 1, z);
        x.keys.add(i, median);
    }

    private void insertNonFull(Node x, T value) {
        int i = x.keys.size() - 1;
        if (x.leaf) {
            x.keys.add(null); // grow by one
            while (i >= 0 && value.compareTo(x.keys.get(i)) < 0) {
                x.keys.set(i + 1, x.keys.get(i));
                i--;
            }
            x.keys.set(i + 1, value);
        } else {
            while (i >= 0 && value.compareTo(x.keys.get(i)) < 0) i--;
            i++;
            if (x.children.get(i).keys.size() == 2 * t - 1) {
                splitChild(x, i);
                if (value.compareTo(x.keys.get(i)) > 0) i++;
            }
            insertNonFull(x.children.get(i), value);
        }
    }

    private int findKeyIndex(Node node, T value) {
        int i = 0;
        while (i < node.keys.size() && node.keys.get(i).compareTo(value) < 0) i++;
        return i;
    }

    private void deleteFromNode(Node node, T value) {
        int idx = findKeyIndex(node, value);

        if (idx < node.keys.size() && node.keys.get(idx).compareTo(value) == 0) {
            if (node.leaf) {
                node.keys.remove(idx);
            } else {
                deleteFromInternalNode(node, idx);
            }
        } else {
            if (node.leaf) {
                return;
            }
            boolean isLastChild = (idx == node.keys.size());
            Node child = node.children.get(idx);
            if (child.keys.size() < t) {
                fill(node, idx);

                if (isLastChild && idx > node.keys.size()) {
                    deleteFromNode(node.children.get(idx - 1), value);
                } else {
                    deleteFromNode(node.children.get(idx), value);
                }
                return;
            }
            deleteFromNode(child, value);
        }
    }

    private void deleteFromInternalNode(Node node, int idx) {
        T k = node.keys.get(idx);
        Node leftChild = node.children.get(idx);
        Node rightChild = node.children.get(idx + 1);

        if (leftChild.keys.size() >= t) {
            T pred = getPredecessor(leftChild);
            node.keys.set(idx, pred);
            deleteFromNode(leftChild, pred);
        } else if (rightChild.keys.size() >= t) {
            T succ = getSuccessor(rightChild);
            node.keys.set(idx, succ);
            deleteFromNode(rightChild, succ);
        } else {
            merge(node, idx);
            deleteFromNode(leftChild, k);
        }
    }

    private T getPredecessor(Node node) {
        while (!node.leaf) node = node.children.get(node.children.size() - 1);
        return node.keys.get(node.keys.size() - 1);
    }

    private T getSuccessor(Node node) {
        while (!node.leaf) node = node.children.get(0);
        return node.keys.get(0);
    }


    private void fill(Node node, int idx) {
        if (idx != 0 && node.children.get(idx - 1).keys.size() >= t) {
            borrowFromPrev(node, idx);
        } else if (idx != node.keys.size() && node.children.get(idx + 1).keys.size() >= t) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.keys.size()) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(Node node, int idx) {
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx - 1);

        child.keys.add(0, node.keys.get(idx - 1));
        if (!child.leaf) {
            child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
        }
        node.keys.set(idx - 1, sibling.keys.remove(sibling.keys.size() - 1));
    }

    private void borrowFromNext(Node node, int idx) {
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.get(idx));
        if (!child.leaf) {
            child.children.add(sibling.children.remove(0));
        }
        node.keys.set(idx, sibling.keys.remove(0));
    }


    private void merge(Node node, int idx) {
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.remove(idx));
        child.keys.addAll(sibling.keys);
        if (!child.leaf) {
            child.children.addAll(sibling.children);
        }
        node.children.remove(idx + 1);
    }
}
