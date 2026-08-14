package servicehub;

import servicehub.ds.Graph;

public class DatabaseConnection {

    private final String url;

    public DatabaseConnection(String url) {
        this.url = url;
    }

    public static DatabaseConnection forDefaultDb() {
        return new DatabaseConnection("jdbc:sqlite:../db/service_hub.db");
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void initializeSchema() throws SQLException {
        try (Connection conn = open(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS services ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL,"
                    + "category TEXT NOT NULL)");
        }
    }
}
