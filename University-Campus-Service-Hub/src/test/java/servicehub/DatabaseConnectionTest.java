package servicehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

class DatabaseConnectionTest {

    @Test
    void connectsToSqliteAndRoundTrips() throws Exception {
        Path dbFile = Files.createTempFile("service-hub", ".db");

        DatabaseConnection db = new DatabaseConnection("jdbc:sqlite:" + dbFile);
        db.initializeSchema();

        try (Connection conn = db.open();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO services (name, category) VALUES (?, ?)")) {
            ps.setString(1, "Laundry");
            ps.setString(2, "Housekeeping");
            assertEquals(1, ps.executeUpdate());
        }

        try (Connection conn = db.open();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, category FROM services")) {
            assertTrue(rs.next());
            assertEquals("Laundry", rs.getString("name"));
            assertEquals("Housekeeping", rs.getString("category"));
            assertFalse(rs.next());
        }

        Files.deleteIfExists(dbFile);
    }
}
