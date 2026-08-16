package servicehub.ds;

public class PriorityQueue {

    public static class VertexDistance {
        private final int vertex;
        private final int distance;

        public VertexDistance(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        public int getVertex() {
            return vertex;
        }

        public int getDistance() {
            return distance;
        }
    }

    private final MyArrayList<VertexDistance> heap = new MyArrayList<>();

    public void add(int vertex, int distance) {
        VertexDistance entry = new VertexDistance(vertex, distance);
        heap.add(entry);
        siftUp(heap.size() - 1);
    }

    public VertexDistance poll() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Cannot poll from an empty priority queue");
        }

        VertexDistance root = heap.get(0);
        int lastIndex = heap.size() - 1;
        if (lastIndex > 0) {
            heap.set(0, heap.get(lastIndex));
        }
        heap.remove(lastIndex);

        if (!heap.isEmpty()) {
            siftDown(0);
        }

        return root;
    }

    public VertexDistance peek() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Cannot peek at an empty priority queue");
        }
        return heap.get(0);
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (isBefore(heap.get(index), heap.get(parentIndex))) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        int smallest = index;

        if (left < heap.size() && isBefore(heap.get(left), heap.get(smallest))) {
            smallest = left;
        }
        if (right < heap.size() && isBefore(heap.get(right), heap.get(smallest))) {
            smallest = right;
        }

        if (smallest != index) {
            swap(index, smallest);
            siftDown(smallest);
        }
    }

    private void swap(int i, int j) {
        VertexDistance temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    private boolean isBefore(VertexDistance a, VertexDistance b) {
        if (a.getDistance() != b.getDistance()) {
            return a.getDistance() < b.getDistance();
        }
        return a.getVertex() < b.getVertex();
    }
}

