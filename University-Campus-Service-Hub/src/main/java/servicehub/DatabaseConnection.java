// To be deprecated

package servicehub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private final String url;

    public DatabaseConnection(String url) {
        this.url = url;
    }

    public static DatabaseConnection forDefaultDb() {
        return new DatabaseConnection("jdbc:sqlite:../db/schema.sql");
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void initializeSchema() throws SQLException {
        try (Connection conn = open(); 
        Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS services ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL,"
                    + "category TEXT NOT NULL)");
        }
    }
}
