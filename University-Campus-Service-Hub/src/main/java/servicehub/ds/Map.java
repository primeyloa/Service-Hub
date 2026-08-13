package servicehub.ds;

public class Map <K, V> {

    private MapEntry<K> root;
    private int size;

    public Map() {
        root = null;
        size = 0;
    }

    // Put — insert new pair, or update resourceID if requestID already exists
    public void put(int requestID, int resourceID) {
        MapEntry<K> newEntry = new MapEntry<K>(requestID, resourceID);
        if (root == null) {
            root = newEntry;
            size++;
            return;
        }
        MapEntry <K> current = root;
        while (true) {
            if (requestID == current.requestID) {
                current.resourceID = resourceID; // update, not a duplicate insert
                return;
            }
            if (requestID < current.requestID) {
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

    // Get — returns the assigned resourceID
    public int get(int requestID) {
        MapEntry<K> current = root;
        while (current != null) {
            if (requestID == current.requestID)
                return current.resourceID;
            current = (requestID < current.requestID) ? current.left : current.right;
        }
        throw new IllegalArgumentException("No mapping for requestID " + requestID);
    }

    // ContainsKey — existence check without the exception, same walk as BinarySearchTree.search
    public boolean containsKey(int requestID) {
        MapEntry<K> current = root;
        while (current != null) {
            if (requestID == current.requestID)
                return true;
            current = (requestID < current.requestID) ? current.left : current.right;
        }
        return false;
    }

    // Remove — same three-case logic as BinarySearchTree.delete, carrying resourceID along
    public void remove(int requestID) {
        root = removeRecursive(root, requestID);
    }

    private MapEntry<K> removeRecursive(MapEntry<K> node, int requestID) {
        if (node == null)
            return null;
        if (requestID < node.requestID) {
            node.left = removeRecursive(node.left, requestID);
        } else if (requestID > node.requestID) {
            node.right = removeRecursive(node.right, requestID);
        } else {
            size--;
            if (node.left == null && node.right == null)
                return null;
            if (node.left == null)
                return node.right;
            if (node.right == null)
                return node.left;
            MapEntry<K> successor = findMin(node.right);
            node.requestID = successor.requestID;
            node.resourceID = successor.resourceID;
            node.right = removeRecursive(node.right, successor.requestID);
            size++;
        }
        return node;
    }

    private MapEntry<K> findMin(MapEntry<K> node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    // Inorder — pairs listed by ascending requestID
    public void inorder() {
        System.out.print("Map (inorder): ");
        inorder(root);
        System.out.println();
    }

    private void inorder(MapEntry<K> node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node + "  ");
            inorder(node.right);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
        size = 0;
    }
}