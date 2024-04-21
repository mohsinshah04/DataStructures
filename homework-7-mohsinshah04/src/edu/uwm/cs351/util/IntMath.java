package edu.uwm.cs351.util;

import java.util.Random;

import junit.framework.TestCase;

public class IntMath {

	/**
	 * Return the integer square root (rounded down) of an unsigned 64 bit integer.
	 * @param x value to perform square root on, treated unsigned
	 * @return largest value y for which y*y <= x where x is treated as unsigned. 
	 */
	public static long isqrt(long x) {
		// System.out.println("isqrt(" + x + ")");
		if (x > 1) {
			long y = 2;
			do {
				// System.out.println(" "+y + " is isqrt of " + x + " ?");
				long r = x/y;
				if (r == y) return y;
				if (r > y && (y+1)*(y+1) > x) return y;
				y = (y + r)/2;
			} while (true);
		} else if (x < -1) {
			// x in [-2^63,-2] and represents an unsigned long in range [2^63,2^64-2]
			// isqrt((unsigned long)x) is in range (2^31+2^29,2^32)
			//  since sqrt(2) = 1.011010...
			long y = (1L << 31) + (1 << 29); 
			do { // y is in range [2^31+2^29,2^32)
				// System.out.println(" "+y + " is isqrt of " + x + " ?");
				// (unsigned long)x = x + 2^64 = x + (2^63-1) + 2 + (2^63-1)
				long r = (x+Long.MAX_VALUE+2)/y + Long.MAX_VALUE/y;
				// because of truncation, r+1 may be the true quotient
				// System.out.println(" quotient ~= " + r);
				if (r == y) return r;
				if (r == y+1) {
					long z = y - Integer.MAX_VALUE - (1 << 29);
					// y - (2^31-1) - 2^29 = z
					// y + 1 = z + (2^31) + (2^29)
					// z is in range [1,2^31-2^29+1) -- z fits in 31 bits + sign bit
					// System.out.println("z = " + z);
					// (y+1)^2 = z^2 + 2z(2^31) + 2^62 + 2z(2^29) + 2^58 + 2*(2^31)(2^29) 
					//         = z^2 + z(2^32) + z(2^30) + 2^62 + 2^58 + 2^61
					long nextSquare = z*z + (z << 32) + (z << 30) + (25L << 58);
					
					// System.out.println(" (y+1)^2 = " + nextSquare);
					if (nextSquare > x) return y;
					return r;
				}
				if (r == y-1) {
					long z = y - (1L << 31);
					// y - (2^31) = z
					// y = z + (2^31)
					// z is in range [2^29,2^31) -- z fits in 31 bits + sign bit
					// System.out.println("z = " + z);
					// y^2 = z^2 + 2z(2^31) + 2^62 
					long thisSquare = z*z + (z << 32) + (1L << 62);
					// System.out.println(" y^2 = " + thisSquare);
					if (thisSquare > x) return r;
					return y;
				}
				y = (y + r)/2;
			} while (true);
		} else if (x == -1) {
			return (1L << 32) -1;
		} else return x; // 0 and 1
	}
	
	/**
	 * Return the (unsigned) square of the (signed) input
	 * if it can be represented
	 * @param x signed value
	 * @return unsigned long square
	 */
	public static long isqr(long x) {
		if (x <= -(1L << 32) || x >= (1L << 32)) throw new ArithmeticException("Overflow");
		if (x < 0) x = -x; // no point in handling negatives now
		if (x <= Integer.MAX_VALUE) return x*x;
		// x is [2^31-1,2^32-1]
		long z = x - (1L << 31);
		// x = z + 2^31
		// z is in range [-1,2^31-1]
		// x^2 = z^2 + 2z(2^31) + 2^62
		return z*z + (z << 32) + (1L << 62);
	}
	
