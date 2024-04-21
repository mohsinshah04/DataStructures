import java.awt.Color;
import java.util.Random;

import edu.uwm.cs351.DynamicRaster;
import edu.uwm.cs351.Pixel;
import junit.framework.TestCase;

public class TestEfficiency extends TestCase {
	
	DynamicRaster s;
	Random r;
	
	@Override
	public void setUp() {
		s = new DynamicRaster();
		r = new Random();
		try {
			assert 1/s.getPixel(0,0).hashCode() == 42 : "OK";
			assertTrue(true);
		} catch (NullPointerException ex) {
			System.err.println("Assertions must NOT be enabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
	}

	private static final int MAX_LENGTH = 1000000;
	private static final int SAMPLE = 100;
	
	
	public void test0() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
		}
		assertEquals(MAX_LENGTH, s.size());
	}
	
	public void test1() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(new Pixel(i % SAMPLE, 1), s.getPixel(i%SAMPLE, 1));
		}
	}
	
	public void test2() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertNull(s.getPixel(i%SAMPLE, 0));
		}
	}
	
	public void test3() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
		}
		for (int i=0; i < SAMPLE; ++i) {
			assertTrue(s.clearAt(SAMPLE-i-1, 1));
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertFalse(s.clearAt(i%SAMPLE, 1));
		}
	}
	
	public void test4() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertTrue(s.add(new Pixel(MAX_LENGTH-i-1, 1)));
			assertFalse(s.add(new Pixel(MAX_LENGTH-i-1, 1)));
		}
	}
	
	public void test5() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
		}
		s.start();
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertTrue(s.isCurrent());
			assertEquals(new Pixel(i, 1), s.getCurrent());
			s.advance();
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertFalse(s.isCurrent());
		}
	}
	
	public void test6() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
		}
		s.start();
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(new Pixel(i, 1), s.getCurrent());
			if ((i%2) == 0) s.advance();
			else s.removeCurrent();
		}
		assertEquals(MAX_LENGTH/2, s.size());		
	}
	
	public void test7() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
		}
		s.start();
		assertNull(s.getPixel(MAX_LENGTH, 1));
		assertTrue(s.add(new Pixel(MAX_LENGTH, 1, Color.RED)));
		s.start();
		assertEquals(new Pixel(MAX_LENGTH, 1, Color.RED), s.getPixel(MAX_LENGTH, 1));
		assertTrue(s.clearAt(MAX_LENGTH, 1));
	}
	
	public void test8() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(new Pixel(MAX_LENGTH-i-1, 1));
			s.advance();
		}
		
		int sum = 0;
		s.start();
		for (int j=0; j < SAMPLE; ++j) {
			int n = r.nextInt(MAX_LENGTH/SAMPLE);
			for (int i=0; i < n; ++i) {
				s.advance();
			}
			sum += n;
			assertEquals(new Pixel(sum,1),s.getCurrent());
		}
	}
	
	private static final int MAX_WIDTH = 50000;
	
	public void test9() {
		DynamicRaster[] a = new DynamicRaster[MAX_WIDTH];
		for (int i=0; i < MAX_WIDTH; ++i) {
			a[i] = s = new DynamicRaster();
			int n = r.nextInt(SAMPLE);
			for (int j=0; j < n; ++j) {
				s.add(new Pixel(n-j-1,0));
			}
		}
		
		for (int j = 0; j < SAMPLE; ++j) {
			int i = r.nextInt(a.length);
			s = a[i];
			if (s.size() == 0) continue;
			int n = r.nextInt(s.size());
			s.start();
			for (int k=0; k < n; ++k) {
				s.advance();
			}
			assertEquals(new Pixel(n,0),s.getCurrent());
		}
	}
	
}
