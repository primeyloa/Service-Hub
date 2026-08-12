package ds;

/**
 * Reference / placeholder Set ADT.
 *
 * REPLACE THIS FILE with your own Set implementation, keeping the class
 * name/package/method signatures the same (or update
 * SetMapCorrectnessTest.java to match yours).
 *
 * Assumed policy: add(null) / remove(null) / contains(null) throw
 * IllegalArgumentException. Adding a value already present is a no-op.
 */
public class MySet<T> {

    private final MyMap<T, Boolean> map = new MyMap<>();

    public void add(T value) { map.put(value, Boolean.TRUE); }

    public void remove(T value) { map.remove(value); }

    public boolean contains(T value) { return map.containsKey(value); }

    public int size() { return map.size(); }

    public boolean isEmpty() { return map.isEmpty(); }
}
