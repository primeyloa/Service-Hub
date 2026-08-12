package ds;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference / placeholder Hash Table (separate chaining, resizes on load factor).
 *
 * REPLACE THIS FILE with your own hash table implementation, keeping the
 * class name/package/method signatures the same (or update
 * HashTableCorrectnessTest.java to match yours).
 *
 * Assumed policy:
 *  - put(null, v) / get(null) / remove(null) / containsKey(null) throw
 *    IllegalArgumentException (null keys are not allowed)
 *  - put on an existing key overwrites the value and does not change size
 */
public class HashTable<K, V> {

    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    @SuppressWarnings("unchecked")
    private List<Entry<K, V>>[] buckets = new List[INITIAL_CAPACITY];
    private int size = 0;

    private int bucketIndex(K key, int capacity) {
        int h = key.hashCode();
        h ^= (h >>> 16);
        return Math.floorMod(h, capacity);
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("key must not be null");
        int idx = bucketIndex(key, buckets.length);
        if (buckets[idx] == null) buckets[idx] = new ArrayList<>();
        for (Entry<K, V> e : buckets[idx]) {
            if (e.key.equals(key)) {
                e.value = value;
                return;
            }
        }
        buckets[idx].add(new Entry<>(key, value));
        size++;
        if ((double) size / buckets.length > LOAD_FACTOR_THRESHOLD) resize();
    }

    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("key must not be null");
        int idx = bucketIndex(key, buckets.length);
        if (buckets[idx] == null) return null;
        for (Entry<K, V> e : buckets[idx]) {
            if (e.key.equals(key)) return e.value;
        }
        return null;
    }

    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("key must not be null");
        int idx = bucketIndex(key, buckets.length);
        if (buckets[idx] == null) return false;
        for (Entry<K, V> e : buckets[idx]) {
            if (e.key.equals(key)) return true;
        }
        return false;
    }

    public void remove(K key) {
        if (key == null) throw new IllegalArgumentException("key must not be null");
        int idx = bucketIndex(key, buckets.length);
        if (buckets[idx] == null) return;
        buckets[idx].removeIf(e -> {
            boolean match = e.key.equals(key);
            if (match) size--;
            return match;
        });
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    @SuppressWarnings("unchecked")
    private void resize() {
        List<Entry<K, V>>[] old = buckets;
        buckets = new List[old.length * 2];
        for (List<Entry<K, V>> bucket : old) {
            if (bucket == null) continue;
            for (Entry<K, V> e : bucket) {
                int idx = bucketIndex(e.key, buckets.length);
                if (buckets[idx] == null) buckets[idx] = new ArrayList<>();
                buckets[idx].add(e);
            }
        }
    }

    public int bucketCount() { return buckets.length; }
}
