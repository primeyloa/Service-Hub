package servicehub;

import servicehub.ds.ArrayList;
import servicehub.model.AuditEvent;
import servicehub.model.Location;
import servicehub.model.Resource;
import servicehub.model.Road;
import servicehub.model.ServiceRequest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central database access for the Campus Service Hub.
 *
 * <p>The application uses a single SQLite file (db/service_hub.db) that the
 * program creates and maintains itself. Tables mirror the project schema:
 * locations, roads, resources, service_requests, algorithm_runs and
 * audit_events.</p>
 */
public class DatabaseManager {

    private static final String DB_FILE = "db/service_hub.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    static {
        try {
            Files.createDirectories(Paths.get("db"));
        } catch (Exception ignored) {
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static String getDatabasePath() {
        return DB_FILE;
    }

    /** Creates all tables if they do not already exist. */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS locations (
                        location_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        area TEXT NOT NULL,
                        location_type TEXT,
                        x_coord REAL,
                        y_coord REAL
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS roads (
                        road_id TEXT PRIMARY KEY,
                        from_location_id TEXT NOT NULL,
                        to_location_id TEXT NOT NULL,
                        distance_km REAL,
                        travel_time_min REAL,
                        condition_weight REAL
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS resources (
                        resource_id TEXT PRIMARY KEY,
                        name TEXT,
                        resource_type TEXT NOT NULL,
                        home_location_id TEXT NOT NULL,
                        capacity INTEGER,
                        availability_status TEXT
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS service_requests (
                        request_id TEXT PRIMARY KEY,
                        source_location_id TEXT NOT NULL,
                        destination_location_id TEXT,
                        category TEXT,
                        urgency INTEGER,
                        time_submitted TEXT,
                        deadline TEXT,
                        status TEXT,
                        cost REAL
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS algorithm_runs (
                        run_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        algorithm_name TEXT,
                        input_size INTEGER,
                        time_ns INTEGER,
                        memory_kb INTEGER,
                        date_run TEXT
                    )""");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS audit_events (
                        event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        action TEXT,
                        details TEXT,
                        timestamp TEXT
                    )""");
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    public static ArrayList<Location> loadLocations() {
        ArrayList<Location> result = new ArrayList<>();
        String sql = "SELECT location_id, name, area, location_type, x_coord, y_coord FROM locations";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new Location(
                        rs.getString("location_id"),
                        rs.getString("name"),
                        rs.getString("area"),
                        rs.getString("location_type"),
                        rs.getDouble("x_coord"),
                        rs.getDouble("y_coord")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading locations: " + e.getMessage());
        }
        return result;
    }

    public static ArrayList<Road> loadRoads() {
        ArrayList<Road> result = new ArrayList<>();
        String sql = "SELECT road_id, from_location_id, to_location_id, distance_km, travel_time_min, condition_weight FROM roads";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new Road(
                        rs.getString("road_id"),
                        rs.getString("from_location_id"),
                        rs.getString("to_location_id"),
                        rs.getDouble("distance_km"),
                        rs.getDouble("travel_time_min"),
                        rs.getDouble("condition_weight")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading roads: " + e.getMessage());
        }
        return result;
    }

    public static ArrayList<Resource> loadResources() {
        ArrayList<Resource> result = new ArrayList<>();
        String sql = "SELECT resource_id, name, resource_type, home_location_id, capacity, availability_status FROM resources";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                result.add(new Resource(
                        rs.getString("resource_id"),
                        rs.getString("resource_type"),
                        rs.getString("home_location_id"),
                        rs.getInt("capacity"),
                        rs.getString("availability_status"),
                        name != null ? name : rs.getString("resource_type") + " " + rs.getString("resource_id")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading resources: " + e.getMessage());
        }
        return result;
    }

    public static ArrayList<ServiceRequest> loadServiceRequests() {
        ArrayList<ServiceRequest> result = new ArrayList<>();
        String sql = "SELECT request_id, source_location_id, destination_location_id, category, urgency, time_submitted, deadline, status, cost FROM service_requests";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                double cost = rs.getDouble("cost");
                if (cost == 0.0) cost = ServiceRequest.defaultCost(rs.getInt("urgency"));
                result.add(new ServiceRequest(
                        rs.getString("request_id"),
                        rs.getString("source_location_id"),
                        rs.getString("destination_location_id"),
                        rs.getString("category"),
                        rs.getInt("urgency"),
                        rs.getString("time_submitted"),
                        rs.getString("deadline"),
                        rs.getString("status"),
                        cost));
            }
        } catch (SQLException e) {
            System.err.println("Error loading service requests: " + e.getMessage());
        }
        return result;
    }

    public static ArrayList<AuditEvent> loadAuditEvents() {
        ArrayList<AuditEvent> result = new ArrayList<>();
        String sql = "SELECT event_id, action, details, timestamp FROM audit_events ORDER BY event_id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new AuditEvent(
                        rs.getInt("event_id"),
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getString("timestamp")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading audit events: " + e.getMessage());
        }
        return result;
    }

    public static void insertServiceRequest(ServiceRequest r) {
        String sql = """
                INSERT OR REPLACE INTO service_requests
                (request_id, source_location_id, destination_location_id, category, urgency, time_submitted, deadline, status, cost)
                VALUES (?,?,?,?,?,?,?,?,?)""";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRequestId());
            ps.setString(2, r.getSourceLocationId());
            ps.setString(3, r.getDestinationLocationId());
            ps.setString(4, r.getCategory());
            ps.setInt(5, r.getUrgency());
            ps.setString(6, r.getTimeSubmitted());
            ps.setString(7, r.getDeadline());
            ps.setString(8, r.getStatus());
            ps.setDouble(9, r.getCost());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting service request: " + e.getMessage());
        }
    }

    public static void updateServiceRequestStatus(String requestId, String status) {
        String sql = "UPDATE service_requests SET status = ? WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
        }
    }

    public static void updateResourceStatus(String resourceId, String status) {
        String sql = "UPDATE resources SET availability_status = ? WHERE resource_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, resourceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating resource status: " + e.getMessage());
        }
    }

    public static void insertAuditEvent(AuditEvent event) {
        String sql = "INSERT INTO audit_events(action, details, timestamp) VALUES (?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getAction());
            ps.setString(2, event.getDetails());
            ps.setString(3, event.getTimestamp());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting audit event: " + e.getMessage());
        }
    }

    /** Empties all tables (used before reseeding). */
    public static void clearAllTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String table : new String[]{"audit_events", "algorithm_runs", "service_requests", "resources", "roads", "locations"}) {
                stmt.execute("DELETE FROM " + table);
            }
        } catch (SQLException e) {
            System.err.println("Error clearing tables: " + e.getMessage());
        }
    }
}
