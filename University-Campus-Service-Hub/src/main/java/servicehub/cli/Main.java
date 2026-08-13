package servicehub.cli;
//Empirical benchmarking Engine in Java

import servicehub.CsvSeeder;
import servicehub.engine.EmpiricalBenchmarker;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" Ghana Smart Campus Service Operations Optimizer  ");
        System.out.println(" University of Ghana, Legon Campus Service Hub    ");
        System.out.println("==================================================");

        CsvSeeder.seedDatabaseFromCsv();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Run Empirical Efficiency Benchmarks");
            System.out.println("2. Display System Info & Team Parameters");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    EmpiricalBenchmarker.runBenchmarks();
                    break;
                case "2":
                    System.out.println("Context: University of Ghana Campus Service Hub");
                    System.out.println("Hash Table Bucket Size: 149");
                    System.out.println("Route Penalty Weight: 7.0");
                    System.out.println("DP Knapsack Budget: GHS 10,500.00");
                    System.out.println("Max Cart Capacity: 5 units");
                    break;
                case "3":
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }
}

