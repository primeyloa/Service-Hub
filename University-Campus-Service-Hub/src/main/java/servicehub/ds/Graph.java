package servicehub.ds;

/**
 * Weighted Graph backed by an adjacency list (with a lazy adjacency-matrix view).
 * Vertices are keyed by String ids (campus location ids).
 */
public class Graph {

    /** Weighted edge between two vertices. */
    public static class Edge {
        public final String from;
        public final String to;
        public final double weight;

        public Edge(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return from + " -" + weight + "-> " + to;
        }
    }

    private final HashMap<String, LinkedList<Edge>> adjList;
    private final DynamicArray<String> vertices;
    private int edgeCount;

    public Graph() {
        adjList = new HashMap<>();
        vertices = new DynamicArray<>();
    }

    public void addVertex(String vertex) {
        if (vertex == null) throw new IllegalArgumentException("Vertex must not be null");
        if (!adjList.containsKey(vertex)) {
            adjList.put(vertex, new LinkedList<>());
            vertices.add(vertex);
        }
    }

    /**
     * Adds an undirected weighted edge (both directions).
     */
    public void addEdge(String from, String to, double weight) {
        addVertex(from);
        addVertex(to);
        if (weight < 0) throw new IllegalArgumentException("Edge weight must be non-negative");
        if (!hasEdge(from, to)) {
            adjList.get(from).addLast(new Edge(from, to, weight));
            adjList.get(to).addLast(new Edge(to, from, weight));
            edgeCount++;
        }
    }

    /**
     * Adds a directed weighted edge (one direction only).
     */
    public void addDirectedEdge(String from, String to, double weight) {
        addVertex(from);
        addVertex(to);
        if (weight < 0) throw new IllegalArgumentException("Edge weight must be non-negative");
        if (!hasEdge(from, to)) {
            adjList.get(from).addLast(new Edge(from, to, weight));
            edgeCount++;
        }
    }

    public boolean hasVertex(String vertex) {
        return adjList.containsKey(vertex);
    }

    public boolean hasEdge(String from, String to) {
        LinkedList<Edge> edges = adjList.get(from);
        if (edges == null) return false;
        for (Edge e : edges) {
            if (e.to.equals(to)) return true;
        }
        return false;
    }

    public LinkedList<Edge> getEdges(String vertex) {
        return adjList.get(vertex);
    }

    public DynamicArray<String> getVertices() {
        return vertices;
    }

    public int vertexCount() {
        return vertices.size();
    }

    public int edgeCount() {
        return edgeCount;
    }

    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    /**
     * Builds an adjacency matrix view of the graph.
     * Entry [i][j] holds the edge weight or Double.POSITIVE_INFINITY when absent.
     */
    public double[][] adjacencyMatrix() {
        int n = vertices.size();
        double[][] matrix = new double[n][n];
        HashMap<String, Integer> index = new HashMap<>();
        for (int i = 0; i < n; i++) {
            index.put(vertices.get(i), i);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = (i == j) ? 0.0 : Double.POSITIVE_INFINITY;
            }
        }
        for (String v : vertices) {
            int i = index.get(v);
            LinkedList<Edge> edges = adjList.get(v);
            if (edges == null) continue;
            for (Edge e : edges) {
                int j = index.get(e.to);
                matrix[i][j] = Math.min(matrix[i][j], e.weight);
            }
        }
        return matrix;
    }
}
