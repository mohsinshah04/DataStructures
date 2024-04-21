import java.net.URI;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import edu.uwm.cs351.FixedObject;
import edu.uwm.cs351.Resource;


public class TestEfficiency extends TestCase {
	Set<Resource> set;
    private Random random;
    
    private static final int POWER = 20; // 1 million entries
    private static final int TESTS = 1_000_000;
    
	protected static final Comparator<Integer> intForward = new Comparator<Integer>(){
		public int compare(Integer arg0, Integer arg1) {
			return arg0 == null?arg1==null?0:-1:arg0.compareTo(arg1);
		}
	};
	protected static final Comparator<Integer> intBackward = new Comparator<Integer>(){
		public int compare(Integer arg0, Integer arg1) {
			return arg0 == null?arg1==null?0:-1:arg1.compareTo(arg0);
		}
	};
    
	protected URI u(int i) {
		return URI.create("test:" + i);
	}

	protected Resource r(int i) {
		return new FixedObject(u(i),i);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		random = new Random();
		try {
			assert set.size() == TESTS : "cannot run test with assertions enabled";
		} catch (NullPointerException ex) {
			throw new IllegalStateException("Cannot run test with assertions enabled");
		}
		set = new Resource.Table();
		int max = (1 << (POWER)); 
		for (int i=0; i < max; i += 2) {
			set.add(r(i));
		}
    	assertEquals((1<<(POWER-1)),set.size());
    }
    
    @Override
    protected void tearDown() {
    	set = null;
    }


    public void testA(){
    	for (int i=0; i < TESTS; i += 2) {
    		assertEquals(r(i), ((Resource.Table)set).get(u(i)));
    	}
    }
    
    public void testB() {
    	for (int i=0; i < TESTS; ++i) {
    		int j = random.nextInt(1<<(POWER)-1) + 1;
    		assertEquals(j%2 == 0,set.contains(r(j)));
    	}
    }
    
    public void testC(){
    	int size = set.size();
    	int max = 1 << POWER;
    	Set<Integer> ref = new HashSet<Integer>();
    	for (int i=0; i < max; i+= 2) {
    		ref.add(i);
    	}
		for (int i=0; i < TESTS; ++i) {
    		int j = random.nextInt(1<<(POWER)-1) + 1;
    		if (ref.remove(j)) {
    			assertTrue(set.remove(r(j)));
    			--size;
    		} else {
    			assertFalse(set.remove(r(j)));
    		}
    	}
		assertEquals(size, set.size());
    }
        
    public void testD() {
    	Set<Integer> found = new HashSet<>();
    	for (Resource r : set) {
    		Integer c = (Integer)((FixedObject)r).getContents();
    		assertTrue(found.add(c)); // no duplicate visits
    	}
    	assertEquals(1<<(POWER-1), found.size()); // everything visited
    }
    
    public void testE() {
     	for(int i = 0; i < (TESTS >> 4); ++i){
    		int j = 0;
    		for(Iterator<Resource> it = set.iterator(); j < 16; ++j){
    			it.next();
    		}
    	}
    	assertTrue(true);
    }

    public void testF() {
    	set.clear();
    	for (int i=0; i < TESTS; ++i) {
    		assertTrue(set.add(r(i)));
    	}
    	for (int i=0; i < TESTS; ++i) {
    		set.clear();
    	}
    }
}
