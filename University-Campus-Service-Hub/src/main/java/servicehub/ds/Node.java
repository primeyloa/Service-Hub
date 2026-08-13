package servicehub.ds;

public class Node <T> {

    int requestID;
    Node<T> left;
    Node<T> right;

    public Node(int requestID) {
        this.requestID = requestID;
        this.left = null;
        this.right = null;
    }

    @Override
    public String toString() {
        return String.valueOf(requestID);
    }
}