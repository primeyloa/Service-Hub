package servicehub.ds;


public class MapEntry<T> {

    int requestID;
    int resourceID;
    MapEntry<T> left;
    MapEntry<T> right;

    public MapEntry(int requestID, int resourceID) {
        this.requestID = requestID;
        this.resourceID = resourceID;
        this.left = null;
        this.right = null;
    }

    @Override
    public String toString() {
        return requestID + " -> " + resourceID;
    }
}