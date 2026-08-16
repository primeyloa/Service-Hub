package servicehub.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Validates B-Tree structural invariants on an arbitrary tree object via
 * reflection: works against the bundled reference BTree AND against your
 * own implementation, as long as it uses reasonably standard field names.
 *
 * Checks:
 *  - every node's keys are sorted
 *  - every node (except the root) has between t-1 and 2t-1 keys
 *  - internal nodes have exactly (keys.size() + 1) children
 *  - every leaf is at the same depth
 *  - keys in each child fall strictly between the correct separator keys
 *
 * If your field names aren't in the candidate lists below, add them.
 */
public final class BTreeInvariantChecker {

    // ---- Edit these if your field names differ ----
    private static final List<String> ROOT_FIELD_CANDIDATES = Arrays.asList("root");
    private static final List<String> DEGREE_FIELD_CANDIDATES =
            Arrays.asList("t", "minimumDegree", "minDegree", "degree");
    private static final List<String> KEYS_FIELD_CANDIDATES = Arrays.asList("keys");
    private static final List<String> CHILDREN_FIELD_CANDIDATES = Arrays.asList("children");
    private static final List<String> LEAF_FIELD_CANDIDATES = Arrays.asList("leaf", "isLeaf");
    // -------------------------------------------------

    private BTreeInvariantChecker() {}

    public static class InvariantViolation extends RuntimeException {
        public InvariantViolation(String message) { super(message); }
    }

    private static int leafDepthSeen = -1;

    public static void check(Object tree) {
        int t = ((Number) getFieldValue(tree, DEGREE_FIELD_CANDIDATES)).intValue();
        Object root = getFieldValue(tree, ROOT_FIELD_CANDIDATES);
        if (root == null) return;

        leafDepthSeen = -1;
        checkNode(root, t, true, null, null, 0);
    }

    @SuppressWarnings("unchecked")
    private static void checkNode(Object node, int t, boolean isRoot,
                                   Comparable lowerExclusive, Comparable upperExclusive, int depth) {
        List<Comparable> keys = (List<Comparable>) getFieldValue(node, KEYS_FIELD_CANDIDATES);
        boolean leaf = (boolean) getFieldValue(node, LEAF_FIELD_CANDIDATES);
        List<Object> children = leaf ? null : (List<Object>) getFieldValue(node, CHILDREN_FIELD_CANDIDATES);

        // sortedness
        for (int i = 1; i < keys.size(); i++) {
            if (keys.get(i - 1).compareTo(keys.get(i)) >= 0) {
                throw new InvariantViolation("Keys not strictly sorted within a node: " + keys);
            }
        }

        // bounds
        if (!keys.isEmpty()) {
            Comparable first = keys.get(0);
            Comparable last = keys.get(keys.size() - 1);
            if (lowerExclusive != null && first.compareTo(lowerExclusive) <= 0) {
                throw new InvariantViolation("Key " + first + " should be > " + lowerExclusive);
            }
            if (upperExclusive != null && last.compareTo(upperExclusive) >= 0) {
                throw new InvariantViolation("Key " + last + " should be < " + upperExclusive);
            }
        }

        // key-count invariant
        int minKeys = isRoot ? (leaf ? 0 : 1) : (t - 1);
        int maxKeys = 2 * t - 1;
        if (keys.size() < minKeys || keys.size() > maxKeys) {
            throw new InvariantViolation("Node has " + keys.size() + " keys; expected between "
                    + minKeys + " and " + maxKeys + " (isRoot=" + isRoot + ", t=" + t + "). Keys: " + keys);
        }

        if (leaf) {
            if (leafDepthSeen == -1) {
                leafDepthSeen = depth;
            } else if (leafDepthSeen != depth) {
                throw new InvariantViolation("Leaves at inconsistent depths: saw " + leafDepthSeen + " and " + depth);
            }
            return;
        }

        if (children.size() != keys.size() + 1) {
            throw new InvariantViolation("Internal node has " + keys.size() + " keys but "
                    + children.size() + " children (expected " + (keys.size() + 1) + ").");
        }

        for (int i = 0; i < children.size(); i++) {
            Comparable childLower = (i == 0) ? lowerExclusive : keys.get(i - 1);
            Comparable childUpper = (i == keys.size()) ? upperExclusive : keys.get(i);
            checkNode(children.get(i), t, false, childLower, childUpper, depth + 1);
        }
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
                        + ". Add your field name to BTreeInvariantChecker's candidate lists.");
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
