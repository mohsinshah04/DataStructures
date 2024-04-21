import java.awt.Color;
import java.util.function.Supplier;

import edu.uwm.cs351.Pixel;
import edu.uwm.cs351.RasterMap;
import junit.framework.TestCase;

public class TestDirect extends TestCase {
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
	
	
	RasterMap r;
	

	/// test0x: constructors

	public void test01() {
		r = new RasterMap();
		assertEquals(0, r.size());
	}
	


	/// test1x: add tests

	public void test10() {
		r = new RasterMap();
		assertTrue(r.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test11() {
		r = new RasterMap();
		assertTrue(r.add(new Pixel(0,0,Color.BLACK)));
	}

	public void test12() {
		r = new RasterMap();
		r.add(new Pixel(0,0,Color.WHITE));
		assertFalse(r.add(new Pixel(0,0,Color.WHITE)));
	}
	
	public void test13() {
		RasterMap r1 = new RasterMap();
		RasterMap r2 = new RasterMap();
		r2.add(new Pixel(0,0,Color.WHITE));
		assertTrue(r1.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test14() {
		r = new RasterMap();
		assertTrue(r.add(new Pixel(0,0,Color.RED)));
		assertTrue(r.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test15() {
		r = new RasterMap();
		r.add(new Pixel(3,3,Color.RED));
		assertFalse(r.add(new Pixel(3,3,Color.RED)));
	}
	
	public void test16() {
		r = new RasterMap();
		r.add(new Pixel(1,4));
		assertTrue(r.add(new Pixel(4,1)));
	}
	
	public void test17() {
		r = new RasterMap();
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
		r = new RasterMap();
		assertException(NullPointerException.class, () -> r.add(null));
	}

	public void test19() {
		r = new RasterMap();
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
		assertNull(new RasterMap().getPixel(0,0));
	}

	public void test21() {
		r = new RasterMap();
		r.add(new Pixel(0,0,Color.BLACK));
		assertEquals(new Pixel(0,0,Color.BLACK), r.getPixel(0,0));
	}

	public void test22() {
		r = new RasterMap();
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
		r = new RasterMap();
		assertNull(r.getPixel(0,0));
	}
	
	public void test24() {
		r = new RasterMap();
		assertNull(r.getPixel(10,0));
	}
	
	public void test25() {
		r = new RasterMap();
		assertNull(r.getPixel(1, 5));
	}
	
	public void test26() {
		r = new RasterMap();
		assertNull(r.getPixel(3, 10));
	}
	
	public void test27() {
		r = new RasterMap();
		assertException(IllegalArgumentException.class, () -> r.getPixel(-1,0));
		assertException(IllegalArgumentException.class, () -> r.getPixel(0,-1));
	}

	public void test28() {
		r = new RasterMap();
		assertException(IllegalArgumentException.class, () -> r.getPixel(-3,8));
		assertException(IllegalArgumentException.class, () -> r.getPixel(3,-8));
	}
	
	public void test29() {
		r = new RasterMap();
		assertException(IllegalArgumentException.class, () -> r.getPixel(-3,-9));
	}
	
	
	// test3x: tests of clearAt
	
	public void test30() {
		r = new RasterMap();
		assertFalse(r.clearAt(3, 0));
	}
	
	public void test31() {
		r = new RasterMap();
		assertFalse(r.clearAt(3, 1));
	}
	
	public void test32() {
		r = new RasterMap();
		r.add(new Pixel(1,1));
		assertTrue(r.clearAt(1, 1));
	}
	
	public void test33() {
		r = new RasterMap();
		r.add(new Pixel(1,1));
		assertFalse(r.clearAt(1, 0));
	}
	
	public void test34() {
		r = new RasterMap();
		r.add(new Pixel(2,2));
		assertTrue(r.clearAt(2, 2));
		assertFalse(r.clearAt(2, 2));
	}
	
	public void test35() {
		r = new RasterMap();
		r.add(new Pixel(3, 2));
		assertFalse(r.clearAt(2, 2));
		assertTrue(r.clearAt(3, 2));
	}
	
	public void test36() {
		r = new RasterMap();
		r.add(new Pixel(3, 3));
		r.add(new Pixel(2, 2));
		assertFalse(r.clearAt(2, 3));
		assertFalse(r.clearAt(3, 2));
		r.add(new Pixel(1, 1));
		assertTrue(r.clearAt(2, 2));
	}
	
	public void test37() {
		RasterMap r1 = new RasterMap();
		RasterMap r2 = new RasterMap();
		r1.add(new Pixel(2, 2));
		r2.add(new Pixel(2, 2));
		assertTrue(r1.clearAt(2, 2));
		assertTrue(r2.clearAt(2, 2));
		assertFalse(r1.clearAt(2, 2));
	}
	
	public void test38() {
		r = new RasterMap();
		r.add(new Pixel(1, 2));
		r.add(new Pixel(2, 2));
		r.add(new Pixel(2, 3));
		r.add(new Pixel(1, 4));
		assertFalse(r.clearAt(1, 3));
		assertTrue(r.clearAt(1, 2));
		assertFalse(r.clearAt(2, 4));
	}
	
	public void test39() {
		r = new RasterMap();
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
		r = new RasterMap();
		assertEquals(0, r.size());
	}
	
	public void test41() {
		r = new RasterMap();
		r.add(new Pixel(3,0));
		assertEquals(1, r.size());
	}
	
	public void test42() {
		r = new RasterMap();
		r.add(new Pixel(3,0));
		r.add(new Pixel(4,1));
		assertEquals(2, r.size());
	}
	
	public void test43() {
		r = new RasterMap();
		r.add(new Pixel(2,2));
		r.add(new Pixel(4,0));
		r.add(new Pixel(4,3));
		assertEquals(3, r.size());
	}
	
	public void test44() {
		r = new RasterMap();
		r.add(new Pixel(0,0));
		r.add(new Pixel(1,1));
		r.add(new Pixel(0,3));
		r.add(new Pixel(1,2));
		r.add(new Pixel(1,0));
		assertEquals(5, r.size());
	}
	
	public void test45() {
		r = new RasterMap();
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
		r = new RasterMap();
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
		RasterMap r1 = new RasterMap();
		RasterMap r2 = new RasterMap();
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
		r = new RasterMap();
		r.add(new Pixel(1, 2, Color.RED));
		r.add(new Pixel(2, 3, Color.BLACK));
		r.add(new Pixel(3, 4, Color.PINK));
		r.add(new Pixel(4, 5, Color.DARK_GRAY));
		r.add(new Pixel(0, 6, new Color(100,100,35)));
		assertEquals(5, r.size());
	}
	
	public void test49() {
		r = new RasterMap();
		r.add(new Pixel(50, 0));
		r.add(new Pixel(25, 0));
		r.add(new Pixel(75, 0));
		r.add(new Pixel(12, 0));
		r.add(new Pixel(37, 0));
		r.add(new Pixel(62, 0));
		r.add(new Pixel(87, 0));
		r.add(new Pixel(6, 0));
		r.add(new Pixel(18, 0));
		r.add(new Pixel(30, 0));
		r.add(new Pixel(42, 0));
		r.add(new Pixel(55, 0));
		r.add(new Pixel(67, 0));
		r.add(new Pixel(80, 0));
		r.add(new Pixel(92, 0));
		for (int i=0; i < 100; ++i) {
			for (int j=0; j < 100; j += 10) {
				r.add(new Pixel(i, j, Color.BLUE));
			}
		}
		assertEquals(1000, r.size());
	}

	
	/// test5x: tests of clone
	
	public void test50() {
		RasterMap r = new RasterMap();
		RasterMap c = r.clone();
		assertEquals(0, c.size());
	}
	
	public void test51() {
		RasterMap r = new RasterMap();
		r.add(new Pixel(9,1,Color.BLUE));
		RasterMap c = r.clone();
		assertEquals(1, c.size());
		assertEquals(new Pixel(9,1, Color.BLUE), c.getPixel(9, 1));
	}
	
	public void test52() {
		RasterMap r = new RasterMap();
		r.add(new Pixel(9,2,Color.BLACK));
		r.add(new Pixel(2,9,Color.RED));
		RasterMap c = r.clone();
		assertEquals(2, c.size());
		assertEquals(new Pixel(9, 2, Color.BLACK), c.getPixel(9, 2));
		assertEquals(new Pixel(2, 9, Color.RED), c.getPixel(2, 9));
	}
	
	public void test53() {
		RasterMap r = new RasterMap();
		r.add(new Pixel(9,3,Color.YELLOW));
		r.add(new Pixel(3,9,Color.RED));
		r.add(new Pixel(6,6));
		RasterMap c = r.clone();
		assertEquals(3, r.size());
		assertEquals(new Pixel(9, 3, Color.YELLOW), r.getPixel(9, 3));
		assertEquals(new Pixel(3, 9, Color.RED), r.getPixel(3, 9));
		assertEquals(new Pixel(6, 6), c.getPixel(6, 6));
	}
	
	public void test54() {
		RasterMap r = new RasterMap();
		r.add(new Pixel(9,4));
		RasterMap c = r.clone();
		c.add(new Pixel(4, 9, Color.GREEN));
		assertEquals(1, r.size());
		assertNull(r.getPixel(4,9));
		assertEquals(new Pixel(4, 9, Color.GREEN), c.getPixel(4, 9));
	}
	
	public void test55() {
		RasterMap r = new RasterMap();
		r.add(new Pixel(9, 5));
		r.add(new Pixel(5, 9, Color.BLACK));
		RasterMap c = r.clone();
		r.add(new Pixel(6, 7, Color.GREEN));
		assertEquals(2, c.size());
		assertNull(c.getPixel(6, 7));
		assertEquals(new Pixel(6, 7, Color.GREEN), r.getPixel(6, 7));
	}
		
	public void test59() {
		class TreePixelCollection extends edu.uwm.cs351.RasterMap {
			int x;
		}
		TreePixelCollection r = new TreePixelCollection();
		r.add(new Pixel(9, 9, Color.BLUE));
		r.x = 59;
		Object c = r.clone();
		assertTrue("clone() didn't use super.clone() and instead created a new DR", c instanceof TreePixelCollection);
		TreePixelCollection cc = (TreePixelCollection)c;
		assertEquals(59, cc.x);
	}
	
	
}
