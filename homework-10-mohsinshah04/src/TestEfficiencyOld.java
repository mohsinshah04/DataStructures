import java.util.Iterator;

import edu.uwm.cs351.Pixel;
import edu.uwm.cs351.RasterMap;
import junit.framework.TestCase;


public class TestEfficiencyOld extends TestCase {
	RasterMap s;
	RasterMap c;
	Iterator<Pixel> it;
	
	private Pixel a(int i) {
		return new Pixel(i,i);
	}

	@Override
	public void setUp() {
		s = new RasterMap();
		c = s;
		try {
			assert 1/s.size() < 0 : "OK";
			assertTrue(true);
		} catch (ArithmeticException ex) {
			System.err.println("Assertions must NOT be enabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
		int max = (1 << (POWER)); // 2^(POWER) = 2 million
		for (int power = POWER; power > 1; --power) {
			int incr = 1 << power;
			for (int i=1 << (power-1); i < max; i += incr) {
				c.add(a(i));
			}
		}

	}

	private static final int POWER = 21;
	private static final int MAX_LENGTH = 1 << POWER;
	
	public void testA() {
		int m = MAX_LENGTH/2-1;
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(m, s.size());
		}
	}

	public void testB() {
		for (int i=2; i < MAX_LENGTH; i += 2) {
			assertEquals(a(i), s.getPixel(i,i));
		}
	}
	
	public void testC() {
		for (int i=2; i < MAX_LENGTH; i += 2) {
			assertTrue(s.clearAt(i,i));
		}
		assertEquals(0, s.size());
	}

	public void testD() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(a(i));
		}
		assertEquals(MAX_LENGTH, s.size());
	}
	
	public void testE() {
		c = s.clone();
		c.add(a(3));
		c.add(a(MAX_LENGTH-3));
		assertNull(s.getPixel(3, 3));
		assertNull(s.getPixel(MAX_LENGTH-3, MAX_LENGTH-3));
	}
	
}
