package servicehub.ds;

public class HashNode {

    int requestID;
    HashNode next;

    public HashNode(int requestID) {
        this.requestID = requestID;
        this.next = null;
    }

    @Override
    public String toString() {
        return String.valueOf(requestID);
    }
}