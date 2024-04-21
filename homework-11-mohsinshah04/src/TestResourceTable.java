import java.net.URI;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.FixedObject;
import edu.uwm.cs351.Resource;


public class TestResourceTable extends LockedTestCase {
	protected void assertException(Class<? extends Throwable> c, Runnable r) {
		try {
			r.run();
			assertFalse("Exception should have been thrown",true);
		} catch (RuntimeException ex) {
			if (!c.isInstance(ex)) ex.printStackTrace();
			assertTrue("should throw exception of " + c + ", not of " + ex.getClass(), c.isInstance(ex));
		}
	}

	private static final int NUM_RES = 21;
	private Resource.Table table;
	private Resource[] r;
	private Resource r0;
	private Iterator<Resource> it;
	
	protected URI u(int i) {
		return URI.create("test:" + i);
	}

	protected Resource r(int i) {
		return new FixedObject(u(i),i);
	}

	@Override
	protected void setUp() {
		try {
			assert table.size() == r.length;
			assertTrue("Assertions not enabled.  Add -ea to VM Args Pane in Arguments tab of Run Configuration",false);
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		table = new Resource.Table();
		r = new Resource[NUM_RES];
		r0 = new FixedObject(null,null);
		r[0] = r0;
		for (int i=1; i < NUM_RES; ++i) {
			r[i] = r(i);
		}
		testRemainder(0);
	}
	
	private void testRemainder(int ignored) {
		// Testing knowledge of Java's remainder operation:
		assertEquals(Ti(2061591460),10 % 3);
		assertEquals(Ti(1585582259),11 % 3);
		assertEquals(Ti(443927344),12 % 3);
		assertEquals(Ti(1455969950),-10 % 3);
		assertEquals(Ti(1515231594),-11 % 3);
		assertEquals(Ti(991733711),-12 % 3);
	}
	
	public void test00() {
		assertEquals(0,table.size());
	}
	
	public void test01() {
		assertNull(table.get(u(1)));
	}
	
	public void test02() {
		assertNull(table.get(null));
	}
	
	public void test03() {
		assertFalse(table.contains(r[1]));
	}

	public void test04() {
		assertFalse(table.contains(r0));
	}

	public void test05() {
		assertFalse(table.contains((Object)0));
		assertFalse(table.contains((Object)"Hello"));
		assertFalse(table.contains(null));
		assertFalse(table.contains((Object)u(6)));
	}
	
	public void test09() {
		assertException(IllegalArgumentException.class,() -> table.add(r0));
	}

	public void test10() {
		assertTrue(table.add(r[1]));
	}

	public void test11() {
		table.add(r[1]);
		assertEquals(1,table.size());
	}
	
	public void test12() {
		table.add(r[2]);
		assertSame(r[2],table.get(u(2)));
	}
	
	public void test13() {
		table.add(r[3]);
		assertNull(table.get(u(2)));
	}
	
	public void test14() {
		table.add(r[4]);
		assertNull(table.get(null));
	}
	
	public void test15() {
		table.add(r[5]);
		assertTrue(table.contains(r(5)));
	}
	
	public void test16() {
		table.add(r[6]);
		assertFalse(table.contains(r(16)));
	}
	
	public void test17() {
		table.add(r[7]);
		assertFalse(table.contains(r0));
	}
	
	public void test18() {
		table.add(r[8]);
		assertFalse(table.contains((Object)r[8].toString()));
	}
	
	public void test19() {
		table.add(r[9]);
		assertFalse(table.add(r[9]));
	}
	
	public void test20() {
		table.add(r[2]);
		assertTrue(table.add(r[9]));
	}
	
	public void test21() {
		table.add(r[2]);
		table.add(r[1]);
		assertEquals(2,table.size());
	}
	
	public void test22() {
		table.add(r[2]);
		assertFalse(table.add(r[2]));
		assertEquals(1,table.size());
	}
	
	public void test23() {
		table.add(r[2]);
		table.add(r[3]);
		assertSame(r[2],table.get(u(2)));
	}
	
	public void test24() {
		table.add(r[2]);
		table.add(r[4]);
		assertSame(r[4],table.get(u(4)));
	}
	
	public void test25() {
		table.add(r[5]);
		table.add(r[15]);
		assertEquals(2,table.size());
	}
	
	public void test26() {
		table.add(r[6]);
		table.add(r[16]);
		assertTrue(table.contains(r(6)));
	}
	
	public void test27() {
		table.add(r[7]);
		table.add(r[17]);
		Resource.Table table2 = new Resource.Table();
		assertException(IllegalArgumentException.class,() -> {
				table2.add(r[17]); // one of these should have non-null "next"
				table2.add(r[7]);  // probably the previousone, but maybe this one
		});	
	}
	
	public void test28() {
		table.add(r[8]);
		table.add(r[18]);
		assertTrue(table.contains(r(18)));
	}
	
	public void test29() {
		table.add(r[2]);
		table.add(r[9]);
		assertSame(r[2],table.get(u(2)));
	}
	
	public void test30() {
		table.add(r[3]);
		table.add(r[4]);
		table.add(r[5]);
		assertEquals(3,table.size());
	}
	
	public void test31() {
		table.add(r[1]);
		table.add(r[8]);
		table.add(r[11]);
		assertEquals(3,table.size());
	}
	
	public void test32() {
		table.add(r[3]);
		table.add(r[2]);
		table.add(r[12]);
		assertSame(r[3],table.get(u(3)));
		assertSame(r[2],table.get(u(2)));
		assertSame(r[12],table.get(u(12)));
	}
	
	public void test33() {
		table.add(r[1]);
		table.add(r[2]);
		table.add(r[3]);
		table.add(r[4]);
		table.add(r[5]);
		assertEquals(5,table.size());
		assertSame(r[1],table.get(u(1)));
		assertSame(r[2],table.get(u(2)));
		assertSame(r[3],table.get(u(3)));
		assertSame(r[4],table.get(u(4)));
		assertSame(r[5],table.get(u(5)));
		assertNull(table.get(null));
		assertNull(table.get(u(0)));
		assertNull(table.get(u(6)));
		assertNull(table.get(u(8)));
	}
	
	public void test34() {
		table.add(r[1]);
		table.add(r[3]);
		table.add(r[5]);
		table.add(r[7]);
		table.add(r[9]);
		table.add(r[11]);
		table.add(r[13]);
		table.add(r[15]);
		table.add(r[17]);
		table.add(r[19]);
		assertEquals(10,table.size());
		assertTrue(table.contains(r(1)));
		assertTrue(table.contains(r(3)));
		assertTrue(table.contains(r(5)));
		assertTrue(table.contains(r(7)));
		assertTrue(table.contains(r(9)));
		assertTrue(table.contains(r(11)));
		assertTrue(table.contains(r(13)));
		assertTrue(table.contains(r(15)));
		assertTrue(table.contains(r(17)));
		assertTrue(table.contains(r(19)));
		assertFalse(table.contains(r(0)));
		assertFalse(table.contains(r(2)));
		assertFalse(table.contains(r(4)));
		assertFalse(table.contains(r(6)));
		assertFalse(table.contains(r(8)));
		assertFalse(table.contains(r(10)));
		assertFalse(table.contains(r(12)));
		assertFalse(table.contains(r(14)));
		assertFalse(table.contains(r(16)));
		assertFalse(table.contains(r(18)));
		assertFalse(table.contains(r(20)));
	}
	
	
	/// Next, testing special cases for add
	
	public void test35() {
		table.add(r[8]);
		assertFalse(table.add(r(8)));
	}

	public void test36() {
		table.add(r[1]);
		table.add(r(1));
		assertEquals(1,table.size());
	}

	public void test37() {
		table.add(r[2]);
		Resource r2 = r(2); // a NEW resource
		table.add(r2);
		assertSame(r2,table.get(u(2)));
	}

	public void test38() {
		table.add(r[3]);
		table.add(new FixedObject(u(3),"different"));
		assertFalse(table.contains(r[3]));
	}

	
	/// test4X: test iterators (without remove)
	
	public void test40() {
		it = table.iterator();
		assertFalse(it.hasNext());
	}
	
	public void test41() {
		table.add(r[4]);
		it = table.iterator();
		assertTrue(it.hasNext());
		assertSame(r[4],it.next());
		assertFalse(it.hasNext());
	}
	
	public void test42() {
		table.add(r[4]);
		table.add(r[2]);
		it = table.iterator();
		assertSame(r[2],it.next());
		assertSame(r[4],it.next());
	}
	
	public void test43() {
		table.add(r[4]);
		table.add(r[3]);
		table.add(r[13]);
		it = table.iterator();
		assertSame(r[13],it.next());
		assertSame(r[3],it.next());
		assertSame(r[4],it.next());
	}
	
	public void test44() {
		table.add(r[4]);
		table.add(r[14]);
		table.add(r[13]);
		table.add(r[3]);
		it = table.iterator();
		assertSame(r[3],it.next());
		assertSame(r[13],it.next());
		assertSame(r[14],it.next());
		assertSame(r[4],it.next());
		assertFalse(it.hasNext());
	}
	
	public void test45() {
		table.add(r[5]);
		table.add(r[1]);
		table.add(r[8]);
		table.add(r[11]);
		table.add(r[18]);
		it = table.iterator();
		assertSame(r[18],it.next());
		assertSame(r[11],it.next());
		assertSame(r[8],it.next());
		assertSame(r[1],it.next());
		assertSame(r[5],it.next());
		assertFalse(it.hasNext());		
	}
	
	public void test46() {
		table.add(r[1]);
		table.add(r[8]);
		table.add(r[11]);
		table.add(r[18]);
		table.add(r[2]);
		table.add(r[9]);
		table.add(r[12]);
		table.add(r[19]);
		it = table.iterator();
		assertSame(r[2],it.next());
		assertSame(r[18],it.next());
		assertSame(r[19],it.next());
		assertSame(r[8],it.next());
		assertSame(r[9],it.next());
		assertSame(r[11],it.next());
		assertSame(r[12],it.next());
		assertSame(r[1],it.next());
	}
	
	public void test47() {
		table.add(r[6]);
		table.add(r[4]);
		it = table.iterator();
		it.next();
		table.add(r[4]); // shouldn't have an effect
		assertSame(r[6],it.next());
	}
	
	public void test48() {
		table.add(r[4]);
		table.add(r[7]);
		it = table.iterator();
		it.next();
		table.add(r[6]);
		assertException(ConcurrentModificationException.class,() -> it.hasNext());
	}
	
	public void test49() {
		table.add(r[4]);
		table.add(r[8]);
		it = table.iterator();
		table.add(r[1]);
		assertException(ConcurrentModificationException.class, () -> it.next());
	}
	
	/// test5X: tests for remove using iterators.
	
	public void test50() {
		table.add(r[5]);
		it = table.iterator();
		it.next();
		it.remove();
		assertEquals(0,table.size());
	}
	
	public void test51() {
		table.add(r[5]);
		table.add(r[1]);
		it = table.iterator();
		it.next();
		it.remove();
		assertTrue(it.hasNext());
	}
	
	public void test52() {
		table.add(r[5]);
		table.add(r[2]);
		it = table.iterator();
		it.next();
		it.remove();
		assertSame(r[5],table.get(u(5)));
	}
	
	public void test53() {
		table.add(r[5]);
		table.add(r[3]);
		table.add(r[1]);
		it = table.iterator();
		it.next();
		it.remove();
		it.next();
		it.next();
		it.remove();
		assertEquals(1,table.size());
	}
	
	public void test54() {
		table.add(r[5]);
		table.add(r[2]);
		table.add(r[9]);
		it = table.iterator();
		it.next();
		it.next();
		it.remove();
		assertEquals(2,table.size());
		assertSame(r[9],table.get(u(9)));
	}
	
	public void test55() {
		table.add(r[5]);
		table.add(r[1]);
		table.add(r[8]);
		table.add(r[11]);
		it = table.iterator();
		it.next();
		it.next();
		it.remove();
		assertFalse(table.contains(r(8)));
	}
	
	public void test56() {
		table.add(r[5]);
		table.add(r[6]);
		table.add(r[15]);
		table.add(r[16]);
		it = table.iterator();
		it.next();
		it.remove();
		it.next();
		it.remove();
		assertEquals(2,table.size());
	}
	
	public void test57() {
		table.add(r[5]);
		table.add(r[7]);
		table.add(r[15]);
		it = table.iterator();
		Iterator<Resource> it2 = table.iterator();
		it.next();
		it2.next();
		it.remove();
		assertException(ConcurrentModificationException.class,() -> it2.hasNext());
	}
		
	/// test6X: tests of remove & clear not using iterators
	
	public void test60() {
		it = table.iterator();
		table.clear();
		assertFalse(it.hasNext());
	}
	
	public void test61() {
		table.add(r[6]);
		table.clear();
		assertEquals(0,table.size());
	}
	
	public void test62() {
		table.add(r[6]);
		table.add(r[2]);
		table.clear();
		assertTrue(table.add(r[2]));
	}
	
	public void test63() {
		table.add(r[6]);
		table.add(r[16]);
		it = table.iterator();
		table.clear();
		assertException(ConcurrentModificationException.class,() -> it.hasNext());
	}
	
	public void test64() {
		table.add(r[4]);
		table.add(r[14]);
		table.clear();
		assertTrue(table.add(r[4]));
	}
	
	public void test65() {
		table.add(r[1]);
		table.add(r[2]);
		table.add(r[3]);
		table.add(r[4]);
		table.add(r[5]);
		table.add(r[6]);
		table.add(r[7]);
		table.add(r[8]);
		table.clear(); // should revert to size 7
		table.add(r[10]);
		table.add(r[2]);
		it = table.iterator();
		assertSame(r[10],it.next());
	}
	
	public void test66() {
		table.add(r[6]);
		assertTrue(table.remove(r(6)));
		assertEquals(0,table.size());
	}
	
	public void test67() {
		table.add(r[7]);
		table.add(r[10]);
		table.add(r[17]);
		table.remove(r[10]);
		assertEquals(2,table.size());
	}
	
	public void test68() {
		table.add(r[8]);
		table.add(r[1]);
		table.add(r[18]);
		table.remove(r(1));
		assertTrue(table.add(r[1]));
	}
	
	public void test69() {
		table.add(r[6]);
		table.add(r[9]);
		it = table.iterator();
		table.remove(r[6]);
		assertException(ConcurrentModificationException.class,() -> it.next());
	}
	
	/// test7X: testing special cases for remove
	
	public void test74() {
		table.add(r[7]);
		table.add(r[4]);
		assertTrue(table.remove(r(4)));
		Resource.Table other = new Resource.Table();
		other.add(r[4]);
	}
	
	public void test75() {
		table.add(r[7]);
		table.add(r[5]);
		assertFalse(table.remove(new FixedObject(u(5),7)));
	}
}
