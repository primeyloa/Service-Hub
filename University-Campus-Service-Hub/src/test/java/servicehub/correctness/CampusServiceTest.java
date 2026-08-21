package servicehub.correctness;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import servicehub.DatabaseManager;
import servicehub.algorithms.GraphAlgorithms;
import servicehub.ds.ArrayList;
import servicehub.model.DispatchRecord;
import servicehub.model.Location;
import servicehub.model.Resource;
import servicehub.model.Road;
import servicehub.model.ServiceRequest;
import servicehub.service.CampusService;

import static org.junit.jupiter.api.Assertions.*;

class CampusServiceTest {

    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearAllTables();

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO locations(location_id, name, area, location_type, x_coord, y_coord) VALUES(?,?,?,?,?,?)")) {
            for (String[] loc : new String[][]{
                    {"L001", "Balme Library", "Legon", "Library", "5.65", "0.19"},
                    {"L002", "CS Department", "Legon", "Academic", "5.651", "0.188"},
                    {"L003", "UG Hospital", "Legon", "Health", "5.655", "0.185"}}) {
                ps.setString(1, loc[0]);
                ps.setString(2, loc[1]);
                ps.setString(3, loc[2]);
                ps.setString(4, loc[3]);
                ps.setDouble(5, Double.parseDouble(loc[4]));
                ps.setDouble(6, Double.parseDouble(loc[5]));
                ps.executeUpdate();
            }
        }
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO roads(road_id, from_location_id, to_location_id, distance_km, travel_time_min, condition_weight) VALUES(?,?,?,?,?,?)")) {
            for (String[] road : new String[][]{
                    {"R1", "L001", "L002", "0.6", "4", "1.0"},
                    {"R2", "L002", "L003", "1.2", "8", "1.0"},
                    {"R3", "L001", "L003", "1.5", "10", "1.0"}}) {
                ps.setString(1, road[0]);
                ps.setString(2, road[1]);
                ps.setString(3, road[2]);
                ps.setDouble(4, Double.parseDouble(road[3]));
                ps.setDouble(5, Double.parseDouble(road[4]));
                ps.setDouble(6, Double.parseDouble(road[5]));
                ps.executeUpdate();
            }
        }
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO resources(resource_id, name, resource_type, home_location_id, capacity, availability_status) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, "R001");
            ps.setString(2, "Rider R001");
            ps.setString(3, "Rider");
            ps.setString(4, "L001");
            ps.setInt(5, 1);
            ps.setString(6, "AVAILABLE");
            ps.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.clearAllTables();
    }

    @Test
    void loadsSeededData() {
        CampusService service = new CampusService();
        assertEquals(3, service.getLocations().size());
        assertEquals(3, service.getRoads().size());
        assertEquals(1, service.getResources().size());
        assertEquals("Balme Library", service.locationName("L001"));
    }

    @Test
    void availableResourcesOnly() {
        CampusService service = new CampusService();
        assertEquals(1, service.availableResources().size());
        DatabaseManager.updateResourceStatus("R001", "BUSY");
        service.reload();
        assertEquals(0, service.availableResources().size());
    }

    @Test
    void createsAndPersistsRequest() {
        CampusService service = new CampusService();
        int before = service.getRequests().size();
        service.addRequest(new ServiceRequest("Q100", "L001", "L003", "Medical Transport", 5,
                "2026-07-01T08:00", "2026-07-01T09:00", "NEW", 700));
        assertEquals(before + 1, service.getRequests().size());
        assertEquals(before + 1, DatabaseManager.loadServiceRequests().size());
        assertEquals("Q100", DatabaseManager.loadServiceRequests().get(before).getRequestId());
    }

    @Test
    void dispatchesNearestResourceAndLogsAudit() {
        CampusService service = new CampusService();
        service.addRequest(new ServiceRequest("Q200", "L003", "L001", "Medical Transport", 5,
                "2026-07-01T08:00", "2026-07-01T09:00", "NEW", 700));
        service.reload();

        ServiceRequest request = service.pendingRequests().get(0);
        Resource resource = service.nearestResource(request);
        assertNotNull(resource);
        assertEquals("R001", resource.getResourceId());

        int auditBefore = service.getAuditLog().size();
        DispatchRecord record = service.dispatch(request, resource);
        assertEquals("Q200", record.getRequestId());
        assertEquals("R001", record.getResourceId());
        assertTrue(record.getRouteSummary().contains("L001"));

        // resource now busy, request assigned
        assertFalse(resource.isAvailable());
        assertEquals("BUSY", DatabaseManager.loadResources().get(0).getAvailabilityStatus());
        assertEquals("ASSIGNED", DatabaseManager.loadServiceRequests().get(0).getStatus());
        assertEquals(auditBefore + 1, service.getAuditLog().size());
    }

    @Test
    void routingAndOptimisationWork() {
        CampusService service = new CampusService();
        GraphAlgorithms.RouteResult route = service.getRoutingEngine().findShortestPath("L001", "L003");
        assertTrue(route.isReachable());
        assertEquals(10.0, route.totalWeight, 1e-9); // direct L001->L003 (10 min) beats via L002 (12 min)

        service.addRequest(new ServiceRequest("Q300", "L001", "L003", "Maintenance", 3,
                "2026-07-01T08:00", "2026-07-01T12:00", "NEW", 500));
        service.reload();
        ArrayList<ServiceRequest> greedy = service.greedySelection(10_000);
        assertEquals(1, greedy.size());
        ArrayList<ServiceRequest> optimal = service.optimizeWithDP(10_000);
        assertEquals(1, optimal.size());
    }
}
