// Testing sequences of 10 commands.
import junit.framework.TestCase;

import java.awt.Color;
import java.awt.Point;
import edu.uwm.cs351.Pixel;
import edu.uwm.cs351.DynamicRaster;


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
		DynamicRaster ts0 = new DynamicRaster();
		assertEquals(false,ts0.isCurrent());
		ts0.start(); // should terminate normally
		assertEquals(true,ts0.add(new Pixel(0,0,Color.WHITE)));
		assertEquals(1,ts0.size());
		ts0.advance(); // should terminate normally
		assertEquals(true,ts0.add(new Pixel(0,1,Color.WHITE)));
		assertEquals(true,ts0.add(new Pixel(1,2,Color.MAGENTA)));
		assertEquals(true,ts0.clearAt(0,1));
		ts0.removeCurrent(); // should terminate normally
	}
}
