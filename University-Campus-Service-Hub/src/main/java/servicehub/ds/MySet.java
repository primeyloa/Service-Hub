package servicehub.ds;


public class MySet<T> {

    private final MyMap<T, Boolean> map = new MyMap<>();

    public void add(T value) { map.put(value, Boolean.TRUE); }

    public void remove(T value) { map.remove(value); }

    public boolean contains(T value) { return map.containsKey(value); }

    public int size() { return map.size(); }

    public boolean isEmpty() { return map.isEmpty(); }
}
