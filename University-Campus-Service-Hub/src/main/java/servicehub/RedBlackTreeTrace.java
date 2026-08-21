package servicehub;

import ds.RedBlackTree;

public class RedBlackTreeTrace {

    public static void main(String[] args) {

        RedBlackTree<Integer> tree = new RedBlackTree<>();

        int[] values = {10, 20, 30, 15};

        for (int value : values) {
            tree.insert(value);
            System.out.println(
                "Insert " + value + " -> " + tree.inorder()
            );
        }

        System.out.println("Search 15: " + tree.contains(15));


tree.delete(15);
System.out.println("After deleting 15: " + tree.inorder());
System.out.println("Search 15: " + tree.contains(15));
    }
}