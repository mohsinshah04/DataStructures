import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.ArrayPixelCollection;
import edu.uwm.cs351.Pixel;
import junit.framework.TestCase;

public class TestInvariant extends TestCase {

	protected ArrayPixelCollection.Spy spy;
	protected int reports;
	protected ArrayPixelCollection r;
	protected Iterator<Pixel> it;

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
	
	protected void assertWellFormed(boolean expected, ArrayPixelCollection r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}

	protected void assertWellFormed(boolean expected, Iterator<Pixel> it) {
		assertReporting(expected, () -> spy.wellFormed(it));
	}

	@Override // implementation
	protected void setUp() {
		spy = new ArrayPixelCollection.Spy();
	}
	
	
	/**
	 * Create a list of lists to use in an ArrayPixelCollection
	 * object.  The columns give the height of each column: if negative,
	 * the column is null, otherwise it is an array list of the given length..
	 * @param columns description of columns, must not be null
	 * @return two-D list with the shape described by the columns.
	 * The result is never null.
	 */
	protected List<List<Color>> a(int... columns) {
		List<List<Color>> result = new ArrayList<>();
		for (int i=0; i < columns.length; ++i) {
			List<Color> column = null;
			if (columns[i] >= 0) {
				column = new ArrayList<>();
				for (int j=0; j < columns[i]; ++j) {
					column.add(null);
				}
			}
			result.add(column);
		}
		return result;
	}
	
	/// testAx: tests of empty rasters
	
	public void testA0() {
		r = spy.create(a(0), 0, 0);
		assertWellFormed(true, r);
	}
	
	public void testA1() {
		r = spy.create(a(0,-1), 0, 1);
		assertWellFormed(true, r);
	}
	
	public void testA2() {
		r = spy.create(a(0,0), 0, 2);
		assertWellFormed(true, r);
	}
	
	public void testA3() {
		r = spy.create(a(0, 1, -1, 0, 10, -1, 0, 3), 0, 3);
		assertWellFormed(true, r);
	}
	
	public void testA4() {
		r = spy.create(a(), 0, 4);
		assertWellFormed(true, r);
	}
	
	public void testA5() {
		r = spy.create(a(-1), 0, 5);
		assertWellFormed(true, r);
	}
	
	public void testA6() {
		r = spy.create(a(-1, 0), 0, 6);
		assertWellFormed(true, r);
	}
	
	public void testA7() {
		r = spy.create(a(0, 0, 0), 0, 7);
		assertWellFormed(true, r);
	}
	
	public void testA8() {
		r = spy.create(a(-1,-1,-1,0,-1), 0, 8);
		assertWellFormed(true, r);
	}
	
	public void testA9() {
		r = spy.create(a(-1, -1, -1, 100, -1, 0, -1, 1000000, 0, -1), 0, 9);
		assertWellFormed(true, r);
	}
	
	
	/// testBx: tests of size
	
	public void testB0() {
		List<List<Color>> a = a(0, -1, 0, -1, 2);
		r = spy.create(a, 1, 0);
		assertWellFormed(false, r);
	}
	
	public void testB1() {
		List<List<Color>> a = a(0, -1, 0, -1, 2);
		r = spy.create(a, 1, 1);
		a.get(4).set(1, Color.BLACK);
		assertWellFormed(true, r);
	}
	
	public void testB2() {
		List<List<Color>> a = a(0, -1, 0, -1, 2);
		r = spy.create(a, 1, 2);
		a.get(4).set(0, Color.BLACK);
		a.get(4).set(1, Color.BLACK);
		assertWellFormed(false, r);
	}
	
