package servicehub.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StackTest {

    @Test
    void startsEmpty() {
        Stack<String> stack = new Stack<>();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void pushPeekPopFollowsLifoOrder() {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.size());
        assertEquals(3, stack.peek());
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void peekDoesNotRemove() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        assertEquals("a", stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    void popOnEmptyThrows() {
        Stack<String> stack = new Stack<>();
        assertThrows(IllegalStateException.class, stack::pop);
        assertThrows(IllegalStateException.class, stack::peek);
    }

    @Test
    void clearEmptiesStack() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");
        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertThrows(IllegalStateException.class, stack::pop);
    }

    @Test
    void largeSequencePreservesLifoOrder() {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 10_000; i++) {
            stack.push(i);
        }
        for (int i = 9_999; i >= 0; i--) {
            assertEquals(i, stack.pop());
        }
        assertTrue(stack.isEmpty());
    }

    @Test
    void toStringRendersContents() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");
        assertEquals("[b, a]", stack.toString());
    }
}
