package servicehub.ds;

/**
 * Disjoint Set (Union-Find) with path compression and union by rank.
 * Generic over element keys; keys are mapped to dense indices internally.
 *
 * @param <K> element type
 */
public class DisjointSet<K> {

    private final HashMap<K, Integer> indexMap;
    private final DynamicArray<Integer> parent;
    private final DynamicArray<Integer> rank;

    public DisjointSet() {
        indexMap = new HashMap<>();
        parent = new DynamicArray<>();
        rank = new DynamicArray<>();
    }

    public void makeSet(K element) {
        if (element == null || indexMap.containsKey(element)) return;
        int index = parent.size();
        indexMap.put(element, index);
        parent.add(index);
        rank.add(0);
    }

    private int indexOf(K element) {
        Integer idx = indexMap.get(element);
        if (idx == null) throw new IllegalArgumentException("Element not in set: " + element);
        return idx;
    }

    public int find(K element) {
        return findIndex(indexOf(element));
    }

    private int findIndex(int i) {
        int p = parent.get(i);
        if (p != i) {
            parent.set(i, findIndex(p)); // path compression
        }
        return parent.get(i);
    }

    public void union(K a, K b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) return;

        int rankA = rank.get(rootA);
        int rankB = rank.get(rootB);
        if (rankA < rankB) {
            parent.set(rootA, rootB);
        } else if (rankA > rankB) {
            parent.set(rootB, rootA);
        } else {
            parent.set(rootB, rootA);
            rank.set(rootA, rankA + 1);
        }
    }

    public boolean connected(K a, K b) {
        return find(a) == find(b);
    }

    public int size() {
        return parent.size();
    }
}
