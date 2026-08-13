package servicehub.ds;

public class HashNode<T> {

    int requestID;
    HashNode<T> next;

    public HashNode(int requestID) {
        this.requestID = requestID;
        this.next = null;
    }

    @Override
    public String toString() {
        return String.valueOf(requestID);
    }
}