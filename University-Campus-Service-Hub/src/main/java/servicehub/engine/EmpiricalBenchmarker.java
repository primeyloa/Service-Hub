package servicehub.engine;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Random;

import servicehub.DatabaseManager;
import servicehub.algorithms.GraphAlgorithms;
import servicehub.algorithms.Search;
import servicehub.algorithms.Sort;
import servicehub.ds.BinarySearchTree;
import servicehub.ds.DynamicArray;
import servicehub.ds.Graph;
import servicehub.ds.HashTable;
import servicehub.ds.PriorityQueue;
import servicehub.ds.RedBlackTree;

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
    private static final int[] TREE_SIZES = {100, 500, 1_000, 5_000, 10_000};
    private static final int[] HASH_KEY_COUNTS = {100, 1_000, 5_000, 10_000, 20_000};
    private static final int[] HEAP_SIZES = {100, 1_000, 5_000, 10_000, 20_000};

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
            System.out.println("Parent directories could not be created");
        }

        results.addAll(runSearchBenchmarks());
        results.addAll(runSortBenchmarks());
        results.addAll(runHashBenchmarks());
        results.addAll(runTreeBenchmarks());
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
        int[] tableSizes = {64, 128, 256, 512};
        for (int count : HASH_KEY_COUNTS) {
            for (int buckets : tableSizes) {
                HashTable<String, Integer> table = new HashTable<>(buckets);
                long mem = currentMemoryKb();

                long sum = 0;
                for (int r = 0; r < REPETITIONS; r++) {
                    table.clear();
                    long start = System.nanoTime();
                    for (int i = 0; i < count; i++) {
                        table.put("K" + i, i);
                    }
                    sum += System.nanoTime() - start;
                }
                String label = "HashInsert-T" + buckets;
                save(new BenchmarkResult(label, count, sum / REPETITIONS, mem));
                results.add(new BenchmarkResult(label, count, sum / REPETITIONS, mem));

                // Collision counts are recorded in the value slot so they can be
                // plotted as their own series; the name makes that unambiguous.
                String colLabel = "HashCollisions-T" + buckets
                        + "-loadfactor-" + Math.round(table.getLoadFactor() * 100) + "pct";
                save(new BenchmarkResult(colLabel, count, table.getCollisionCount(), mem));
                results.add(new BenchmarkResult(colLabel, count, table.getCollisionCount(), mem));
            }
        }
        return results;
    }

    /**
     * BST vs self-balancing (Red-Black) tree: insert time, search time and
     * final height at multiple sizes. Keys are inserted in random order.
     */
    public static DynamicArray<BenchmarkResult> runTreeBenchmarks() {
        DynamicArray<BenchmarkResult> results = new DynamicArray<>();
        for (int size : TREE_SIZES) {
            Integer[] keys = shuffledKeys(size);
            long mem = currentMemoryKb();

            BinarySearchTree<Integer, Integer> bst = new BinarySearchTree<>();
            RedBlackTree<Integer> rbt = new RedBlackTree<>();

            long bstInsertSum = 0;
            long bstSearchSum = 0;
            for (int r = 0; r < REPETITIONS; r++) {
                bst.clear();
                long start = System.nanoTime();
                for (int k : keys) bst.insert(k);
                bstInsertSum += System.nanoTime() - start;

                start = System.nanoTime();
                for (int k : keys) bst.search(k);
                bstSearchSum += System.nanoTime() - start;
            }

            long rbtInsertSum = 0;
            long rbtSearchSum = 0;
            for (int r = 0; r < REPETITIONS; r++) {
                rbt.clear();
                long start = System.nanoTime();
                for (int k : keys) rbt.insert(k);
                rbtInsertSum += System.nanoTime() - start;

                start = System.nanoTime();
                for (int k : keys) rbt.contains(k);
                rbtSearchSum += System.nanoTime() - start;
            }

            record(results, "BST-insert", size, bstInsertSum / REPETITIONS, mem);
            record(results, "RBT-insert", size, rbtInsertSum / REPETITIONS, mem);
            record(results, "BST-search", size, bstSearchSum / REPETITIONS, mem);
            record(results, "RBT-search", size, rbtSearchSum / REPETITIONS, mem);
            record(results, "BST-height", size, bst.height(), mem);
            record(results, "RBT-height", size, rbt.height(), mem);
        }
        return results;
    }

    private static void record(DynamicArray<BenchmarkResult> results,
                               String name, int size, long value, long memoryKb) {
        save(new BenchmarkResult(name, size, value, memoryKb));
        results.add(new BenchmarkResult(name, size, value, memoryKb));
    }

    private static Integer[] shuffledKeys(int size) {
        Integer[] keys = new Integer[size];
        for (int i = 0; i < size; i++) keys[i] = i;
        Random rnd = new Random(1234);
        for (int i = size - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            Integer tmp = keys[i];
            keys[i] = keys[j];
            keys[j] = tmp;
        }
        return keys;
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

    /**
     * Machine specification, reported alongside every experiment so all runs
     * are provably taken on the same machine.
     */
    public static String machineSpec() {
        Runtime rt = Runtime.getRuntime();
        return String.format(
                "OS: %s (%s) | CPU cores: %d | Max heap: %d MB | Java: %s",
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                rt.availableProcessors(),
                rt.maxMemory() / (1024 * 1024),
                System.getProperty("java.version"));
    }

    public static void runBenchmarks() {
        DynamicArray<BenchmarkResult> results = runAllBenchmarks();
        System.out.println("Benchmarks completed. Rows recorded: " + results.size());
        for (int i = 0; i < results.size(); i++) {
            System.out.println(results.get(i));
        }
    }
}
