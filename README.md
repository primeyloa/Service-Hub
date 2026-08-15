# University Campus Service Hub

A joint Level-300 / Level-200 Data Structures and Algorithms project
(DCIT 204/308, 2025/2026 Second Semester) at the **University of Ghana**.

Local context: a **Smart Service Operations Optimizer for the University of
Ghana, Legon campus** — dispatching maintenance, repair and daily service
requests raised by departments, halls and institutions across campus.

## What is included

- **Custom data-structure library** (`servicehub.ds`) implemented from scratch:
  `ArrayList` / `DynamicArray`, `SinglyLinkedList`, `LinkedList` (doubly),
  `Stack`, `Queue`, `CircularQueue`, `Deque`, `PriorityQueue` (binary heap),
  `BinarySearchTree`, `RedBlackTree` (balanced), `HashTable`, `HashMap`,
  `Set`, `Map` (BST-based), `DisjointSet` / `UnionFind` and `Graph`
  (adjacency list + lazy adjacency matrix).
  All structures are generic and iterable.
- **Algorithms** (`servicehub.algorithms`): linear/binary search,
  selection/insertion/merge/quick sort, BFS, DFS, Dijkstra (shortest path),
  Prim and Kruskal (MST), greedy request selection (with a documented
  counterexample) and 0/1 knapsack dynamic programming.
- **Engines** (`servicehub.engine`): scheduling engine modelling FIFO, circular
  queue, deque and priority-heap dispatch; routing engine over the campus road
  network; empirical efficiency benchmarker.
- **Database**: SQLite (`db/service_hub.db`) seeded from the local Legon campus
  dataset in `data/`; requests, resources, routes, audit events and algorithm
  runs are all persisted.
- **Simulation interface**: a simple Java Swing GUI (`servicehub.ui.ServiceHubGUI`)
  to create service requests, dispatch available personnel/assets, inspect
  routes and reachable zones, watch the scheduling queues, run optimisations
  and execute the empirical lab. A console menu (`servicehub.cli.Main`) is also
  available via `--cli`.

## Build, test and run

Run everything from inside the `University-Campus-Service-Hub` folder:

| Task | Command |
| --- | --- |
| Run the tests | `mvn test` |
| Build the jar | `mvn package` |
| Launch the Swing simulator | `mvn exec:java` |
| Launch the console menu | `mvn exec:java -Dexec.args="--cli"` |

The application creates `db/service_hub.db`, seeds it from `../data/*_template.csv`
on first run, and writes benchmark results to `reports/benchmark_results.csv`.

## Team parameters (derived from member index numbers)

- Hash-table bucket size: 149
- Route penalty weight: 7.0 (poor road condition multiplier)
- DP knapsack budget: GHS 10,500.00
- Max dispatch window (round-robin): 5 units
