package servicehub.ds;

/**
 * Node for the chaining hash table. Holds a key/value pair.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class HashNode<K, V> {
    public K key;
    public V value;
    public HashNode<K, V> next;

    public HashNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
