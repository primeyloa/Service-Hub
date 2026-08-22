package servicehub.cli;

import servicehub.CsvSeeder;
import servicehub.DatabaseManager;
import servicehub.algorithms.GraphAlgorithms;
import servicehub.ds.ArrayList;
import servicehub.engine.EmpiricalBenchmarker;
import servicehub.engine.ServiceSchedulingEngine;
import servicehub.model.DispatchRecord;
import servicehub.model.Resource;
import servicehub.model.ServiceRequest;
import servicehub.service.CampusService;

import java.util.Scanner;

/**
 * Console menu fallback for the Campus Service Hub (usable when a GUI cannot
 * be shown). Demonstrates request creation, dispatch, routing and benchmarks.
 */
public class Main {

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        CsvSeeder.seedDatabaseFromCsv();
        run();
    }

    public static void run() {
        System.out.println("==================================================");
        System.out.println(" Ghana Smart Campus Service Operations Optimizer  ");
        System.out.println(" University of Ghana, Legon Campus Service Hub    ");
        System.out.println("==================================================");

        CampusService service = new CampusService();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. List pending service requests");
            System.out.println("2. Create a new service request");
            System.out.println("3. Dispatch next request (priority)");
            System.out.println("4. Find shortest route between locations");
            System.out.println("5. Show reachable locations (BFS)");
            System.out.println("6. Minimum connection network (Kruskal)");
            System.out.println("7. Optimal request selection (DP) / greedy counterexample");
            System.out.println("8. Run empirical efficiency benchmarks");
            System.out.println("9. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listPending(service);
                case "2" -> createRequest(service, scanner);
                case "3" -> dispatch(service);
                case "4" -> showRoute(service, scanner);
                case "5" -> showReachable(service, scanner);
                case "6" -> showMst(service);
                case "7" -> {
                    ArrayList<ServiceRequest> selected = service.optimizeWithDP(10500.0);
                    System.out.println("Optimal (DP, GHS 10,500): " + selected.size() + " requests selected");
                    System.out.println("\n" + service.greedyCounterExample());
                }
                case "8" -> EmpiricalBenchmarker.runBenchmarks();
                case "9" -> {
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void listPending(CampusService service) {
        ArrayList<ServiceRequest> pending = service.pendingRequests();
        System.out.println("Pending requests: " + pending.size());
        for (int i = 0; i < pending.size(); i++) {
            ServiceRequest r = pending.get(i);
            System.out.println("  " + r.getRequestId() + "  " + r.getCategory() + "  U" + r.getUrgency()
                    + "  " + service.locationName(r.getSourceLocationId()) + "  GHS " + r.getCost());
        }
    }

    private static void createRequest(CampusService service, Scanner scanner) {
        System.out.print("Source location id (e.g. L001): ");
        String src = scanner.nextLine().trim();
        System.out.print("Destination location id (e.g. L003): ");
        String dst = scanner.nextLine().trim();
        System.out.print("Category (Maintenance/Repair/Cleaning/Medical Transport/IT Support): ");
        String category = scanner.nextLine().trim();
        System.out.print("Urgency (1-5): ");
        int urgency = Integer.parseInt(scanner.nextLine().trim());
        String id = "Q" + (900 + service.getRequests().size() + 1);
        ServiceRequest req = new ServiceRequest(id, src, dst, category, urgency,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")),
                "", "NEW", ServiceRequest.defaultCost(urgency));
        service.addRequest(req);
        System.out.println("Created " + req);
    }

    private static void dispatch(CampusService service) {
        ArrayList<ServiceRequest> pending = service.pendingRequests();
        ArrayList<Resource> available = service.availableResources();
        if (pending.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        if (available.isEmpty()) {
            System.out.println("No available resources.");
            return;
        }
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(pending, ServiceSchedulingEngine.DispatchRule.PRIORITY);
        ServiceRequest next = engine.dispatchNext();
        DispatchRecord record = service.dispatch(next, available.get(0));
        System.out.println("Dispatched: " + record);
        service.reload();
    }

    private static void showRoute(CampusService service, Scanner scanner) {
        System.out.print("From location id: ");
        String from = scanner.nextLine().trim();
        System.out.print("To location id: ");
        String to = scanner.nextLine().trim();
        GraphAlgorithms.RouteResult result = service.getRoutingEngine().findShortestPath(from, to);
        if (result.isReachable()) {
            System.out.println("Path: " + result.pathToString() + "  ETA " + result.totalWeight + " min");
        } else {
            System.out.println("No route found.");
        }
    }

    private static void showReachable(CampusService service, Scanner scanner) {
        System.out.print("From location id: ");
        String from = scanner.nextLine().trim();
        servicehub.ds.DynamicArray<String> reachable = service.getRoutingEngine().reachableFrom(from);
        System.out.println("Reachable from " + from + ": " + reachable.size());
        for (int i = 0; i < reachable.size(); i++) {
            System.out.println("  " + reachable.get(i) + " - " + service.locationName(reachable.get(i)));
        }
    }

    private static void showMst(CampusService service) {
        GraphAlgorithms.MSTResult mst = service.getRoutingEngine().minimumConnectionNetwork();
        System.out.println("Minimum connection network (Kruskal) - total cost " + mst.totalWeight);
        for (int i = 0; i < mst.edges.size(); i++) {
            System.out.println("  " + mst.edges.get(i));
        }
    }
}
