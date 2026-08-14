public class Node {

    int requestID;
    Node left;
    Node right;

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