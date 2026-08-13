package servicehub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:schema.sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Read schema.sql if exists or execute DDL directly
            String schemaPath = "data/schema.sql";
            if (Files.exists(Paths.get(schemaPath))) {
                String schemaSql = new String(Files.readAllBytes(Paths.get(schemaPath)));
                for (String sqlStatement : schemaSql.split(";")) {
                    if (!sqlStatement.trim().isEmpty()) {
                        stmt.execute(sqlStatement);
                    }
                }
            } else {
                stmt.execute("CREATE TABLE IF NOT EXISTS locations (location_id TEXT PRIMARY KEY, name TEXT, area TEXT, location_type TEXT, x_coord REAL, y_coord REAL);");
                stmt.execute("CREATE TABLE IF NOT EXISTS roads (road_id TEXT PRIMARY KEY, from_location_id TEXT, to_location_id TEXT, distance_km REAL, travel_time_min REAL, condition_weight REAL);");
                stmt.execute("CREATE TABLE IF NOT EXISTS service_requests (request_id TEXT PRIMARY KEY, source_location_id TEXT, destination_location_id TEXT, category TEXT, urgency INTEGER, time_submitted TEXT, deadline TEXT, status TEXT, cost REAL);");
                stmt.execute("CREATE TABLE IF NOT EXISTS resources (resource_id TEXT PRIMARY KEY, resource_type TEXT, home_location_id TEXT, capacity INTEGER, availability_status TEXT);");
                stmt.execute("CREATE TABLE IF NOT EXISTS algorithm_runs (run_id INTEGER PRIMARY KEY AUTOINCREMENT, algorithm_name TEXT, input_size INTEGER, time_ns INTEGER, memory_kb INTEGER, date_run TEXT);");
                stmt.execute("CREATE TABLE IF NOT EXISTS audit_events (event_id INTEGER PRIMARY KEY AUTOINCREMENT, action TEXT, details TEXT, timestamp TEXT);");
            }
            System.out.println("Database schema initialized successfully.");
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
}
