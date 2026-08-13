package servicehub;

import java.io.*;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.nio.file.Files;
import java.nio.file.Paths;


public class CsvSeeder {

    public static void seedDatabaseFromCsv() {
        DatabaseManager.initializeDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            seedLocations(conn);
            seedRoads(conn);
            seedResources(conn);
            seedServiceRequests(conn);

            conn.commit();
            System.out.println("Database seeded successfully from CSV files.");
        } catch (Exception e) {
            System.err.println("Error seeding database: " + e.getMessage());
        }
    }

    private static void seedLocations(Connection conn) {
        String path = "data/locations.csv";
        if (!Files.exists(Paths.get(path))) return;
        String sql = "INSERT OR REPLACE INTO locations(location_id, name, area, location_type, x_coord, y_coord) VALUES(?,?,?,?,?,?)";
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    pstmt.setString(1, parts[0].trim());
                    pstmt.setString(2, parts[1].trim());
                    pstmt.setString(3, parts[2].trim());
                    pstmt.setString(4, parts[3].trim());
                    pstmt.setDouble(5, Double.parseDouble(parts[4].trim()));
                    pstmt.setDouble(6, Double.parseDouble(parts[5].trim()));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding locations: " + e.getMessage());
        }
    }

    private static void seedRoads(Connection conn) {
        String path = "data/roads.csv";
        if (!Files.exists(Paths.get(path))) return;
        String sql = "INSERT OR REPLACE INTO roads(road_id, from_location_id, to_location_id, distance_km, travel_time_min, condition_weight) VALUES(?,?,?,?,?,?)";
        try (BufferedReader br = new BufferedReader(new FileReader(path));
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    pstmt.setString(1, parts[0].trim());
                    pstmt.setString(2, parts[1].trim());
                    pstmt.setString(3, parts[2].trim());
                    pstmt.setDouble(4, Double.parseDouble(parts[3].trim()));
                    pstmt.setDouble(5, Double.parseDouble(parts[4].trim()));
                    pstmt.setDouble(6, Double.parseDouble(parts[5].trim()));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding roads: " + e.getMessage());
        }
    }

    private static void seedResources(Connection conn) {
        String path = "data/resources.csv";
        if (!Files.exists(Paths.get(path))) return;
        String sql = "INSERT OR REPLACE INTO resources(resource_id, resource_type, home_location_id, capacity, availability_status) VALUES(?,?,?,?,?)";
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    pstmt.setString(1, parts[0].trim());
                    pstmt.setString(2, parts[1].trim());
                    pstmt.setString(3, parts[2].trim());
                    pstmt.setInt(4, Integer.parseInt(parts[3].trim()));
                    pstmt.setString(5, parts[4].trim());
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding resources: " + e.getMessage());
        }
    }

    private static void seedServiceRequests(Connection conn) {
        String path = "data/service_requests.csv";
        if (!Files.exists(Paths.get(path))) return;
        String sql = "INSERT OR REPLACE INTO service_requests(request_id, source_location_id, destination_location_id, category, urgency, time_submitted, deadline, status, cost) VALUES(?,?,?,?,?,?,?,?,?)";
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    pstmt.setString(1, parts[0].trim());
                    pstmt.setString(2, parts[1].trim());
                    pstmt.setString(3, parts[2].trim());
                    pstmt.setString(4, parts[3].trim());
                    pstmt.setInt(5, Integer.parseInt(parts[4].trim()));
                    pstmt.setString(6, parts[5].trim());
                    pstmt.setString(7, parts[6].trim());
                    pstmt.setString(8, parts[7].trim());
                    pstmt.setDouble(9, Double.parseDouble(parts[8].trim()));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding service requests: " + e.getMessage());
        }
    }
}
