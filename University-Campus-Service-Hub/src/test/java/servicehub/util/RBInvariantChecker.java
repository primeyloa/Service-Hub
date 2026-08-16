package servicehub.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Validates the red-black tree invariants on an arbitrary tree object via
 * reflection, so it works against the bundled reference RedBlackTree AND
 * against your own implementation, as long as it uses reasonably standard
 * field names.
 *
 * It looks for a "root" field on the tree object, then walks node objects
 * looking for left/right child fields, a key/value field, and either:
 *   - a boolean field indicating "is red" (true = red), or
 *   - an enum/String/char field indicating color, where a case-insensitive
 *     match of "RED"/"R" means red.
 *
 * If your field names aren't in the candidate lists below, just add them --
 * that's the only change needed to point this at your own class.
 */
public final class RBInvariantChecker {

    // ---- Edit these if your field names differ ----
    private static final List<String> ROOT_FIELD_CANDIDATES =
            Arrays.asList("root", "head");
    private static final List<String> LEFT_FIELD_CANDIDATES =
            Arrays.asList("left", "leftChild", "lft");
    private static final List<String> RIGHT_FIELD_CANDIDATES =
            Arrays.asList("right", "rightChild", "rgt");
    private static final List<String> KEY_FIELD_CANDIDATES =
            Arrays.asList("key", "value", "data", "item", "val");
    private static final List<String> COLOR_BOOLEAN_CANDIDATES =
            Arrays.asList("red", "isRed", "isRed()");
    private static final List<String> COLOR_OTHER_CANDIDATES =
            Arrays.asList("color", "colour");
    // -------------------------------------------------

    private RBInvariantChecker() {}

    /** Thrown with a human-readable message describing exactly which invariant broke. */
    public static class InvariantViolation extends RuntimeException {
        public InvariantViolation(String message) { super(message); }
    }

    /** Returns normally if all invariants hold; throws InvariantViolation otherwise. */
    public static void check(Object tree) {
        Object root = getFieldValue(tree, ROOT_FIELD_CANDIDATES);
        if (root == null) return; // empty tree via a null root reference: trivially valid

        Object rootKey = getFieldValue(root, KEY_FIELD_CANDIDATES);
        if (rootKey == null) return; // empty tree via a NIL sentinel root: trivially valid

        // property 1: root is black
        if (isRed(root)) {
            throw new InvariantViolation("Root node must be black, but was red.");
        }
        // property 2: BST ordering + no red node has a red child + equal black-heights
        checkNode(root, null, null);
    }

    @SuppressWarnings("unchecked")
    private static int checkNode(Object node, Comparable lowerBoundExclusive, Comparable upperBoundExclusive) {
        if (node == null) return 1; // NIL leaves are black by convention; contribute 1 to black-height

        Comparable key = (Comparable) getFieldValue(node, KEY_FIELD_CANDIDATES);
        if (key == null) return 1; // reached a sentinel NIL node (has a null key field)

        if (lowerBoundExclusive != null && key.compareTo(lowerBoundExclusive) <= 0) {
            throw new InvariantViolation("BST property violated: key " + key + " is not > " + lowerBoundExclusive);
        }
        if (upperBoundExclusive != null && key.compareTo(upperBoundExclusive) >= 0) {
            throw new InvariantViolation("BST property violated: key " + key + " is not < " + upperBoundExclusive);
        }

        Object left = getFieldValue(node, LEFT_FIELD_CANDIDATES);
        Object right = getFieldValue(node, RIGHT_FIELD_CANDIDATES);
        Object leftKey = left == null ? null : getFieldValue(left, KEY_FIELD_CANDIDATES);
        Object rightKey = right == null ? null : getFieldValue(right, KEY_FIELD_CANDIDATES);

        boolean nodeRed = isRed(node);
        if (nodeRed) {
            if (leftKey != null && isRed(left)) {
                throw new InvariantViolation("Red node with key " + key + " has a red left child (" + leftKey + ").");
            }
            if (rightKey != null && isRed(right)) {
                throw new InvariantViolation("Red node with key " + key + " has a red right child (" + rightKey + ").");
            }
        }

        int leftBH = checkNode(leftKey == null ? null : left, lowerBoundExclusive, key);
        int rightBH = checkNode(rightKey == null ? null : right, key, upperBoundExclusive);

        if (leftBH != rightBH) {
            throw new InvariantViolation("Black-height mismatch at key " + key
                    + ": left subtree black-height=" + leftBH + ", right subtree black-height=" + rightBH);
        }
        return leftBH + (nodeRed ? 0 : 1);
    }

    private static boolean isRed(Object node) {
        for (String name : COLOR_BOOLEAN_CANDIDATES) {
            Field f = findField(node.getClass(), name);
            if (f != null) {
                try {
                    f.setAccessible(true);
                    return f.getBoolean(node);
                } catch (Exception ignored) { }
            }
        }
        for (String name : COLOR_OTHER_CANDIDATES) {
            Field f = findField(node.getClass(), name);
            if (f != null) {
                try {
                    f.setAccessible(true);
                    Object val = f.get(node);
                    if (val == null) return false;
                    String s = val.toString();
                    return s.equalsIgnoreCase("RED") || s.equalsIgnoreCase("R");
                } catch (Exception ignored) { }
            }
        }
        throw new IllegalStateException(
                "Could not find a color field on " + node.getClass()
                        + ". Add your field name to RBInvariantChecker's candidate lists.");
    }

    private static Object getFieldValue(Object obj, List<String> candidates) {
        if (obj == null) return null;
        for (String name : candidates) {
            Field f = findField(obj.getClass(), name);
            if (f != null) {
                try {
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (Exception ignored) { }
            }
        }
        throw new IllegalStateException(
                "Could not find any of " + candidates + " on " + obj.getClass()
                        + ". Add your field name to RBInvariantChecker's candidate lists.");
    }

    private static Field findField(Class<?> cls, String name) {
        Class<?> c = cls;
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        return null;
    }
}
