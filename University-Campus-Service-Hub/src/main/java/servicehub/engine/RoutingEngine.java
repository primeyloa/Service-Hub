package servicehub.engine;

import servicehub.algorithms.GraphAlgorithms;
import servicehub.ds.Graph;
import servicehub.model.Road;
import java.util.List;
import java.util.Map;

public class RoutingEngine {
    private Graph campusGraph;

    public RoutingEngine(List<Road> roads) {
        campusGraph = new Graph();
        for (Road road : roads) {
            // Apply route penalty team parameter (7.0) if condition weight is high or poor
            double effectiveWeight = road.getTravelTimeMin() * (road.getConditionWeight() > 1.2 ? 7.0 : 1.0);
            campusGraph.addEdge(road.getFromLocationId(), road.getToLocationId(), effectiveWeight);
        }
    }

    public Map<String, Double> findShortestPaths(String startLocation) {
        return GraphAlgorithms.dijkstra(campusGraph, startLocation);
    }
}
