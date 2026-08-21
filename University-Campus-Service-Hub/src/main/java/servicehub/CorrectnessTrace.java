package servicehub;

import ds.BTree;
import ds.RedBlackTree;
import ds.MySet;
import ds.MyMap;

public class CorrectnessTrace {

    public static void main(String[] args) {

        System.out.println("CORRECTNESS TRACE");
        System.out.println("-----------------");

        // B-TREE
        BTree<Integer> btree = new BTree<>(2);
        btree.insert(10);
        btree.insert(20);
        btree.insert(5);
        btree.insert(17);

        System.out.println(
            "B-Tree search 17 | Expected: true | Actual: "
            + btree.search(17)
        );

        btree.delete(17);

        System.out.println(
            "B-Tree delete 17 | Expected: false | Actual: "
            + btree.search(17)
        );

        // RED-BLACK TREE
        RedBlackTree<Integer> rbTree = new RedBlackTree<>();
        rbTree.insert(10);
        rbTree.insert(20);
        rbTree.insert(30);
        rbTree.insert(15);

        System.out.println(
            "Red-Black search 15 | Expected: true | Actual: "
            + rbTree.contains(15)
        );

        rbTree.delete(15);

        System.out.println(
            "Red-Black delete 15 | Expected: false | Actual: "
            + rbTree.contains(15)
        );

        // SET
        MySet<Integer> set = new MySet<>();

        set.add(10);
        set.add(20);
        set.add(10);

        System.out.println(
            "Set duplicate test | Expected size: 2 | Actual size: "
            + set.size()
        );

        set.remove(20);

        System.out.println(
            "Set remove 20 | Expected: false | Actual: "
            + set.contains(20)
        );

        // MAP
        MyMap<String, Integer> map = new MyMap<>();

        map.put("Ama", 10);
        map.put("Kojo", 20);

        System.out.println(
            "Map get Ama | Expected: 10 | Actual: "
            + map.get("Ama")
        );

        map.remove("Kojo");

        System.out.println(
            "Map remove Kojo | Expected: false | Actual: "
            + map.containsKey("Kojo")
        );
    }
}