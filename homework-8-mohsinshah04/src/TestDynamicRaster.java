import java.awt.Color;
import java.util.function.Supplier;

import edu.uwm.cs351.DynamicRaster;
import edu.uwm.cs351.Pixel;
import junit.framework.TestCase;

public class TestDynamicRaster extends TestCase{
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
	
	



	/// test0x: constructors

	public void test00() {
		DynamicRaster r = new DynamicRaster();
		assertEquals(0, r.size());
	}
	


	/// test1x: add tests

	public void test10() {
		DynamicRaster r = new DynamicRaster();
		assertTrue(r.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test11() {
		DynamicRaster r = new DynamicRaster();
		assertTrue(r.add(new Pixel(0,0,Color.BLACK)));
	}

	public void test12() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(0,0,Color.WHITE));
		assertFalse(r.add(new Pixel(0,0,Color.WHITE)));
	}
	
	public void test13() {
		DynamicRaster r1 = new DynamicRaster();
		DynamicRaster r2 = new DynamicRaster();
		r2.add(new Pixel(0,0,Color.WHITE));
		assertTrue(r1.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test14() {
		DynamicRaster r = new DynamicRaster();
		assertTrue(r.add(new Pixel(0,0,Color.RED)));
		assertTrue(r.add(new Pixel(0,0,Color.WHITE)));
	}

	public void test15() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(3,3,Color.RED));
		assertFalse(r.add(new Pixel(3,3,Color.RED)));
	}
	
