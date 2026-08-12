package servicehub.ds;

// Handling disjoint sets with the UnionFind Algorithm

public class UnionFind {
    
    private int[] parent;

    public UnionFind(int size){
        //whatever is in position i of the parent array is the representative of its own virtual tree or its own set
        parent = new int[size];
        for (int i =0; i<size; i++){
            parent[i] = i;
        }
    }

    //finding the representative (root - parent of a disjoint set) that includes i
    public int find(int i){
        //check if parent[i] - i itself is the representative or root
        if(parent[i]==i){
            return i;
        }

        //recursively checks until its representative is found
        return find(parent[i]);
    }


    //merge the representatives of the sets that include i and j respectively
    public void union(int i, int j){
        //get the representative of the set containing i
        int irep = find(i);

        //representative of set containing j
        int jrep = find(j);

        //set representative of set containing i to the representative of the set containing j
        parent[irep] = jrep;
    }

    public static void main(String[] args){
        UnionFind union = new UnionFind(6);
        union.union(0, 1);
        union.union(1, 3);
        union.union(3, 6);
        System.out.println(union.find(3));

    }
}
