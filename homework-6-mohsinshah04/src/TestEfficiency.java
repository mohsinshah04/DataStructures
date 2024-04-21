import java.util.Random;

import edu.uwm.cs351.SortedSequence;
import junit.framework.TestCase;


public class TestEfficiency extends TestCase {	
	SortedSequence<Integer> s;
	Random r;
	
	@Override
	public void setUp() {
		s = new SortedSequence<>();
		r = new Random();
		try {
			assert 1/s.size() == 42 : "OK";
			assertTrue(true);
		} catch (ArithmeticException ex) {
			System.err.println("Assertions must NOT be enabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
	}

	private static final int POWER = 20;
	private static final int MAX_LENGTH = 1 << POWER; // one million
	private static final int SAMPLE = 100;
	
	private void addMany(int number) {
		for (int i=0; i < number; ++i) {
			s.add(number - 1 - i);
		}
	}

	public void testA() {
		addMany(MAX_LENGTH);
		assertEquals(MAX_LENGTH, s.size());
	}
	
	public void testB() {
		s.add(MAX_LENGTH+10);
		s.add(-1);
		addMany(MAX_LENGTH/2);
		assertEquals(MAX_LENGTH/2+2, s.size());
	}
	
	public void testC() {
		addMany(MAX_LENGTH);
		s.start();
		while (s.isCurrent()) {
			s.removeCurrent();
		}
		assertEquals(0, s.size());
	}
	
	public void testD() {
		int width = MAX_LENGTH/SAMPLE;
		@SuppressWarnings("unchecked")
		SortedSequence<Integer>[] sss = (SortedSequence<Integer>[]) new SortedSequence<?>[width];
		for (int i=0; i < width; ++i) {
			addMany(SAMPLE);
			assertEquals(SAMPLE, s.size());
			sss[i] = s;
			s = new SortedSequence<>();
		}		
	}
	
	public void testE() {
		s.add(63);
		s.add(17);
		for (int i=0 ; i < MAX_LENGTH; ++i) {
			s.add(42);
		}
		assertEquals(3, s.size());
	}
	
	public void testF() {
		for (int i=0; i < POWER; ++i) {
			s.add(i*POWER);
		}
		addMany(MAX_LENGTH/POWER);
		assertEquals(MAX_LENGTH/POWER, s.size());
	}
	
	public void testG() {
		addMany(MAX_LENGTH);
		s.start();
		int count = 0;
		while (s.isCurrent()) {
			assertEquals(Integer.valueOf(count), s.getCurrent());
			s.advance();
			++count;
		}
		assertEquals(MAX_LENGTH, count);
	}
	
	public void testH() {
		addMany(MAX_LENGTH);
		Integer zero = 0;
		s.start();
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(zero, s.getCurrent());
		}
	}
		
	public void testI() {
		addMany(MAX_LENGTH);
		s.start();
		for (int i=0; i < MAX_LENGTH; i+= 2) {
			assertEquals(Integer.valueOf(i), s.getCurrent());
			s.removeCurrent();
			s.advance();
		}
		assertEquals(MAX_LENGTH/2, s.size());
	}
	
}
