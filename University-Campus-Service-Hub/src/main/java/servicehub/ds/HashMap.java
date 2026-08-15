package servicehub.ds;

public class HashMap<K, V> {

    // Node class representing the key-value pair chain
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V>[] table;
    private int size;
    private int capacity;
    private float loadFactor = 0;

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public HashMap() {
        this.capacity = DEFAULT_INITIAL_CAPACITY;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.table = new Node[capacity];
    }

    // Secondary hash function to distribute bits evenly
    private int getBucketIndex(K key) {
        if (key == null) return 0;
        return (key.hashCode() & Integer.MAX_VALUE) % capacity;
    }

    // Insert or update a key-value pair
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        Node<K, V> head = table[index];

        // Traverse the bucket chain to see if the key already exists
        while (head != null) {
            if ((key == null && head.key == null) || (key != null && key.equals(head.key))) {
                head.value = value; // Update value if key matches
                return;
            }
            head = head.next;
        }

        // Insert new node at the head of the chain (O(1) insertion time)
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;

        // Check if load factor threshold is violated
        if ((float) size / capacity >= loadFactor) {
            resize();
        }
    }

    // Retrieve a value by its key
    public V get(K key) {
        int index = getBucketIndex(key);
        Node<K, V> head = table[index];

        while (head != null) {
            if ((key == null && head.key == null) || (key != null && key.equals(head.key))) {
                return head.value;
            }
            head = head.next;
        }
        return null; // Key not found
    }

    public boolean containsKey(K key) {
        int index = getBucketIndex(key);
        Node<K, V> head = table[index];
        while (head != null) {
            if ((key == null && head.key == null) || (key != null && key.equals(head.key))) {
                return true;
            }
            head = head.next;
        }
        return false;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) table[i] = null;
        size = 0;
    }

    // Remove a key-value pair
    public V remove(K key) {
        int index = getBucketIndex(key);
        Node<K, V> head = table[index];
        Node<K, V> prev = null;

        while (head != null) {
            if ((key == null && head.key == null) || (key != null && key.equals(head.key))) {
                if (prev == null) {
                    table[index] = head.next; // Target was the head of the chain
                } else {
                    prev.next = head.next; // Bypass the target node
                }
                size--;
                return head.value;
            }
            prev = head;
            head = head.next;
        }
        return null; // Key not found
    }

    // Double the table array capacity and rehash all nodes
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        capacity = capacity * 2;
        table = new Node[capacity];
        size = 0; // Reset size, it will increment inside put() during rehashing

        for (Node<K, V> headNode : oldTable) {
            while (headNode != null) {
                put(headNode.key, headNode.value); // Rehash entries into the new table
                headNode = headNode.next;
            }
        }
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}
