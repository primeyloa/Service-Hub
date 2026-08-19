package servicehub.ds;

/**
 * Generic chaining HashTable (HashMap built from scratch).
 * Supports put, get, remove, containsKey, load-factor and collision statistics.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class HashTable<K, V> {

    private static final int DEFAULT_SIZE = 149;

    @SuppressWarnings("unchecked")
    private HashNode<K, V>[] table;
    private int bucketCount;
    private int elementCount;
    private int collisionCount;

    public HashTable() {
        this(DEFAULT_SIZE);
    }

    @SuppressWarnings("unchecked")
    public HashTable(int bucketSize) {
        if (bucketSize <= 0) {
            throw new IllegalArgumentException("Bucket size must be positive");
        }
        this.bucketCount = bucketSize;
        this.table = new HashNode[bucketSize];
        this.elementCount = 0;
        this.collisionCount = 0;
    }

    private int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return (h & Integer.MAX_VALUE) % bucketCount;
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Null keys are not allowed");
        int index = hash(key);
        HashNode<K, V> head = table[index];

        if (head == null) {
            table[index] = new HashNode<>(key, value);
            elementCount++;
            return;
        }

        HashNode<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // update existing
                return;
            }
            current = current.next;
        }

        collisionCount++; // existing element already in the bucket
        HashNode<K, V> newNode = new HashNode<>(key, value);
        newNode.next = head;
        table[index] = newNode;
        elementCount++;
    }

    public V get(K key) {
        if (key == null) return null;
        int index = hash(key);
        HashNode<K, V> current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        if (key == null) return false;
        int index = hash(key);
        HashNode<K, V> current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public V remove(K key) {
        if (key == null) return null;
        int index = hash(key);
        HashNode<K, V> current = table[index];
        HashNode<K, V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                elementCount--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public int size() {
        return elementCount;
    }

    public boolean isEmpty() {
        return elementCount == 0;
    }

    public double getLoadFactor() {
        return (double) elementCount / bucketCount;
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public int getBucketCount() {
        return bucketCount;
    }

    public void clear() {
        for (int i = 0; i < bucketCount; i++) table[i] = null;
        elementCount = 0;
        collisionCount = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (int i = 0; i < bucketCount; i++) {
            HashNode<K, V> current = table[i];
            while (current != null) {
                if (!first) sb.append(", ");
                sb.append(current.key).append("=").append(current.value);
                first = false;
                current = current.next;
            }
        }
        return sb.append("}").toString();
    }
}
