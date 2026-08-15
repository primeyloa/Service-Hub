package servicehub.engine;

import org.junit.jupiter.api.Test;
import servicehub.algorithms.GraphAlgorithms;
import servicehub.ds.ArrayList;
import servicehub.model.Road;

import static org.junit.jupiter.api.Assertions.*;

class RoutingEngineTest {

    private ArrayList<Road> roads() {
        ArrayList<Road> list = new ArrayList<>();
        list.add(new Road("R1", "A", "B", 0.5, 5.0, 1.0));
        list.add(new Road("R2", "B", "C", 0.5, 3.0, 1.0));
        list.add(new Road("R3", "A", "C", 2.0, 20.0, 1.0));
        return list;
    }

    @Test
    void findsShortestPath() {
        RoutingEngine engine = new RoutingEngine(roads());
        GraphAlgorithms.RouteResult result = engine.findShortestPath("A", "C");
        assertTrue(result.isReachable());
        // A->B(5) + B->C(3) = 8, cheaper than A->C direct (20)
        assertEquals(8.0, result.totalWeight, 1e-9);
        assertEquals(3, result.path.size());
    }

    @Test
    void poorConditionRoadsArePenalised() {
        ArrayList<Road> list = roads();
        // degrade the direct road so the direct route should be avoided
        list.set(2, new Road("R3", "A", "C", 2.0, 20.0, 1.9));
        RoutingEngine engine = new RoutingEngine(list);
        GraphAlgorithms.RouteResult result = engine.findShortestPath("A", "C");
        // direct 20 * 7 = 140; via B = 8
        assertEquals(8.0, result.totalWeight, 1e-9);
    }

    @Test
    void reachableFromStart() {
        RoutingEngine engine = new RoutingEngine(roads());
        servicehub.ds.DynamicArray<String> reachable = engine.reachableFrom("A");
        assertEquals(3, reachable.size());
        assertTrue(reachable.contains("B"));
        assertTrue(reachable.contains("C"));
    }

    @Test
    void mstConnectsAllVertices() {
        RoutingEngine engine = new RoutingEngine(roads());
        GraphAlgorithms.MSTResult mst = engine.minimumConnectionNetwork();
        assertEquals(2, mst.edges.size());
        // A-B(5) + B-C(3) = 8
        assertEquals(8.0, mst.totalWeight, 1e-9);
    }

    @Test
    void nullRoadsCreatesEmptyGraph() {
        RoutingEngine engine = new RoutingEngine(null);
        assertEquals(0, engine.getGraph().vertexCount());
        assertFalse(engine.findShortestPath("A", "B").isReachable());
    }

    @Test
    void disconnectedLocationsReportUnreachable() {
        ArrayList<Road> list = new ArrayList<>();
        list.add(new Road("R1", "A", "B", 0.5, 5.0, 1.0));
        RoutingEngine engine = new RoutingEngine(list);
        assertFalse(engine.findShortestPath("A", "Z").isReachable());
    }
}
