package servicehub.algorithms;

import servicehub.ds.DynamicArray;
import servicehub.ds.Graph;
import servicehub.ds.HashTable;
import servicehub.ds.PriorityQueue;
import servicehub.ds.Queue;
import servicehub.ds.Stack;
import servicehub.ds.UnionFind;

/**
 * Graph algorithms implemented entirely with the project's custom
 * data structures: BFS, DFS, Dijkstra, Prim and Kruskal.
 */
public class GraphAlgorithms {

    /** Result of a shortest-path query. */
    public static class RouteResult {
        public final DynamicArray<String> path;
        public final double totalWeight;

        public RouteResult(DynamicArray<String> path, double totalWeight) {
            this.path = path;
            this.totalWeight = totalWeight;
        }

        public boolean isReachable() {
            return path != null && path.size() > 0;
        }

        public String pathToString() {
            if (path == null || path.size() == 0) return "NO ROUTE";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) sb.append(" -> ");
                sb.append(path.get(i));
            }
            return sb.toString();
        }
    }

    /** Result of a minimum-spanning-tree computation. */
    public static class MSTResult {
        public final DynamicArray<Edge> edges;
        public final double totalWeight;

        public MSTResult(DynamicArray<Edge> edges, double totalWeight) {
            this.edges = edges;
            this.totalWeight = totalWeight;
        }
    }

    /** Comparable edge used by Kruskal's sort. */
    public static class Edge implements Comparable<Edge> {
        public final String from;
        public final String to;
        public final double weight;

        public Edge(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.weight, other.weight);
        }

        @Override
        public String toString() {
            return from + " -- " + to + " (" + weight + ")";
        }
    }

    private static final class DistEntry {
        final String vertex;
        final double distance;

        DistEntry(String vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    /**
     * Breadth-first traversal order starting from {@code start}.
     */
    public static DynamicArray<String> bfs(Graph graph, String start) {
        DynamicArray<String> order = new DynamicArray<>();
        if (start == null || !graph.hasVertex(start)) return order;

        HashTable<String, Boolean> visited = new HashTable<>();
        Queue<String> queue = new Queue<>();
        visited.put(start, true);
        queue.enqueue(start);

        while (!queue.isEmpty()) {
            String v = queue.dequeue();
            order.add(v);
            servicehub.ds.LinkedList<Graph.Edge> edges = graph.getEdges(v);
            if (edges != null) {
                for (Graph.Edge e : edges) {
                    if (visited.get(e.to) == null) {
                        visited.put(e.to, true);
                        queue.enqueue(e.to);
                    }
                }
            }
        }
        return order;
    }

    /**
     * Depth-first traversal order starting from {@code start}.
     */
    public static DynamicArray<String> dfs(Graph graph, String start) {
        DynamicArray<String> order = new DynamicArray<>();
        if (start == null || !graph.hasVertex(start)) return order;

        HashTable<String, Boolean> visited = new HashTable<>();
        Stack<String> stack = new Stack<>();
        visited.put(start, true);
        stack.push(start);

        while (!stack.isEmpty()) {
            String v = stack.pop();
            order.add(v);
            servicehub.ds.LinkedList<Graph.Edge> edges = graph.getEdges(v);
            if (edges != null) {
                for (Graph.Edge e : edges) {
                    if (visited.get(e.to) == null) {
                        visited.put(e.to, true);
                        stack.push(e.to);
                    }
                }
            }
        }
        return order;
    }

    /**
     * Dijkstra's shortest paths from {@code start}. Returns distance map.
     */
    public static servicehub.ds.Map<String, Double> dijkstra(Graph graph, String start) {
        servicehub.ds.Map<String, Double> distances = new servicehub.ds.Map<>();
        if (start == null || !graph.hasVertex(start)) return distances;

        DynamicArray<String> vertices = graph.getVertices();
        PriorityQueue<DistEntry> pq = new PriorityQueue<>(
                Math.max(1, vertices.size()),
                (a, b) -> Double.compare(a.distance, b.distance));
        HashTable<String, Double> dist = new HashTable<>();

        for (int i = 0; i < vertices.size(); i++) {
            dist.put(vertices.get(i), Double.MAX_VALUE);
        }
        dist.put(start, 0.0);
        pq.insert(new DistEntry(start, 0.0));

        while (!pq.isEmpty()) {
            DistEntry current = pq.extract();
            String u = current.vertex;
            double d = current.distance;
            if (d > dist.get(u)) continue; // stale entry

            servicehub.ds.LinkedList<Graph.Edge> edges = graph.getEdges(u);
            if (edges == null) continue;
            for (Graph.Edge e : edges) {
                double newDist = d + e.weight;
                if (newDist < dist.get(e.to)) {
                    dist.put(e.to, newDist);
                    pq.insert(new DistEntry(e.to, newDist));
                }
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            distances.put(vertices.get(i), dist.get(vertices.get(i)));
        }
        return distances;
    }

    /**
     * Dijkstra's shortest path from {@code start} to {@code target}.
     */
    public static RouteResult shortestPath(Graph graph, String start, String target) {
        if (start == null || target == null || !graph.hasVertex(start) || !graph.hasVertex(target)) {
            return new RouteResult(null, Double.MAX_VALUE);
        }
        DynamicArray<String> vertices = graph.getVertices();
        PriorityQueue<DistEntry> pq = new PriorityQueue<>(
                Math.max(1, vertices.size()),
                (a, b) -> Double.compare(a.distance, b.distance));
        HashTable<String, Double> dist = new HashTable<>();
        HashTable<String, String> prev = new HashTable<>();

        for (int i = 0; i < vertices.size(); i++) {
            dist.put(vertices.get(i), Double.MAX_VALUE);
        }
        dist.put(start, 0.0);
        pq.insert(new DistEntry(start, 0.0));

        while (!pq.isEmpty()) {
            DistEntry current = pq.extract();
            String u = current.vertex;
            if (u.equals(target)) break;
            if (current.distance > dist.get(u)) continue;

            servicehub.ds.LinkedList<Graph.Edge> edges = graph.getEdges(u);
            if (edges == null) continue;
            for (Graph.Edge e : edges) {
                double newDist = dist.get(u) + e.weight;
                if (newDist < dist.get(e.to)) {
                    dist.put(e.to, newDist);
                    prev.put(e.to, u);
                    pq.insert(new DistEntry(e.to, newDist));
                }
            }
        }

        if (!dist.containsKey(target) || dist.get(target) == Double.MAX_VALUE) {
            return new RouteResult(null, Double.MAX_VALUE);
        }

        DynamicArray<String> path = new DynamicArray<>();
        Stack<String> reverse = new Stack<>();
        String step = target;
        reverse.push(step);
        while (prev.containsKey(step)) {
            step = prev.get(step);
            reverse.push(step);
        }
        while (!reverse.isEmpty()) {
            path.add(reverse.pop());
        }
        return new RouteResult(path, dist.get(target));
    }

    /**
     * Prim's minimum spanning tree (adjacency-list based, O(V^2) selection).
     */
    public static MSTResult primMST(Graph graph) {
        DynamicArray<String> vertices = graph.getVertices();
        int n = vertices.size();
        if (n == 0) return new MSTResult(new DynamicArray<>(), 0.0);

        HashTable<String, Double> key = new HashTable<>();
        HashTable<String, String> parent = new HashTable<>();
        HashTable<String, Boolean> inMST = new HashTable<>();

        for (int i = 0; i < n; i++) key.put(vertices.get(i), Double.MAX_VALUE);
        key.put(vertices.get(0), 0.0);

        DynamicArray<Edge> mstEdges = new DynamicArray<>();
        double total = 0.0;

        for (int count = 0; count < n; count++) {
            String u = null;
            double minKey = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                String v = vertices.get(i);
                if (inMST.get(v) == null && key.get(v) < minKey) {
                    minKey = key.get(v);
                    u = v;
                }
            }
            if (u == null) break;
            inMST.put(u, true);
            total += minKey;
            if (parent.containsKey(u)) {
                mstEdges.add(new Edge(parent.get(u), u, minKey));
            }

            servicehub.ds.LinkedList<Graph.Edge> edges = graph.getEdges(u);
            if (edges == null) continue;
            for (Graph.Edge e : edges) {
                if (inMST.get(e.to) == null && e.weight < key.get(e.to)) {
                    key.put(e.to, e.weight);
                    parent.put(e.to, u);
                }
            }
        }
        return new MSTResult(mstEdges, total);
    }

    /**
     * Kruskal's minimum spanning tree using the custom UnionFind and merge sort.
     */
    public static MSTResult kruskalMST(Graph graph) {
        DynamicArray<Edge> allEdges = new DynamicArray<>();
        DynamicArray<String> vertices = graph.getVertices();
        int n = vertices.size();

        HashTable<String, Integer> vertexIndex = new HashTable<>();
        for (int i = 0; i < n; i++) vertexIndex.put(vertices.get(i), i);

        for (int i = 0; i < n; i++) {
            String v = vertices.get(i);
            servicehub.ds.LinkedList<Graph.Edge> edges = graph.getEdges(v);
            if (edges == null) continue;
            for (Graph.Edge e : edges) {
                // only keep one copy of each undirected edge
                if (e.from.compareTo(e.to) < 0) {
                    allEdges.add(new Edge(e.from, e.to, e.weight));
                }
            }
        }

        Edge[] edgeArray = new Edge[allEdges.size()];
        for (int i = 0; i < allEdges.size(); i++) edgeArray[i] = allEdges.get(i);
        servicehub.algorithms.Sort.mergeSort(edgeArray);

        UnionFind uf = new UnionFind(n);
        DynamicArray<Edge> mstEdges = new DynamicArray<>();
        double total = 0.0;

        for (Edge e : edgeArray) {
            int ri = vertexIndex.get(e.from);
            int rj = vertexIndex.get(e.to);
            if (!uf.connected(ri, rj)) {
                uf.union(ri, rj);
                mstEdges.add(e);
                total += e.weight;
            }
        }
        return new MSTResult(mstEdges, total);
    }
}
