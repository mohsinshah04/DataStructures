import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import edu.uwm.cs.junit.LockedTestCase;

/**
 * An abstract test suite for testing maps.
 * @param K key type
 * @param V value type
 */
public abstract class AbstractTestMap<K, V> extends LockedTestCase {

	protected <T> void assertException(Class<?> excClass, Runnable f) {
		try {
			f.run();
			assertFalse("Should have thrown an exception, not returned",true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				ex.printStackTrace();
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}
	
	protected Map<K,V> m;
	protected K[] k; // at least 10 keys, sorted if "sorted" is true, including null only if null keys are allowed
	protected K[] l; // keys equal to k but not identical (If possible)
	protected V[] v; // at least 10 values, never including null
	protected V[] w; // values equal to the v values
	
	protected boolean 
	    permitNulls = false, // values
	    sorted = false,  // element order
	    preserveOrder = false, // put order
	    failFast = false,
	    hasRemove = false;

	protected Set<Map.Entry<K,V>> es;
	protected Iterator<Map.Entry<K,V>> it;
	
	/** Create an entry with the given key and value.
	 * This method is wasteful in space.
	 * @param key key value, may be null
	 * @param value value, may be null
	 * @return entry with given key and value
	 */
	protected <Key,Value> Map.Entry<Key,Value> e(Key key, Value value) {
		HashMap<Key,Value> temp = new HashMap<>();
		temp.put(key, value);
		return temp.entrySet().iterator().next();
	}

	/**
	 * Create a new instance of the map type being tested.
	 * @return a new map
	 */
	protected abstract Map<K,V> create();
	
	/**
	 * Initialize the key and value arrays ({@link #k} and {@link #v}).
	 */
	protected abstract void initMapElements();
	
	@Override // implementation
	protected final void setUp() {
		initMapElements();
		m = create();
	}
	
	
	/// test0xx: tests of size and put
	
	public void test000() {
		assertEquals(0, m.size());
	}
	
	public void test001() {
		assertNull(m.put(k[3], v[3]));
	}
	
	public void test002() {
		m.put(k[0], v[2]);
		assertEquals(1, m.size());
	}
	
	public void test003() {
		m.put(k[0], v[3]);
		assertNull(m.put(k[3], v[0]));
	}
	
	public void test004() {
		m.put(k[0], v[4]);
		m.put(k[4], v[0]);
		assertEquals(2, m.size());
	}
	
	public void test005() {
		m.put(k[0], v[5]);
		assertEquals(v[5], m.put(l[0], v[3]));
	}
	
	public void test006() {
		m.put(k[1], v[6]);
		m.put(l[1], v[4]);
		assertEquals(1, m.size());
	}
	
	public void test007() {
		m.put(k[0], v[0]);
		m.put(k[7], v[1]);
		assertEquals(v[0], m.put(l[0], v[7]));
	}
	
	public void test008() {
		m.put(k[1], v[1]);
		m.put(k[8], v[2]);
		m.put(l[1], v[8]);
		assertEquals(2, m.size());
	}
	
	public void test009() {
		m.put(k[9], v[0]);
		m.put(k[1], v[4]);
		assertEquals(v[4], m.put(l[1], v[9]));
		assertEquals(2, m.size());
	}
		
	public void test010() {
		m.put(k[1], v[0]);
		m.put(k[9], v[1]);
		m.put(k[3], v[4]);
		assertEquals(v[1], m.put(l[9], v[5]));
		assertEquals(3, m.size());
	}
	
	public void test011() {
		if (!permitNulls) return;
		assertNull(m.put(k[1], null));
		assertEquals(1, m.size());	
	}
	
	public void test012() {
		if (!permitNulls) return;
		m.put(k[3], null);
		m.put(k[6], v[7]);
		assertNull(m.put(k[3], v[4]));
	}
	
	public void test013() {
		m.put(k[6], v[6]);
		m.put(k[0], v[1]);
		m.put(k[1], v[3]);
		assertEquals(v[6], m.put(l[6], v[9]));
	}
	
	public void test014() {
		m.put(k[5], v[4]);
		m.put(k[3], v[7]);
		m.put(k[1], v[9]);
		m.put(k[6], v[0]);
		assertEquals(4, m.size());
	}
	
	public void test015() {
		m.put(k[1], v[2]);
		m.put(k[3], v[6]);
		m.put(k[5], v[0]);
		m.put(k[2], v[4]);
		m.put(k[4], v[8]);
		assertEquals(v[6], m.put(l[3], v[7]));
		assertEquals(5, m.size());
	}
	
	public void test016() {
		m.put(k[3], v[1]);
		m.put(k[6], v[2]);
		m.put(k[9], v[3]);
		m.put(k[2], v[4]);
		m.put(k[5], v[5]);
		m.put(k[8], v[6]);
		assertEquals(v[1], m.put(l[3], v[7]));
		assertEquals(6, m.size());
	}
	
	public void test017() {
		m.put(k[7], v[0]);
		m.put(k[4], v[1]);
		m.put(k[1], v[2]);
		m.put(k[8], v[3]);
		m.put(k[5], v[4]);
		m.put(k[2], v[5]);
		m.put(k[9], v[6]);
		m.put(k[1], v[9]);
		m.put(k[8], v[0]);
		assertEquals(v[9], m.put(l[1], v[8]));
		assertEquals(7, m.size());
	}
	
	public void test018() {
		m.put(k[8], v[4]);
		m.put(k[6], v[3]);
		m.put(k[4], v[2]);
		m.put(k[2], v[1]);
		m.put(k[0], v[0]);
		m.put(k[9], v[5]);
		m.put(k[7], v[6]);
		m.put(k[5], v[7]);
		m.put(k[4], v[8]);
		m.put(k[2], v[9]);
		m.put(k[0], v[4]);
		assertEquals(v[4], m.put(l[8], v[8]));
		assertEquals(8, m.size());
	}
	
	public void test019() {
		m.put(k[9], v[0]);
		m.put(k[8], v[1]);
		m.put(k[7], v[2]);
		m.put(k[6], v[3]);
		m.put(k[5], v[4]);
		m.put(k[4], v[5]);
		m.put(k[3], v[6]);
		m.put(k[2], v[7]);
		m.put(k[1], v[8]);
		m.put(k[1], v[9]);
		m.put(k[2], v[0]);
		m.put(k[3], v[1]);
		m.put(k[4], v[2]);
		m.put(k[5], v[3]);
		m.put(k[6], v[4]);
		m.put(k[7], v[5]);
		m.put(k[8], v[6]);
		m.put(k[9], v[7]);
		assertEquals(9, m.size());
	}
	
	public void test020() {
		m.put(k[0], v[2]);
		m.put(k[2], v[2]);
		assertNull(m.put(k[4], v[2]));
		assertEquals(3, m.size());
	}
	
	
	/// test1xx: test of get
	
	public void test100() {
		assertNull(m.get(k[1]));
	}
	
	public void test101() {
		m.put(k[1], v[1]);
		assertEquals(v[1], m.get(l[1]));
	}
	
	public void test102() {
		m.put(k[2], v[0]);
		assertNull(m.get(l[1]));
	}
	
	public void test103() {
		m.put(k[1], v[0]);
		m.put(k[3], v[4]);
		assertEquals(v[0], m.get(l[1]));
	}
	
	public void test104() {
		m.put(k[1], v[4]);
		m.put(k[0], v[1]);
		assertEquals(v[1], m.get(l[0]));
	}
	
	public void test105() {
		m.put(k[1], v[0]);
		m.put(k[0], v[5]);
		assertNull(m.get(k[5]));
	}
	
	public void test106() {
		m.put(k[1], v[0]);
		m.put(k[6], v[1]);
		assertEquals(v[1], m.get(l[6]));
	}
	
	public void test107() {
		m.put(k[1], v[4]);
		m.put(k[0], v[5]);
		m.put(k[7], v[6]);
		assertEquals(v[5], m.get(k[0]));
	}
	
	public void test108() {
		m.put(k[1], v[4]);
		m.put(k[0], v[5]);
		m.put(k[8], v[6]);
		assertEquals(v[4], m.get(k[1]));
	}
	
	public void test109() {
		m.put(k[1], v[2]);
		m.put(k[0], v[3]);
		m.put(k[9], v[4]);
		assertEquals(v[4], m.get(k[9]));
	}
	
	public void test110() {
		m.put(k[1], v[0]);
		m.put(k[1], v[1]);
		m.put(k[0], v[2]);
		assertEquals(v[1], m.get(l[1]));
		assertEquals(v[2], m.get(l[0]));
	}
	
	public void test111() {
		m.put(k[1], v[5]);
		m.put(l[1], v[6]);
		m.put(k[1], v[7]);
		assertEquals(v[7], m.get(k[1]));
		assertNull(m.get(k[0]));
		assertEquals(v[7], m.get(l[1]));
	}
	
	public void test123() {
		m.put(k[1], v[0]);
		m.put(k[2], v[1]);
		m.put(k[3], v[2]);
		assertEquals(v[0], m.get(l[1]));
		assertEquals(v[1], m.get(l[2]));
		assertEquals(v[2], m.get(l[3]));
		assertEquals(3, m.size());
	}
	
	public void test139() {
		m.put(k[3], v[0]);
		m.put(k[6], v[1]);
		m.put(k[9], v[2]);
		m.put(k[2], v[3]);
		m.put(k[5], v[4]);
		m.put(k[8], v[5]);
		m.put(k[1], v[6]);
		m.put(k[4], v[7]);
		m.put(k[7], v[8]);
		assertNull(m.get(l[0]));
		assertEquals(v[6], m.get(l[1]));
		assertEquals(v[3], m.get(l[2]));
		assertEquals(v[0], m.get(l[3]));
		assertEquals(v[7], m.get(l[4]));
		assertEquals(v[4], m.get(l[5]));
		assertEquals(v[1], m.get(l[6]));
		assertEquals(v[8], m.get(l[7]));
		assertEquals(v[5], m.get(l[8]));
		assertEquals(v[2], m.get(l[9]));
	}
	
	public void test140() {
		m.put(k[1], v[4]);
		m.put(k[4], v[0]);
		m.put(k[0], v[1]);
		assertNull(m.get(null));
	}
	
	public void test141() {
		m.put(k[1], v[4]);
		m.put(k[4], v[1]);
		m.put(k[1], v[1]);
		assertEquals(v[1], m.get(l[1]));
	}
	
	public void test142() {
		m.put(k[1], v[4]);
		m.put(k[4], v[2]);
		m.put(k[2], v[1]);
		assertNull(m.get(new Object()));
	}
	
	
	/// test2xx: tests of entry set, including iterator
	
	public void test200() {
		assertTrue(m.entrySet().isEmpty());
	}
	
	public void test201() {
		m.put(k[2], v[0]);
		assertEquals(1, m.entrySet().size());
	}
	
	public void test202() {
		es = m.entrySet();
		m.put(k[2], v[0]);
		m.put(k[0], v[2]);
		m.put(k[2], v[2]);
		assertEquals(2, es.size());
	}
	
	public void test203() {
		m.put(k[2], v[1]);
		m.put(k[0], v[2]);
		es = m.entrySet();
		m.put(k[3], v[3]);
		assertEquals(3, es.size());
	}
	
	public void test204() {
		m.entrySet();
		m.put(k[2], v[4]);
		m.put(k[0], v[5]);
		m.put(k[4], v[6]);
		es = m.entrySet();
		m.put(k[6], v[7]);
		assertEquals(4, es.size());
	}
	
	public void test205() {
		es = m.entrySet();
		m.put(k[2], v[0]);
		m.put(k[0], v[5]);
		m.put(k[5], v[2]);
		assertSame(es, m.entrySet()); // should always return the same thing
	}
	
	public void test208() {
		es = m.entrySet();
		m.put(k[2], v[0]);
		assertException(UnsupportedOperationException.class, () -> es.add(e(k[2],v[8])));
	}
	
	
	public void test210() {
		es = m.entrySet();
		assertFalse(es.iterator().hasNext());
	}
	
	public void test211() {
		es = m.entrySet();
		m.put(k[2], v[1]);
		it = es.iterator();
		assertTrue(it.hasNext());
	}
	
	public void test212() {
		es = m.entrySet();
		m.put(k[2], v[1]);
		it = es.iterator();
		assertEquals(e(k[2],v[1]), it.next());
	}
	
	public void test213() {
		es = m.entrySet();
		m.put(k[2], v[1]);
		it = es.iterator();
		m.put(k[2], v[3]);
		assertEquals(e(k[2],v[3]), it.next());
	}
	
	public void test214() {
		es = m.entrySet();
		m.put(k[2], v[4]);
		it = es.iterator();
		it.next();
		assertFalse(it.hasNext());
	}
	
	public void test215() {
		m.put(k[1], v[2]);
		it = m.entrySet().iterator();
		it.next();
		m.put(k[1], v[4]);
		assertFalse(it.hasNext());
	}
	
	public void test218() {
		it = m.entrySet().iterator();
		assertException(NoSuchElementException.class, () -> it.next());
	}
	
	public void test219() {
		it = m.entrySet().iterator();
		m.put(k[2], v[8]);
		if (!failFast) return;
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
	}
	
	
	public void test223() {
		m.put(k[2], v[1]);
		es = m.entrySet();
		m.put(k[1], v[1]);
		it = es.iterator();
		assertEquals(v[1], it.next().getValue());
	}
	
	public void test224() {
		m.put(k[2], v[1]);
		m.put(k[4], v[2]);
		it = m.entrySet().iterator();
		assertNotNull(it.next());
		if (!sorted && !preserveOrder) return;
		assertEquals(e(k[4], v[2]), it.next());
	}
	
	public void test229() {
		m.put(k[2], v[2]);
		it = m.entrySet().iterator();
		assertTrue(it.hasNext());
		m.put(k[9], v[2]);
		if (!failFast) return;
		assertException(ConcurrentModificationException.class, () -> it.next());
	}
	

	public void test235() {
		es = m.entrySet();
		m.put(k[5], v[1]);
		m.put(k[4], v[2]);
		m.put(k[3], v[3]);
		it = es.iterator();
		Map.Entry<K,V> e = it.next();
		if (!sorted && !preserveOrder) return;
		if (sorted) {
			assertEquals(e(k[3],v[3]), e);
		} else if (preserveOrder) {
			assertEquals(e(k[5],v[1]), e);
		}
		e = it.next();
		assertEquals(e(k[4], v[2]), e);
		e = it.next();
		if (preserveOrder) {
			assertEquals(e(k[3],v[3]), e);
		} else if (sorted) {
			assertEquals(e(k[5],v[1]), e);
		}
	}
	
	public void test236() {
		es = m.entrySet();
		m.put(k[7], v[3]);
		m.put(k[8], v[2]);
		m.put(k[9], v[1]);
		it = es.iterator();
		Map.Entry<K,V> e = it.next();
		m.put(k[8], v[3]);
		m.put(k[9], v[3]);
		if (sorted || preserveOrder) {
			assertEquals(e(k[7], v[3]), e);
		}
		e = it.next();
		if (sorted || preserveOrder) {
			assertEquals(e(k[8], v[3]), e);
		} else {
			assertEquals(v[3], e.getValue());
		}
		e = it.next();
		if (sorted || preserveOrder) {
			assertEquals(e(k[9], v[3]), e);
		} else {
			assertEquals(v[3], e.getValue());
		}
		assertFalse(it.hasNext());
	}
	
	
	public void test240() {
		it = m.entrySet().iterator();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test241() {
		m.put(k[2], v[4]);
		it = m.entrySet().iterator();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test242() {
		m.put(k[2], v[4]);
		it = m.entrySet().iterator();
		it.next();
		it.remove();
		assertEquals(0, m.size());
	}
	
	public void test243() {
		m.put(k[2], v[4]);
		m.put(k[3], v[4]);
		it = m.entrySet().iterator();
		it.next();
		it.remove();
		assertTrue(it.hasNext());
	}
	
	public void test244() {
		m.put(k[2], v[4]);
		m.put(k[3], v[4]);
		it = m.entrySet().iterator();
		it.next();
		m.put(l[2], v[5]);
		m.put(l[3], v[5]);
		it.remove();
		assertEquals(1, m.size());
	}
	
	public void test245() {
		m.put(k[2], v[4]);
		m.put(k[4], v[5]);
		it = m.entrySet().iterator();
		it.next();
		m.put(k[5], v[2]);
		if (!failFast) return;
		assertException(ConcurrentModificationException.class, () -> it.remove());
	}
	
	public void test246() {
		m.put(k[2], v[4]);
		m.put(k[4], v[6]);
		m.put(k[6], v[2]);
		it = m.entrySet().iterator();
		K k = it.next().getKey();
		it.remove();
		assertNull(m.get(k));
	}
	
	public void test247() {
		m.put(k[2], v[4]);
		m.put(k[4], v[7]);
		m.put(k[7], v[2]);
		it = m.entrySet().iterator();
		it.next();
		it.next();
		assertTrue(it.hasNext());
		it.remove();
		assertTrue(it.hasNext());
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test248() {
		m.put(k[2], v[4]);
		m.put(k[4], v[8]);
		m.put(k[8], v[2]);
		it = m.entrySet().iterator();
		it.next();
		it.next();
		K k3 = it.next().getKey();
		assertFalse(it.hasNext());
		it.remove();
		assertNull(m.get(k3));
		assertEquals(2, m.size());
	}
	
	public void test249() {
		if (!permitNulls) return;
		m.put(k[2], null);
		m.put(k[4], null);
		m.put(k[9], null);
		it = m.entrySet().iterator();
		K k1 = it.next().getKey();
		it.remove();
		assertEquals(2, m.size());
		assertFalse(Objects.equals(k1, m.entrySet().iterator().next().getKey()));
	}
	
	public void test250() {
		m.put(k[2], v[5]);
		m.put(k[5], v[0]);
		m.put(k[0], v[2]);
		it = m.entrySet().iterator();
		it.next();
		assertException(IllegalStateException.class, () -> m.entrySet().iterator().remove());
		assertTrue(it.hasNext());
	}
	
	public void test251() {
		m.put(k[2], v[5]);
		m.put(k[5], v[1]);
		m.put(k[1], v[2]);
		it = m.entrySet().iterator();
		it.next();
		it.next();
		Iterator<?> it2 = m.entrySet().iterator();
		it2.next();
		assertTrue(it.hasNext());
		it2.remove();
		if (!failFast) return;
		assertException(ConcurrentModificationException.class, () -> it.next());
	}
	
	public void test253() {
		m.put(k[2], v[5]);
		m.put(k[5], v[3]);
		m.put(k[3], v[2]);
		it = m.entrySet().iterator();
		it.next();
		it.remove();
		it.next();
		it.next();
		it.remove();
		assertFalse(it.hasNext());
		assertEquals(1, m.size());
		if (!sorted) return;
		assertEquals(v[2], m.get(l[3]));
	}
	
	
	public void test260() {
		m.put(k[2], v[1]);
		@SuppressWarnings("unchecked")
		Map.Entry<K, V> x = (Map.Entry<K,V>)e(new Object(), null);
		assertFalse(m.entrySet().contains(x));
	}
	
	public void test261() {
		m.put(k[2], v[6]);
		assertFalse(m.entrySet().contains(e((K)null,(V)null)));
	}
	
	public void test262() {
		m.put(k[2], v[6]);
		m.put(k[6], v[2]);
		assertTrue(m.entrySet().contains(e(l[6], w[2])));
	}
	
	public void test263() {
		m.put(k[2], v[6]);
		m.put(k[6], v[3]);
		assertFalse(m.entrySet().contains(e(k[2], v[3])));
	}
	
	public void test264() {
		m.put(k[2], v[6]);
		m.put(k[6], v[4]);
		m.put(k[4], v[2]);
		assertTrue(m.entrySet().contains(e(l[2], w[6])));
		assertFalse(m.entrySet().contains(e(l[4], w[6])));
	}
	
	public void test265() {
		es = m.entrySet();
		m.put(k[2], v[6]);
		m.put(k[6], v[5]);
		m.put(k[5], v[2]);
		assertTrue(es.contains(e(l[6], w[5])));
		m.put(k[6], v[2]);
		assertFalse(es.contains(e(l[6], w[5])));
	}
	
	public void test266() {
		Map<K,V> other = create();
		other.put(k[2], v[6]);
		es = other.entrySet();
		assertFalse(es == m.entrySet());
		m.put(k[2], v[7]);
		es = m.entrySet();
		assertTrue(es.contains(e(l[2], w[7])));
		assertFalse(es.contains(e(l[2], w[6])));
	}
	
	public void test267() {
		m.put(k[2], v[6]);
		m.put(k[6], v[7]);
		m.put(k[7], v[2]);
		assertFalse(m.entrySet().contains(null));
	}
	
	public void test268() {
		m.put(k[2], v[6]);
		m.put(k[6], v[7]);
		m.put(k[7], v[2]);
		assertFalse(m.entrySet().contains(new Object()));
	}

	
	
	public void test280() {
		m.put(k[2], v[8]);
		@SuppressWarnings("unchecked")
		Map.Entry<K, V> x = (Map.Entry<K,V>)e(new Object(), null);
		assertFalse(m.entrySet().remove(x));
		assertFalse(m.isEmpty());
	}
	
	public void test281() {
		m.put(k[2], v[8]);
		assertFalse(m.entrySet().remove(e((K)null,(V)null)));
		assertEquals(1, m.entrySet().size());
	}
	
	public void test282() {
		m.put(k[2], v[8]);
		m.put(k[8], v[2]);
		assertTrue(m.entrySet().remove(e(l[8], w[2])));
		assertEquals(1, m.size());
		assertEquals(e(k[2], v[8]), m.entrySet().iterator().next());
	}
	
	public void test283() {
		m.put(k[2], v[8]);
		m.put(k[8], v[3]);
		assertFalse(m.entrySet().remove(e(k[2], v[3])));
		assertEquals(2, m.size());
	}
	
	public void test284() {
		m.put(k[2], v[8]);
		m.put(k[8], v[4]);
		m.put(k[4], v[2]);
		assertFalse(m.entrySet().remove(e(l[4], w[8])));
		assertTrue(m.entrySet().remove(e(l[2], w[8])));
		assertEquals(2, m.size());
	}
	
	public void test285() {
		es = m.entrySet();
		m.put(k[2], v[8]);
		m.put(k[8], v[5]);
		m.put(k[5], v[2]);
		assertTrue(es.remove(e(l[8], w[5])));
		m.put(k[5], v[8]);
		assertFalse(es.remove(e(l[5], w[2])));
		assertEquals(2, m.size());
	}
	
	public void test286() {
		Map<K,V> other = create();
		other.put(k[2], v[8]);
		es = other.entrySet();
		assertFalse(es == m.entrySet());
		m.put(k[2], v[6]);
		es = m.entrySet();
		assertFalse(es.remove(e(l[2], w[8])));
		assertTrue(es.remove(e(l[2], w[6])));
	}
	
	public void test287() {
		m.put(k[2], v[8]);
		m.put(k[8], v[7]);
		m.put(k[7], v[2]);
		assertFalse(m.entrySet().remove(null));
		assertEquals(3, m.size());
	}
	
	public void test288() {
		m.put(k[2], v[8]);
		m.put(k[8], v[8]);
		m.put(k[8], v[2]);
		assertFalse(m.entrySet().remove(new Object()));
		assertEquals(2, m.size());
	}
	
	public void test289() {
		m.put(k[2], v[8]);
		m.put(k[8], v[9]);
		m.put(k[9], v[2]);
		assertFalse(m.entrySet().remove(k[9]));
		assertEquals(3, m.entrySet().size());
	}
	
	
	public void test290() {
		m.put(k[2], v[9]);
		Entry<K, V> entry = m.entrySet().iterator().next();
		assertTrue(entry.equals(e(l[2], w[9])));
	}

	public void test291() {
		m.put(k[2], v[9]);
		Entry<K, V> entry = m.entrySet().iterator().next();
		assertFalse(entry.equals(new Object()));
		assertFalse(entry.equals(null));
	}
	
	public void test292() {
		m.put(k[2], v[9]);
		Entry<K, V> entry = m.entrySet().iterator().next();
		assertFalse(entry.equals(e((K)null,v[9])));
		assertFalse(entry.equals(e(k[2], (V)null)));
	}
	
	public void test293() {
		m.put(k[2], v[9]);
		Entry<K, V> entry = m.entrySet().iterator().next();
		assertFalse(entry.equals(e(k[2], v[8])));
		assertFalse(entry.equals(e(k[9], v[9])));
	}

	public void test294() {
		m.put(k[9], v[4]);
		Entry<K, V> entry = m.entrySet().iterator().next();
		assertEquals(e(l[9], w[4]).hashCode(), entry.hashCode());
	}

	public void test295() {
		m.put(k[9], v[5]);
		Entry<K, V> entry = m.entrySet().iterator().next();
		assertEquals(k[9], entry.getKey());
		assertEquals(v[5], entry.getValue());
	}
	
	public void test296() {
		m.put(k[2], v[9]);
		m.put(k[9], v[6]);
		it = m.entrySet().iterator();
		Entry<K, V> entry = it.next();
		entry.setValue(v[0]);
		assertEquals(2, m.size());
		assertTrue(it.hasNext());
	}
	
	public void test297() {
		m.put(k[2], v[9]);
		m.put(k[9], v[7]);
		Entry<K, V> entry = m.entrySet().iterator().next();
		K key = entry.getKey();
		entry.setValue(v[1]);
		assertEquals(v[1], m.get(key));
	}
	
	public void test298() {
		m.put(k[2], v[9]);
		m.put(k[9], v[8]);
		it = m.entrySet().iterator();
		it.next();
		Entry<K, V> entry = it.next();
		V value = entry.getValue();
		assertEquals(value, entry.setValue(v[3]));
	}
	
	public void test299() {
		m.put(k[2], v[9]);
		m.put(k[9], v[9]);
		m.put(k[0], v[2]);
		it = m.entrySet().iterator();
		it.next();
		Entry<K, V> entry = it.next();
		K key = entry.getKey();
		entry.setValue(v[3]);
		assertEquals(key, entry.getKey());
		assertEquals(v[3], entry.getValue());
	}
	
	
	/// test3xx: tests of remove
	
	public void test300() {
		assertNull(m.remove(k[3]));
	}
	
	public void test301() {
		assertNull(m.remove((Object)v[3]));
	}
	
	public void test302() {
		assertNull(m.remove(new Object()));
	}
	
	
	public void test310() {
		m.put(k[3], v[0]);
		assertEquals(v[0], m.remove(l[3]));
	}
	
	public void test311() {
		m.put(k[3], v[1]);
		m.remove(l[3]);
		assertTrue(m.isEmpty());
	}
	
	public void test312() {
		m.put(k[3], v[2]);
		assertNull(m.remove(l[2]));
		assertFalse(m.isEmpty());
	}
	
	public void test313() {
		m.put(k[3], v[3]);
		assertEquals(v[3], m.remove(k[3]));
		assertTrue(m.isEmpty());
	}
	
	public void test314() {
		m.put(k[3], v[4]);
		m.remove(l[3]);
		assertNull(m.remove(l[3]));
	}
	
	public void test315() {
		m.put(k[3], v[5]);
		assertNull(m.remove((Object)e(k[3], v[5])));
		assertEquals(1, m.size());
	}
	
	public void test320() {
		m.put(k[3], v[2]);
		m.put(k[2], v[0]);
		assertEquals(v[2], m.remove(l[3]));
		assertEquals(1, m.size());
	}
	
	public void test321() {
		m.put(k[3], v[2]);
		m.put(k[2], v[1]);
		assertEquals(v[1], m.remove(l[2]));
		assertEquals(v[2], m.get(l[3]));
	}
	
	public void test322() {
		m.put(k[3], v[2]);
		m.put(k[2], v[3]);
		assertNull(m.remove(l[1]));
	}
	
	public void test323() {
		m.put(k[2], v[3]);
		m.put(k[3], v[2]);
		assertEquals(v[3], m.remove(l[2]));
		assertNull(m.get(l[2]));
	}
	
	public void test324() {
		m.put(k[2], v[4]);
		m.put(k[3], v[2]);
		assertEquals(v[2], m.remove(l[3]));
		assertEquals(e(k[2], v[4]), m.entrySet().iterator().next());
	}
	
	public void test325() {
		m.put(k[2], v[5]);
		m.put(k[3], v[2]);
		assertNull(m.remove(k[5]));
	}
	
	public void test326() {
		m.put(k[2], v[6]);
		m.put(k[3], v[2]);
		assertEquals(v[6], m.remove(l[2]));
		assertEquals(v[2], m.remove(l[3]));
		assertTrue(m.isEmpty());
	}
	
	public void test330() {
		m.put(k[3], v[3]);
		m.put(k[0], v[3]);
		m.put(k[3], v[0]);
		assertEquals(v[0], m.remove(l[3]));
		assertEquals(1, m.size());
	}
	
	public void test331() {
		m.put(k[3], v[3]);
		m.put(k[1], v[3]);
		m.put(k[7], v[1]);
		assertEquals(v[3], m.remove(l[1]));
		assertEquals(2, m.size());
	}
	
	public void test332() {
		m.put(k[3], v[3]);
		m.put(k[2], v[3]);
		m.put(k[8], v[2]);
		assertEquals(v[2], m.remove(l[8]));
		assertEquals(v[3], m.get(l[3]));
		assertEquals(v[3], m.get(l[2]));
	}
	
	public void test333() {
		m.put(k[3], v[1]);
		m.put(k[6], v[2]);
		m.put(k[9], v[3]);
		it = m.entrySet().iterator();
		assertEquals(v[3], m.remove(l[9]));
		if (!failFast) return;
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
	}
	
	public void test340() {
		m.put(k[3], v[1]);
		m.put(k[4], v[2]);
		m.put(k[0], v[3]);
		m.clear();
		assertTrue(m.isEmpty());
	}
	
	public void test341() {
		it = m.entrySet().iterator();
		m.clear();
		assertFalse(it.hasNext());
	}
	
	public void test342() {
		m.put(k[3], v[4]);
		m.put(k[4], v[2]);
		it = m.entrySet().iterator();
		assertTrue(it.hasNext());
		m.clear();
		if (!failFast) return;
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
	}
	
	
	/// test35x+: tests of containsKey
	
	public void test350() {
		assertFalse(m.containsKey(k[3]));
	}
	
	public void test351() {
		m.put(k[3], v[5]);
		assertTrue(m.containsKey(l[3]));
	}
	
	public void test352() {
		m.put(k[5], v[2]);
		assertFalse(m.containsKey(l[1]));
	}
	
	public void test353() {
		m.put(k[3], v[5]);
		m.put(k[5], v[3]);
		assertTrue(m.containsKey(l[3]));
	}
	
	public void test354() {
		m.put(k[5], v[4]);
		m.put(k[3], v[5]);
		assertTrue(m.containsKey(l[3]));
	}
	
	public void test355() {
		m.put(k[3], v[5]);
		m.put(k[0], v[5]);
		assertFalse(m.containsKey(k[5]));
	}
	
	public void test356() {
		m.put(k[3], v[5]);
		m.put(k[6], v[5]);
		assertTrue(m.containsKey(l[6]));
	}
	
	public void test357() {
		m.put(k[3], v[4]);
		m.put(k[5], v[5]);
		m.put(k[7], v[6]);
		assertTrue(m.containsKey(k[5]));
	}
	
	public void test358() {
		m.put(k[3], v[5]);
		m.put(k[0], v[8]);
		m.put(k[8], v[3]);
		assertTrue(m.containsKey(k[3]));
	}
	
	public void test359() {
		m.put(k[3], v[5]);
		m.put(k[5], v[0]);
		m.put(k[9], v[3]);
		assertTrue(m.containsKey(k[9]));
	}
	
	public void test360() {
		m.put(k[3], v[0]);
		m.put(k[3], v[1]);
		m.put(k[6], v[2]);
		assertTrue(m.containsKey(l[3]));
		assertTrue(m.containsKey(l[6]));
	}
	
	public void test373() {
		m.put(k[1], v[0]);
		m.put(k[2], v[1]);
		m.put(k[3], v[2]);
		assertTrue(m.containsKey(l[1]));
		assertTrue(m.containsKey(l[2]));
		assertTrue(m.containsKey(l[3]));
		assertEquals(3, m.size());
	}
	
	public void test389() {
		m.put(k[3], v[0]);
		m.put(k[6], v[1]);
		m.put(k[9], v[2]);
		m.put(k[2], v[3]);
		m.put(k[5], v[4]);
		m.put(k[8], v[5]);
		m.put(k[1], v[6]);
		m.put(k[4], v[7]);
		m.put(k[7], v[8]);
		assertFalse(m.containsKey(l[0]));
		assertTrue(m.containsKey(l[1]));
		assertTrue(m.containsKey(l[2]));
		assertTrue(m.containsKey(l[3]));
		assertTrue(m.containsKey(l[4]));
		assertTrue(m.containsKey(l[5]));
		assertTrue(m.containsKey(l[6]));
		assertTrue(m.containsKey(l[7]));
		assertTrue(m.containsKey(l[8]));
		assertTrue(m.containsKey(l[9]));
	}
	
	public void test390() {
		m.put(k[3], v[9]);
		m.put(k[9], v[0]);
		m.put(k[0], v[3]);
		assertFalse(m.containsKey(null));
	}
	
	public void test391() {
		m.put(k[3], v[9]);
		m.put(k[9], v[1]);
		m.put(k[1], v[3]);
		assertTrue(m.containsKey(l[1]));
	}
	
	public void test392() {
		m.put(k[3], v[9]);
		m.put(k[9], v[2]);
		m.put(k[2], v[3]);
		assertFalse(m.containsKey(new Object()));
	}

		
	/// test40x: tests of keySet
	
	public void test400() {
		Set<K> s = m.keySet();
		assertTrue(s.isEmpty());
	}
	
	public void test401() {
		Set<K> s = m.keySet();
		assertFalse(s.contains(k[4]));
	}
	
	public void test402() {
		Set<K> s = m.keySet();
		m.put(k[4], v[0]);
		m.put(k[0], v[2]);
		assertEquals(2, s.size());
	}
	
	public void test403() {
		Set<K> s = m.keySet();
		m.put(k[4], v[0]);
		m.put(k[0], v[3]);
		s.size();
		m.put(k[3], v[4]);
		assertEquals(3, s.size());
	}
	
	public void test405() {
		Set<K> s = m.keySet();
		m.put(k[4], v[0]);
		assertSame(s, m.keySet()); // should cache result
	}
	
	public void test406() {
		Set<K> s1 = create().keySet();
		m.put(k[4], v[0]);
		assertEquals(1, m.keySet().size());
		assertEquals(0, s1.size());
	}
	
	public void test408() {
		m.put(k[4], v[0]);
		Set<K> s = m.keySet();
		if (permitNulls) return;
		assertException(UnsupportedOperationException.class, () -> s.add(k[8]));
	}
	
	public void test410() {
		Iterator<K> it = m.keySet().iterator();
		assertFalse(it.hasNext());
	}
	
	public void test411() {
		m.put(k[4], v[1]);
		Iterator<K> it = m.keySet().iterator();
		assertTrue(it.hasNext());
		assertEquals(k[4], it.next());
		assertFalse(it.hasNext());
	}
	
	public void test412() {
		m.put(k[4], v[1]);
		m.put(k[1], v[2]);
		Iterator<K> it = m.keySet().iterator();
		K k1 = it.next();
		K k2 = it.next();
		assertFalse(it.hasNext());
		if (preserveOrder) {
			assertEquals(k[4], k1);
			assertEquals(k[1], k2);
		} else if (sorted) {
			assertEquals(k[1], k1);
			assertEquals(k[4], k2);
		} else {
			assertTrue(
					Objects.equals(k[1], k1) && Objects.equals(k[4], k2) ||
					Objects.equals(k[1], k2) && Objects.equals(k[4], k1));
		}
	}
	
	public void test413() {
		m.put(k[4], v[1]);
		m.put(k[1], v[3]);
		m.put(k[3], v[4]);
		Iterator<K> it = m.keySet().iterator();
		it.next();
		it.next();
		it.next();
		assertException(NoSuchElementException.class, () -> it.next());
	}
	
	public void test414() {
		m.put(k[4], v[1]);
		m.put(k[1], v[4]);
		Iterator<K> it = m.keySet().iterator();
		m.put(l[4], w[4]);
		it.next();
		assertTrue(it.hasNext());
	}
	
	public void test415() {
		m.put(k[4], v[1]);
		Iterator<K> it = m.keySet().iterator();
		it.next();
		it.remove();
		assertFalse(it.hasNext());
		assertEquals(0, m.size());
	}
	
	public void test416() {
		m.put(k[4], v[1]);
		m.put(k[1], v[2]);
		m.put(k[6], v[3]);
		Iterator<K> it = m.keySet().iterator();
		it.next();
		it.next();
		it.remove();
		assertTrue(it.hasNext());
		assertEquals(2, m.size());
	}
	
	public void test417() {
		m.put(k[4], v[1]);
		m.put(k[1], v[2]);
		m.put(k[8], v[3]);
		it = m.entrySet().iterator();
		it.next();
		it.next();
		it.remove();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test418() {
		m.put(k[4], v[1]);
		m.put(k[1], v[2]);
		m.put(k[8], v[3]);
		it = m.entrySet().iterator();
		it.next();
		assertTrue(it.hasNext());
		Iterator<K> it2 = m.keySet().iterator();
		it2.next();
		it2.next();
		it2.next();
		it2.remove();
		assertException(ConcurrentModificationException.class, () -> it.remove());
	}
	
	public void test419() {
		m.put(k[4], v[1]);
		m.put(k[1], v[9]);
		m.put(k[9], v[4]);
		Iterator<K> it = m.keySet().iterator();
		it.next();
		it.next();
		it.next();
		m.put(k[3], v[3]);
		assertException(ConcurrentModificationException.class, () -> it.next());
	}

	public void test420() {
		Set<K> s = m.keySet();
		assertFalse(s.contains(k[0]));
	}
	
	public void test421() {
		Set<K> s = m.keySet();
		m.put(k[4], v[2]);
		assertTrue(s.contains(l[4]));
	} 
	
	public void test422() {
		Set<K> s = m.keySet();
		m.put(k[4], v[0]);
		assertFalse(s.contains(l[2]));
	}
	
	public void test423() {
		m.put(k[2], v[6]);
		m.put(k[6], v[3]);
		assertFalse(m.keySet().contains(e(k[2], v[6])));
	}
	
	public void test424() {
		m.put(k[2], v[6]);
		m.put(k[6], v[4]);
		m.put(k[4], v[2]);
		assertTrue(m.keySet().contains(l[2]));
	}
	
	public void test425() {
		Set<K> ks = m.keySet();
		m.put(k[2], v[6]);
		m.put(k[6], v[5]);
		m.put(k[5], v[2]);
		assertTrue(ks.contains(l[6]));
		m.remove(k[6]);
		assertFalse(ks.contains(l[6]));
	}
	
	public void test426() {
		Map<K,V> other = create();
		other.put(k[2], v[6]);
		Set<K> ks = other.keySet();
		assertFalse(ks == m.keySet());
		m.put(k[4], v[2]);
		ks = m.keySet();
		assertTrue(ks.contains(l[4]));
		assertFalse(ks.contains(l[2]));
	}
	
	public void test427() {
		m.put(k[2], v[6]);
		m.put(k[6], v[7]);
		m.put(k[7], v[2]);
		assertFalse(m.keySet().contains(null));
	}
	
	public void test428() {
		m.put(k[2], v[6]);
		m.put(k[6], v[7]);
		m.put(k[7], v[2]);
		assertFalse(m.keySet().contains(new Object()));
	}

	public void test430() {
		assertFalse(m.keySet().remove(l[4]));
		assertTrue(m.isEmpty());
	}

	public void test432() {
		m.put(k[4], v[3]);
		m.put(k[3], v[2]);
		assertTrue(m.keySet().remove(l[4]));
		assertEquals(1, m.size());
		assertEquals(k[3], m.keySet().iterator().next());
	}
	
	public void test433() {
		m.put(k[4], v[3]);
		m.put(k[3], v[3]);
		assertFalse(m.keySet().remove(k[2]));
		assertEquals(2, m.size());
	}
	
	public void test434() {
		m.put(k[4], v[3]);
		m.put(k[3], v[4]);
		m.put(k[7], v[5]);
		assertFalse(m.keySet().remove((Object)e(l[4], w[3])));
		assertTrue(m.keySet().remove(l[3]));
		assertEquals(2, m.size());
	}
	
	public void test435() {
		Set<K> ks = m.keySet();
		m.put(k[4], v[3]);
		m.put(k[3], v[5]);
		m.put(k[5], v[4]);
		assertTrue(ks.remove(l[3]));
		m.remove(k[5]);
		assertFalse(ks.remove(l[5]));
		assertEquals(1, m.size());
	}
	
	public void test436() {
		Map<K,V> other = create();
		other.put(k[4], v[3]);
		Set<K> ks = other.keySet();
		assertFalse(ks == m.keySet());
		m.put(k[3], v[6]);
		ks = m.keySet();
		assertFalse(ks.remove(l[4]));
		assertTrue(ks.remove(l[3]));
	}
	
	public void test437() {
		m.put(k[4], v[3]);
		m.put(k[3], v[7]);
		m.put(k[7], v[4]);
		assertFalse(m.keySet().remove(null));
		assertEquals(3, m.size());
	}
	
	public void test438() {
		m.put(k[4], v[3]);
		m.put(k[3], v[8]);
		m.put(k[8], v[4]);
		assertFalse(m.keySet().remove(new Object()));
		assertEquals(3, m.size());
	}
	
	public void test439() {
		m.put(k[2], v[8]);
		m.put(k[8], v[9]);
		m.put(k[9], v[2]);
		assertFalse(m.keySet().remove(k[7]));
		assertEquals(3, m.keySet().size());
	}


	
	/// test45x: tests of values
		
	public void test450() {
		Collection<V> s = m.values();
		assertTrue(s.isEmpty());
	}
	
	public void test451() {
		Collection<V> s = m.values();
		assertFalse(s.contains(v[4]));
	}
	
	public void test452() {
		Collection<V> s = m.values();
		m.put(k[4], v[0]);
		m.put(k[0], v[2]);
		assertEquals(2, s.size());
	}
	
	public void test453() {
		Collection<V> s = m.values();
		m.put(k[4], v[0]);
		m.put(k[0], v[3]);
		s.size();
		m.put(k[3], v[4]);
		assertEquals(3, s.size());
	}
	
	public void test455() {
		Collection<V> s = m.values();
		m.put(k[4], v[0]);
		assertSame(s, m.values()); // should cache result
	}
	
	public void test456() {
		Collection<V> s1 = create().values();
		m.put(k[4], v[0]);
		assertEquals(1, m.values().size());
		assertEquals(0, s1.size());
	}
	
	public void test458() {
		m.put(k[4], v[0]);
		Collection<V> s = m.values();
		assertException(UnsupportedOperationException.class, () -> s.add(v[8]));
	}
	
	public void test460() {
		Iterator<V> it = m.values().iterator();
		assertFalse(it.hasNext());
	}
	
	public void test461() {
		m.put(k[4], v[1]);
		Iterator<V> it = m.values().iterator();
		assertTrue(it.hasNext());
		assertEquals(v[1], it.next());
		assertFalse(it.hasNext());
	}
	
	public void test462() {
		m.put(k[4], v[1]);
		m.put(k[6], v[2]);
		Iterator<V> it = m.values().iterator();
		V v1 = it.next();
		V v2 = it.next();
		assertFalse(it.hasNext());
		if (preserveOrder || sorted) {
			assertEquals(v[1], v1);
			assertEquals(v[2], v2);
		} else {
			assertTrue(
					Objects.equals(v[1], v1) && Objects.equals(v[2], v2) ||
					Objects.equals(v[1], v2) && Objects.equals(v[2], v1));
		}
	}
	
	public void test463() {
		m.put(k[4], v[6]);
		m.put(k[6], v[3]);
		m.put(k[3], v[4]);
		Iterator<V> it = m.values().iterator();
		it.next();
		it.next();
		it.next();
		assertException(NoSuchElementException.class, () -> it.next());
	}
	
	public void test464() {
		m.put(k[4], v[6]);
		m.put(k[6], v[4]);
		Iterator<V> it = m.values().iterator();
		m.put(l[4], w[4]);
		it.next();
		assertTrue(it.hasNext());
	}
	
	public void test465() {
		m.put(k[4], v[1]);
		Iterator<V> it = m.values().iterator();
		it.next();
		it.remove();
		assertFalse(it.hasNext());
		assertEquals(0, m.size());
	}
	
	public void test466() {
		m.put(k[4], v[4]);
		m.put(k[0], v[4]);
		m.put(k[6], v[4]);
		Iterator<V> it = m.values().iterator();
		assertEquals(v[4], it.next());
		assertEquals(v[4], it.next());
		it.remove();
		assertTrue(it.hasNext());
		assertEquals(2, m.size());
	}
	
	public void test467() {
		m.put(k[4], v[1]);
		m.put(k[6], v[2]);
		m.put(k[7], v[3]);
		it = m.entrySet().iterator();
		it.next();
		it.next();
		it.remove();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test468() {
		m.put(k[4], v[1]);
		m.put(k[6], v[2]);
		m.put(k[8], v[3]);
		it = m.entrySet().iterator();
		it.next();
		assertTrue(it.hasNext());
		Iterator<V> it2 = m.values().iterator();
		it2.next();
		it2.next();
		it2.next();
		it2.remove();
		assertException(ConcurrentModificationException.class, () -> it.remove());
	}
	
	public void test469() {
		m.put(k[4], v[6]);
		m.put(k[6], v[9]);
		m.put(k[9], v[4]);
		Iterator<V> it = m.values().iterator();
		it.next();
		it.next();
		it.next();
		m.put(k[3], v[3]);
		assertException(ConcurrentModificationException.class, () -> it.next());
	}

	public void test470() {
		Collection<V> s = m.values();
		assertFalse(s.contains(v[0]));
	}
	
	public void test471() {
		Collection<V> s = m.values();
		m.put(k[4], v[7]);
		assertTrue(s.contains(w[7]));
	} 
	
	public void test472() {
		Collection<V> s = m.values();
		m.put(k[4], v[2]);
		assertFalse(s.contains(w[4]));
	}
	
	public void test473() {
		m.put(k[2], v[6]);
		m.put(k[6], v[3]);
		assertFalse(m.values().contains((Object)e(k[2], v[6])));
	}
	
	public void test474() {
		m.put(k[2], v[6]);
		m.put(k[6], v[4]);
		m.put(k[4], v[2]);
		assertTrue(m.values().contains(w[6]));
	}
	
	public void test475() {
		Collection<V> vs = m.values();
		m.put(k[2], v[6]);
		m.put(k[6], v[5]);
		m.put(k[5], v[2]);
		assertTrue(vs.contains(w[5]));
		m.remove(k[6]);
		assertFalse(vs.contains(w[5]));
	}
	
	public void test476() {
		Map<K,V> other = create();
		other.put(k[7], v[6]);
		Collection<V> vs = other.values();
		assertFalse(vs == m.values());
		m.put(k[4], v[7]);
		vs = m.values();
		assertTrue(vs.contains(w[7]));
		assertFalse(vs.contains(w[6]));
	}
	
	public void test477() {
		m.put(k[4], v[7]);
		m.put(k[1], v[7]);
		m.put(k[7], v[2]);
		assertFalse(m.values().contains(null));
	}
	
	public void test478() {
		m.put(k[4], v[7]);
		m.put(k[7], v[8]);
		m.put(k[8], v[4]);
		assertFalse(m.values().contains(new Object()));
	}

	public void test480() {
		assertFalse(m.values().remove(w[4]));
		assertTrue(m.isEmpty());
	}

	public void test482() {
		m.put(k[4], v[8]);
		m.put(k[8], v[2]);
		assertTrue(m.values().remove(w[8]));
		assertEquals(1, m.size());
		assertEquals(v[2], m.values().iterator().next());
	}
	
	public void test483() {
		m.put(k[4], v[8]);
		m.put(k[8], v[3]);
		assertFalse(m.values().remove(v[2]));
		assertEquals(2, m.size());
	}
	
	public void test484() {
		m.put(k[4], v[8]);
		m.put(k[8], v[4]);
		m.put(k[7], v[5]);
		assertFalse(m.values().remove((Object)e(l[4], w[8])));
		assertTrue(m.values().remove(w[8]));
		assertEquals(2, m.size());
	}
	
	public void test485() {
		Collection<V> vs = m.values();
		m.put(k[4], v[8]);
		m.put(k[8], v[5]);
		m.put(k[5], v[4]);
		assertTrue(vs.remove(w[8]));
		m.remove(k[5]);
		assertFalse(vs.remove(v[4]));
		assertEquals(1, m.size());
	}
	
	public void test486() {
		Map<K,V> other = create();
		other.put(k[4], v[8]);
		Collection<V> vs = other.values();
		assertFalse(vs == m.values());
		m.put(k[8], v[6]);
		vs = m.values();
		assertFalse(vs.remove(w[8]));
		assertTrue(vs.remove(w[6]));
	}
	
	public void test487() {
		m.put(k[4], v[8]);
		m.put(k[8], v[7]);
		m.put(k[7], v[4]);
		assertFalse(m.values().remove(null));
		assertEquals(3, m.size());
	}
	
	public void test488() {
		m.put(k[4], v[8]);
		m.put(k[8], v[8]);
		m.put(k[8], v[4]);
		assertFalse(m.values().remove(new Object()));
		assertEquals(2, m.size());
	}
	
	public void test489() {
		m.put(k[4], v[8]);
		m.put(k[8], v[9]);
		m.put(k[9], v[4]);
		assertFalse(m.values().remove(v[7]));
		assertEquals(3, m.values().size());
	}

}
