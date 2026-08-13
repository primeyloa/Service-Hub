package servicehub.ds;
import servicehub.ds.DynamicArray;

public class Graph {
    public static class Edge {
        public String from;
        public String to;
        public double weight;

        public Edge(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private HashTable<String, LinkedList<Edge>> adjList;
    private DynamicArray<String> vertices;

    public Graph() {
        this.adjList = new HashTable<>();
        this.vertices = new DynamicArray<String>(); // susceptible to change
    }

    public void addVertex(Node<String> vertex) {
        if (adjList.get(vertex) == null) {
            adjList.set(vertex, new LinkedList<>());
            vertices.add(vertex);
        }
    }

    public void addEdge(String from, String to, double weight) {
        addVertex(from);
        addVertex(to);
        adjList.get(from).addLast(new Edge(from, to, weight));
    }

    public LinkedList<Edge> getEdges(String vertex) {
        return adjList.get(vertex);
    }

    public DynamicArray<String> getVertices() {
        return vertices;
    }

    public Edge removeVertex(String from, String to){
        if(adjList.contains()){
            adjList.remove
        }
    }
}
