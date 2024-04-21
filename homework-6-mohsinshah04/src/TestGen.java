// Testing sequences of 10 commands.
import java.awt.Color;

import edu.uwm.cs351.Pixel;
import edu.uwm.cs351.SortedSequence;
import edu.uwm.cs351.test.PixelComparator;
import junit.framework.TestCase;


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
		SortedSequence<Pixel> r0 = new SortedSequence<Pixel>(new PixelComparator());
		r0.start(); // should terminate normally
		assertEquals(true,r0.add(new Pixel(2,1,Color.BLACK)));
		r0.advance(); // should terminate normally
		assertEquals(true,r0.add(new Pixel(0,0,Color.BLACK)));
		r0.removeCurrent(); // should terminate normally
	}
}
