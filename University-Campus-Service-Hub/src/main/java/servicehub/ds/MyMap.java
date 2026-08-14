package servicehub.ds;

import servicehub.ds.Graph;


public class MyMap<K, V> {

    private final HashTable<K, V> table = new HashTable<>();

    public void put(K key, V value) { table.put(key, value); }

    public V get(K key) { return table.get(key); }

    public void remove(K key) { table.remove(key); }

    public boolean containsKey(K key) { return table.containsKey(key); }

    public int size() { return table.size(); }

    public boolean isEmpty() { return table.isEmpty(); }
}
