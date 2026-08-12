package ds;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference / placeholder Map ADT.
 *
 * REPLACE THIS FILE with your own Map implementation, keeping the class
 * name/package/method signatures the same (or update
 * SetMapCorrectnessTest.java to match yours).
 *
 * Assumed policy: put(null, v) / get(null) / remove(null) / containsKey(null)
 * throw IllegalArgumentException.
 */
public class MyMap<K, V> {

    private final HashTable<K, V> table = new HashTable<>();

    public void put(K key, V value) { table.put(key, value); }

    public V get(K key) { return table.get(key); }

    public void remove(K key) { table.remove(key); }

    public boolean containsKey(K key) { return table.containsKey(key); }

    public int size() { return table.size(); }

    public boolean isEmpty() { return table.isEmpty(); }
}
