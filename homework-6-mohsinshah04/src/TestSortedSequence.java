import java.util.function.Supplier;

import edu.uwm.cs351.SortedSequence;
import junit.framework.TestCase;


public class TestSortedSequence extends TestCase {
	String e1 = "a";
	String e2 = "b";
	String e3 = "c";
	String e4 = "d";
	String e5 = "e";

	SortedSequence<String> s;

	@Override
	protected void setUp() {
		try {
			assert 1/s.size() == 42 : "OK";
			System.err.println("Assertions must be enabled to use this test suite.");
			System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
		} catch (NullPointerException ex) {
			// muffle exception
		}
		s = new SortedSequence<>(String.CASE_INSENSITIVE_ORDER);
	}

	protected <T> void assertException(Class<?> excClass, Supplier<T> producer) {
		try {
			T result = producer.get();
			assertFalse("Should have thrown an exception, not returned " + result,true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				ex.printStackTrace();
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}

	protected <T> void assertException(Runnable f, Class<?> excClass) {
		try {
			f.run();
			assertFalse("Should have thrown an exception, not returned",true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				ex.printStackTrace();
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}

	/**
	 * Return the appointment as an integer
	 * <dl>
	 * <dt>-1<dd><i>(an exception was thrown)
	 * <dt>0<dd>null
	 * <dt>1<dd>e1
	 * <dt>2<dd>e2
	 * <dt>3<dd>e3
	 * <dt>4<dd>e4
	 * <dt>5<dd>e5
	 * </dl>
	 * @return integer encoding of appointment supplied
	 */
	protected int asInt(Supplier<String> g) {
		try {
			String n = g.get();
			if (n == null) return 0;
			return (n.charAt(0))-'a'+1;
		} catch (RuntimeException ex) {
			return -1;
		}
	}

	public void test00() {
		// Nothing inserted yet:
		assertEquals(0,s.size());
		assertFalse(s.isCurrent());
		s.start();
		assertFalse(s.isCurrent());
	}

	public void test01() {
		assertException(IllegalStateException.class, () -> s.getCurrent());
		s.add(e1);
		assertTrue(s.isCurrent());
		assertEquals(e1, s.getCurrent());
		s.start();
		assertSame(e1, s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
	}

	public void test02() {
		// Initially empty.
		s.add(e5);
		s.add(e4);
		assertSame(e4, s.getCurrent());
		s.start();
		assertSame(e4, s.getCurrent());
		s.advance();
		assertSame(e5, s.getCurrent());
		s.advance();
		assertException(IllegalStateException.class, () -> s.getCurrent());
	}

	public void test03() {
		// Initially empty
		s.add(e3);
		s.start();
		s.add(e2);
		assertSame(e2, s.getCurrent());
		s.advance();
		assertSame(e3, s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		s.start();
		assertSame(e2, s.getCurrent());
	}

	public void test04() {
		// Initial empty
		s.add(e1);
		s.start();
		s.add(e5);
		s.start();
		assertSame(e1, s.getCurrent());
		s.advance();
		assertSame(e5, s.getCurrent());
		s.add(e3);
		assertSame(e3, s.getCurrent());
		s.advance();
		assertSame(e5, s.getCurrent());
		s.advance();
		assertException(IllegalStateException.class, () -> s.getCurrent());
	}

	public void test05() {
		assertException(() -> s.add(null), NullPointerException.class);
		assertEquals(0,s.size());
	}

	public void test06() {
		s.add(e1);
		s.add(e2);
		s.start();
		s.advance();
		assertSame(e2,s.getCurrent());
		s.add(e4);
		assertEquals(3,s.size());
		assertSame(e4, s.getCurrent());
		s.advance();
		assertEquals(false, s.isCurrent());
	}

	public void test07() {
		s.start();
		assertException(IllegalStateException.class, () -> s.getCurrent());
	}

	public void test08() {
		s.start();
		assertException(() -> s.removeCurrent(), IllegalStateException.class);
	}

	public void test09() {
		assertException(() -> s.advance(), IllegalStateException.class);
	}

	public void test10() {
		s.add(e1);
		assertEquals(1,s.size());
		assertTrue(s.isCurrent());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertEquals(1,s.size());
		assertFalse(s.isCurrent());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		assertEquals(1,s.size());
	}

	public void test11() {
		s.add(e1);
		s.start();
		s.removeCurrent();
		assertFalse(s.isCurrent());
		assertEquals(0,s.size());	
		s.add(e2);
		s.start();
		assertSame(e2,s.getCurrent());
		assertEquals(1,s.size());
	}

	public void test12() {
		assertTrue(s.add(e2));
		s.start();
		s.advance();
		assertException(() -> s.advance(), IllegalStateException.class);
		assertFalse(s.isCurrent());
		assertEquals(1,s.size());
	}

	public void test13() {
		s.add(e2);
		s.removeCurrent();
		assertFalse(s.isCurrent());
		assertEquals(0,s.size());
	}

	public void test14() {
		s.add(e2);
		s.advance();
		assertFalse(s.add(e2));
		assertTrue(s.isCurrent());
		assertEquals(1,s.size());
	}

	public void test15() {
		s.add(e1);
		assertTrue(s.add(e4));
		assertFalse(s.add(e1));
		assertEquals(e1, s.getCurrent());
		assertEquals(2, s.size());
	}

	public void test16() {
		s.add(e3);
		assertTrue(s.add(e1));
		assertFalse(s.add(e3));
		assertEquals(e3, s.getCurrent());
		assertEquals(2, s.size());
	}

	public void test17() {
		s = new SortedSequence<>();
		s.add("hello");
		assertTrue(s.add("HELLO"));
		assertEquals(2, s.size());
	}

	public void test18() {
		s = new SortedSequence<>();
		s.add("18");
		assertFalse(s.add("18"));
		assertTrue(s.add("eighteen"));
		assertTrue(s.add("Eighteen"));
		assertEquals("Eighteen", s.getCurrent());
		s.advance();
		assertEquals("eighteen", s.getCurrent());
		assertEquals(3, s.size());
	}

	public void test20() {
		s.add(e1);
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.add(e2);
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		assertEquals(2,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(2,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
		s.advance();
		s.start();
		assertSame(e1,s.getCurrent());
	}

	public void test21() {
		s.add(e1);
		assertTrue(s.isCurrent());
		s.start();
		s.advance();
		s.add(e2);
		assertTrue(s.isCurrent());
		assertEquals(2,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e2,s.getCurrent());
		assertTrue(s.isCurrent());
	}

	public void test22() {
		s.add(e2);
		s.add(e1);
		s.start();
		s.removeCurrent();
		assertTrue(s.isCurrent());
		assertEquals(1,s.size());
		assertEquals(e2,s.getCurrent());
		s.add(e3);
		assertSame(e3,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(2,s.size());
		s.start();
		assertSame(e2,s.getCurrent());
	}

	public void test23() {
		s.add(e2);
		s.add(e1);
		s.start();
		s.advance();
		s.removeCurrent();
		assertFalse(s.isCurrent());
		assertEquals(1,s.size());
		try {
			s.getCurrent();
			assertFalse("s.getCurrent() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex,ex instanceof IllegalStateException);
		}
		s.add(e3);
		assertEquals(2,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertEquals(e1,s.getCurrent());
		s.advance();
		assertEquals(e3,s.getCurrent());
	}

	public void test24() {
		String e1a = "A";
		s.add(e1);
		s.start();
		assertTrue(s.add(e1a));
		assertEquals(1, s.size());
		assertSame(e1a,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
	}

	public void test27() {
		// equivalent to e1:
		String e1a = "A";
		s.add(e1);
		s.add(e1a);
		assertSame(e1a,s.getCurrent());
		assertEquals(1, s.size());
		s.advance();
		assertException(IllegalStateException.class, () -> s.getCurrent());
	}

	public void test30() {
		s.add(e3);
		s.add(e1);
		s.add(e2);
		assertEquals(3,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertSame(e2,s.getCurrent());
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(3,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
		s.advance();
		s.start();
		assertSame(e1,s.getCurrent());
	}

	public void test31() {
		s.add(e1);
		s.start();
		s.advance();
		s.add(e4);
		s.add(e3);
		assertEquals(3,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e4,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(3,s.size());
		s.start();
		assertSame(e1,s.getCurrent());
	}

	public void test32() {
		s.add(e2);
		s.start();
		s.advance();
		s.add(e3);
		s.start();
		s.add(e1);
		assertEquals(3,s.size());
		s.start();
		assertTrue(s.isCurrent());
		assertSame(e1,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
		s.advance();
		assertFalse(s.isCurrent());
		assertEquals(3,s.size());
	}

	public void test33() {
		s.add(e3);
		s.add(e2);
		s.add(e1);
		s.start(); 
		assertEquals(e1,s.getCurrent());
		s.removeCurrent();
		assertEquals(2,s.size());
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent());
		s.advance();
		assertSame(e3,s.getCurrent());
	}

	public void test34() {
		s.add(e3);
		s.add(e2);
		s.add(e1);
		s.start(); // REDUNDANT
		s.advance();
		assertSame(e2,s.getCurrent());
		s.removeCurrent();
		assertEquals(2,s.size());
		assertTrue(s.isCurrent());
		assertSame(e3,s.getCurrent());
	}

	public void test35() {
		s.add(e3);
		s.add(e2);
		s.add(e1);
		s.start();
		s.advance();
		s.advance();
		assertSame(e3,s.getCurrent());
		s.removeCurrent();
		assertEquals(2,s.size());
		assertFalse(s.isCurrent());
		try {
			s.getCurrent();
			assertFalse("s.getCurrent() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex, ex instanceof IllegalStateException);
		}
		try {
			s.advance();
			assertFalse("s.advance() should not return",true);
		} catch (RuntimeException ex) {
			assertTrue("wrong exception thrown: " + ex, ex instanceof IllegalStateException);
		}
		s.start();
		assertSame(e1,s.getCurrent());
	}

	public void test37() {
		s.add(e1);
		s.add(e1);
		s.start();
		s.advance();
		s.add(e1);
		assertSame(e1,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());
	}

	public void test39() {
		s.add(e1);
		s.add(e2);
		s.add(e3);
		s.add(e4);
		s.add(e5);
		assertTrue(s.isCurrent());
		s.add(e1);
		s.add(e2);
		s.add(e3);
		s.add(e4);
		s.add(e5);
		s.add(e1);
		s.add(e2);
		assertEquals(5,s.size());
		s.start();
		s.removeCurrent();
		assertTrue(s.isCurrent());
		s.start();
		s.removeCurrent();
		assertSame(e3,s.getCurrent());
		assertEquals(3,s.size());
		s.start();
		assertSame(e3,s.getCurrent()); s.advance();
		assertSame(e4,s.getCurrent()); s.advance();
		assertSame(e5,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());
	}


	public void test54() {
		s.add(e1);
		s.start();
		s.removeCurrent();
		assertEquals(0,s.size());
		assertFalse(s.isCurrent());
	}

	public void test55() {
		s.add(e2);
		s.add(e1);
		s.start();
		s.add(e1);
		s.add(e2);
		assertEquals(2,s.size());
		assertTrue(s.isCurrent());
		assertSame(e2,s.getCurrent()); s.advance();
		assertFalse(s.isCurrent());		
	}


	public void test59() {
		String e1 = "Apple";
		String e1a = "apple";
		String e1b = "APPLE";
		s.add(e1a);
		s.start();
		s.add(e1b);	
		s.add(e1);
		s.add(e5);
		s.start();
		s.advance();
		s.add(e4); // s = e1 e1a e1b e4 * e5
		assertTrue(s.isCurrent());
		assertSame(e4,s.getCurrent());
		assertEquals(3,s.size());
		s.start();
		assertSame(e1, s.getCurrent());
		s.advance();
		assertSame(e4,  s.getCurrent());
	}


	/// testing clone

	public void test60() {
		SortedSequence<String> c = s.clone();
		assertFalse(c.isCurrent());
		assertEquals(0, c.size());
	}

	public void test61() {
		s.add(e1);
		s.start();
		SortedSequence<String> c = s.clone();

		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent()); s.advance();
		assertSame(e1,c.getCurrent()); c.advance();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
	}

	public void test62() {
		s.add(e1);
		SortedSequence<String> c = s.clone();

		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
	}

	public void test63() {
		SortedSequence<String> c = s.clone();
		assertFalse(c.isCurrent());

		s.add(e1);
		s.start();
		c = s.clone();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,c.getCurrent());

		s.add(e2);
		s.advance();
		c = s.clone();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());

		s.add(e3);
		assertTrue(s.isCurrent());
		assertFalse(c.isCurrent());

		s.start();
		s.advance();
		s.advance();
		c = s.clone();
		assertSame(e3,s.getCurrent());
		assertSame(e3,c.getCurrent());
		s.advance();
		c.advance();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
		s.start();
		c.start();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,c.getCurrent());
		s.advance();
		c.advance();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e2,s.getCurrent());
		assertSame(e2,c.getCurrent());

		s.start();
		c = s.clone();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e1,s.getCurrent());
		assertSame(e1,c.getCurrent());
		s.advance();
		c.advance();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e2,s.getCurrent());
		assertSame(e2,c.getCurrent());
		s.advance();
		c.advance();
		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());
		assertSame(e3,s.getCurrent());
		assertSame(e3,c.getCurrent());		
	}

	public void test64() {
		s.add(e1);
		s.add(e3);
		s.add(e2);
		s.start();
		s.advance();
		s.removeCurrent();

		SortedSequence<String> c = s.clone();

		assertEquals(2,c.size());

		assertTrue(s.isCurrent());
		assertTrue(c.isCurrent());

		assertSame(e3,s.getCurrent());
 	}

	public void test65() {
		s.add(e3);
		s.add(e4);

		SortedSequence<String> c = s.clone();
		s.add(e1);
		c.add(e2);

		s.start();
		c.start();
		assertSame(e1,s.getCurrent());
		assertSame(e2,c.getCurrent());
		s.advance();
		c.advance();
		assertSame(e3,s.getCurrent());
		assertSame(e3,c.getCurrent());
		s.advance();
		c.advance();
		assertSame(e4,s.getCurrent());
		assertSame(e4,c.getCurrent());
		s.advance();
		c.advance();
		assertFalse(s.isCurrent());
		assertFalse(c.isCurrent());
	}

}