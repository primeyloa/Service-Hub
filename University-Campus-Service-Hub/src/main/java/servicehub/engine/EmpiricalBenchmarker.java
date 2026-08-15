package servicehub.engine;

import servicehub.DatabaseManager;
import servicehub.algorithms.Search;
import servicehub.algorithms.Sort;
import servicehub.ds.DynamicArray;
import servicehub.ds.Graph;
import servicehub.ds.HashTable;
import servicehub.ds.PriorityQueue;
import servicehub.algorithms.GraphAlgorithms;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Random;

/**
 * Empirical efficiency laboratory. Measures time and memory for the required
 * algorithms across growing input sizes, exports CSV and persists to the
 * algorithm_runs table. Each experiment is repeated {@code REPETITIONS} times
 * and the average is reported.
 */
public class EmpiricalBenchmarker {

    public static final int REPETITIONS = 3;
    private static final int[] SEARCH_SORT_SIZES = {100, 500, 1_000, 5_000, 10_000};
    private static final int[] GRAPH_SIZES = {50, 100, 200, 500};

    public record BenchmarkResult(String algorithm, int inputSize, long avgTimeNs, long memoryKb) {
        @Override
        public String toString() {
            return String.format("%s,%d,%d,%d", algorithm, inputSize, avgTimeNs, memoryKb);
        }
    }

    /**
     * Runs all experiments and returns collected results (also persisted).
     */
    public static DynamicArray<BenchmarkResult> runAllBenchmarks() {
        DynamicArray<BenchmarkResult> results = new DynamicArray<>();
        try {
            Files.createDirectories(Paths.get("reports"));
        } catch (Exception ignored) {
        }

        results.addAll(runSearchBenchmarks());
        results.addAll(runSortBenchmarks());
        results.addAll(runHashBenchmarks());
        results.addAll(runHeapBenchmarks());
        results.addAll(runGraphBenchmarks());
        writeCsv(results);
        return results;
    }

    public static DynamicArray<BenchmarkResult> runSearchBenchmarks() {
        DynamicArray<BenchmarkResult> results = new DynamicArray<>();
        for (int size : SEARCH_SORT_SIZES) {
            Integer[] data = generateSortedArray(size);
            long mem = currentMemoryKb();

            long linearSum = 0;
            for (int r = 0; r < REPETITIONS; r++) {
                long start = System.nanoTime();
                Search.linearSearch(data, -999);
                linearSum += System.nanoTime() - start;
            }
            save(new BenchmarkResult("LinearSearch", size, linearSum / REPETITIONS, mem));
            results.add(new BenchmarkResult("LinearSearch", size, linearSum / REPETITIONS, mem));

            long binarySum = 0;
            for (int r = 0; r < REPETITIONS; r++) {
                long start = System.nanoTime();
                Search.binarySearch(data, -999);
                binarySum += System.nanoTime() - start;
            }
            save(new BenchmarkResult("BinarySearch", size, binarySum / REPETITIONS, mem));
            results.add(new BenchmarkResult("BinarySearch", size, binarySum / REPETITIONS, mem));
        }
        return results;
    }

    public static DynamicArray<BenchmarkResult> runSortBenchmarks() {
        DynamicArray<BenchmarkResult> results = new DynamicArray<>();
        String[] names = {"SelectionSort", "InsertionSort", "MergeSort", "QuickSort"};
        for (int size : SEARCH_SORT_SIZES) {
            Integer[] base = generateRandomArray(size);
            long mem = currentMemoryKb();

            for (String name : names) {
                long sum = 0;
                for (int r = 0; r < REPETITIONS; r++) {
                    Integer[] clone = base.clone();
                    long start = System.nanoTime();
                    switch (name) {
                        case "SelectionSort" -> Sort.selectionSort(clone);
                        case "InsertionSort" -> Sort.insertionSort(clone);
                        case "MergeSort" -> Sort.mergeSort(clone);
                        default -> Sort.quickSort(clone, 0, clone.length - 1);
                    }
                    sum += System.nanoTime() - start;
                }
                save(new BenchmarkResult(name, size, sum / REPETITIONS, mem));
                results.add(new BenchmarkResult(name, size, sum / REPETITIONS, mem));
            }
        }
        return results;
    }

    public static DynamicArray<BenchmarkResult> runHashBenchmarks() {
        DynamicArray<BenchmarkResult> results = new DynamicArray<>();
        int[] keys = {100, 1_000, 5_000, 10_000, 20_000};
        for (int count : keys) {
            HashTable<String, Integer> table = new HashTable<>(101);
            long start = System.nanoTime();
            for (int i = 0; i < count; i++) {
                table.put("K" + i, i);
            }
            long duration = System.nanoTime() - start;
            long mem = currentMemoryKb();
            String name = "HashTable-" + count + "keys-" + table.getCollisionCount() + "collisions";
            save(new BenchmarkResult(name, count, duration, mem));
            results.add(new BenchmarkResult("HashTable-loadfactor-" + Math.round(table.getLoadFactor() * 100) + "pct", count, duration, mem));
        }
        return results;
    }

