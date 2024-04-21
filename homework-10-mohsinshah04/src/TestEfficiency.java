import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import edu.uwm.cs351.RasterMap;
import edu.uwm.cs351.util.DefaultEntry;
import junit.framework.TestCase;


public class TestEfficiency extends TestCase {

	private RasterMap m;
	
	private Random random;
	
	private static final int POWER = 21; // million entries
	private static final int SIZE = (1 << (POWER-1)) - 1;
	private static final int TESTS = SIZE/POWER; //
	
	private Point p(int i) {
		return new Point(i,i);
	}
	
	private Color[] colors = {
		Color.BLACK, Color.RED, Color.BLUE, Color.GREEN,
		Color.WHITE, Color.CYAN, Color.YELLOW, Color.MAGENTA,
		Color.PINK, Color.ORANGE, Color.LIGHT_GRAY, Color.DARK_GRAY,
		new Color(120,0,0), new Color(130,0,0), new Color(140,0,0), new Color(150,0,0),
		new Color(160,0,0), new Color(170,0,0), new Color(180,0,0), new Color(190,0,0),
		new Color(200,0,0), new Color(210,0,0), new Color(220,0,0)
	};
	
	protected void setUp() throws Exception {
		super.setUp();
		random = new Random();
		try {
			assert m.size() == TESTS : "cannot run test with assertions enabled";
		} catch (NullPointerException ex) {
			throw new IllegalStateException("Cannot run test with assertions enabled");
		}
		m = new RasterMap();
		int max = (1 << (POWER)); // 2^(POWER) = 2 million
		for (int power = POWER; power > 1; --power) {
			int incr = 1 << power;
			for (int i=1 << (power-1); i < max; i += incr) {
				m.put(p(i), colors[power]);
			}
		}
	}
		
	
	@Override
	protected void tearDown() throws Exception {
		m = null;
		super.tearDown();
	}


	public void testA() {
		for (int i=0; i < SIZE; ++i) {
			assertFalse(m.isEmpty());
		}
	}
	
	public void testB() {
		for (int i=0; i < SIZE; ++i) {
			assertEquals(SIZE,m.size());
		}
	}

	public void testC() {
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			assertTrue(m.containsKey(p(r*4+2)));
			assertEquals(colors[2],m.get(p(r*4+2)));
			assertNull(m.get(p(r*2+1)));
			assertFalse(m.containsKey(p(r*2+1)));
		}
	}
	
	public void testD() {
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			assertTrue(m.containsKey(p(r*4+2)));
		}
	}

	public void testE() {
		Set<Integer> touched = new HashSet<Integer>();
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			if (!touched.add(r)) continue; // don't check again
			assertEquals(colors[2],m.remove(p(r*4+2)));
			assertNull(m.remove(p(r*2+1)));
		}
	}

	public void testF() {
		for (int i=0; i < SIZE; ++i) {
			assertEquals(SIZE, m.entrySet().size());
		}
	}
	
	public void testG() {
		for (int i=0; i < TESTS; ++i) {
			assertFalse("should not contain bad entry for " + i,
					m.entrySet().contains(new DefaultEntry<>(p(i*4+2),colors[1])));
			assertTrue("should contain entry for " + i,
					m.entrySet().contains(new DefaultEntry<>(p(i*4+2),colors[2])));
			assertFalse("should not contain non-existent entry for " + i,
					m.entrySet().contains(new DefaultEntry<>(p(i*2+1),colors[1])));	
		}
	}
	
	public void testH() {
		int removed = 0;
		Set<Entry<Point,Color>> es = m.entrySet();
		for (int i=0; i < TESTS; ++i) {
			if ((i%2) == 0) {
				++removed;
				assertFalse("should not remove bad entry for " + i,
						es.remove(new DefaultEntry<>(p(i*4+2),colors[1])));
				assertTrue(es.remove(new DefaultEntry<>(p(i*4+2),colors[2])));	
				assertFalse("should not remove twice entry for " + i,
						es.remove(new DefaultEntry<>(p(i*4+2),colors[12])));
			}
		}
		assertEquals(SIZE-removed, m.size());
	}

	public void testI() {
		Set<Entry<Point,Color>> es = m.entrySet();
		for (int i=0; i < TESTS; ++i) {
			Iterator<Entry<Point,Color>> it= es.iterator();
			assertEquals(new DefaultEntry<>(p(2),colors[2]), it.next());
		}
	}
	
	public void testJ() {
		Iterator<Entry<Point,Color>> it = m.entrySet().iterator();
		for (int i=0; i < SIZE; ++i) {
			assertTrue("After " + i + " next(), should still have next",it.hasNext());
			it.next();
		}
	}
	
	public void testK() {
		int removed = 0;
		assertEquals(SIZE,m.size());
		Iterator<Entry<Point,Color>> it = m.entrySet().iterator();
		for (int i = 2; i < TESTS; i += 2) {
			assertEquals(p(i),it.next().getKey());
			if (random.nextBoolean()) {
				it.remove();
				++removed;
			}
		}
		assertEquals(SIZE-removed,m.size());
	}

	public void testL() {
		for (int i=0; i < SIZE; ++i) {
			Set<Point> s = m.keySet();
			assertEquals(SIZE, s.size());
		}
	}
	
	public void testM() {
		Set<Point> s = m.keySet();
		for (int i=0; i < TESTS; ++i) {
			assertTrue(s.contains(p(i*4+2)));
		}
		assertEquals(SIZE, s.size());
	}
	
	public void testN() {
		Set<Point> s = m.keySet();
		int removed = 0;
		for (int i=0; i < TESTS; ++i) {
			if ((i%8) == 0) {
				++removed;
				assertTrue(s.remove(p(i*4+2)));
			} else {
				assertFalse(s.remove(p(i*4+1)));			}
		}
		assertEquals(SIZE - removed, s.size());
	}
	
	public void testO() {
		for (int i=0; i < TESTS; ++i) {
			Iterator<Point> it = m.keySet().iterator();
			assertEquals(p(2), it.next());
		}
	}

	public void testP() {
		Iterator<Point> it = m.keySet().iterator();
		for (int i=0; i < SIZE; ++i) {
			assertTrue("After " + i + " next(), should still have next",it.hasNext());
			it.next();
		}
	}
	
	public void testQ() {
		int removed = 0;
		assertEquals(SIZE,m.size());
		Iterator<Point> it = m.keySet().iterator();
		for (int i = 2; i < TESTS; i += 2) {
			assertEquals(p(i),it.next());
			if (random.nextBoolean()) {
				it.remove();
				++removed;
			}
		}
		assertEquals(SIZE-removed,m.size());
	}
	
	public void testR() {
		for (int i=0; i < SIZE; ++i) {
			assertEquals(SIZE, m.values().size());
		}
	}
	
	public void testS() {
		Iterator<Color> it = m.values().iterator();
		for (int i=0; i < SIZE; ++i) {
			assertTrue("After " + i + " next(), should still have next",it.hasNext());
			it.next();
		}
	}
	
	public void testT() {
		Iterator<Color> it = m.values().iterator();
		int removed = 0;
		for (int i=0; i < TESTS; ++i) {
			if ((i%2) == 0) {
				assertEquals(colors[2], it.next());
				it.remove();
				++removed;
			} else {
				it.next();
			}
		}
		assertEquals(SIZE - removed, m.values().size());
	}
	
}