	public static void main(String[] args) {
		for (String s : args) {
			long l = Long.parseLong(s);
			if (l < 0) {
				long q = isqrt(l);
				System.out.println("isqrt(" + l + ") = " + q);
			} else {
				long q = isqr(l);
				System.out.println("isqr(" + l + "=0x"+ Long.toHexString(l) + ") = " + q);
			}
		}
	}
	public static class Test extends TestCase {
		public void testFirst25() {
			assertEquals(0,isqrt(0));
			assertEquals(1,isqrt(1));
			assertEquals(1,isqrt(2));
			assertEquals(1,isqrt(3));
			assertEquals(2,isqrt(4));
			assertEquals(2,isqrt(5));
			assertEquals(2,isqrt(6));
			assertEquals(2,isqrt(7));
			assertEquals(2,isqrt(8));
			assertEquals(3,isqrt(9));
			assertEquals(3,isqrt(10));
			assertEquals(3,isqrt(11));
			assertEquals(3,isqrt(12));
			assertEquals(3,isqrt(13));
			assertEquals(3,isqrt(14));
			assertEquals(3,isqrt(15));
			assertEquals(4,isqrt(16));
			assertEquals(4,isqrt(17));
			assertEquals(4,isqrt(18));
			assertEquals(4,isqrt(19));
			assertEquals(4,isqrt(20));
			assertEquals(4,isqrt(21));
			assertEquals(4,isqrt(22));
			assertEquals(4,isqrt(23));
			assertEquals(4,isqrt(24));
			assertEquals(5,isqrt(25));
		}
		
		public void testLarge() {
			long l = Integer.MAX_VALUE;
			long l2 = l*l;
			assertEquals(l,isqrt(l2));
			assertEquals(l-1,isqrt(l2-1));
			assertEquals(l,isqrt(l2+1));
		}
		
		public void testLarger() {
			long l = Integer.MAX_VALUE+10L;
			long l2 = Integer.MAX_VALUE*20L+100L;
			l2 += Integer.MAX_VALUE*(long)Integer.MAX_VALUE;
			assertTrue(l2 > 0);
			assertEquals(l,isqrt(l2));
			assertEquals(l-1,isqrt(l2-1));
			assertEquals(l,isqrt(l2+1));
		}
		
		public void testEvenLarger() {
			long l = Integer.MAX_VALUE+(1L << 30);
			long l2 = Integer.MAX_VALUE*(1L << 31)+(1L<<60);
			l2 += Integer.MAX_VALUE*(long)Integer.MAX_VALUE; // overflow
			assertFalse(l2 > 0);
			assertEquals(l,isqrt(l2));
			assertEquals(l-1,isqrt(l2-1));
			assertEquals(l,isqrt(l2+1));
		}
		
		public void testLargerStill() {
			long l = Integer.MAX_VALUE + (long)Integer.MAX_VALUE;
			long l2 = Integer.MAX_VALUE*(long)Integer.MAX_VALUE;
			l2 += l2; // still OK
			assertTrue(l2 > 0);
			l2 += l2; // overflow
			assertFalse(l2 > 0);
			assertEquals(l,isqrt(l2));
			assertEquals(l-1,isqrt(l2-1));
			assertEquals(l,isqrt(l2+1));			
		}
		
		public void testSpecial() {
			long l = -6871947672L;
			long l2 = isqrt(l);
			assertEquals(4294967295L,l2);
		}
		
		public void testLargest() {
			assertEquals((1L<<32)-1,isqrt(-1));
		}
		
		public void testStochastic() {
			Random r = new Random();
			for (int i=0; i < 10000; ++i) {
				long l = r.nextLong();
				long q = isqrt(l);
				long s = isqr(q);
				assertTrue("isqrt(" + l + ")", s <= l);
				try {
					s = isqr(q+1);
					assertTrue("isqrt(" + l + ")", s > l);
				} catch (ArithmeticException ex) {
					// everything is OK.
				}
			}
		}
	}
}
