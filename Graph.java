import java.util.ArrayList;

public class Graph {

    private ArrayList<Vertex> vertices;
    private ArrayList<ArrayList<Edge>> adjacencyList;

    // Required adjacency matrix
    private int[][] adjacencyMatrix;

    public Graph() {

        vertices = new ArrayList<>();
        adjacencyList = new ArrayList<>();
        adjacencyMatrix = new int[0][0];

    }

    // Add a location
    public void addVertex(Vertex vertex) {

        vertices.add(vertex);
        adjacencyList.add(new ArrayList<>());

        resizeMatrix();

    }

    // Resize matrix whenever a new vertex is added
    private void resizeMatrix() {

        int n = vertices.size();

        int[][] newMatrix = new int[n][n];

        for (int i = 0; i < adjacencyMatrix.length; i++) {

            for (int j = 0; j < adjacencyMatrix.length; j++) {

                newMatrix[i][j] = adjacencyMatrix[i][j];

            }

        }

        adjacencyMatrix = newMatrix;
    }

    // Add an edge
    public void addEdge(int sourceIndex, int destinationIndex, int weight) {

        Vertex destination = vertices.get(destinationIndex);

        adjacencyList.get(sourceIndex).add(
                new Edge(destination, weight)
        );

        adjacencyMatrix[sourceIndex][destinationIndex] = weight;

    }

    // Undirected graph
    public void addUndirectedEdge(int sourceIndex, int destinationIndex, int weight) {

        addEdge(sourceIndex, destinationIndex, weight);
        addEdge(destinationIndex, sourceIndex, weight);

    }

    // Display adjacency list
    public void displayGraph() {

        System.out.println("\n===== ADJACENCY LIST =====");

        for (int i = 0; i < vertices.size(); i++) {

            System.out.print(vertices.get(i).getName() + " -> ");

            for (Edge edge : adjacencyList.get(i)) {

                System.out.print(edge + " ");

            }

            System.out.println();

        }

    }

    // Display adjacency matrix
    public void displayMatrix() {

        System.out.println("\n===== ADJACENCY MATRIX =====");

        for (int i = 0; i < adjacencyMatrix.length; i++) {

            for (int j = 0; j < adjacencyMatrix.length; j++) {

                System.out.print(adjacencyMatrix[i][j] + "\t");

            }

            System.out.println();

        }

    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public ArrayList<ArrayList<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public int size() {
        return vertices.size();
    }
}