package servicehub.correctness;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import servicehub.DatabaseManager;
import servicehub.ds.ArrayList;
import servicehub.model.AuditEvent;
import servicehub.model.Location;
import servicehub.model.Resource;
import servicehub.model.Road;
import servicehub.model.ServiceRequest;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @BeforeEach
    void setUp() {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearAllTables();
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.clearAllTables();
    }

    @Test
    void roundTripsAllEntityTypes() {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO locations(location_id, name, area, location_type, x_coord, y_coord) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, "L001");
            ps.setString(2, "Balme Library");
            ps.setString(3, "Legon");
            ps.setString(4, "Library");
            ps.setDouble(5, 5.65);
            ps.setDouble(6, 0.19);
            ps.executeUpdate();
        } catch (Exception e) {
            fail("location insert failed: " + e.getMessage());
        }

        ArrayList<Location> locations = DatabaseManager.loadLocations();
        assertEquals(1, locations.size());
        assertEquals("Balme Library", locations.get(0).getName());

        DatabaseManager.insertServiceRequest(new ServiceRequest(
                "Q001", "L001", "L002", "Maintenance", 5, "2026-07-01T08:00", "2026-07-01T12:00", "NEW", 700));
        ArrayList<ServiceRequest> requests = DatabaseManager.loadServiceRequests();
        assertEquals(1, requests.size());
        assertEquals("Q001", requests.get(0).getRequestId());
        assertEquals(5, requests.get(0).getUrgency());

        DatabaseManager.updateServiceRequestStatus("Q001", "ASSIGNED");
        assertEquals("ASSIGNED", DatabaseManager.loadServiceRequests().get(0).getStatus());

        insertResource();
        assertEquals(1, DatabaseManager.loadResources().size());

        insertRoad();
        assertEquals(1, DatabaseManager.loadRoads().size());

        DatabaseManager.insertAuditEvent(new AuditEvent("DISPATCH", "Q001 dispatched", "2026-07-01T08:01"));
        assertEquals(1, DatabaseManager.loadAuditEvents().size());
    }

    private void insertResource() {
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
        } catch (Exception e) {
            fail("resource insert failed: " + e.getMessage());
        }
    }

    private void insertRoad() {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO roads(road_id, from_location_id, to_location_id, distance_km, travel_time_min, condition_weight) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, "R1");
            ps.setString(2, "L001");
            ps.setString(3, "L002");
            ps.setDouble(4, 0.6);
            ps.setDouble(5, 4.0);
            ps.setDouble(6, 1.0);
            ps.executeUpdate();
        } catch (Exception e) {
            fail("road insert failed: " + e.getMessage());
        }
    }
}
