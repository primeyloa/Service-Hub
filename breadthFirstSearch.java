// Breadth-First Search (BFS)
    // Traverses the graph level by level using a queue
    // and returns all locations reachable from the starting location.
    public ArrayList<Vertex> breadthFirstSearch(Vertex start) {

        ArrayList<Vertex> reachableLocations = new ArrayList<>();

        Queue<Vertex> queue = new LinkedList<>();

        Set<Vertex> visited = new HashSet<>();

        // Add the starting location to the queue
        queue.add(start);
        visited.add(start);

        // Continue until the queue is empty
        while (!queue.isEmpty()) {

            // Remove the first location from the queue
            Vertex current = queue.poll();

            // Add the current location to the result
            reachableLocations.add(current);

            // Find the current location in the graph
            int currentIndex = vertices.indexOf(current);

            // Check all locations connected to the current location
            for (Edge edge : adjacencyList.get(currentIndex)) {

                Vertex neighbour = edge.getDestination();

                // Add only unvisited locations to the queue
                if (!visited.contains(neighbour)) {

                    visited.add(neighbour);
                    queue.add(neighbour);

                }
            }
        }

        return reachableLocations;
    }