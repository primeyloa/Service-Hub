package servicehub.ds;

/**
 * Entry node for the BST-based Map.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class MapEntry<K, V> {
    public K key;
    public V value;
    public MapEntry<K, V> left;
    public MapEntry<K, V> right;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
    }

    @Override
    public String toString() {
        return key + " -> " + value;
    }
}
