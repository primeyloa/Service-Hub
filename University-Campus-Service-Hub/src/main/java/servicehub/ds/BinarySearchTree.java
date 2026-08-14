package servicehub.ds;

public class BinarySearchTree<T> {

    private Node<T> root;
    private int size;

    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    // Insert a request ID
    public void insert(int requestID) {

        Node<T> newNode = new Node<T>(requestID);

        if (root == null) {
            root = newNode;
            size++;
            return;
        }

        Node<T> current = root;

        while (true) {

            if (requestID < current.requestID) {

                if (current.left == null) {
                    current.left = newNode;
                    size++;
                    return;
                }

                current = current.left;

            } else if (requestID > current.requestID) {

                if (current.right == null) {
                    current.right = newNode;
                    size++;
                    return;
                }

                current = current.right;

            } else {
                // Duplicate IDs are ignored
                return;
            }
        }
    }

    // Search for a request ID
    public boolean search(int requestID) {

        Node<T> current = root;

        while (current != null) {

            if (requestID == current.requestID)
                return true;

            if (requestID < current.requestID)
                current = current.left;
            else
                current = current.right;
        }

        return false;
    }

    // Delete a request ID
    public void delete(int requestID) {
        root = deleteRecursive(root, requestID);
    }

    private Node<T> deleteRecursive(Node<T> node, int requestID) {

        if (node == null)
            return null;

        if (requestID < node.requestID) {

            node.left = deleteRecursive(node.left, requestID);

        } else if (requestID > node.requestID) {

            node.right = deleteRecursive(node.right, requestID);

        } else {

            size--;

            // No child
            if (node.left == null && node.right == null)
                return null;

            // One child
            if (node.left == null)
                return node.right;

            if (node.right == null)
                return node.left;

            // Two children
            Node<T> successor = findMin(node.right);

            node.requestID = successor.requestID;

            node.right = deleteRecursive(node.right, successor.requestID);

            size++;
        }

        return node;
    }

    // Find minimum node
    private Node<T> findMin(Node<T> node) {

        while (node.left != null)
            node = node.left;

        return node;
    }

    // Display requests in ascending order
    public void inorder() {

        System.out.print("Inorder: ");
        inorder(root);
        System.out.println();
    }

    private void inorder(Node<T> node) {

        if (node != null) {

            inorder(node.left);

            System.out.print(node + " ");

            inorder(node.right);
        }
    }

    // Display Preorder
    public void preorder() {

        System.out.print("Preorder: ");
        preorder(root);
        System.out.println();
    }

    private void preorder(Node<T> node) {

        if (node != null) {

            System.out.print(node + " ");

            preorder(node.left);

            preorder(node.right);
        }
    }

    // Display Postorder
    public void postorder() {

        System.out.print("Postorder: ");
        postorder(root);
        System.out.println();
    }

    private void postorder(Node<T> node) {

        if (node != null) {

            postorder(node.left);

            postorder(node.right);

            System.out.print(node + " ");
        }
    }

    // Find minimum value
    public int findMin() {

        if (root == null)
            throw new IllegalStateException("Tree is empty.");

        return findMin(root).requestID;
    }

    // Find maximum value
    public int findMax() {

        if (root == null)
            throw new IllegalStateException("Tree is empty.");

        Node<T> current = root;

        while (current.right != null)
            current = current.right;

        return current.requestID;
    }

    // Height of tree
    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {

        if (node == null)
            return -1;

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        return Math.max(leftHeight, rightHeight) + 1;
    }

    // Number of nodes
    public int size() {
        return size;
    }

    // Check if tree is empty
    public boolean isEmpty() {
        return root == null;
    }

    // Clear the tree
    public void clear() {
        root = null;
        size = 0;
    }
}