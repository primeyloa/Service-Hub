package servicehub.engine;
import servicehub.ds.*;
import servicehub.algorithms.*;
import servicehub.algorithms.GraphAlgorithms.*;
import servicehub.ds.ArrayList;
import servicehub.ds.DynamicArray;
import servicehub.ds.Graph;
import servicehub.model.Road;

/**
 * Routing engine over the campus road network. Applies the team's route-penalty
 * parameter for roads in poor condition and exposes shortest-path, reachability
 * and MST queries backed by the custom graph library.
 */
public class RoutingEngine {

    /** Team parameter: multiplier applied when a road is in poor condition. */
    public static final double ROUTE_PENALTY = 7.0;

    /** Roads with condition weight above this threshold are penalised. */
    public static final double CONDITION_THRESHOLD = 1.2;

    private final Graph campusGraph;

    public RoutingEngine(ArrayList<Road> roads) {
        campusGraph = new Graph();
        if (roads == null) return;
        for (int i = 0; i < roads.size(); i++) {
            Road road = roads.get(i);
            double effectiveWeight = road.getTravelTimeMin() * (road.getConditionWeight() > CONDITION_THRESHOLD ? ROUTE_PENALTY : 1.0);
            campusGraph.addEdge(road.getFromLocationId(), road.getToLocationId(), effectiveWeight);
        }
    }

    public Graph getGraph() {
        return campusGraph;
    }

    public Map<String, Double> findShortestPaths(String startLocation) {
        return GraphAlgorithms.dijkstra(campusGraph, startLocation);
    }

    public RouteResult findShortestPath(String from, String to) {
        return GraphAlgorithms.shortestPath(campusGraph, from, to);
    }

    public DynamicArray<String> reachableFrom(String startLocation) {
        return GraphAlgorithms.bfs(campusGraph, startLocation);
    }

    public GraphAlgorithms.MSTResult minimumConnectionNetwork() {
        return GraphAlgorithms.kruskalMST(campusGraph);
    }
}