	public void testB3() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 3);
		a.get(0).set(0, Color.BLUE);
		assertWellFormed(false, r);
	}
	
	public void testB4() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 4);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		assertWellFormed(true, r);
	}
	
	public void testB5() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 5);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		a.get(6).set(1, Color.YELLOW);
		assertWellFormed(false, r);
	}
	
	public void testB6() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 3, 6);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		a.get(6).set(1, Color.YELLOW);
		assertWellFormed(true, r);
	}
	
	public void testB7() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 4, 7);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		a.get(6).set(1, Color.YELLOW);
		assertWellFormed(false, r);
	}
	
	public void testB8() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 5, 8);
		a.get(0).set(0, Color.RED);
		a.get(3).set(6, Color.MAGENTA);
		a.get(6).set(0, Color.PINK);
		a.get(6).set(2, Color.ORANGE);
		assertWellFormed(false, r);
		a.get(3).set(7, Color.yellow);
		assertWellFormed(true, r);
	}
	
	public void testB9() {
		List<List<Color>> a = a(1, 2, 3, -1, -1, -1, 3, 2, 1, 0);
		r = spy.create(a, 6, 9);
		a.get(1).set(0, Color.BLACK);
		a.get(2).set(0, Color.DARK_GRAY);
		a.get(2).set(2, Color.LIGHT_GRAY);
		a.get(7).set(1, Color.GRAY);
		a.get(6).set(2, Color.WHITE);
		a.get(8).set(0, Color.CYAN);
		assertWellFormed(true, r);
		a.get(7).set(1, null);
		assertWellFormed(false, r);
	}

	/// testCx: tests of empty rasters with different iterators
	
	public void testC0() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 0);
		it = spy.newIterator(r, 0, 0, 0, 0);
		assertWellFormed(true, it);
	}
	
	public void testC1() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 1);
		it = spy.newIterator(r, 0, 1, 0, 1);
		assertWellFormed(false, it);
	}
	
	public void testC2() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 2);
		it = spy.newIterator(r, 1, 0, 0, 2);
		assertWellFormed(false, it);
	}
	
	public void testC3() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 3);
		it = spy.newIterator(r, 2, 0, 0, 3);
		assertWellFormed(false, it);
	}
	
	public void testC4() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 4);
		it = spy.newIterator(r, 2, -1, 0, 4);
		assertWellFormed(false, it);
	}
	
	public void testC5() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 5);
		it = spy.newIterator(r, 3, -1, 0, 5);
		assertWellFormed(false, it);
	}
	
	public void testC6() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 6);
		it = spy.newIterator(r, 3, 0, 0, 6);
		assertWellFormed(true, it);
	}
	
	public void testC7() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 7);
		it = spy.newIterator(r, 3, 9, 0, 7);
		assertWellFormed(true, it);
	}
	
	public void testC8() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 8);
		it = spy.newIterator(r, 3, 10, 0, 8);
		assertWellFormed(false, it);
	}
	
	public void testC9() {
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 9);
		it = spy.newIterator(r, 0, -1, 0, 9);
		assertWellFormed(true, it);
	}
	
	
	/// testDn: tests of iterators with single pixel
	
	public void testD0() {
		List<List<Color>> a = a(1);
		r = spy.create(a, 1, 0);
		a.get(0).set(0, Color.WHITE);
		it = spy.newIterator(r, 0, 0, 0, 0);
		assertWellFormed(true, it);
	}
	
	public void testD1() {
		List<List<Color>> a = a(0, 1);
		r = spy.create(a, 1, 1);
		it = spy.newIterator(r, 0, -1, 1, 1);
		a.get(1).set(0, Color.WHITE);
		assertWellFormed(true, it);
	}
	
	public void testD2() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 2);
		it = spy.newIterator(r, 0, 0, 0, 2);
		a.get(0).set(0, Color.WHITE);
		assertWellFormed(true, it);
	}
	
	public void testD3() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 3);
		it = spy.newIterator(r, 0, 0, 0, 3);
		a.get(3).set(0, Color.WHITE);
		assertWellFormed(false, it);
	}
	
	public void testD4() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 4);
		it = spy.newIterator(r, 0, 0, 1, 4);
		a.get(3).set(0, Color.WHITE);
		assertWellFormed(true, it);
	}
	
	public void testD5() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 5);
		it = spy.newIterator(r, 3, 1, 1, 5);
		a.get(0).set(0, Color.MAGENTA);
		assertWellFormed(false, it);
	}
	
	public void testD6() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 6);
		it = spy.newIterator(r, 3, 0, 1, 6);
		a.get(3).set(0, Color.MAGENTA);
		assertWellFormed(false, it);
	}
	
	public void testD7() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 7);
		it = spy.newIterator(r, 3, 0, 1, 7);
		a.get(3).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testD8() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 8);
		it = spy.newIterator(r, 3, 9, 0, 8);
		a.get(3).set(9, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testD9() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 9);
		it = spy.newIterator(r, 3, 9, 0, 9);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(false, it);
	}

	
	/// testEx: tests of iterators with multiple pixels
	
	public void testE0() {
		List<List<Color>> a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 0);
		it = spy.newIterator(r, 1, 0, 1, 0);
		a.get(1).set(0, Color.BLACK);
		a.get(3).set(1, Color.WHITE);
		assertWellFormed(true, it);
	}
	
	public void testE1() {
		List<List<Color>> a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 1);
		it = spy.newIterator(r, 3, 1, 0, 1);
		a.get(1).set(0, Color.BLACK);
		a.get(3).set(1, Color.WHITE);
		assertWellFormed(true, it);
	}
	
	public void testE2() {
		List<List<Color>> a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 2);
		it = spy.newIterator(r, 3, 0, 0, 2);
		a.get(1).set(0, Color.BLACK);
		a.get(3).set(1, Color.WHITE);
		assertWellFormed(false, it);
	}
	
	public void testE3() {
		List<List<Color>> a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 3);
		it = spy.newIterator(r, 3, 1, 0, 3);
		a.get(1).set(0, Color.BLACK);
		a.get(3).set(0, Color.WHITE);
		assertWellFormed(true, it);
	}
	
	public void testE4() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 4);
		it = spy.newIterator(r, 3, 9, 0, 4);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(false, it);
	}
	
	public void testE5() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 5);
		it = spy.newIterator(r, 0, 0, 5, 5);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testE6() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 6);
		it = spy.newIterator(r, 3, 1, 3, 6);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testE7() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 7);
		it = spy.newIterator(r, 3, 3, 3, 7);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(false, it);
	}
	
	public void testE8() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 8);
		it = spy.newIterator(r, 3, 8, 1, 8);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testE9() {
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 9);
		it = spy.newIterator(r, 6, 1, 0, 9);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	
	/// testFn: tests of iterator testing outer invariant
	
	
	public void testF0() {
		r = spy.create(null, 0, 0);
		it = spy.newIterator(r, 0, -1, 0, 0);
		assertWellFormed(false, it);
	}
	
	public void testF1() { //compare testB0
		List<List<Color>> a = a(0, -1, 0, -1, 2);
		r = spy.create(a, 1, 1);
		it = spy.newIterator(r, 0, -1, 0, 1);
		assertWellFormed(false, it);
	}
	
	public void testF2() {
		r = spy.create(a(0, 1, -1, 0, 10, -1, 0, 3), 0, 2);
		it = spy.newIterator(r, 0, -1, 0, 2);
		assertWellFormed(true, it);
	}

	public void testF3() { // compare testB3
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 3);
		it = spy.newIterator(r, 0, 0, 0, 3);
		a.get(0).set(0, Color.BLUE);
		assertWellFormed(false, it);
	}

	public void testF4() { // compare testB4
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 4);
		it = spy.newIterator(r, 3, 0, 1, 4);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		assertWellFormed(true, it);
	}
	
	public void testF5() { // compare testB5
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 2, 5);
		it = spy.newIterator(r, 3, 0, 2, 5);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		a.get(6).set(1, Color.YELLOW);
		assertWellFormed(false, it);
	}

	public void testF6() { // compare testB6
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 3, 6);
		it = spy.newIterator(r, 3, 2, 1, 6);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		a.get(6).set(1, Color.YELLOW);
		assertWellFormed(true, it);
	}
	
	public void testF7() { // compare testB7
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 4, 7);
		it = spy.newIterator(r, 4, 0, 2, 5);
		a.get(0).set(0, Color.BLUE);
		a.get(3).set(2, Color.GREEN);
		a.get(6).set(1, Color.YELLOW);
		assertWellFormed(false, it);
	}

	public void testF8() { // compare testB8
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 5, 8);
		it = spy.newIterator(r, -1, -1, 4, 8);
		a.get(0).set(0, Color.RED);
		a.get(3).set(6, Color.MAGENTA);
		a.get(6).set(0, Color.PINK);
		a.get(6).set(2, Color.ORANGE);
		assertWellFormed(false, it);
	}
	
	public void testF9() { // compare testB9
		List<List<Color>> a = a(1, 2, 3, -1, -1, -1, 3, 2, 1, 0);
		r = spy.create(a, 6, 9);
		ArrayPixelCollection r2 = spy.create(a, 5, 9);
		it = spy.newIterator(r, 2, 1, 3, 9);
		Iterator<Pixel> it2 = spy.newIterator(r2, 2, 1, 3, 9);
		a.get(1).set(0, Color.BLACK);
		a.get(2).set(0, Color.DARK_GRAY);
		a.get(2).set(2, Color.LIGHT_GRAY);
		a.get(6).set(2, Color.WHITE);
		a.get(8).set(0, Color.CYAN);
		assertWellFormed(false, it);
		assertWellFormed(true, it2);
	}

	
	/// testGn: tests of stale iterators
	
	public void testG0() { // compare testC1
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 1);
		it = spy.newIterator(r, 0, 1, 0, 0);
		assertWellFormed(true, it);
	}
	
	public void testG1() { // comapre testC2
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 1);
		it = spy.newIterator(r, 1, 0, 0, 2);
		assertWellFormed(true, it);
	}
	
	public void testG2() { // compare testC3
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 3);
		it = spy.newIterator(r, 2, 0, 0, 1);
		assertWellFormed(true, it);
	}

	public void testG3() { // compare testC4
		r = spy.create(a(1, -1, 0, 10, -1, 0, 3), 0, 3);
		it = spy.newIterator(r, 2, -1, 0, 1);
		assertWellFormed(true, it);
	}
	
	public void testG4() { // compare testD3
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 3);
		it = spy.newIterator(r, 0, 0, 0, 4);
		a.get(3).set(0, Color.WHITE);
		assertWellFormed(true, it);
	}
	
	public void testG5() { // compare testD6
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 6);
		it = spy.newIterator(r, 3, 0, 1, 5);
		a.get(3).set(0, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testG6() { // compare testD9
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 1, 6);
		it = spy.newIterator(r, 3, 9, 0, 9);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testG7() { // compare testE2
		List<List<Color>> a = a(0,1,-1,2,-1,0);
		r = spy.create(a, 2, 7);
		it = spy.newIterator(r, 3, 0, 0, 2);
		a.get(1).set(0, Color.BLACK);
		a.get(3).set(1, Color.WHITE);
		assertWellFormed(true, it);
	}

	public void testG8() { // compare testE4
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 8);
		it = spy.newIterator(r, 3, 9, 0, 4);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}
	
	public void testG9() { // compare testE7
		List<List<Color>> a = a(1, -1, 0, 10, -1, 0, 3);
		r = spy.create(a, 6, 7);
		it = spy.newIterator(r, 3, 3, 3, 9);
		a.get(0).set(0, Color.RED);
		a.get(3).set(0, Color.ORANGE);
		a.get(3).set(1, Color.YELLOW);
		a.get(3).set(3, Color.GREEN);
		a.get(3).set(7, Color.CYAN);
		a.get(6).set(1, Color.MAGENTA);
		assertWellFormed(true, it);
	}	
}
