import edu.uwm.cs351.Pixel;
import edu.uwm.cs351.ArrayPixelCollection;
import edu.uwm.cs.junit.LockedTestCase;
import java.awt.Color;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class TestDirect extends LockedTestCase{
	protected void assertNotEquals(Object x, Object y) {
		if (x == null) {
			assertFalse(x == y);
		} else {
			assertFalse(x + " should not equal " + y, x.equals(y));
		}
	}

	protected void assertException(Class<? extends Throwable> c, Runnable r) {
		try {
			r.run();
			assertFalse("Exception should have been thrown",true);
		} catch (Throwable ex) {
			assertTrue("should throw exception of " + c + ", not of " + ex.getClass(), c.isInstance(ex));
		}
	}

	@Override // implementation
	public void setUp() {
		try {
			assert 3/new Pixel(0,3).loc().x == 42 : "OK";
			System.err.println("Assertions must be enabled to use this test suite.");
			System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
		} catch (ArithmeticException ex) {
			return;
		}
	}
	
	private static final String[] COLORS = { 
			"WHITE", "BLACK", "RED", "BLUE", "GREEN", "YELLOW",
			"CYAN", "MAGENTA", "PINK", "ORANGE", "GRAY"
	};
	private static final String[] COLOR_STRINGS = new String[COLORS.length];
	
	static {
		try {
			for (int i=0; i < COLORS.length; ++i) {
				Color c = (Color)Color.class.getField(COLORS[i]).get(null);
				COLOR_STRINGS[i] = c.toString();
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
			
	}

	protected String asString(Supplier<?> s) {
		String result = "";
		try {
			result = "" + s.get();
		} catch (RuntimeException ex) {
			return ex.getClass().getSimpleName();
		}
		for (int i=0; i < COLORS.length; ++i) {
			result = result.replace(COLOR_STRINGS[i], COLORS[i]);
		}
		return result;
	}
	
	
	ArrayPixelCollection r;
	Iterator<Pixel> it;
	
	/// test00: the locked tests

	public void test00() {
		r = new ArrayPixelCollection();
		it = r.iterator();
		// how does the result print? or give the name of exception thrown
		assertEquals(Ts(353642306), asString(() -> it.hasNext()));
		assertEquals(Ts(1334561460), asString(() -> it.next()));
		assertEquals(Ts(1090715015),asString(() -> r.add(new Pixel(100,3,Color.RED))));
		// Does the iterator now have a next ?
		assertEquals(Ts(92011390), asString(() -> it.hasNext()));
		it = r.iterator(); // let's get a fresh iterator
		assertEquals(Ts(905192595), asString(() -> it.next())); // pixel prints as <x,y,COLOR>
		assertEquals(Ts(615839172), asString(() -> it.hasNext()));
		r.clear();
		assertEquals(Ts(162368898), asString(() -> it.hasNext()));
	}



	/// test0x: constructors

	public void test01() {
		r = new ArrayPixelCollection();
		assertEquals(0, r.size());
	}
	
	public void test02() {
		r = new ArrayPixelCollection(1000, 1000);
		assertEquals(0, r.size());
	}
	
	public void test03() {
		r = new ArrayPixelCollection(1,0);
		assertEquals(0, r.size());
	}
	
	public void test04() {
		assertException(IllegalArgumentException.class, () -> new ArrayPixelCollection(0,0));
	}
	
	public void test05() {
		assertException(IllegalArgumentException.class, () -> new ArrayPixelCollection(0,1));
	}
	
	public void test06() {
		assertException(OutOfMemoryError.class, () -> new ArrayPixelCollection(1_000_000_000,1_000_000_000));		
	}

	public void test07() {
		assertException(IllegalArgumentException.class, () -> new ArrayPixelCollection(-8, -8));
	}
	
	public void test08() {
		assertException(IllegalArgumentException.class, () -> new ArrayPixelCollection(-1, 2));		
	}
	
	public void test09() {
		assertException(IllegalArgumentException.class, () -> new ArrayPixelCollection(3, -1));		
	}


	/// test1x: add tests

	public void test10() {
		r = new ArrayPixelCollection(1,1);
		assertTrue(r.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test11() {
		r = new ArrayPixelCollection(1,1);
		assertTrue(r.add(new Pixel(0,0,Color.BLACK)));
	}

	public void test12() {
		r = new ArrayPixelCollection(1,1);
		r.add(new Pixel(0,0,Color.WHITE));
		assertFalse(r.add(new Pixel(0,0,Color.WHITE)));
	}
	
	public void test13() {
		ArrayPixelCollection r1 = new ArrayPixelCollection(1,1);
		ArrayPixelCollection r2 = new ArrayPixelCollection(1,1);
		r2.add(new Pixel(0,0,Color.WHITE));
		assertTrue(r1.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test14() {
		r = new ArrayPixelCollection(1,1);
		assertTrue(r.add(new Pixel(0,0,Color.RED)));
		assertTrue(r.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test15() {
		r = new ArrayPixelCollection(1,1);
		r.add(new Pixel(3,3,Color.RED));
		assertFalse(r.add(new Pixel(3,3,Color.RED)));
	}
	
	public void test16() {
		r = new ArrayPixelCollection(1,0);
		r.add(new Pixel(1,4));
		assertTrue(r.add(new Pixel(4,1)));
	}
	
	public void test17() {
		r = new ArrayPixelCollection(4,4);
		for (int i=1; i < 9; ++i) {
			for (int j=0; j < 8; ++j) {
				assertTrue("Problem for " + i + "," + j, r.add(new Pixel(i,j)));
			}
		}
		assertFalse(r.add(new Pixel(5,5)));
		assertTrue(r.add(new Pixel(4,8)));
		assertTrue(r.add(new Pixel(4,4, Color.RED)));
	}
	
	public void test18() {
		r = new ArrayPixelCollection(1,1);
		assertException(NullPointerException.class, () -> r.add(null));
	}

	public void test19() {
		r = new ArrayPixelCollection(1,1);
		r.add(new Pixel(1,0));
		r.add(new Pixel(0,1));
		r.add(new Pixel(1,1));
		assertException(IllegalArgumentException.class, () -> r.add(new Pixel(-1,-1)));
		assertException(IllegalArgumentException.class, () -> r.add(new Pixel(-1,0)));
		assertException(IllegalArgumentException.class, () -> r.add(new Pixel(0,-1)));
		assertTrue(r.add(new Pixel(10,1)));
		assertTrue(r.add(new Pixel(4,10)));
	}

	
	//test2x: getPixel tests

	public void test20() {
		assertNull(new ArrayPixelCollection(1,1).getPixel(0,0));
	}

	public void test21() {
		r = new ArrayPixelCollection(1,1);
		r.add(new Pixel(0,0,Color.BLACK));
		assertEquals(new Pixel(0,0,Color.BLACK), r.getPixel(0,0));
	}

	public void test22() {
		r = new ArrayPixelCollection(2,2);
		Pixel p1 = new Pixel(0,1,Color.RED);
		Pixel p2 = new Pixel(1,0,Color.GREEN);
		Pixel p3 = new Pixel(1,1,Color.BLUE);
		r.add(p1);
		r.add(p2);
		r.add(p3);
		assertEquals(p1, r.getPixel(0,1));
		assertEquals(p2, r.getPixel(1,0));
		assertEquals(p3, r.getPixel(1,1));
		assertNull(r.getPixel(0, 0));
	}

	public void test23() {
		r = new ArrayPixelCollection(1,0);
		assertNull(r.getPixel(0,0));
	}
	
	public void test24() {
		r = new ArrayPixelCollection(1,1);
		assertNull(r.getPixel(10,0));
	}
	
	public void test25() {
		r = new ArrayPixelCollection(2, 5);
		assertNull(r.getPixel(1, 5));
	}
	
	public void test26() {
		r = new ArrayPixelCollection(2, 6);
		assertNull(r.getPixel(3, 10));
	}
	
	public void test27() {
		r = new ArrayPixelCollection(10,10);
		assertException(IllegalArgumentException.class, () -> r.getPixel(-1,0));
		assertException(IllegalArgumentException.class, () -> r.getPixel(0,-1));
	}

	public void test28() {
		r = new ArrayPixelCollection(2,2);
		assertException(IllegalArgumentException.class, () -> r.getPixel(-3,8));
		assertException(IllegalArgumentException.class, () -> r.getPixel(3,-8));
	}
	
	public void test29() {
		r = new ArrayPixelCollection(10,10);
		assertException(IllegalArgumentException.class, () -> r.getPixel(-3,-9));
	}
	
	
	// test3x: tests of clearAt
	
	public void test30() {
		r = new ArrayPixelCollection(10,10);
		assertFalse(r.clearAt(3, 0));
	}
	
	public void test31() {
		r = new ArrayPixelCollection(1,1);
		assertFalse(r.clearAt(3, 1));
	}
	
	public void test32() {
		r = new ArrayPixelCollection(2, 2);
		r.add(new Pixel(1,1));
		assertTrue(r.clearAt(1, 1));
	}
	
	public void test33() {
		r = new ArrayPixelCollection(3, 3);
		r.add(new Pixel(1,1));
		assertFalse(r.clearAt(1, 0));
	}
	
	public void test34() {
		r = new ArrayPixelCollection(3, 4);
		r.add(new Pixel(2,2));
		assertTrue(r.clearAt(2, 2));
		assertFalse(r.clearAt(2, 2));
	}
	
	public void test35() {
		r = new ArrayPixelCollection(3, 5);
		r.add(new Pixel(3, 2));
		assertFalse(r.clearAt(2, 2));
		assertTrue(r.clearAt(3, 2));
	}
	
	public void test36() {
		r = new ArrayPixelCollection(3, 6);
		r.add(new Pixel(3, 3));
		r.add(new Pixel(2, 2));
		assertFalse(r.clearAt(2, 3));
		assertFalse(r.clearAt(3, 2));
		r.add(new Pixel(1, 1));
		assertTrue(r.clearAt(2, 2));
	}
	
	public void test37() {
		ArrayPixelCollection r1 = new ArrayPixelCollection(3,7);
		ArrayPixelCollection r2 = new ArrayPixelCollection(7,3);
		r1.add(new Pixel(2, 2));
		r2.add(new Pixel(2, 2));
		assertTrue(r1.clearAt(2, 2));
		assertTrue(r2.clearAt(2, 2));
		assertFalse(r1.clearAt(2, 2));
	}
	
	public void test38() {
		r = new ArrayPixelCollection(3, 8);
		r.add(new Pixel(1, 2));
		r.add(new Pixel(2, 2));
		r.add(new Pixel(2, 3));
		r.add(new Pixel(1, 4));
		assertFalse(r.clearAt(1, 3));
		assertTrue(r.clearAt(1, 2));
		assertFalse(r.clearAt(2, 4));
	}
	
	public void test39() {
		r = new ArrayPixelCollection(3, 9);
		r.add(new Pixel(0, 1));
		r.add(new Pixel(1, 1));
		r.add(new Pixel(2, 1));
		r.add(new Pixel(3, 1));
		r.add(new Pixel(0, 2));
		r.add(new Pixel(2, 2));
		r.add(new Pixel(0, 3));
		r.add(new Pixel(3, 3));
		assertFalse(r.clearAt(4, 4));
		assertFalse(r.clearAt(1, 2));
		assertTrue(r.clearAt(3, 1));
		assertTrue(r.clearAt(3, 3));
	}
	
	
	/// test4x: tests of size
	
	public void test40() {
		r = new ArrayPixelCollection(4,5);
		assertEquals(0, r.size());
	}
	
	public void test41() {
		r = new ArrayPixelCollection(4,1);
		r.add(new Pixel(3,0));
		assertEquals(1, r.size());
	}
	
	public void test42() {
		r = new ArrayPixelCollection(4, 2);
		r.add(new Pixel(3,0));
		r.add(new Pixel(4,1));
		assertEquals(2, r.size());
	}
	
	public void test43() {
		r = new ArrayPixelCollection(4, 3);
		r.add(new Pixel(2,2));
		r.add(new Pixel(4,0));
		r.add(new Pixel(4,3));
		assertEquals(3, r.size());
	}
	
	public void test44() {
		r = new ArrayPixelCollection(4,4);
		r.add(new Pixel(0,0));
		r.add(new Pixel(1,1));
		r.add(new Pixel(0,3));
		r.add(new Pixel(1,2));
		r.add(new Pixel(1,0));
		assertEquals(5, r.size());
	}
	
	public void test45() {
		r = new ArrayPixelCollection(4,5);
		r.add(new Pixel(0,1));
		r.add(new Pixel(0,3));
		r.add(new Pixel(1,0));
		r.add(new Pixel(1,1));
		r.add(new Pixel(1,3));
		r.add(new Pixel(1,4));
		r.add(new Pixel(3,3));
		r.add(new Pixel(4,2));
		r.add(new Pixel(4,4));
		assertEquals(9, r.size());
	}
	
	public void test46() {
		r = new ArrayPixelCollection(4,5);
		r.add(new Pixel(0, 0));
		r.add(new Pixel(3, 3));
		r.add(new Pixel(4, 2));
		r.add(new Pixel(2, 4));
		assertEquals(4, r.size());
		r.add(new Pixel(4, 4));
		r.add(new Pixel(2, 2));
		assertEquals(6, r.size());
	}
	
	public void test47() {
		ArrayPixelCollection r1 = new ArrayPixelCollection(4,7);
		ArrayPixelCollection r2 = new ArrayPixelCollection(4,7);
		r1.add(new Pixel(3,2));
		r1.add(new Pixel(4,3));
		r1.add(new Pixel(2,6));
		r2.add(new Pixel(0,5));
		r2.add(new Pixel(3,2));
		r2.add(new Pixel(1,4));
		r2.add(new Pixel(4,4));
		assertEquals(3, r1.size());
		assertEquals(4, r2.size());
	}
	
	public void test48() {
		r = new ArrayPixelCollection(4, 8);
		r.add(new Pixel(1, 2, Color.RED));
		r.add(new Pixel(2, 3, Color.BLACK));
		r.add(new Pixel(3, 4, Color.PINK));
		r.add(new Pixel(4, 5, Color.DARK_GRAY));
		r.add(new Pixel(0, 6, new Color(100,100,35)));
		assertEquals(5, r.size());
	}
	
	public void test49() {
		r = new ArrayPixelCollection(100, 100);
		for (int i=0; i < 100; ++i) {
			for (int j=0; j < 100; j += 2) {
				r.add(new Pixel(i, j, Color.BLUE));
			}
		}
		assertEquals(5000, r.size());
	}

	
	/// test5x: test of toString()

	public void test50() {
		r = new ArrayPixelCollection();
		assertEquals("[0]:0", r.toString());
	}
	
	public void test51() {
		r = new ArrayPixelCollection(3,0);
		assertEquals("[0,0,0]:0", r.toString());
	}
	
	public void test52() {
		r = new ArrayPixelCollection(2,3);
		assertEquals("[0,0]:0", r.toString());
	}
	
	public void test53() {
		r = new ArrayPixelCollection(3,2);
		r.add(new Pixel(1, 6));
		assertEquals("[0,7,0]:1", r.toString());
	}
	
	public void test54() {
		r = new ArrayPixelCollection(3,2);
		r.add(new Pixel(5,2));
		assertEquals("[0,0,0,,,3]:1", r.toString());
	}
	
	public void test55() {
		r = new ArrayPixelCollection(3, 3);
		r.add(new Pixel(4,5));
		r.clearAt(4, 5);
		assertEquals("[0,0,0,,6]:0", r.toString());
	}
	
	public void test56() {
		r = new ArrayPixelCollection(2,2);
		r.add(new Pixel(6, 6));
		r.add(new Pixel(6, 10));
		assertEquals("[0,0,,,,,11]:2", r.toString());
	}
	
	public void test57() {
		r = new ArrayPixelCollection(2,2);
		r.add(new Pixel(2, 2));
		r.add(new Pixel(6, 10));
		assertEquals("[0,0,3,,,,11]:2", r.toString());
	}
	
	public void test58() {
		r = new ArrayPixelCollection(2,2);
		r.add(new Pixel(2, 2));
		r.add(new Pixel(2, 3));
		r.add(new Pixel(3, 0));
		r.add(new Pixel(1, 1));
		assertEquals("[0,2,4,1]:4", r.toString());
	}
	
	public void test59() {
		r = new ArrayPixelCollection(2,2);
		r.add(new Pixel(2, 2));
		r.add(new Pixel(2, 3));
		r.add(new Pixel(3, 0));
		r.add(new Pixel(1, 1));
		r.clearAt(1, 1);
		assertEquals("[0,2,4,1]:3", r.toString());
	}
	
	/*
	public void test50() {
		r = new ArrayPixelCollection(6,6);
		r.add(new Pixel(3,4));
		r.clearAt(3, 4);
		assertNull(r.getPixel(3, 4));
		assertEquals(0, r.size());
	}
	
	public void test51() {
		r = new ArrayPixelCollection(6, 6);
		r.add(new Pixel(3,2));
		r.add(new Pixel(3,3));
		r.add(new Pixel(2,5));
		r.clearAt(3, 4);
		r.clearAt(3, 3);
		assertTrue(r.add(new Pixel(3,3)));
		r.clearAt(2, 5);
		assertEquals(2, r.size());
		assertEquals(new Pixel(3,3), r.getPixel(3, 3));
	}
	
	public void test52() {
		r = new ArrayPixelCollection(6, 6);
		r.add(new Pixel(0, 0));
		r.add(new Pixel(0, 2));
		r.add(new Pixel(0, 4));
		r.add(new Pixel(1, 1));
		r.add(new Pixel(1, 2));
		r.add(new Pixel(1, 3));
		r.add(new Pixel(1, 4));
		r.add(new Pixel(2, 2));
		r.add(new Pixel(2, 5));
		r.add(new Pixel(3, 0));
		r.add(new Pixel(3, 3));
		r.clearAt(3, 0);
		assertEquals(10, r.size());
		assertNull(r.getPixel(3, 0));
		assertNull(r.getPixel(3, 1));
		assertNull(r.getPixel(3, 2));
		assertEquals(new Pixel(3, 3), r.getPixel(3, 3));
		assertNull(r.getPixel(4, 4));
		r.add(new Pixel(4, 4, Color.RED));
		r.add(new Pixel(5, 5));
		assertEquals(new Pixel(4,4,Color.RED), r.getPixel(4, 4));
		assertEquals(12, r.size());
	}
	*/
	
	
	/// test6x: tests of iterator semantics (without removal)
	
	public void test60() {
		r = new ArrayPixelCollection();
		it = r.iterator();
		assertFalse(it.hasNext());
	}
	
	public void test61() {
		r = new ArrayPixelCollection();
		it = r.iterator();
		assertException(NoSuchElementException.class, () -> it.next());
		assertException(IllegalStateException.class, () -> it.remove());		
	}
	
	public void test62() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(3,0));
		it = r.iterator();
		assertEquals(new Pixel(3,0), it.next());
	}
	
	public void test63() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(3,0));
		it = r.iterator();
		it.next();
		assertFalse(it.hasNext());
		assertException(NoSuchElementException.class, () -> it.next());
	}
	
	public void test64() {
		r = new ArrayPixelCollection();
		it = r.iterator();
		r.add(new Pixel(3,0));
		assertException(ConcurrentModificationException.class, () -> it.next());
	}
	
	public void test65() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(5, 0, Color.BLUE));
		r.add(new Pixel(2, 1, Color.BLACK));
		it = r.iterator();
		assertEquals(new Pixel(2, 1, Color.BLACK), it.next());
		assertEquals(new Pixel(5, 0, Color.BLUE), it.next());
		assertFalse(it.hasNext());
	}
	
	public void test66() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(8, 3, Color.YELLOW));
		it = r.iterator();
		r.add(new Pixel(7, 6, Color.RED));
		assertException(ConcurrentModificationException.class, () -> it.next());
		it = r.iterator();
		assertEquals(new Pixel(7, 6, Color.RED), it.next());
	}
	
	public void test67() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(7, 7, Color.PINK));
		it = r.iterator();
		r.add(new Pixel(8, 4, Color.GRAY));
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
		it = r.iterator();
		assertEquals(new Pixel(7, 7, Color.PINK), it.next());
	}
	
	public void test68() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(7, 8, Color.GREEN));
		it = r.iterator();
		assertFalse(r.add(new Pixel(7, 8, Color.GREEN)));
		assertEquals(new Pixel(7, 8, Color.GREEN), it.next());
	}
	
	public void test69() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(7, 9, Color.WHITE));
		r.add(new Pixel(7, 147, Color.BLACK));
		it = r.iterator();
		assertEquals(new Pixel(7, 9), it.next());
		assertTrue(r.add(new Pixel(7,147, Color.GREEN)));
		assertTrue(it.hasNext());
		assertEquals(new Pixel(7,147,Color.GREEN), it.next());
	}
	
	
	/// test7x: longer tests of iterators
	
	public void test70() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(2, 2, Color.CYAN));
		r.add(new Pixel(6, 1, Color.YELLOW));
		r.add(new Pixel(2, 0, Color.GREEN));
		it = r.iterator();
		assertEquals(new Pixel(2, 0, Color.GREEN), it.next());
		assertEquals(new Pixel(2, 2, Color.CYAN), it.next());
		assertEquals(new Pixel(6, 1, Color.YELLOW), it.next());
		assertFalse(it.hasNext());
	}
	
	public void test71() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(5, 2));
		r.add(new Pixel(5, 3));
		r.add(new Pixel(5, 0));
		r.add(new Pixel(1, 3));
		r.add(new Pixel(0, 2));
		r.clearAt(1, 3);
		it = r.iterator();
		assertEquals(new Pixel(0, 2), it.next());
		assertEquals(new Pixel(5, 0), it.next());
		assertEquals(new Pixel(5, 2), it.next());
		assertEquals(new Pixel(5, 3), it.next());
		assertException(NoSuchElementException.class, () -> it.next());
	}
	
	public void test72() {
		r = new ArrayPixelCollection(7, 2);
		r.add(new Pixel(5, 2));
		r.add(new Pixel(0, 2));
		r.add(new Pixel(5, 3));
		r.add(new Pixel(5, 0));
		r.add(new Pixel(1, 3));
		r.clearAt(1, 3);
		it = r.iterator();
		assertEquals(new Pixel(0, 2), it.next());
		Iterator<Pixel> other = r.iterator();
		assertEquals(new Pixel(0, 2), other.next());
		assertEquals(new Pixel(5, 0), other.next());
		assertEquals(new Pixel(5, 2), other.next());
		assertEquals(new Pixel(5, 0), it.next());
	}
	
	public void test73() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(6, 0));
		r.add(new Pixel(5, 1));
		r.add(new Pixel(4, 2));
		r.add(new Pixel(3, 3));
		r.add(new Pixel(2, 4));
		r.add(new Pixel(1, 0));
		r.add(new Pixel(4, 2));
		it = r.iterator();
		assertEquals(new Pixel(1, 0), it.next());
		r.add(new Pixel(2, 1));
		r.add(new Pixel(3, 2));
		r.add(new Pixel(4, 3));
		r.add(new Pixel(5, 4));
		r.add(new Pixel(6, 3));
		assertEquals(11, r.size());
		r.add(new Pixel(0, 3));
		
		assertException(ConcurrentModificationException.class, () -> it.next());
		it = r.iterator();
		r.clearAt(4, 1);
		assertEquals(new Pixel(0, 3), it.next());
	}
	
	public void test74() {
		r = new ArrayPixelCollection(5,5);
		r.add(new Pixel(3, 0));
		r.add(new Pixel(2, 5));
		r.add(new Pixel(4, 2));
		it = r.iterator();
		it.next();
		it.next();
		assertEquals(new Pixel(4, 2), it.next());
		r.clearAt(4, 2);
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
	}
	
	public void test75() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(10, 10));
		r.add(new Pixel(1, 4));
		r.clearAt(1, 4);
		assertEquals("[0,5,,,,,,,,,11]:1",r.toString());
	}
	
	public void test76() {
		r = new ArrayPixelCollection(5,5);
		r.add(new Pixel(4, 3));
		r.add(new Pixel(0, 2));
		r.clearAt(0, 2);
		assertEquals("[3,0,0,0,4]:1", r.toString());
	}
	
	public void test77() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(10, 10));
		r.add(new Pixel(1, 4));
		r.add(new Pixel(1, 6));
		r.add(new Pixel(1, 4));
		r.clearAt(10, 10);
		r.clearAt(1, 6);
		r.clearAt(1, 4);
		assertEquals("[0,7,,,,,,,,,11]:0",r.toString());
	}
	
	public void test78() {
		r = new ArrayPixelCollection(5, 5);
		r.add(new Pixel(3, 0));
		r.add(new Pixel(2, 5));
		r.add(new Pixel(3, 3));
		r.clearAt(3, 3);
		assertEquals("[0,0,6,4,0]:2", r.toString());
	}
	
	public void test79() {
		r = new MyPixelCollection();
		r.add(new Pixel(3, 3, Color.YELLOW));
		ArrayPixelCollection copy = r.clone();
		// If the followjng fails: you are not using the result of super.clone():
		assertTrue(copy instanceof MyPixelCollection);
		copy.add(new Pixel(3, 1, Color.GREEN));
		assertEquals(2, copy.size());
		// If the following gets an invariant error, you are implementing a shallow clone
		assertEquals(1, r.size());
	}
	
	private class MyPixelCollection extends ArrayPixelCollection {
		public MyPixelCollection() {
			super();
		}
	}
	
	
	/// test8x: test iterator remove
	
	public void test80() {
		r = new ArrayPixelCollection(10,10);
		it = r.iterator();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test81() {
		r = new ArrayPixelCollection(8, 1);
		r.add(new Pixel(5, 3, Color.RED));
		it = r.iterator();
		it.next();
		it.remove();
		assertFalse(it.hasNext());
		assertEquals(0, r.size());
	}
	
	public void test82() {
		r = new ArrayPixelCollection(8, 2);
		r.add(new Pixel(3, 1, Color.BLUE));
		it = r.iterator();
		assertException(IllegalStateException.class, () -> it.remove());		
	}
	
	public void test83() {
		r = new ArrayPixelCollection(8, 3);
		r.add(new Pixel(7, 2, Color.GREEN));
		r.add(new Pixel(0, 5, Color.YELLOW));
		it = r.iterator();
		it.next();
		it.remove();
		assertEquals(1, r.size());
		assertEquals(true, it.hasNext());
	}
	
	public void test84() {
		r = new ArrayPixelCollection(8, 4);
		r.add(new Pixel(6, 6, Color.GRAY));
		r.add(new Pixel(2, 10, Color.CYAN));
		it = r.iterator();
		it.next();
		it.remove();
		assertEquals(new Pixel(6, 6, Color.GRAY), it.next());
	}
	
	public void test85() {
		r = new ArrayPixelCollection(8, 5);
		r.add(new Pixel(5, 1));
		r.add(new Pixel(8, 3, Color.BLACK));
		r.add(new Pixel(1, 4, Color.PINK));
		it = r.iterator();
		it.next();
		it.next();
		it.remove();
		assertException(IllegalStateException.class, () -> it.remove());		
	}
	
	public void test86() {
		r = new ArrayPixelCollection(8, 6);
		r.add(new Pixel(8, 3, Color.ORANGE));
		r.add(new Pixel(6, 4, Color.DARK_GRAY));
		r.add(new Pixel(6, 2, Color.MAGENTA));
		r.add(new Pixel(0, 0, Color.LIGHT_GRAY));
		it = r.iterator();
		it.next();
		it.next();
		it.remove();
		assertEquals(new Pixel(6, 4, Color.DARK_GRAY), it.next());
		assertEquals(3, r.size());
	}
	
	public void test87() {
		r = new ArrayPixelCollection(8, 7);
		r.add(new Pixel(7, 2));
		r.add(new Pixel(3, 5));
		r.add(new Pixel(2, 4));
		r.add(new Pixel(0, 10));
		r.add(new Pixel(9, 9));
		r.add(new Pixel(7, 5));
		it = r.iterator();
		it.next();
		it.next();
		r.clearAt(3, 5);
		assertException(ConcurrentModificationException.class, () -> it.remove());
	}
	
	public void test88() {
		r = new ArrayPixelCollection(8, 8);
		r.add(new Pixel(1, 0));
		r.add(new Pixel(6, 3));
		r.add(new Pixel(10, 5));
		r.add(new Pixel(5, 2));
		r.add(new Pixel(2, 1));
		it = r.iterator();
		it.next();
		it.next();
		it.next();
		it.next();
		Iterator<Pixel> other = r.iterator();
		assertEquals(new Pixel(1, 0), other.next());
		other.remove();
		assertException(ConcurrentModificationException.class, () -> it.remove());
		assertException(ConcurrentModificationException.class, () -> it.next());
		assertEquals(new Pixel(2, 1), other.next());
	}
	
	public void test89() {
		r = new ArrayPixelCollection(8, 9);
		r.add(new Pixel(5, 2));
		r.add(new Pixel(2, 4));
		r.add(new Pixel(8, 8));
		r.add(new Pixel(1, 3));
		it = r.iterator();
		it.next();
		Iterator<Pixel> other = r.iterator();
		other.next();
		other.next();
		assertEquals(new Pixel(5, 2), other.next());
		other.remove();
		r.add(new Pixel(0, 3));
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
	}
	
	
	/// test9x: tests of contains and remove
	
	public void test90() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(4, 5));
		assertFalse(r.contains(new Pixel(5, 4)));
	}
	
	public void test91() {
		r = new ArrayPixelCollection(9, 1);
		r.add(new Pixel(7, 2));
		assertFalse(r.contains(new Pixel(7, 2, Color.BLUE)));
	}
	
	public void test92() {
		r = new ArrayPixelCollection(9, 2);
		r.add(new Pixel(7, 2));
		assertFalse(r.contains(new Object()));
		assertFalse(r.contains(null));
	}
	
	public void test93() {
		r = new ArrayPixelCollection(9, 3);
		r.add(new Pixel(7, 2));
		r.add(new Pixel(7, 5, Color.BLUE));
		r.add(new Pixel(7, 9));
		assertTrue(r.contains(new Pixel(7, 5, Color.BLUE)));	
	}
	
	public void test94() {
		r = new ArrayPixelCollection(9, 4);
		r.add(new Pixel(3, 2, Color.RED));
		r.add(new Pixel(8, 9, Color.GREEN));
		assertTrue(r.contains(new Pixel(3, 2, Color.RED)));
	}

	public void test95() {
		r = new ArrayPixelCollection();
		r.add(new Pixel(4, 5));
		assertFalse(r.remove(new Pixel(5, 4)));
		assertFalse(r.isEmpty());
	}
	
	public void test96() {
		r = new ArrayPixelCollection(9, 1);
		r.add(new Pixel(7, 2));
		assertFalse(r.remove(new Pixel(7, 2, Color.BLUE)));
		assertFalse(r.isEmpty());
	}
	
	public void test97() {
		r = new ArrayPixelCollection(9, 2);
		r.add(new Pixel(7, 2));
		assertFalse(r.remove(new Object()));
		assertFalse(r.remove(null));
		assertFalse(r.isEmpty());
	}
	
	public void test98() {
		r = new ArrayPixelCollection(9, 3);
		r.add(new Pixel(7, 2));
		r.add(new Pixel(7, 5, Color.BLUE));
		r.add(new Pixel(7, 9));
		assertTrue(r.remove(new Pixel(7, 5, Color.BLUE)));
		assertEquals(2, r.size());
	}
	
	public void test99() {
		r = new ArrayPixelCollection(9, 4);
		r.add(new Pixel(3, 2, Color.RED));
		r.add(new Pixel(8, 9, Color.GREEN));
		assertTrue(r.remove(new Pixel(3, 2, Color.RED)));
		assertEquals(1, r.size());
	}
}
