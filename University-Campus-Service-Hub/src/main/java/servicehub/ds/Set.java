public class Set {

    private HashTable table;

    public Set() {
        table = new HashTable();
    }

    public void add(int requestID) {
        table.insert(requestID);
    }

    public boolean contains(int requestID) {
        return table.contains(requestID);
    }

    public void remove(int requestID) {
        table.delete(requestID);
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.size() == 0;
    }

    public double getLoadFactor() {
        return table.getLoadFactor();
    }

    public int getCollisionCount() {
        return table.getCollisionCount();
    }

    public void display() {
        table.display();
    }

    public void clear() {
        table.clear();
    }
}