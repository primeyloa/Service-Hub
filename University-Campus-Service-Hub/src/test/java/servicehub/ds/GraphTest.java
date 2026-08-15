package servicehub.ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Graph sample() {
        Graph g = new Graph();
        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 2.0);
        g.addEdge("C", "D", 1.5);
        return g;
    }

    @Test
    void addsVerticesAndEdges() {
        Graph g = sample();
        assertEquals(4, g.vertexCount());
        assertEquals(3, g.edgeCount());
        assertTrue(g.hasVertex("A"));
        assertFalse(g.hasVertex("Z"));
        assertTrue(g.hasEdge("A", "B"));
        assertTrue(g.hasEdge("B", "A")); // undirected
        assertFalse(g.hasEdge("A", "C"));
    }

    @Test
    void duplicateEdgeNotAddedTwice() {
        Graph g = sample();
        g.addEdge("A", "B", 5.0);
        assertEquals(3, g.edgeCount());
    }

    @Test
    void negativeWeightRejected() {
        Graph g = new Graph();
        assertThrows(IllegalArgumentException.class, () -> g.addEdge("A", "B", -1));
        assertThrows(IllegalArgumentException.class, () -> g.addVertex(null));
    }

    @Test
    void adjacencyMatrixReflectsEdges() {
        Graph g = sample();
        double[][] matrix = g.adjacencyMatrix();
        // order of vertices is insertion order: A, B, C, D
        assertEquals(0.0, matrix[0][0]);
        assertEquals(1.0, matrix[0][1]);
        assertEquals(1.0, matrix[1][0]);
        assertEquals(2.0, matrix[1][2]);
        assertEquals(Double.POSITIVE_INFINITY, matrix[0][3]);
    }

    @Test
    void edgesRetrievedInOrder() {
        Graph g = new Graph();
        g.addEdge("X", "Y", 3.0);
        g.addEdge("X", "Z", 4.0);
        LinkedList<Graph.Edge> edges = g.getEdges("X");
        assertNotNull(edges);
        assertEquals(2, edges.size());
        assertEquals("Y", edges.get(0).to);
        assertEquals("Z", edges.get(1).to);
    }

    @Test
    void emptyGraph() {
        Graph g = new Graph();
        assertTrue(g.isEmpty());
        assertEquals(0, g.vertexCount());
        assertEquals(0, g.edgeCount());
        assertEquals(0, g.adjacencyMatrix().length);
    }

    @Test
    void dynamicArrayAndLinkedListIteration() {
        DynamicArray<Integer> da = new DynamicArray<>();
        da.add(1);
        da.add(2);
        da.add(3);
        int sum = 0;
        for (Integer v : da) sum += v;
        assertEquals(6, sum);

        LinkedList<String> ll = new LinkedList<>();
        ll.addFirst("b");
        ll.addFirst("a");
        StringBuilder sb = new StringBuilder();
        for (String s : ll) sb.append(s);
        assertEquals("ab", sb.toString());

        ArrayList<String> al = new ArrayList<>();
        al.add("x");
        al.add("y");
        int count = 0;
        for (String s : al) count++;
        assertEquals(2, count);
    }
}
