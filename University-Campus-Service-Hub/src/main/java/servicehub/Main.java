package servicehub;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== University Campus Service Hub ===\n");


        // Initialize database connection
        DatabaseConnection db = DatabaseConnection.forDefaultDb();
        db.initializeSchema();

        System.out.println("✓ Database connected and schema initialized");

        
    }
}
