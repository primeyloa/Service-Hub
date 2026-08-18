package servicehub.ds;

/**
 * Integer-indexed Union-Find with path compression and union by rank.
 * Suitable for graph algorithms (Kruskal, connectivity) over dense indices.
 */
public class UnionFind {

    private int[] parent;
    private int[] rank;
    private int count;

    public UnionFind(int size) {
        if (size < 0) throw new IllegalArgumentException("Size must be non-negative");
        parent = new int[size];
        rank = new int[size]; //zeros are fixed in each index position. evidence in Obsidian
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
        count = size;
    }

    public int find(int i) {
        if (i < 0 || i >= parent.length) throw new IndexOutOfBoundsException("Index " + i);
        if (parent[i] != i) {
            parent[i] = find(parent[i]); // path compression
        }
        return parent[i];
    }

    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI == rootJ) return;
        if (rank[rootI] < rank[rootJ]) {
            parent[rootI] = rootJ;
        } else if (rank[rootI] > rank[rootJ]) {
            parent[rootJ] = rootI;
        } else {
            parent[rootJ] = rootI;
            rank[rootI]++; // for that root position that pertains to the item number (representative of the set containing i)
        }
        count--;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public int count() {
        return count;
    }

    public int size() {
        return parent.length;
    }
}
