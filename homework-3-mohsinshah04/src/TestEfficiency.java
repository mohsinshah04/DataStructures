import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import edu.uwm.cs351.ArrayPixelCollection;
import edu.uwm.cs351.Pixel;
import junit.framework.TestCase;


public class TestEfficiency extends TestCase {
	ArrayPixelCollection s;
	Collection<Pixel> c;
	Iterator<Pixel> it;
	
	@Override
	public void setUp() {
		s = new ArrayPixelCollection();
		c = s;
		try {
			assert 1/s.size() < 0 : "OK";
			assertTrue(true);
		} catch (ArithmeticException ex) {
			System.err.println("Assertions must NOT be enabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
	}

	private static final int POWER = 22;
	private static final int MAX_LENGTH = 1 << POWER;
	
	public void test0() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(i, s.size());
			s.add(new Pixel(0,i));
		}
	}

	public void test1() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(i, s.size());
			s.add(new Pixel(i,0));
		}
	}
		
	public void test2() {
		s.add(new Pixel(0, MAX_LENGTH));
		s.add(new Pixel(MAX_LENGTH, MAX_LENGTH));
		s.clearAt(0, MAX_LENGTH);
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(1, s.size());
		}
	}

	public void test3() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(0,i));
		}
		it = s.iterator();
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(i, it.next().loc().y);
		}
	}

	public void test4() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(i,0));
		}
		it = s.iterator();
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(i, it.next().loc().x);
		}
	}

	public void test5() {
		s.add(new Pixel(0, 0));
		s.add(new Pixel(MAX_LENGTH, MAX_LENGTH));
		for (int i=0; i < MAX_LENGTH; ++i) {
			it = s.iterator();
			assertEquals(new Pixel(0,0), it.next());
			assertTrue(it.hasNext());
		}
		it.remove();
		assertTrue(it.hasNext());
	}

	public void test6() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(i,0));
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			it = s.iterator();
			assertTrue(it.hasNext());
			assertEquals(0, it.next().loc().x);
		}
	}	
	
	public void test7() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(0,i));
		}
		it = s.iterator();
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(MAX_LENGTH-i, s.size());
			it.next();
			it.remove();
		}
		assertTrue(s.isEmpty());
	}
	
	public void test8() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(i,0));
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertTrue(c.contains(new Pixel(i,0)));
			assertFalse(c.contains(new Object()));
			assertFalse(c.contains(new Pixel(i,i+1)));
		}
	}

	public void test9() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(i,0));
		}
		for (int i=MAX_LENGTH-1; i >= 0; --i) {
			assertFalse(c.remove(new Pixel(1, 0, Color.RED)));
			assertTrue(c.remove(new Pixel(i, 0)));
			assertFalse(c.remove(new Object()));
		}
		assertEquals(0, s.size());
	}
}
