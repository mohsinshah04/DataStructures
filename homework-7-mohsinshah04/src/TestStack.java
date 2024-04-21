import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;

import edu.uwm.cs351.util.Stack;
import junit.framework.TestCase;


public class TestStack extends TestCase {
	protected <T> void assertException(Class<?> excClass, Runnable f) {
		try {
			f.run();
			assertFalse("Should have thrown an exception, not returned",true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}

	private Stack<Integer> s;
	
	@Override
	protected void setUp() {
		try {
			assert 42 == s.pop();
			assertTrue("Assertions not enabled.  Add -ea to VM Args Pane in Arguments tab of Run Configuration",false);
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		s = new Stack<Integer>(Integer.class);
	}

	public void test00() {
		s = new Stack<Integer>();
		assertTrue(s.isEmpty());
	}
	
	public void test01() {
		assertTrue(s.isEmpty());
	}
	
	public void test10() {
		s.push(10);
		assertFalse(s.isEmpty());
	}
	
	public void test11() {
		s.push(11);
		assertEquals(11,s.peek().intValue());
	}
	
	public void test12() {
		s.push(12);
		s.peek();
		assertFalse(s.isEmpty());
	}
	
	public void test13() {
		s.push(13);
		assertEquals(13,s.pop().intValue());
	}
	
	public void test14() {
		s.push(14);
		s.pop();
		assertTrue(s.isEmpty());
	}
	
	public void test15() {
		s.push(null);
		assertFalse(s.isEmpty());
	}
	
	public void test16() {
		s.push(null);
		assertNull(s.peek());
	}
	
	public void test17() {
		s.push(null);
		assertNull(s.pop());
	}
	
	public void test18() {
		s.push(null);
		s.peek();
		assertFalse(s.isEmpty());
	}
	
	public void test19() {
		s.push(null);
		s.pop();
		assertTrue(s.isEmpty());
	}
	
	public void test20() {
		s.push(2);
		s.push(0);
		assertFalse(s.isEmpty());
	}
	
	public void test21() {
		s.push(2);
		s.push(1);
		assertEquals(1,s.peek().intValue());
	}
	
	public void test22() {
		s.push(2);
		s.push(22);
		assertEquals(22,s.pop().intValue());
	}
	
	public void test23() {
		s.push(2);
		s.push(3);
		s.pop();
		assertFalse(s.isEmpty());
	}
	
	public void test24() {
		s.push(2);
		s.push(4);
		s.pop();
		assertEquals(2,s.peek().intValue());
	}
	
	public void test25() {
		s.push(2);
		s.push(5);
		s.pop();
		assertEquals(2,s.pop().intValue());
	}
	
	public void test26() {
		s.push(2);
		s.push(6);
		s.pop();
		s.peek();
		assertFalse(s.isEmpty());
	}
	
	public void test27() {
		s.push(2);
		s.push(7);
		s.pop();
		s.peek();
		assertEquals(2,s.peek().intValue());
	}
	
	public void test28() {
		s.push(8);
		s.push(2);
		s.pop();
		assertEquals(8,s.pop().intValue());
	}
	
	public void test29() {
		s.push(9);
		s.push(2);
		s.pop();
		s.pop();
		assertTrue(s.isEmpty());
	}

	public void test30() {
		s.push(1);
		s.push(2);
		s.push(3);
		assertFalse(s.isEmpty());
		assertEquals(3,s.peek().intValue());
		assertFalse(s.isEmpty());
		assertEquals(3,s.pop().intValue());
		assertFalse(s.isEmpty());
		assertEquals(2,s.peek().intValue());
		assertFalse(s.isEmpty());
		assertEquals(2,s.pop().intValue());
		assertFalse(s.isEmpty());
		assertEquals(1,s.peek().intValue());
		assertFalse(s.isEmpty());
		assertEquals(1,s.pop().intValue());
		assertTrue(s.isEmpty());
	}
	
	public void test31() {
		s.push(-3);
		s.push(null);
		s.push(0);
		assertFalse(s.isEmpty());
		assertEquals(0,s.peek().intValue());
		assertFalse(s.isEmpty());
		assertEquals(0,s.pop().intValue());
		assertFalse(s.isEmpty());
		assertNull(s.peek());
		assertFalse(s.isEmpty());
		assertNull(s.pop());
		assertFalse(s.isEmpty());
		assertEquals(-3,s.peek().intValue());
		assertFalse(s.isEmpty());
		assertEquals(-3,s.pop().intValue());
		assertTrue(s.isEmpty());
	}
	
	public void test32() {
		s = new Stack<Integer>();; // test with no class object
		s.push(-3);
		s.push(null);
		s.push(0);
		assertFalse(s.isEmpty());
		assertEquals(0,s.peek().intValue());
		assertFalse(s.isEmpty());
		assertEquals(0,s.pop().intValue());
		assertFalse(s.isEmpty());
		assertNull(s.peek());
		assertFalse(s.isEmpty());
		assertNull(s.pop());
		assertFalse(s.isEmpty());
		assertEquals(-3,s.peek().intValue());
		assertFalse(s.isEmpty());
		assertEquals(-3,s.pop().intValue());
		assertTrue(s.isEmpty());
	}
	
	public void test38() {
		for (int i=0; i < 1000; ++i) {
			s.push(i);
			assertFalse(s.isEmpty());
		}
		for (int i=0; i < 1000; ++i) {
			assertFalse(s.isEmpty());
			if ((i & 1) == 0) {
				assertEquals(999,i+s.peek());
			}
			assertEquals(999,i+s.pop());
		}
		assertTrue(s.isEmpty());
	}
	
	public void test39() {
		s = new Stack<Integer>();
		for (int i=0; i < 1000; ++i) {
			s.push(i);
			assertFalse(s.isEmpty());
		}
		for (int i=0; i < 1000; ++i) {
			assertFalse(s.isEmpty());
			if ((i & 1) == 0) {
				assertEquals(999,i+s.peek());
			}
			assertEquals(999,i+s.pop());
		}
		assertTrue(s.isEmpty());
	}
	
	
	/// test4X: tests of clear
	
	public void test40() {
		s.clear();
		assertTrue(s.isEmpty());
	}
	
	public void test41() {
		s.push(41);
		s.clear();
		assertTrue(s.isEmpty());
	}
	
	public void test42() {
		s.push(4);
		s.push(2);
		s.clear();
		assertTrue(s.isEmpty());
	}
	
	public void test43() {
		s.push(4);
		s.push(3);
		s.push(-43);
		s.clear();
		assertTrue(s.isEmpty());
	}
	
	public void test48() {
		for (int i=0; i < 1000; ++i) {
			s.push(i);
			assertFalse(s.isEmpty());
		}
		s.clear();
		assertTrue(s.isEmpty());
		s.clear();
		assertTrue(s.isEmpty());
	}
	
	public void test49() {
		s = new Stack<>();
		for (int i=0; i < 1000; ++i) {
			s.push(i);
			assertFalse(s.isEmpty());
		}
		s.clear();
		assertTrue(s.isEmpty());
		s.clear();
		assertTrue(s.isEmpty());
	}
	
	
	/// test5X: tests of clone
	
	public void test50() {
		assertTrue(s.clone().isEmpty());
	}
	
	public void test51() {
		Stack<Integer> c = s.clone();
		c.push(51);
		assertTrue(s.isEmpty());
	}
	
	public void test52() {
		s.push(52);
		Stack<Integer> c = s.clone();
		assertEquals(52,c.pop().intValue());
	}
	
	public void test53() {
		s.push(53);
		Stack<Integer> c = s.clone();
		s.pop();
		s.push(-19);
		assertEquals(53,c.peek().intValue());
	}
	
	public void test54() {
		class Hidden extends Stack<String> {
			public boolean isEmpty() {
				return true;
			}
		}
		Hidden h = new Hidden();
		Stack<String> st = h.clone();
		assertTrue("clone didn't use super.clone()",st instanceof Hidden);
	}
	
	public void test55() {
		final Collection<Object> flags = new ArrayList<>();
		class Hidden {
			@Override
			protected Object clone() {
				System.err.println("Should not be cloning elements");
				flags.add("ERROR");
				throw new AssertionError("clone should not be called.");
			}
		}
		Stack<Hidden> st = new Stack<>();
		st.push(new Hidden());
		st.push(new Hidden());
		st.pop();
		st.clone();
		assertTrue(flags.isEmpty());
	}
	
	public void test56() {
		s.push(169);
		Stack<Integer> c = s.clone();
		s.push(343);
		c.push(-55);
		assertEquals(-55,c.peek().intValue());
		assertEquals(343,s.pop().intValue());
		assertEquals(-55,c.pop().intValue());
		assertEquals(169,s.pop().intValue());
		assertEquals(169,c.pop().intValue());
	}
	
	public void test57() {
		Stack<String> s1 = new Stack<String>();
		Stack<String> s2 = s1.clone();
		s1.push("hello");
		s2.push("bye");
		assertEquals("hello",s1.pop());
		assertEquals("bye",s2.pop());
	}
	
	public void test58() {
		for (int i=0; i < 1000; ++i) {
			s.push(i);
		}
		Stack<Integer> c = s.clone();
		for (int i=1; i < 500; ++i) {
			assertEquals(1000,i+c.pop());
			assertEquals(1000,i+s.pop());
		}
		s.push(-17);
		c.push(33);
		assertEquals(-17,s.pop().intValue());
		assertEquals(33,c.pop().intValue());
	}
	
	public void test59() {
		s = new Stack<>();
		for (int i=0; i < 1000; ++i) {
			s.push(i);
		}
		Stack<Integer> c= s.clone();
		s.clear();
		
		assertTrue(s.isEmpty());
		assertFalse(c.isEmpty());
	}
	
	
	/// test6x: testing capacity
	
	public void test60() {
		assertEquals(1, s.getCapacity());
		s = new Stack<>();
		assertEquals(1, s.getCapacity());
	}
	
	public void test61() {
		s.push(1);
		assertEquals(1, s.getCapacity());
	}
	
	public void test62() {
		s = new Stack<>();
		s.push(99);
		s.push(62);
		assertEquals(2, s.getCapacity());
	}
	
	public void test63() {
		s.push(6);
		s.push(3);
		s.push(63);
		assertEquals(4, s.getCapacity());
	}
	
	public void test64() {
		s = new Stack<>();
		s.push(6);
		s.push(4);
		s.push(64);
		s.push(46);
		assertEquals(4, s.getCapacity());
	}
	
	public void test65() {
		s.push(1);
		s.push(2);
		s.push(3);
		s.push(4);
		s.push(5);
		assertEquals(8, s.getCapacity());
	}
	
	public void test66() {
		s.push(-1);
		s.push(-2);
		s.push(-3);
		s.push(-4);
		s.push(-5);
		s.push(-6);
		s.pop();
		s.pop();
		s.pop();
		s.pop();
		s.pop();
		s.pop();
		assertEquals(8, s.getCapacity());
	}
	
	public void test67() {
		for (int i=0; i < 100; ++i) {
			s.push(i);
		}
		assertEquals(128, s.getCapacity());
	}
	
	public void test68() {
		for (int i=0; i < 100; ++i) {
			s.push(i);
		}
		s.pop();
		s.pop();
		for (int i=0; i < 100; ++i) {
			s.push(i);
		}
		assertEquals(256, s.getCapacity());
	}
	
	
	/// test 9X: testing errors
	
	public void test90() {
		assertException(EmptyStackException.class, () -> s.peek());
	}
	
	public void test91() {
		assertException(EmptyStackException.class, () -> s.pop());		
	}
	
	public void test92() {
		s.push(92);
		s.pop();
		assertException(EmptyStackException.class, () -> s.pop());	
	}
	
	public void test93() {
		s.push(9);
		s.push(3);
		s.clear();
		assertException(EmptyStackException.class, () -> s.peek());
	}
	
	public void test94() {
		s.push(9);
		s.push(4);
		s.push(94);
		s.push(-94);
		s.pop();
		s.pop();
		s.pop();
		s.push(9944);
		s.pop();
		s.pop();
		assertException(EmptyStackException.class, () -> s.pop());	
	}
	
	public void test99() {
		for (int i=0; i < 1000; ++i) {
			s.push(null);
		}
		for (int i=0; i < 1000; ++i) {
			s.peek();
			s.pop();
		}
		assertException(EmptyStackException.class, () -> s.peek());		
	}
}
