package servicehub.engine;

import servicehub.algorithms.Search;
import servicehub.algorithms.Sort;
import servicehub.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.io.FileWriter;
import java.io.PrintWriter;

public class EmpiricalBenchmarker {

    public static void runBenchmarks() {
        System.out.println("Running Empirical Efficiency Benchmarks...");
        int[] inputSizes = {100, 500, 1000, 5000, 10000};

        try (PrintWriter writer = new PrintWriter(new FileWriter("reports/benchmark_results.csv"))) {
            writer.println("algorithm_name,input_size,time_ns,memory_kb,date_run");

            for (int size : inputSizes) {
                Integer[] data = generateRandomArray(size);
                Integer[] cloneData = data.clone();

                // Benchmark Binary Search
                long startTime = System.nanoTime();
                Search.binarySearch(cloneData, -999);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                long memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;

                writer.printf("BinarySearch,%d,%d,%d,%s\n", size, duration, memory, LocalDate.now());
                saveRun("BinarySearch", size, duration, memory);

                // Benchmark Quicksort
                cloneData = data.clone();
                startTime = System.nanoTime();
                Sort.quickSort(cloneData, 0, cloneData.length - 1);
                endTime = System.nanoTime();
                duration = endTime - startTime;

                writer.printf("QuickSort,%d,%d,%d,%s\n", size, duration, memory, LocalDate.now());
                saveRun("QuickSort", size, duration, memory);
            }
            System.out.println("Benchmarks completed successfully. Results saved to reports/benchmark_results.csv");
        } catch (Exception e) {
            System.err.println("Error running benchmarks: " + e.getMessage());
        }
    }

    private static Integer[] generateRandomArray(int size) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = (int) (Math.random() * 100000);
        }
        java.util.Arrays.sort(arr); // for binary search
        return arr;
    }

    private static void saveRun(String algo, int size, long timeNs, long memoryKb) {
        String sql = "INSERT INTO algorithm_runs(algorithm_name, input_size, time_ns, memory_kb, date_run) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, algo);
            pstmt.setInt(2, size);
            pstmt.setLong(3, timeNs);
            pstmt.setLong(4, memoryKb);
            pstmt.setString(5, LocalDate.now().toString());
            pstmt.executeUpdate();
        } catch (Exception e) {
            // ignore DB log errors if tables aren't ready
        }
    }
}
