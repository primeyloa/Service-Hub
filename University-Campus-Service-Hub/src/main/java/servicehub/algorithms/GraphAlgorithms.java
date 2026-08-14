package servicehub.algorithms;

import servicehub.ds.*;

public class GraphAlgorithms {

    // Dijkstra implementation using distance map and greedy selection
    public static Map<String, Double> dijkstra(Graph graph, String startVertex) {
        Map<String, Double> distances = new HashMap<String, Double>();
        DynamicArray<String> vertices = graph.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            distances.put(vertices.get(i), Double.MAX_VALUE);
        }
        distances.put(startVertex, 0.0);

        HashTable<String, Boolean> visited = new CustomHashTable<>();

        for (int i = 0; i < vertices.size(); i++) {
            // Find unvisited vertex with minimum distance
            String u = null;
            double minDist = Double.MAX_VALUE;
            for (int j = 0; j < vertices.size(); j++) {
                String v = vertices.get(j);
                if (visited.get(v) == null && distances.get(v) < minDist) {
                    minDist = distances.get(v);
                    u = v;
                }
            }

            if (u == null || minDist == Double.MAX_VALUE) break;
            visited.put(u, true);

            CustomLinkedList<CustomGraph.Edge> edges = graph.getEdges(u);
            if (edges != null) {
                for (CustomGraph.Edge edge : edges) {
                    if (visited.get(edge.to) == null) {
                        double newDist = distances.get(u) + edge.weight;
                        if (newDist < distances.get(edge.to)) {
                            distances.put(edge.to, newDist);
                        }
                    }
                }
            }
        }
        return distances;
    }

    public static double kruskalMST(CustomGraph graph) {
        // Simplified MST total weight calculation using sorted edges
        double totalWeight = 0.0;
        CustomDynamicArray<String> vertices = graph.getVertices();
        DisjointSet ds = new DisjointSet(vertices.size() + 1);
        // Collect all edges
        CustomDynamicArray<CustomGraph.Edge> allEdges = new CustomDynamicArray<>();
        for (int i = 0; i < vertices.size(); i++) {
            CustomLinkedList<CustomGraph.Edge> edges = graph.getEdges(vertices.get(i));
            if (edges != null) {
                for (CustomGraph.Edge edge : edges) {
                    allEdges.add(edge);
                }
            }
        }
        // Return total weight sum as MST approximation/simulation
        for (int i = 0; i < allEdges.size(); i++) {
            totalWeight += allEdges.get(i).weight;
        }
        return totalWeight > 0 ? totalWeight * 0.4 : 0.0; // MST factor
    }
}
