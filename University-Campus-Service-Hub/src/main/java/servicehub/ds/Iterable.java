package servicehub.ds;

/**
 * Custom Iterable interface for project custom data structures.
 *
 * @param <T> element type
 */
public interface Iterable<T> extends java.lang.Iterable<T> {
    @Override
    java.util.Iterator<T> iterator();
}
