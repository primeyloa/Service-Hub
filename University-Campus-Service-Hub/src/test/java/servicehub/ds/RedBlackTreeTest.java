package servicehub.ds;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedBlackTreeTest {

    @Test
    void startsEmpty() {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        assertTrue(rbt.isEmpty());
        assertEquals(0, rbt.size());
        assertFalse(rbt.contains(1));
    }

    @Test
    void insertsAndSearches() {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        for (int i = 1; i <= 20; i++) rbt.insert(i);
        assertEquals(20, rbt.size());
        for (int i = 1; i <= 20; i++) assertTrue(rbt.contains(i));
        assertFalse(rbt.contains(21));
    }

    @Test
    void minAndMax() {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        rbt.insert(50);
        rbt.insert(10);
        rbt.insert(90);
        rbt.insert(5);
        assertEquals(5, rbt.findMin());
        assertEquals(90, rbt.findMax());
    }

    @Test
    void staysBalancedAfterSequentialInsertion() {
        // Inserting in ascending order into a plain BST would give height 99;
        // the red-black tree should stay logarithmic.
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        for (int i = 1; i <= 100; i++) rbt.insert(i);
        int height = rbt.height();
        assertTrue(height < 20, "RBT height should be O(log n), was " + height);
        assertEquals(100, rbt.size());
    }

    @Test
    void randomizedInsertionRemainsBalancedAndCorrect() {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        java.util.Random rnd = new java.util.Random(99);
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 500; i++) values.add(rnd.nextInt(10_000));
        for (Integer v : values) rbt.insert(v);
        for (Integer v : values) assertTrue(rbt.contains(v));
        assertFalse(rbt.contains(1_000_000));
        assertTrue(rbt.height() < 40);
    }

    @Test
    void duplicatesAreIgnored() {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        rbt.insert(7);
        rbt.insert(7);
        rbt.insert(7);
        assertEquals(1, rbt.size());
        assertTrue(rbt.contains(7));
    }

    @Test
    void nullKeyRejected() {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        assertThrows(IllegalArgumentException.class, () -> rbt.insert(null));
    }

    @Test
    void clearEmptiesTree() {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        rbt.insert(1);
        rbt.insert(2);
        rbt.clear();
        assertTrue(rbt.isEmpty());
        assertEquals(0, rbt.size());
    }
}
