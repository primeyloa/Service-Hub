package servicehub.ds;

/**
 * Generic Set built on top of the custom chaining HashTable.
 *
 * @param <T> element type
 */
public class Set<T> {

    private final HashTable<T, Boolean> table;

    public Set() {
        table = new HashTable<>();
    }

    public void add(T item) {
        if (item == null) throw new IllegalArgumentException("Null values not allowed");
        table.put(item, true);
    }

    public boolean contains(T item) {
        if (item == null) throw new IllegalArgumentException("Null values not allowed");
        return table.containsKey(item);
    }

    public boolean remove(T item) {
        if (item == null) throw new IllegalArgumentException("Null values not allowed");
        return table.remove(item) != null;
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public double getLoadFactor() {
        return table.getLoadFactor();
    }

    public int getCollisionCount() {
        return table.getCollisionCount();
    }

    public void clear() {
        table.clear();
    }
}
