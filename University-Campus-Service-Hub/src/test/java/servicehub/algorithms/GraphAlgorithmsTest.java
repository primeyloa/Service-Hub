package servicehub.algorithms;

import org.junit.jupiter.api.Test;
import servicehub.ds.Graph;
import servicehub.ds.DynamicArray;

import static org.junit.jupiter.api.Assertions.*;

class GraphAlgorithmsTest {

    private Graph sample() {
        Graph g = new Graph();
        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 2.0);
        g.addEdge("C", "D", 1.0);
        g.addEdge("A", "D", 10.0);
        return g;
    }

    private Graph disconnected() {
        Graph g = new Graph();
        g.addEdge("A", "B", 1.0);
        g.addEdge("X", "Y", 1.0);
        return g;
    }

    @Test
    void bfsTraversesInOrder() {
        DynamicArray<String> order = GraphAlgorithms.bfs(sample(), "A");
        assertEquals(4, order.size());
        assertEquals("A", order.get(0));
        assertTrue(order.contains("B"));
        assertTrue(order.contains("D"));
        assertTrue(order.contains("C"));
    }

    @Test
    void dfsVisitsAllReachable() {
        DynamicArray<String> order = GraphAlgorithms.dfs(sample(), "A");
        assertEquals(4, order.size());
    }

    @Test
    void bfsOnDisconnectedGraph() {
        DynamicArray<String> order = GraphAlgorithms.bfs(disconnected(), "A");
        assertEquals(2, order.size());
        assertTrue(order.contains("A"));
        assertTrue(order.contains("B"));
    }

    @Test
    void bfsOnMissingStartReturnsEmpty() {
        DynamicArray<String> order = GraphAlgorithms.bfs(sample(), "ZZZ");
        assertEquals(0, order.size());
    }

    @Test
    void dijkstraFindsDistances() {
        servicehub.ds.Map<String, Double> dist = GraphAlgorithms.dijkstra(sample(), "A");
        assertEquals(0.0, dist.get("A"));
        assertEquals(1.0, dist.get("B"));
        assertEquals(3.0, dist.get("C"));
        assertEquals(4.0, dist.get("D")); // via C, not the direct 10.0 edge
    }

    @Test
    void shortestPathReconstruction() {
        GraphAlgorithms.RouteResult result = GraphAlgorithms.shortestPath(sample(), "A", "D");
        assertTrue(result.isReachable());
        assertEquals(4.0, result.totalWeight, 1e-9);
        assertEquals(4, result.path.size());
        assertEquals("A", result.path.get(0));
        assertEquals("D", result.path.get(result.path.size() - 1));
    }

    @Test
    void unreachablePath() {
        GraphAlgorithms.RouteResult result = GraphAlgorithms.shortestPath(disconnected(), "A", "Y");
        assertFalse(result.isReachable());
        assertEquals(Double.MAX_VALUE, result.totalWeight);
    }

    @Test
    void shortestPathSingleNode() {
        Graph g = new Graph();
        g.addVertex("Solo");
        GraphAlgorithms.RouteResult result = GraphAlgorithms.shortestPath(g, "Solo", "Solo");
        assertTrue(result.isReachable());
        assertEquals(1, result.path.size());
        assertEquals(0.0, result.totalWeight);
    }

    @Test
    void primProducesConnectedMst() {
        GraphAlgorithms.MSTResult mst = GraphAlgorithms.primMST(sample());
        assertEquals(3, mst.edges.size()); // V-1 edges
        assertTrue(mst.totalWeight > 0);
        // A-B(1) + B-C(2) + C-D(1) = 4
        assertEquals(4.0, mst.totalWeight, 1e-9);
    }

    @Test
    void kruskalMatchesPrim() {
        GraphAlgorithms.MSTResult prim = GraphAlgorithms.primMST(sample());
        GraphAlgorithms.MSTResult kruskal = GraphAlgorithms.kruskalMST(sample());
        assertEquals(3, kruskal.edges.size());
        assertEquals(prim.totalWeight, kruskal.totalWeight, 1e-9);
        assertEquals(4.0, kruskal.totalWeight, 1e-9);
    }

    @Test
    void kruskalOnDisconnectedReturnsForest() {
        GraphAlgorithms.MSTResult mst = GraphAlgorithms.kruskalMST(disconnected());
        assertEquals(2, mst.edges.size());
    }

    @Test
    void mstOnEmptyGraph() {
        Graph g = new Graph();
        GraphAlgorithms.MSTResult mst = GraphAlgorithms.kruskalMST(g);
        assertEquals(0, mst.edges.size());
        assertEquals(0.0, mst.totalWeight);
    }
}
