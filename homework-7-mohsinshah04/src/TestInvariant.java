import java.util.function.Consumer;

import edu.uwm.cs351.util.Stack;
import junit.framework.TestCase;

public class TestInvariant extends TestCase {

	Stack.Spy<Number> spy = new Stack.Spy<Number>();
	int reports = 0;
	
	protected void assertWellFormed(boolean expected, Stack<Number> seq) {
		reports = 0;
		Consumer<String> savedReporter = spy.getReporter();
		try {
			spy.setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, spy.wellFormed(seq));
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
		} finally {
			spy.setReporter(savedReporter);
		}
	}
	
	protected Stack<Number> stack;
	
	public void test0() {
		stack = spy.create(null, 0);
		assertWellFormed(false, stack);
	}
	
	public void test1() {
		stack = spy.create(new Number[0], 0);
		assertWellFormed(true, stack);
	}
	
	public void test2() {
		stack = spy.create(new Number[1], 0);
		assertWellFormed(true, stack);
	}
	
	public void test3() {
		stack = spy.create(new Number[1], 1);
		assertWellFormed(true, stack);
	}
	
	public void test4() {
		stack = spy.create(new Number[0], 1);
		assertWellFormed(false, stack);
	}
	
	public void test5() {
		stack = spy.create(new Number[1], 2);
		assertWellFormed(false, stack);
	}
	
	public void test6() {
		stack = spy.create(new Number[1], -1);
		assertWellFormed(false, stack);
	}
	
	public void test7() {
		stack = spy.create(new Number[10], -1);
		assertWellFormed(false, stack);
	}
	
	public void test8() {
		stack = spy.create(new Number[10], 10);
		assertWellFormed(true, stack);
	}
	
	public void test9() {
		stack = spy.create(new Number[10], 11);
		assertWellFormed(false, stack);
	}
}
