import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.Pixel;
import edu.uwm.cs351.DynamicRaster;
import junit.framework.TestCase;


public class TestInvariant extends TestCase {
	protected DynamicRaster.Spy spy = new DynamicRaster.Spy();
    protected int reports;
    
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
    
	DynamicRaster dr;
	
	Pixel t1 = new Pixel(1,4,Color.RED);
	Pixel t2 = new Pixel(2,3,Color.BLUE);
	Pixel t2a= new Pixel(2,3,Color.PINK);
	Pixel t3 = new Pixel(3,1,Color.GREEN);
	Pixel t4 = new Pixel(3,2,Color.YELLOW);
	Pixel t5 = new Pixel(4,0,Color.GRAY);
	
	public void testA() {
		dr = spy.create(null, 0, null);
		assertReporting(true, () -> spy.wellFormed(dr));		
	}
	
	public void testB() {
		dr = spy.create(null, 1, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
		
	public void testC() {
		DynamicRaster.Spy.Node h = spy.newNode(t1,null);
		dr = spy.create(h, 1, null);
		assertReporting(true, () -> spy.wellFormed(dr));
	}
	
	public void testD() {
		DynamicRaster.Spy.Node h = spy.newNode(null,null);
		dr = spy.create(h, 1, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
	
	public void testE() {
		DynamicRaster.Spy.Node h = spy.newNode(null,null);
		dr = spy.create(h, 0, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
	
	public void testF() {
		DynamicRaster.Spy.Node h = spy.newNode(t1,null);
		dr = spy.create(h, 0, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
	
	public void testG() {
		DynamicRaster.Spy.Node h = spy.newNode(t1,null);
		dr = spy.create(h, 2, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
	
	public void testH() {
		DynamicRaster.Spy.Node h = spy.newNode(t2,null);
		
		dr = spy.create(h, 1, h);
		assertReporting(true, () -> spy.wellFormed(dr));
	}
	
	public void testI() {
		DynamicRaster.Spy.Node h = spy.newNode(t2,null);
		DynamicRaster.Spy.Node impostor = spy.newNode(t2,null);
		
		dr = spy.create(h, 1, impostor);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testJ() {
		DynamicRaster.Spy.Node h = spy.newNode(t2,null);
		DynamicRaster.Spy.Node impostor = spy.newNode(t1,h);
		
		dr = spy.create(h, 1, impostor);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	/* superfluous
	public void testK() {
		DynamicRaster.Spy.Node h = spy.newNode(t2,null);
		spy.setNext(h, h);
		
		// These tests involve cycles.  
		// For Homework #4,  we have done the check for you.  
		// So these tests should all pass unless you broke them.
		
		ts = spy.create(h, 0, null);
		assertReporting(false, () -> spy.wellFormed(ts));
		ts = spy.create(h, 1, null);
		assertReporting(false, () -> spy.wellFormed(ts));
		ts = spy.create(h, 2, null);
		assertReporting(false, () -> spy.wellFormed(ts));
		ts = spy.create(h, -1, null);
		assertReporting(false, () -> spy.wellFormed(ts));
	}
	*/
	
	public void testL() {
		DynamicRaster.Spy.Node t = spy.newNode(t2,null);
		DynamicRaster.Spy.Node h = spy.newNode(t1,t);
		
		dr = spy.create(h, 2, null);
		assertReporting(true, () -> spy.wellFormed(dr));
	}
	
	public void testM() {
		DynamicRaster.Spy.Node t = spy.newNode(t2,null);
		DynamicRaster.Spy.Node h = spy.newNode(t1,t);
		
		dr = spy.create(h, 2, h);
		assertReporting(true, () -> spy.wellFormed(dr));
	}

	public void testN() {
		DynamicRaster.Spy.Node t = spy.newNode(t2,null);
		DynamicRaster.Spy.Node h = spy.newNode(t1,t);
		
		dr = spy.create(h, 2, t);
		assertReporting(true, () -> spy.wellFormed(dr));
	}

	public void testO() {
		DynamicRaster.Spy.Node t = spy.newNode(t2,null);
		DynamicRaster.Spy.Node h = spy.newNode(t1,t);
		
		dr = spy.create(h, 1, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 1, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 1, t);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testP() {
		DynamicRaster.Spy.Node t = spy.newNode(t2,null);
		DynamicRaster.Spy.Node h = spy.newNode(t1,t);
		
		dr = spy.create(h, 3, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, t);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testQ() {
		DynamicRaster.Spy.Node t = spy.newNode(t2,null);
		DynamicRaster.Spy.Node h = spy.newNode(t1,t);
		
		// impostor nodes
		DynamicRaster.Spy.Node it = spy.newNode(t2,null);
		DynamicRaster.Spy.Node ih = spy.newNode(t1,t);
		
		dr = spy.create(h, 2, ih);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 2, it);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testR() {
		DynamicRaster.Spy.Node t = spy.newNode(t1,null);
		DynamicRaster.Spy.Node h = spy.newNode(t2,t);
		
		dr = spy.create(h, 2, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 2, t);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 2, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testS() {
		DynamicRaster.Spy.Node t = spy.newNode(t2a,null);
		DynamicRaster.Spy.Node h = spy.newNode(t2,t);
		
		dr = spy.create(h, 2, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 2, t);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 2, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testT() {
		DynamicRaster.Spy.Node t = spy.newNode(null,null);
		DynamicRaster.Spy.Node h = spy.newNode(t2,t);
		
		dr = spy.create(h, 2, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 2, t);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 2, null);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
	
	public void testU() {
		DynamicRaster.Spy.Node t = spy.newNode(t3,null);
		DynamicRaster.Spy.Node n = spy.newNode(t2,t);
		DynamicRaster.Spy.Node h = spy.newNode(t1,n);
		
		dr = spy.create(h, 3, null);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, h);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, n);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, t);
		assertReporting(true, () -> spy.wellFormed(dr));

		// impostor nodes
		DynamicRaster.Spy.Node i1 = spy.newNode(t3, null);
		DynamicRaster.Spy.Node i2 = spy.newNode(t2, t);
		DynamicRaster.Spy.Node i3 = spy.newNode(t1, n);

		dr = spy.create(h, 2, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 4, null);
		assertReporting(false, () -> spy.wellFormed(dr));

		dr = spy.create(h, 3, i1);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, i2);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, i3);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
	
	public void testV() {
		DynamicRaster.Spy.Node t = spy.newNode(t3,null);
		DynamicRaster.Spy.Node n = spy.newNode(t4,t);
		DynamicRaster.Spy.Node h = spy.newNode(t1,n);
		
		dr = spy.create(h, 3, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, n);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, t);
		assertReporting(false, () -> spy.wellFormed(dr));
	}
	
	public void testW() {
		DynamicRaster.Spy.Node t = spy.newNode(null,null);
		DynamicRaster.Spy.Node n = spy.newNode(t4,t);
		DynamicRaster.Spy.Node h = spy.newNode(t1,n);
		
		dr = spy.create(h, 3, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, n);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, t);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testX() {
		DynamicRaster.Spy.Node t = spy.newNode(t2,null);
		DynamicRaster.Spy.Node n = spy.newNode(t2a,t);
		DynamicRaster.Spy.Node h = spy.newNode(t1,n);
		
		dr = spy.create(h, 3, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, h);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, n);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(h, 3, t);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testY() {
		DynamicRaster.Spy.Node n4 = spy.newNode(t5,null);
		DynamicRaster.Spy.Node n3 = spy.newNode(t4,n4);
		DynamicRaster.Spy.Node n2 = spy.newNode(t3,n3);
		DynamicRaster.Spy.Node n1 = spy.newNode(t2,n2);
		DynamicRaster.Spy.Node n0 = spy.newNode(t1,n1);
		
		dr = spy.create(n0, 5, null);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, n0);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, n1);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, n2);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, n3);
		assertReporting(true, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, n4);
		assertReporting(true, () -> spy.wellFormed(dr));

		DynamicRaster.Spy.Node i4 = spy.newNode(t5, null);
		DynamicRaster.Spy.Node i3 = spy.newNode(t4, n4);
		DynamicRaster.Spy.Node i2 = spy.newNode(t3, n3);
		DynamicRaster.Spy.Node i1 = spy.newNode(t2,n2);
		DynamicRaster.Spy.Node i0 = spy.newNode(t1,n1);

		dr = spy.create(n0, 4, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 4, n1);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 6, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 6, n2);
		assertReporting(false, () -> spy.wellFormed(dr));
		
		dr = spy.create(n0, 5, i0);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, i1);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, i2);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, i3);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 5, i4);
		assertReporting(false, () -> spy.wellFormed(dr));
	}

	public void testZ() {
		DynamicRaster.Spy.Node n4 = spy.newNode(t5,null);
		DynamicRaster.Spy.Node n3 = spy.newNode(t4,n4);
		DynamicRaster.Spy.Node n2 = spy.newNode(t3,n3);
		DynamicRaster.Spy.Node n1 = spy.newNode(t2,n2);
		DynamicRaster.Spy.Node n0 = spy.newNode(t1,n1);

		// more cycle checks
		
		spy.setNext(n4, n0);
		dr = spy.create(n0, 5, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 6, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, -1, null);
		assertReporting(false, () -> spy.wellFormed(dr));

		spy.setNext(n4, n1);
		dr = spy.create(n0, 5, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 6, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, -1, null);
		assertReporting(false, () -> spy.wellFormed(dr));

		spy.setNext(n4, n2);
		dr = spy.create(n0, 5, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 6, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, -1, null);
		assertReporting(false, () -> spy.wellFormed(dr));

		spy.setNext(n4, n3);
		dr = spy.create(n0, 5, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 6, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, -1, null);
		assertReporting(false, () -> spy.wellFormed(dr));

		spy.setNext(n4, n4);
		dr = spy.create(n0, 5, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, 6, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		dr = spy.create(n0, -1, null);
		assertReporting(false, () -> spy.wellFormed(dr));
		
		spy.setNext(n4,  null);
		dr = spy.create(n0, 5, null);
		assertReporting(true, () -> spy.wellFormed(dr));
	}
}
