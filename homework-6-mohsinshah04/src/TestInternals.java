import java.util.Comparator;
import java.util.function.Consumer;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.SortedSequence;
import edu.uwm.cs351.SortedSequence.Spy;
import edu.uwm.cs351.SortedSequence.Spy.Node;


public class TestInternals extends LockedTestCase {

	protected static Comparator<String> ascending = (s1,s2) -> s1.compareTo(s2);
	protected static Comparator<String> descending = (s1,s2) -> s2.compareTo(s1);
	protected static Comparator<String> nondiscrimination = (s1,s2) -> 0;

	private SortedSequence.Spy<String> spy = new SortedSequence.Spy<>();
	
	int reports = 0;
	
	protected void assertWellFormed(boolean expected, SortedSequence<String> seq) {
		reports = 0;
		Consumer<String> savedReporter = Spy.getReporter();
		try {
			Spy.setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, spy.wellFormed(seq));
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
		} finally {
			Spy.setReporter(savedReporter);
		}
	}

	protected Node<String> d0, d1, d2;
	protected Node<String> n0, n1, n2, n3, n4, n5;
	protected Node<String> n0a, n1a, n2a, n3a, n4a, n5a;
	protected Node<String> n1A, n2A, n3A, n4A, n5A;
	
	@Override // implementation
	protected void setUp() {
		d0 = spy.createNode();
		d1 = spy.createNode();
		d2 = spy.createNode();
		
		n0 = spy.createNode(null, null);
		n0a = spy.createNode(null, null);
		
		n1 = spy.createNode("one", null);
		n1a = spy.createNode("one", null);
		n1A = spy.createNode("One", null);
		
		n2 = spy.createNode("two", null);
		n2a = spy.createNode("two", null);
		n2A = spy.createNode("TWO", null);
		
		n3 = spy.createNode("three", null);
		n3a = spy.createNode("three", null);
		n3A = spy.createNode("Three", null);
		
		n4 = spy.createNode("four", null);
		n4a = spy.createNode("four", null);
		n4A = spy.createNode("fOuR", null);
		
		n5 = spy.createNode("five", null);
		n5a = spy.createNode("five", null);
		n5A = spy.createNode("FIVE", null);
	}
	
	protected SortedSequence<String> sut;	
	
	// testAn: empty lists
	
	public void testA0() {
		sut = spy.create(null, null, 0, ascending);
		assertWellFormed(false, sut);
	}
	
	public void testA1() {
		sut = spy.create(d0, null, 0, descending);
		assertWellFormed(false, sut);
	}
	
	public void testA2() {
		sut = spy.create(d0, d0, 0, ascending);
		assertWellFormed(true, sut);
	}
	
	public void testA3() {
		sut = spy.create(null, d0, 0, descending);
		assertWellFormed(false, sut);
	}
	
	public void testA4() {
		sut = spy.create(d0, d0, 0, null);
		assertWellFormed(false, sut);
	}
	
	public void testA5() {
		sut = spy.create(d0, d0, 1, nondiscrimination);
		assertWellFormed(false, sut);
	}
	
	public void testA6() {
		sut = spy.create(d0, d1, 0, ascending);
		assertWellFormed(false, sut);
	}
	
	public void testA7() {
		sut = spy.create(d0, d1, 0, ascending);
		d1.setData(d0);
		d1.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testA8() {
		sut = spy.create(n1, n1, 0, descending);
		n1.setNext(n1);
		assertWellFormed(false, sut);
	}
	
	public void testA9() {
		sut = spy.create(n0, n0, 0, descending);
		n0.setNext(n0);
		assertWellFormed(false, sut);
	}
	
	
	/// testBn: tests with one node
	
	public void testB0() {
		sut = spy.create(d0, n1, 1, nondiscrimination);
		assertWellFormed(false, sut);
	}
	
	public void testB1() {
		sut = spy.create(d0, n2, 1, nondiscrimination);
		d0.setNext(n2);
		assertWellFormed(false, sut);
	}
	
	public void testB2() {
		sut = spy.create(d0, n3, 1, nondiscrimination);
		d0.setNext(n3);
		n3.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testB3() {
		sut = spy.create(n4, d0, 1, nondiscrimination);
		d0.setNext(n4);
		n4.setNext(d0);
		assertWellFormed(true, sut);
	}
	
	public void testB4() {
		sut = spy.create(n5, n5, 1, ascending);
		d0.setNext(n5);
		n5.setNext(d0);
		assertWellFormed(true, sut);
	}
	
	public void testB5() {
		sut = spy.create(n0, d0, 1, descending);
		d0.setNext(n0);
		n0.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testB6() {
		sut = spy.create(n1, n1a, 1, nondiscrimination);
		d0.setNext(n1);
		n1.setNext(d0);
		n1a.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testB7() {
		sut = spy.create(n2, null, 1, ascending);
		d0.setNext(n2);
		n2.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testB8() {
		sut = spy.create(n3, n3, 1, ascending);
		n0.setNext(n3);
		n3.setNext(n0);
		assertWellFormed(false, sut);
	}
	
	public void testB9() {
		sut = spy.create(n4, d1, 1, null);
		d1.setNext(n4);
		n4.setNext(d1);
		assertWellFormed(false, sut);
	}
	
	
	/// testCx: tests with two nodes
	
	public void testC0() {
		sut = spy.create(n1, d0, 2, ascending);
		d0.setNext(n5);
		n5.setNext(n1);
		n1.setNext(d0);
		assertWellFormed(true, sut);
	}
	
	public void testC1() {
		sut = spy.create(n1, d0, 2, descending);
		d0.setNext(n5);
		n5.setNext(n1);
		n1.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testC2() {
		sut = spy.create(n2, n2, 2, nondiscrimination);
		n2.setNext(d1);
		d1.setNext(n3);
		n3.setNext(n2);
		assertWellFormed(false, sut);
	}
	
	public void testC3() {
		sut = spy.create(n2, n3, 2, ascending);
		n2.setNext(d1);
		d1.setNext(n3);
		n3.setNext(n2);
		assertWellFormed(true, sut);
	}
	
	public void testC4() {
		sut = spy.create(n2, d1, 2, descending);
		n2.setNext(d1);
		d1.setNext(n3);
		n3.setNext(n2);
		assertWellFormed(false, sut);
	}
	
	public void testC5() {
		sut = spy.create(n3, n3, 2, null);
		n3.setNext(d0);
		d0.setNext(n4);
		n4.setNext(n3);
		assertWellFormed(false, sut);
	}
	
	public void testC6() {
		sut = spy.create(n4, n4a, 2, ascending);
		n4.setNext(d0);
		d0.setNext(n5);
		n5.setNext(n4);
		n4a.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testC7() {
		sut = spy.create(n4, n2a, 2, descending);
		n4.setNext(d0);
		d0.setNext(n2);
		n2.setNext(n4);
		n2a.setNext(n4);
		assertWellFormed(false, sut);
	}
	
	public void testC8() {
		sut = spy.create(n4, d1, 2, descending);
		n4.setNext(d0);
		d0.setNext(n1);
		n1.setNext(n4);
		d1.setNext(n1);
		assertWellFormed(false, sut);
	}
	
	public void testC9() {
		sut = spy.create(n4, d1, 2, descending);
		n4.setNext(d0);
		d0.setNext(n1);
		n1.setNext(n4);
		d1.setNext(n1);
		d1.setData(d0);
		assertWellFormed(false, sut);
	}
	
	
	/// testDn: simple cycle checks
	
	public void testD0() {
		sut = spy.create(n1, n1, 1, nondiscrimination);
		n1.setNext(d0);
		d0.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testD1() {
		sut = spy.create(n2, d0, 1, ascending);
		n2.setNext(d0);
		d0.setNext(n2a);
		n2a.setNext(d0);
		assertWellFormed(false, sut);		
	}
	
	public void testD2() {
		sut = spy.create(n3, n3, 2, nondiscrimination);
		n3.setNext(n3);
		assertWellFormed(false, sut);
	}
	
	public void testD3() {
		sut = spy.create(n3, n3, 2, descending);
		n3.setNext(d0);
		d0.setNext(n2);
		n2.setNext(n2);
		assertWellFormed(false, sut);
	}
	
	public void testD4() {
		sut = spy.create(n4, n5, 2, ascending);
		n4.setNext(d0);
		d0.setNext(n5);
		n5.setNext(n4a);
		n4a.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testD5() {
		sut = spy.create(n5, n1, 3, descending);
		n5.setNext(d1);
		d1.setNext(n1);
		n1.setNext(n1);
		assertWellFormed(false, sut);
	}
	
	public void testD6() {
		sut = spy.create(n1, n1, 3, ascending);
		n1.setNext(d1);
		d1.setNext(n4);
		n4.setNext(n3);
		n3.setNext(n4);
		assertWellFormed(false, sut);
	}
	
	public void testD7() {
		sut = spy.create(n2, d0, 3, ascending);
		n2.setNext(d0);
		d0.setNext(n4);
		n4.setNext(n1);
		n1.setNext(n2a);
		n2a.setNext(d0);
		assertWellFormed(false, sut);
	}
	
	public void testD8() {
		sut = spy.create(n3, n3, 3, ascending);
		n3.setNext(d0);
		d0.setNext(n5);
		n5.setNext(n0);
		n0.setNext(n3);
		assertWellFormed(false, sut);
	}
	
	public void testD9() {
		sut = spy.create(n4, n3, 3, descending);
		d1.setData(d0);
		n4.setNext(d1);
		d1.setNext(n2);
		n2.setNext(n3);
		n3.setNext(n4);
		assertWellFormed(false, sut);
	}
	
	
	/// testEn: test ordering
	
	public void testE0() {
		sut = spy.create(n1, n1, 2, ascending);
		n1.setNext(d0);
		d0.setNext(n1a);
		n1a.setNext(n1);
		assertWellFormed(false, sut);
	}
	
	public void testE1() {
		sut = spy.create(n2, n2, 2, ascending);
		n2.setNext(d1);
		d1.setNext(n2A);
		n2A.setNext(n2);
		assertWellFormed(true, sut);
	}
	
	public void testE2() {
		sut = spy.create(n2, n2, 2, String.CASE_INSENSITIVE_ORDER);
		n2.setNext(d1);
		d1.setNext(n2A);
		n2A.setNext(n2);
		assertWellFormed(false, sut);
	}
	
	public void testE3() {
		sut = spy.create(n3A, n3, 2, descending);
		n3A.setNext(d2);
		d2.setNext(n3);
		n3.setNext(n3A);
		assertWellFormed(true, sut);
	}
	
	public void testE4() {
		sut = spy.create(n4A, n5, 2, String.CASE_INSENSITIVE_ORDER);
		n4A.setNext(d0);
		d0.setNext(n5);
		n5.setNext(n4A);
		assertWellFormed(true, sut);
	}
	
	public void testE5() {
		sut = spy.create(n5, n5, 3, descending);
		n5.setNext(d0);
		d0.setNext(n2);
		n2.setNext(n1);
		n1.setNext(n5);
		assertWellFormed(true, sut);
	}
	
	public void testE6() {
		sut = spy.create(n4, n5, 3, ascending);
		n4.setNext(d1);
		d1.setNext(n5);
		n5.setNext(n1);
		n1.setNext(n4);
		assertWellFormed(false, sut);
	}
	
	public void testE7() {
		sut = spy.create(n4, n5, 3, String.CASE_INSENSITIVE_ORDER);
		n4.setNext(d0);
		d0.setNext(n5);
		n5.setNext(n4A);
		n4A.setNext(n4);
		assertWellFormed(false, sut);
	}
	
	public void testE8() {
		sut = spy.create(n1, n3, 3, descending);
		n1.setNext(d0);
		d0.setNext(n3);
		n3.setNext(n2);
		n1.setNext(n1);
		assertWellFormed(false, sut);
	}
	
	public void testE9() {
		sut = spy.create(n2, d0, 3, ascending);
		n2.setNext(d0);
		d0.setNext(n1);
		n1.setNext(n1a);
		n1a.setNext(n2);
		assertWellFormed(false, sut);
	}
	
	
	private void ignore(String ignored) {}
	private void ignore(int val) {}
	
	public void testT() {
		// Assume the Node class has "data" and "next" fields.
		// Assume we have fields "tail", "manyItems" and "precursor" in the data structure.
		// Don't use "this", spaces or parens in your answers.
		ignore(Ts(1879723487)); // How do we get the dummy node? 
		ignore(Ts(210447483)); // How do we get the first element of the Sequence (assuming it exists)?
		ignore(Ts(1194659650)); // How do we get the current data, assuming it exists?
	}
	
	public void testU() {
		ignore(Ti(659926051)); // If we have ten elements in the sequence, how many nodes are in the data structure?
	}
	
	public void testV() {
		// Assume that Node.toString is overridden to return the string
		// "Node(D)" where "D" is replaced with "Null" if the data is null,
		// and otherwise with the name of the class of the data (e.g. "Integer" or "Node").
		// Assume we representing the Sequence [1,*2,3] -- three elements 1, 2, and 3 where the middle element is current
		ignore(Ts(1449661744)); // What is tail.toString() ?
		ignore(Ts(910927874)); // What is precursor.data.toString() ?
		ignore(Ts(1023092366)); // What is tail.next.toString() ?
	}
	
	public void testZ() {
		/*pdateNode(d0,n2);
		updateNode(n2,n3);
		updateNode(n3,d0);
		Sequence<Integer> s = makeSequence(n3,2,n3);
		assertWellformed(s);*/
	}
}
