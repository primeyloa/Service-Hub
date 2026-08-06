import java.util.ArrayList;

public class DepthFirstSearch {

    private Graph graph;
    private boolean[] visited;
    private ArrayList<Vertex> reachableLocations;

    public DepthFirstSearch(Graph graph) {

        this.graph = graph;

    }

    public ArrayList<Vertex> traverse(int startIndex) {

        visited = new boolean[graph.size()];
        reachableLocations = new ArrayList<>();

        dfs(startIndex);

        return reachableLocations;

    }

    private void dfs(int currentIndex) {

        visited[currentIndex] = true;

        Vertex currentVertex = graph.getVertices().get(currentIndex);

        reachableLocations.add(currentVertex);

        for (Edge edge : graph.getAdjacencyList().get(currentIndex)) {

            Vertex neighbour = edge.getDestination();

            int neighbourIndex = graph.getVertices().indexOf(neighbour);

            if (!visited[neighbourIndex]) {

                dfs(neighbourIndex);

            }

        }

    }

    public void displayTraversal(int startIndex) {

        ArrayList<Vertex> traversal = traverse(startIndex);

        System.out.println("\n             DEPTH FIRST SEARCH             ");

        for (Vertex vertex : traversal) {

            System.out.print(vertex.getName() + " -> ");

        }

        System.out.println("END");

    }

}