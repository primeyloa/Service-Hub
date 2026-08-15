package servicehub.ds;

/**
 * Node for Binary Search Tree and tree-based structures.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class Node<K, V> {
    public K key;
    public V value;
    public Node<K, V> left;
    public Node<K, V> right;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
    }

    public Node(K key) {
        this(key, null);
    }

    @Override
    public String toString() {
        return value != null ? key + ":" + value : String.valueOf(key);
    }
}