    public static DynamicArray<BenchmarkResult> runHeapBenchmarks() {
        DynamicArray<BenchmarkResult> results = new DynamicArray<>();
        int[] sizes = {100, 1_000, 5_000, 10_000, 20_000};
        for (int count : sizes) {
            PriorityQueue<Integer> heap = new PriorityQueue<>(count);
            long mem = currentMemoryKb();

            long insertSum = 0;
            long extractSum = 0;
            for (int r = 0; r < REPETITIONS; r++) {
                heap.clear();
                long start = System.nanoTime();
                for (int i = 0; i < count; i++) heap.insert(count - i);
                insertSum += System.nanoTime() - start;

                start = System.nanoTime();
                while (!heap.isEmpty()) heap.extract();
                extractSum += System.nanoTime() - start;
            }
            save(new BenchmarkResult("HeapInsert", count, insertSum / REPETITIONS, mem));
            results.add(new BenchmarkResult("HeapInsert", count, insertSum / REPETITIONS, mem));
            save(new BenchmarkResult("HeapExtract", count, extractSum / REPETITIONS, mem));
            results.add(new BenchmarkResult("HeapExtract", count, extractSum / REPETITIONS, mem));
        }
        return results;
    }

    public static DynamicArray<BenchmarkResult> runGraphBenchmarks() {
        DynamicArray<BenchmarkResult> results = new DynamicArray<>();
        for (int size : GRAPH_SIZES) {
            Graph graph = buildRandomGraph(size);
            long mem = currentMemoryKb();

            long bfsSum = 0, dfsSum = 0, dijSum = 0, mstSum = 0;
            for (int r = 0; r < REPETITIONS; r++) {
                long start = System.nanoTime();
                GraphAlgorithms.bfs(graph, "V0");
                bfsSum += System.nanoTime() - start;

                start = System.nanoTime();
                GraphAlgorithms.dfs(graph, "V0");
                dfsSum += System.nanoTime() - start;

                start = System.nanoTime();
                GraphAlgorithms.dijkstra(graph, "V0");
                dijSum += System.nanoTime() - start;

                start = System.nanoTime();
                GraphAlgorithms.kruskalMST(graph);
                mstSum += System.nanoTime() - start;
            }
            save(new BenchmarkResult("BFS", size, bfsSum / REPETITIONS, mem));
            results.add(new BenchmarkResult("BFS", size, bfsSum / REPETITIONS, mem));
            save(new BenchmarkResult("DFS", size, dfsSum / REPETITIONS, mem));
            results.add(new BenchmarkResult("DFS", size, dfsSum / REPETITIONS, mem));
            save(new BenchmarkResult("Dijkstra", size, dijSum / REPETITIONS, mem));
            results.add(new BenchmarkResult("Dijkstra", size, dijSum / REPETITIONS, mem));
            save(new BenchmarkResult("KruskalMST", size, mstSum / REPETITIONS, mem));
            results.add(new BenchmarkResult("KruskalMST", size, mstSum / REPETITIONS, mem));
        }
        return results;
    }

    private static Graph buildRandomGraph(int size) {
        Random rnd = new Random(42);
        Graph graph = new Graph();
        for (int i = 0; i < size; i++) graph.addVertex("V" + i);
        int targetEdges = size * 3;
        while (graph.edgeCount() < targetEdges) {
            int a = rnd.nextInt(size);
            int b = rnd.nextInt(size);
            if (a != b) graph.addEdge("V" + a, "V" + b, 1 + rnd.nextInt(50));
        }
        return graph;
    }

    private static Integer[] generateSortedArray(int size) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) arr[i] = i;
        return arr;
    }

    private static Integer[] generateRandomArray(int size) {
        Random rnd = new Random(7);
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) arr[i] = rnd.nextInt(100_000);
        return arr;
    }

    private static long currentMemoryKb() {
        Runtime rt = Runtime.getRuntime();
        return (rt.totalMemory() - rt.freeMemory()) / 1024;
    }

    private static void save(BenchmarkResult result) {
        String sql = "INSERT INTO algorithm_runs(algorithm_name, input_size, time_ns, memory_kb, date_run) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, result.algorithm());
            pstmt.setInt(2, result.inputSize());
            pstmt.setLong(3, result.avgTimeNs());
            pstmt.setLong(4, result.memoryKb());
            pstmt.setString(5, LocalDate.now().toString());
            pstmt.executeUpdate();
        } catch (Exception ignored) {
            // DB may not be available during pure unit tests
        }
    }

    private static void writeCsv(DynamicArray<BenchmarkResult> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("reports/benchmark_results.csv"))) {
            writer.println("algorithm_name,input_size,time_ns,memory_kb,date_run");
            for (int i = 0; i < results.size(); i++) {
                BenchmarkResult r = results.get(i);
                writer.printf("%s,%d,%d,%d,%s%n", r.algorithm(), r.inputSize(), r.avgTimeNs(), r.memoryKb(), LocalDate.now());
            }
        } catch (Exception e) {
            System.err.println("Error writing benchmark CSV: " + e.getMessage());
        }
    }

    public static void runBenchmarks() {
        DynamicArray<BenchmarkResult> results = runAllBenchmarks();
        System.out.println("Benchmarks completed. Rows recorded: " + results.size());
        for (int i = 0; i < results.size(); i++) {
            System.out.println(results.get(i));
        }
    }
}
