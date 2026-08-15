package servicehub;

import servicehub.ui.ServiceHubGUI;

/**
 * Application entry point. Initialises the SQLite database, seeds it from the
 * local Legon campus dataset (if empty) and launches the Swing simulation
 * interface.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== University Campus Service Hub ===");
        System.out.println("Context: University of Ghana, Legon Campus");
        System.out.println("Database: " + DatabaseManager.getDatabasePath());

        DatabaseManager.initializeDatabase();
        System.out.println("Database connected and schema initialized");

        CsvSeeder.seedDatabaseFromCsv();

        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            servicehub.cli.Main.run();
        } else {
            ServiceHubGUI.launch();
        }
    }
}
