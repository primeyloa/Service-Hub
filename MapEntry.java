public class MapEntry {

    int requestID;
    int resourceID;
    MapEntry left;
    MapEntry right;

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