	public void test16() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(1,4));
		assertTrue(r.add(new Pixel(4,1)));
	}
	
	public void test17() {
		DynamicRaster r = new DynamicRaster();
		for (int i=1; i < 9; ++i) {
			for (int j=0; j < 8; ++j) {
				assertTrue("Problem for " + i + "," + j, r.add(new Pixel(i,j)));
			}
		}
		assertFalse(r.add(new Pixel(5,5)));
		assertTrue(r.add(new Pixel(4,8)));
		assertTrue(r.add(new Pixel(4,4, Color.RED)));
	}
	
	
	/// test2x: Error (or not error!) tests of add
	
	public void test20() {
		DynamicRaster r = new DynamicRaster();
		assertTrue(r.add(new Pixel(2,0)));
	}

	public void test21() {
		DynamicRaster r = new DynamicRaster();
		assertException(NullPointerException.class, () -> r.add(null));
	}

	public void test27() {
		DynamicRaster r2 = new DynamicRaster();
		assertTrue(r2.add(new Pixel(0,0)));
		DynamicRaster r3 = new DynamicRaster();
		assertTrue(r3.add(new Pixel(0,0)));
	}

	public void test28() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(1,0));
		r.add(new Pixel(0,1));
		r.add(new Pixel(1,1));
		assertException(IllegalArgumentException.class, () -> r.add(new Pixel(-1,-1)));
	}

	public void test29() {
		DynamicRaster r = new DynamicRaster();
		assertException(IllegalArgumentException.class, () -> r.add(new Pixel(-1,0)));
		assertException(IllegalArgumentException.class, () -> r.add(new Pixel(0,-1)));
		assertTrue(r.add(new Pixel(10,1)));
		assertTrue(r.add(new Pixel(4,10)));
	}

	//test3x: getPixel tests

	public void test30() {
		assertNull(new DynamicRaster().getPixel(0,0));
	}

	public void test31() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(0,0,Color.BLACK));
		assertEquals(new Pixel(0,0,Color.BLACK), r.getPixel(0,0));
	}

	public void test32() {
		DynamicRaster r = new DynamicRaster();
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

	public void test33() {
		DynamicRaster r = new DynamicRaster();
		assertNull(r.getPixel(0,0));
	}
	
	public void test34() {
		DynamicRaster r = new DynamicRaster();
		assertNull(r.getPixel(10,0));
	}
	
	public void test35() {
		DynamicRaster r = new DynamicRaster();
		assertNull(r.getPixel(2, 5));
	}
	
	public void test36() {
		DynamicRaster r = new DynamicRaster();
		assertNull(r.getPixel(3, 10));
	}
	
	public void test37() {
		DynamicRaster r = new DynamicRaster();
		assertException(IllegalArgumentException.class, () -> r.getPixel(-1,0));
		assertException(IllegalArgumentException.class, () -> r.getPixel(0,-1));
	}

	public void test38() {
		DynamicRaster r = new DynamicRaster();
		assertException(IllegalArgumentException.class, () -> r.getPixel(-3,8));
		assertException(IllegalArgumentException.class, () -> r.getPixel(3,-8));
	}
	
	public void test39() {
		DynamicRaster r = new DynamicRaster();
		assertException(IllegalArgumentException.class, () -> r.getPixel(-3,-9));
	}
	
	
	// test4x: tests of clearAt (omitted)
	
	/// test5x: tests of size
	
	public void test50() {
		DynamicRaster r = new DynamicRaster();
		assertEquals(0, r.size());
	}
	
	public void test51() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(3,0));
		assertEquals(1, r.size());
	}
	
	public void test52() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(3,0));
		r.add(new Pixel(4,1));
		assertEquals(2, r.size());
	}
	
	public void test53() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(2,2));
		r.add(new Pixel(4,0));
		r.add(new Pixel(4,3));
		assertEquals(3, r.size());
	}
	
	public void test54() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(0,0));
		r.add(new Pixel(1,1));
		r.add(new Pixel(0,3));
		r.add(new Pixel(1,2));
		r.add(new Pixel(1,0));
		assertEquals(5, r.size());
	}
	
	public void test55() {
		DynamicRaster r = new DynamicRaster();
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
	
	public void test56() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(0, 0));
		r.add(new Pixel(3, 3));
		r.add(new Pixel(4, 2));
		r.add(new Pixel(2, 4));
		assertEquals(4, r.size());
		r.add(new Pixel(4, 4));
		r.add(new Pixel(2, 2));
		assertEquals(6, r.size());
	}
	
	public void test57() {
		DynamicRaster r1 = new DynamicRaster();
		DynamicRaster r2 = new DynamicRaster();
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
	
	public void test58() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(1, 2, Color.RED));
		r.add(new Pixel(2, 3, Color.BLACK));
		r.add(new Pixel(3, 4, Color.PINK));
		r.add(new Pixel(4, 5, Color.DARK_GRAY));
		r.add(new Pixel(0, 6, new Color(100,100,35)));
		assertEquals(5, r.size());
	}
	
	public void test59() {
		DynamicRaster r = new DynamicRaster();
		for (int i=0; i < 100; ++i) {
			for (int j=0; j < 100; j += 2) {
				r.add(new Pixel(i, j, Color.BLUE));
			}
		}
		assertEquals(5000, r.size());
	}

	
	
	/// test7x: tests of cursor semantics (without removal)
	
	public void test70() {
		DynamicRaster r = new DynamicRaster();
		assertFalse(r.isCurrent());
		r.start();
		assertFalse(r.isCurrent());
	}
	
	public void test71() {
		DynamicRaster r = new DynamicRaster();
		assertException(IllegalStateException.class, () -> r.getCurrent());
		assertException(IllegalStateException.class, () -> r.advance());		
	}
	
	public void test72() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(3,0));
		assertEquals(new Pixel(3,0), r.getCurrent());
	}
	
	public void test73() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(3,0));
		r.start();
		assertEquals(new Pixel(3,0), r.getCurrent());
		assertEquals(new Pixel(3,0), r.getCurrent());
	}
	
	public void test74() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(3,0));
		r.start();
		r.advance();
		assertFalse(r.isCurrent());
	}
	
	public void test75() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(5, 0, Color.BLUE));
		r.add(new Pixel(2, 1, Color.BLACK));
		r.start();
		assertEquals(new Pixel(2, 1, Color.BLACK), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(5, 0, Color.BLUE), r.getCurrent());
		r.advance();
		assertFalse(r.isCurrent());
	}
	
	public void test76() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(8, 3, Color.YELLOW));
		r.advance();
		r.add(new Pixel(7, 6, Color.RED));
		assertEquals(new Pixel(7, 6, Color.RED), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(8, 3, Color.YELLOW), r.getCurrent());
	}
	
	public void test77() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(7, 7, Color.PINK));
		r.advance();
		r.add(new Pixel(8, 4, Color.GRAY));
		assertEquals(new Pixel(8, 4, Color.GRAY), r.getCurrent());
		r.advance();
		assertFalse(r.isCurrent());
		r.start();
		assertEquals(new Pixel(7, 7, Color.PINK), r.getCurrent());
	}
	
	public void test78() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(7, 8, Color.GREEN));
		r.advance();
		assertFalse(r.add(new Pixel(7, 8, Color.GREEN)));
		assertEquals(new Pixel(7, 8, Color.GREEN), r.getCurrent());
	}
	
	public void test79() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(7, 9, Color.WHITE));
		r.add(new Pixel(7, 147, Color.BLACK));
		assertEquals(new Pixel(7, 147, Color.BLACK), r.getCurrent());
		r.start();
		assertEquals(new Pixel(7, 9), r.getCurrent());
		r.advance();
		assertTrue(r.isCurrent());
	}
	
	/// test8x: longer tests of cursor
	
	public void test80() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(2, 2, Color.CYAN));
		r.add(new Pixel(6, 1, Color.YELLOW));
		r.add(new Pixel(2, 0, Color.GREEN));
		assertEquals(new Pixel(2, 0, Color.GREEN), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(2, 2, Color.CYAN), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(6, 1, Color.YELLOW), r.getCurrent());
	}
	
	public void test81() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(5, 2));
		r.add(new Pixel(5, 3));
		r.add(new Pixel(5, 0));
		r.add(new Pixel(1, 3));
		r.add(new Pixel(0, 2));
		assertEquals(new Pixel(0, 2), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(1, 3), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(5, 0), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(5, 2), r.getCurrent());
		r.advance();
		assertEquals(new Pixel(5, 3), r.getCurrent());
	}
	
	public void test82() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(5, 2));
		r.add(new Pixel(0, 2));
		r.add(new Pixel(5, 3));
		r.add(new Pixel(5, 0));
		r.add(new Pixel(1, 3));
		assertEquals(new Pixel(1, 3), r.getCurrent());
		r.start();
		assertEquals(new Pixel(0, 2), r.getCurrent());
		r.advance();
		r.advance();
		r.advance();
		assertEquals(new Pixel(5, 2), r.getCurrent());
	}
	
	public void test83() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(6, 0));
		r.add(new Pixel(5, 1));
		r.add(new Pixel(4, 2));
		r.add(new Pixel(3, 3));
		r.add(new Pixel(2, 4));
		r.add(new Pixel(1, 0));
		r.add(new Pixel(4, 2));
		assertEquals(new Pixel(4, 2), r.getCurrent());
		r.start();
		assertEquals(new Pixel(1, 0), r.getCurrent());
		r.add(new Pixel(2, 1));
		r.add(new Pixel(3, 2));
		r.add(new Pixel(4, 3));
		r.add(new Pixel(5, 4));
		r.add(new Pixel(6, 3));
		assertEquals(11, r.size());
		r.add(new Pixel(0, 3));
		
		assertEquals(new Pixel(0, 3), r.getCurrent());
		r.advance(); r.advance();
		r.advance(); r.advance();
		r.advance(); r.advance();
		r.advance(); r.advance();
		r.advance(); r.advance();
		r.advance();
		assertEquals(new Pixel(6, 3), r.getCurrent());
	}
	
	
	/// test9x: tests of clone
	
	public void test90() {
		DynamicRaster r = new DynamicRaster();
		DynamicRaster c = r.clone();
		assertEquals(0, c.size());
	}
	
	public void test91() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9,1,Color.BLUE));
		DynamicRaster c = r.clone();
		assertEquals(1, c.size());
		assertEquals(new Pixel(9,1, Color.BLUE), c.getPixel(9, 1));
	}
	
	public void test92() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9,2,Color.BLACK));
		r.add(new Pixel(2,9,Color.RED));
		DynamicRaster c = r.clone();
		assertEquals(2, c.size());
		assertEquals(new Pixel(9, 2, Color.BLACK), c.getPixel(9, 2));
		assertEquals(new Pixel(2, 9, Color.RED), c.getPixel(2, 9));
	}
	
	public void test93() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9,3,Color.YELLOW));
		r.add(new Pixel(3,9,Color.RED));
		r.add(new Pixel(6,6));
		DynamicRaster c = r.clone();
		assertEquals(3, r.size());
		assertEquals(new Pixel(9, 3, Color.YELLOW), r.getPixel(9, 3));
		assertEquals(new Pixel(3, 9, Color.RED), r.getPixel(3, 9));
		assertEquals(new Pixel(6, 6), c.getPixel(6, 6));
	}
	
	public void test94() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9,4));
		DynamicRaster c = r.clone();
		c.add(new Pixel(4, 9, Color.GREEN));
		assertEquals(1, r.size());
		assertNull(r.getPixel(4,9));
		assertEquals(new Pixel(4, 9, Color.GREEN), c.getPixel(4, 9));
	}
	
	public void test95() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9, 5));
		r.add(new Pixel(5, 9, Color.BLACK));
		DynamicRaster c = r.clone();
		r.add(new Pixel(6, 7, Color.GREEN));
		assertEquals(2, c.size());
		assertNull(c.getPixel(6, 7));
		assertEquals(new Pixel(6, 7, Color.GREEN), r.getPixel(6, 7));
	}
	
	public void test96() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9, 6));
		r.add(new Pixel(6, 9, Color.GRAY));
		r.add(new Pixel(7, 8, Color.CYAN));
		DynamicRaster c = r.clone();
		assertEquals(new Pixel(7, 8, Color.CYAN), c.getCurrent());
	}
	
	public void test97() {
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9, 7, Color.RED));
		r.add(new Pixel(7, 9, Color.PINK));
		r.advance();
		r.advance();
		DynamicRaster c = r.clone();
		assertFalse(c.isCurrent());
	}
	
	public void test99() {
		class DynamicRaster extends edu.uwm.cs351.DynamicRaster {
			int x;
		}
		DynamicRaster r = new DynamicRaster();
		r.add(new Pixel(9, 9, Color.BLUE));
		r.x = 99;
		Object c = r.clone();
		assertTrue("clone() didn't use super.clone() and instead created a new DR", c instanceof DynamicRaster);
		DynamicRaster cc = (DynamicRaster)c;
		assertEquals(99, cc.x);
	}
}
