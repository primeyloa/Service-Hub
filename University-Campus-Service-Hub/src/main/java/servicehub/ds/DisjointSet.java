package servicehub.ds;

public class DisjointSet {
    private int[] parent;
    private int[] rank;

    public DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int i) {
        if (parent[i] == i) {
            return i;
        }
        // Path compression
        return parent[i] = find(parent[i]);
    }

    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);

        if (rootI != rootJ) {
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootJ] = rootI;
                rank[rootI]++;
            }
        }
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }
}
