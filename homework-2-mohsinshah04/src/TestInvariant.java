import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.DynamicRaster;
import junit.framework.TestCase;

public class TestInvariant extends TestCase {

	protected DynamicRaster.Spy spy;
	protected int reports;
	protected DynamicRaster r;

	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
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
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			spy.setReporter(null);
		} finally {
			spy.setReporter(savedReporter);
		}
	}
	
	protected void assertWellFormed(boolean expected, DynamicRaster r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}

	@Override // implementation
	protected void setUp() {
		spy = new DynamicRaster.Spy();
	}
	
	
	/**
	 * Create an array of arrays to use in a DynamicRaster
	 * object.  The number of parameters is the number of columns (the length of the first diemsnion).
	 * The columns give the height of each column: if negative,
	 * the column is null, otherwise it is an array of the given length.
	 * For example, if you call <code>a(1,-1,0)</code> the result is
	 * an array of length 3: the first element of it is an array of length 1, 
	 * the second is null and the third is a zero-length array.
	 * @param columns description of columns, must not be null
	 * @return two-D array with the shape described by the columns.
	 * The result is never null.
	 */
	protected Color[][] a(int... columns) {
		Color[][] result = new Color[columns.length][];
		for (int i=0; i < columns.length; ++i) {
			if (columns[i] >= 0) {
				result[i] = new Color[columns[i]];
			}
		}
		return result;
	}
	
	/// testAx: tests of empty rasters
	
	public void testA0() {
		r = spy.create(a(0), 0, 0, 0);
		assertWellFormed(true, r);
	}
	
	public void testA1() {
		r = spy.create(a(0,-1), 0, 0, 0);
		assertWellFormed(true, r);
	}
	
	public void testA2() {
		r = spy.create(a(0, 0), 0, 0, 0);
		assertWellFormed(true, r);
	}
	
	public void testA3() {
		r = spy.create(a(0, 1, -1, 0, 10, -1, 0, 3), 0, 0, 0);
		assertWellFormed(true, r);
	}
	
	public void testA4() {
		r = spy.create(a(), 0, 0, 0);
		assertWellFormed(false, r);
	}
	
	public void testA5() {
		r = spy.create(a(-1), 0, 0, 0);
		assertWellFormed(false, r);
	}
	
	public void testA6() {
		r = spy.create(a(-1, 0), 0, 1, 0);
		assertWellFormed(true, r);
	}
	
	public void testA7() {
		r = spy.create(a(), 0, 0, -1);
		assertWellFormed(false, r);
	}
	
	public void testA8() {
		r = spy.create(a(-1), 0, 0, -1);
		assertWellFormed(false, r);
	}
	
	public void testA9() {
		r = spy.create(a(-1, -1), 0, 1, -1);
		assertWellFormed(false, r);
	}
	
	
	/// testBx: tests of empty rasters with different cursors
	
	public void testB0() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 0, 0);
		assertWellFormed(false, r);
	}
	
	public void testB1() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 0, 1);
		assertWellFormed(true, r);
	}
	
	public void testB2() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 1, 0);
		assertWellFormed(false, r);
	}
	
	public void testB3() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 2, 0);
		assertWellFormed(true, r);
	}
	
	public void testB4() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 2, 1);
		assertWellFormed(false, r);
	}
	
	public void testB5() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 3, 0);
		assertWellFormed(false, r);
	}
	
	public void testB6() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 3, 1);
		assertWellFormed(false, r);
	}
	
	public void testB7() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 3, 10);
		assertWellFormed(true, r);
	}
	
	public void testB8() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 4, 0);
		assertWellFormed(false, r);
	}
	
	public void testB9() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 5, 0);
		assertWellFormed(true, r);
	}
	
	
	/// testCn: tests of current with single pixel
	
	public void testC0() {
		Color[][] a = a(1);
		r = spy.create(a, 1, 0, 0);
		a[0][0] = Color.WHITE;
		assertWellFormed(true, r);
	}
	
	public void testC1() {
		Color[][] a = a(0, 1);
		r = spy.create(a, 1, 0, 0);
		a[1][0] = Color.WHITE;
		assertWellFormed(false, r);
	}
	
	public void testC2() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 0, 1);
		a[0][0] = Color.WHITE;
		assertWellFormed(true, r);
	}
	
	public void testC3() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 1, 0);
		a[0][0] = Color.WHITE;
		assertWellFormed(false, r);
	}
	
	public void testC4() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 2, 0);
		a[0][0] = Color.WHITE;
		assertWellFormed(true, r);
	}
	
	public void testC5() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 3, 1);
		a[0][0] = Color.MAGENTA;
		assertWellFormed(false, r);
	}
	
	public void testC6() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 0, 1);
		a[3][0] = Color.MAGENTA;
		assertWellFormed(false, r);
	}
	
	public void testC7() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 3, 1);
		a[3][1] = Color.MAGENTA;
		assertWellFormed(true, r);
	}
	
	public void testC8() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 3, 9);
		a[3][9] = Color.MAGENTA;
		assertWellFormed(true, r);
	}
	
	public void testC9() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 3, 9);
		a[3][1] = Color.MAGENTA;
		assertWellFormed(false, r);
	}

	
	/// testDx: tests of current with multiple pixels
	
	public void testD0() {
		Color[][] a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 1, 0);
		a[1][0] = Color.BLACK;
		a[3][1] = Color.WHITE;
		assertWellFormed(true, r);
	}
	
	public void testD1() {
		Color[][] a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 3, 1);
		a[1][0] = Color.BLACK;
		a[3][1] = Color.WHITE;
		assertWellFormed(true, r);
	}
	
	public void testD2() {
		Color[][] a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 3, 0);
		a[1][0] = Color.BLACK;
		a[3][1] = Color.WHITE;
		assertWellFormed(false, r);
	}
	
	public void testD3() {
		Color[][] a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 3, 2);
		a[1][0] = Color.BLACK;
		a[3][1] = Color.WHITE;
		assertWellFormed(true, r);
	}
	
	public void testD4() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 3, 9);
		a[0][0] = Color.RED;
		a[3][0] = Color.ORANGE;
		a[3][1] = Color.YELLOW;
		a[3][3] = Color.GREEN;
		a[3][7] = Color.CYAN;
		a[6][1] = Color.MAGENTA;
		assertWellFormed(false, r);
	}
	
	public void testD5() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 0, 0);
		a[0][0] = Color.RED;
		a[3][0] = Color.ORANGE;
		a[3][1] = Color.YELLOW;
		a[3][3] = Color.GREEN;
		a[3][7] = Color.CYAN;
		a[6][1] = Color.MAGENTA;
		assertWellFormed(true, r);
	}
	
	public void testD6() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 3, 1);
		a[0][0] = Color.RED;
		a[3][0] = Color.ORANGE;
		a[3][1] = Color.YELLOW;
		a[3][3] = Color.GREEN;
		a[3][7] = Color.CYAN;
		a[6][1] = Color.MAGENTA;
		assertWellFormed(true, r);
	}
	
	public void testD7() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 3, 2);
		a[0][0] = Color.RED;
		a[3][0] = Color.ORANGE;
		a[3][1] = Color.YELLOW;
		a[3][3] = Color.GREEN;
		a[3][7] = Color.CYAN;
		a[6][1] = Color.MAGENTA;
		assertWellFormed(false, r);
	}
	
	public void testD8() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 3, 7);
		a[0][0] = Color.RED;
		a[3][0] = Color.ORANGE;
		a[3][1] = Color.YELLOW;
		a[3][3] = Color.GREEN;
		a[3][7] = Color.CYAN;
		a[6][1] = Color.MAGENTA;
		assertWellFormed(true, r);
	}
	
	public void testD9() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 6, 1);
		a[0][0] = Color.RED;
		a[3][0] = Color.ORANGE;
		a[3][1] = Color.YELLOW;
		a[3][3] = Color.GREEN;
		a[3][7] = Color.CYAN;
		a[6][1] = Color.MAGENTA;
		assertWellFormed(true, r);
	}
	
	
	/// testEx: tests of size
	
	public void testE0() {
		Color[][] a = a(0, -1, 0, -1, 2);
		r = spy.create(a, 1, 0, 0);
		assertWellFormed(false, r);
	}
	
	public void testE1() {
		Color[][] a = a(0, -1, 0, -1, 2);
		r = spy.create(a, 1, 0, 0);
		a[4][1] = Color.BLACK;
		assertWellFormed(false, r);
	}
	
	public void testE2() {
		Color[][] a = a(0, -1, 0, -1, 2);
		r = spy.create(a, 1, 0, 0);
		a[4][0] = Color.BLACK;
		a[4][1] = Color.BLACK;
		assertWellFormed(false, r);
	}
	
	public void testE3() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 0, 0);
		a[0][0] = Color.BLUE;
		assertWellFormed(false, r);
	}
	
	public void testE4() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 0, 0);
		a[0][0] = Color.BLUE;
		a[3][2] = Color.GREEN;
		assertWellFormed(true, r);
	}
	
	public void testE5() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 0, 0);
		a[0][0] = Color.BLUE;
		a[3][2] = Color.GREEN;
		a[6][1] = Color.YELLOW;
		assertWellFormed(false, r);
	}
	
	public void testE6() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 3, 0, 0);
		a[0][0] = Color.BLUE;
		a[3][2] = Color.GREEN;
		a[6][1] = Color.YELLOW;
		assertWellFormed(true, r);
	}
	
	public void testE7() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 4, 0, 0);
		a[0][0] = Color.BLUE;
		a[3][2] = Color.GREEN;
		a[6][1] = Color.YELLOW;
		assertWellFormed(false, r);
	}
	
	public void testE8() {
		Color[][] a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 5, 0, 0);
		a[0][0] = Color.RED;
		a[3][6] = Color.MAGENTA;
		a[6][0] = Color.PINK;
		a[6][2] = Color.ORANGE;
		assertWellFormed(false, r);
		a[3][7] = Color.yellow;
		assertWellFormed(true, r);
	}
	
	public void testE9() {
		Color[][] a = a(1, 2, 3, -1, -1, -1, 3, 2, 1, 0);
		r = spy.create(a, 6, 2, 0);
		a[1][0] = Color.BLACK;
		a[2][0] = Color.DARK_GRAY;
		a[2][2] = Color.LIGHT_GRAY;
		a[7][1] = Color.GRAY;
		a[6][2] = Color.WHITE;
		a[8][0] = Color.CYAN;
		assertWellFormed(true, r);
		a[7][1] = null;
		assertWellFormed(false, r);
	}
}
