import junit.framework.TestCase;

import edu.uwm.cs351.Operation;
import edu.uwm.cs351.Calculator;


public class TestGen extends TestCase {
	protected void assertException(Class<?> exc, Runnable r) {
		try {
			r.run();
			assertFalse("should have thrown an exception.",true);
		} catch (RuntimeException e) {
			if (exc == null) return;
			assertTrue("threw wrong exception type: " + e.getClass(),exc.isInstance(e));
		}
	}

	protected void assertEquals(int expected, Integer result) {
		super.assertEquals(Integer.valueOf(expected),result);
	}

	public void test() {
		Calculator calc0 = new Calculator();
		calc0.value(80L); // should terminate normally
		assertEquals(80L,calc0.getCurrent());
		assertEquals(80L,calc0.getCurrent());
		assertException(java.util.EmptyStackException.class, () -> calc0.close());
		assertException(java.lang.IllegalStateException.class, () -> calc0.value(39L));
		assertEquals(80L,calc0.getCurrent());
		assertEquals(80L,calc0.compute());
		assertEquals(80L,calc0.getCurrent());
		assertEquals(80L,calc0.getCurrent());
		calc0.sqrt(); // should terminate normally
		assertException(java.lang.IllegalStateException.class, () -> calc0.value(-32L));
		assertEquals(8L,calc0.getCurrent());
		assertException(java.util.EmptyStackException.class, () -> calc0.close());
		assertException(java.lang.IllegalStateException.class, () -> calc0.open());
		assertEquals(8L,calc0.compute());
		assertEquals(8L,calc0.compute());
		calc0.value(65L); // should terminate normally
		assertEquals(65L,calc0.getCurrent());
		assertEquals(65L,calc0.getCurrent());
		calc0.binop(Operation.TIMES); // should terminate normally
		assertException(java.lang.IllegalStateException.class, () -> calc0.binop(Operation.TIMES));
		assertException(java.lang.IllegalStateException.class, () -> calc0.compute());
		assertException(java.lang.IllegalStateException.class, () -> calc0.close());
		calc0.value(93L); // should terminate normally
		assertException(java.util.EmptyStackException.class, () -> calc0.close());
		assertEquals(6045L,calc0.getCurrent());
		assertException(java.lang.IllegalStateException.class, () -> calc0.value(37L));
		assertException(java.lang.IllegalStateException.class, () -> calc0.value(11L));
		assertEquals(6045L,calc0.getCurrent());
		assertException(java.util.EmptyStackException.class, () -> calc0.close());
		calc0.sqrt(); // should terminate normally
		assertEquals(77L,calc0.getCurrent());
		calc0.sqrt(); // should terminate normally
		assertEquals(8L,calc0.getCurrent());
		assertException(java.lang.IllegalStateException.class, () -> calc0.value(-62L));
		assertEquals(8L,calc0.getCurrent());
		assertException(java.util.EmptyStackException.class, () -> calc0.close());
		assertEquals(8L,calc0.getCurrent());
		calc0.binop(Operation.MINUS); // should terminate normally
		calc0.open(); // should terminate normally
		assertEquals(8L,calc0.getCurrent());
		assertException(java.lang.IllegalStateException.class, () -> calc0.compute());
		calc0.value(38L); // should terminate normally
		calc0.close(); // should terminate normally
		assertException(java.lang.IllegalStateException.class, () -> calc0.value(-30L));
		assertException(java.util.EmptyStackException.class, () -> calc0.close());
		assertEquals(-30L,calc0.getCurrent());
		calc0.binop(Operation.TIMES); // should terminate normally
		calc0.value(-34L); // should terminate normally
		assertEquals(1020L,calc0.compute());
		calc0.sqrt(); // should terminate normally
		assertEquals(31L,calc0.getCurrent());
		calc0.binop(Operation.DIVIDE); // should terminate normally
		calc0.open(); // should terminate normally
		assertException(java.lang.IllegalStateException.class, () -> calc0.close());
		calc0.value(32L); // should terminate normally
		assertEquals(32L,calc0.getCurrent());
		assertException(java.lang.IllegalStateException.class, () -> calc0.value(72L));
		calc0.binop(Operation.DIVIDE); // should terminate normally
		calc0.value(-88L); // should terminate normally
		assertException(java.lang.ArithmeticException.class, () -> calc0.compute());
		calc0.binop(Operation.MINUS); // should terminate normally
		calc0.open(); // should terminate normally
		assertException(java.lang.IllegalStateException.class, () -> calc0.binop(Operation.PLUS));
		assertEquals(0L,calc0.getCurrent());
	}
}