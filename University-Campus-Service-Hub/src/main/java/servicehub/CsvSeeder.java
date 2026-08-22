package servicehub;

import servicehub.model.AuditEvent;
import servicehub.model.Location;
import servicehub.model.Resource;
import servicehub.model.Road;
import servicehub.model.ServiceRequest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Seeds the SQLite database from the project CSV datasets (Ghana Legon Campus).
 * Only seeds tables that are currently empty.
 */
public class CsvSeeder {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private static String resolveCsv(String name) {
        Path fromModule = Paths.get("../data/" + name);
        Path fromRoot = Paths.get("data/" + name);
        if (Files.exists(fromModule)) return fromModule.toString();
        if (Files.exists(fromRoot)) return fromRoot.toString();
        return null;
    }

    public static void seedDatabaseFromCsv() {
        DatabaseManager.initializeDatabase();

        if (DatabaseManager.loadLocations().isEmpty()) seedLocations();
        if (DatabaseManager.loadRoads().isEmpty()) seedRoads();
        if (DatabaseManager.loadResources().isEmpty()) seedResources();
        if (DatabaseManager.loadServiceRequests().isEmpty()) seedServiceRequests();

        DatabaseManager.insertAuditEvent(new AuditEvent("SEED", "Database seeded from local Legon campus dataset", now()));
        System.out.println("Database seeded successfully from CSV files.");
    }

    private static String now() {
        return LocalDateTime.now().format(TS);
    }

    private static void seedLocations() {
        String path = resolveCsv("locations_template.csv");
        if (path == null) return;
        String sql = "INSERT OR REPLACE INTO locations(location_id, name, area, location_type, x_coord, y_coord) VALUES(?,?,?,?,?,?)";
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = splitCsv(line);
                if (p.length >= 6) {
                    ps.setString(1, p[0].trim());
                    ps.setString(2, p[1].trim());
                    ps.setString(3, p[2].trim());
                    ps.setString(4, p[3].trim());
                    ps.setDouble(5, Double.parseDouble(p[4].trim()));
                    ps.setDouble(6, Double.parseDouble(p[5].trim()));
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding locations: " + e.getMessage());
        }
    }

    private static void seedRoads() {
        String path = resolveCsv("roads_template.csv");
        if (path == null) return;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = splitCsv(line);
                if (p.length >= 6) {
                    String sql = "INSERT OR REPLACE INTO roads(road_id, from_location_id, to_location_id, distance_km, travel_time_min, condition_weight) VALUES(?,?,?,?,?,?)";
                    try (var conn = DatabaseManager.getConnection();
                         var ps = conn.prepareStatement(sql)) {
                        ps.setString(1, p[0].trim());
                        ps.setString(2, p[1].trim());
                        ps.setString(3, p[2].trim());
                        ps.setDouble(4, Double.parseDouble(p[3].trim()));
                        ps.setDouble(5, Double.parseDouble(p[4].trim()));
                        ps.setDouble(6, Double.parseDouble(p[5].trim()));
                        ps.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding roads: " + e.getMessage());
        }
    }

    private static void seedResources() {
        String path = resolveCsv("resources_template.csv");
        if (path == null) return;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = splitCsv(line);
                if (p.length >= 5) {
                    String type = p[1].trim();
                    String id = p[0].trim();
                    Resource resource = new Resource(
                            id, type, p[2].trim(), Integer.parseInt(p[3].trim()),
                            p[4].trim(), type + " " + id);
                    insertResource(resource);
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding resources: " + e.getMessage());
        }
    }

    private static void seedServiceRequests() {
        String path = resolveCsv("service_requests_template.csv");
        if (path == null) return;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = splitCsv(line);
                if (p.length >= 8) {
                    int urgency = Integer.parseInt(p[4].trim());
                    ServiceRequest request = new ServiceRequest(
                            p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
                            urgency, p[5].trim(), p[6].trim(), p[7].trim(),
                            ServiceRequest.defaultCost(urgency));
                    DatabaseManager.insertServiceRequest(request);
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding service requests: " + e.getMessage());
        }
    }

    private static void insertResource(Resource resource) {
        String sql = "INSERT OR REPLACE INTO resources(resource_id, name, resource_type, home_location_id, capacity, availability_status) VALUES(?,?,?,?,?,?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, resource.getResourceId());
            ps.setString(2, resource.getName());
            ps.setString(3, resource.getResourceType());
            ps.setString(4, resource.getHomeLocationId());
            ps.setInt(5, resource.getCapacity());
            ps.setString(6, resource.getAvailabilityStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error inserting resource: " + e.getMessage());
        }
    }

    /** Rudimentary CSV splitter that tolerates quoted values. */
    private static String[] splitCsv(String line) {
        if (!line.contains("\"")) return line.split(",");
        java.util.List<String> tokens = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }
}
