package servicehub.correctness;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Runs every correctness test class in one go.
 * In an IDE (IntelliJ/Eclipse), right-click this class and "Run" to execute
 * the full pack. From the command line, see run_tests.sh.
 */
@Suite
@SelectClasses({
        BSTCorrectnessTest.class,
        RedBlackTreeCorrectnessTest.class,
        BTreeCorrectnessTest.class,
        HashTableCorrectnessTest.class,
        SetMapCorrectnessTest.class
})
public class CorrectnessPackSuite {
}
