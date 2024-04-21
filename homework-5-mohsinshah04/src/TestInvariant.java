import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.Card;
import edu.uwm.cs351.Card.Rank;
import edu.uwm.cs351.Card.Spy;
import edu.uwm.cs351.Card.Spy.Group;
import edu.uwm.cs351.Card.Spy.TestCard;
import edu.uwm.cs351.Card.Suit;

public class TestInvariant extends LockedTestCase {
	private Spy spy;
	private int reports;
	
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
	
	protected void assertWellFormed(boolean expected, Card.Group g) {
		assertReporting(expected, () -> spy.wellFormed(g));
	}
	
	private Spy.Group g;
	private TestCard c[] = { null,
			new TestCard(Rank.ACE,Suit.DIAMOND),
			new TestCard(Rank.DEUCE,Suit.DIAMOND),
			new TestCard(Rank.KING,Suit.DIAMOND),
			new TestCard(Rank.DEUCE,Suit.CLUB),
			new TestCard(Rank.KING,Suit.HEART),
			new TestCard(Rank.SIX,Suit.HEART),	
			new TestCard(Rank.ACE,Suit.SPADE),
	};

	@Override
	protected void setUp() {
		spy = new Card.Spy();
		g = new Group();
	}

	
	/// testAx: empty groups
	
	public void testA0() {
		g = spy.newGroup(null, null, 0);
		assertWellFormed(true, g);
	}
	
	public void testA1() {
		g = spy.newGroup(null, null, 1);
		assertWellFormed(false, g);
	}
	
	public void testA2() {
		g = spy.newGroup(null, null, -1);
		assertWellFormed(false, g);
	}
	
	public void testA3() {
		g = spy.newGroup(null, c[1], 0);
		assertWellFormed(false, g);
	}
	
	public void testA4() {
		g = spy.newGroup(null, c[1], 0);
		c[1].setGroup(g);
		assertWellFormed(false, g);
	}
	
	public void testA5() {
		g = spy.newGroup(c[2], null, 0);
		c[2].setGroup(g);
		assertWellFormed(false, g);
	}
	
	public void testA6() {
		g = spy.newGroup(c[2], null, 0);
		assertWellFormed(false, g);
	}
	
	public void testA7() {
		g = spy.newGroup(c[2], null, 0);
		c[2].setGroup(g);
		assertWellFormed(false, g);
	}
	
	
	/// testBx: groups with one element
	
	public void testB0() {
		g = spy.newGroup(c[3], null, 1);
		assertWellFormed(false, g);
	}
	
	public void testB1() {
		g = spy.newGroup(c[3], null, 1);
		c[3].setGroup(g);
		assertWellFormed(false, g);
	}
	
	public void testB2() {
		g = spy.newGroup(c[3], c[3], 1);
		assertWellFormed(false, g);
	}
	
	public void testB3() {
		g = spy.newGroup(c[3], c[3], 1);
		c[3].setGroup(g);
		assertWellFormed(true, g);
	}
	
	public void testB4() {
		TestCard ks1 = spy.newCard(Rank.KING, Suit.DIAMOND);
		TestCard ks2 = spy.newCard(Rank.KING, Suit.DIAMOND);
		g = spy.newGroup(ks1, ks2, 1);
		ks1.setGroup(g);
		ks2.setGroup(g);
		assertWellFormed(false, g);
	}
	
	public void testB5() {
		g = spy.newGroup(c[4], c[4], 1);
		c[4].setGroup(g);
		c[4].setPrev(c[3]);
		c[3].setGroup(g);
		assertWellFormed(false, g);
		c[3].setNext(c[4]);
		assertWellFormed(false, g);
	}
	
	public void testB6() {
		g = spy.newGroup(c[4], c[4], 1);
		c[4].setGroup(g);
		c[4].setNext(c[5]);
		c[5].setGroup(g);
		assertWellFormed(false, g);
		c[5].setPrev(c[4]);
		assertWellFormed(false, g);
	}

	public void testB7() {
		g = spy.newGroup(c[4], c[4], 2);
		c[4].setGroup(g);
		c[4].setNext(c[5]);
		c[5].setGroup(g);
		assertWellFormed(false, g);
		c[5].setPrev(c[4]);
		assertWellFormed(false, g);
	}

	public void testB8() {
		g = spy.newGroup(c[4], c[4], 1);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[3].setGroup(g);
		c[5].setPrev(c[4]);
		c[5].setGroup(g);
		assertWellFormed(true, g);
	}
	
	public void testB9() {
		g = spy.newGroup(c[4], c[4], 1);
		c[4].setGroup(g);
		c[4].setNext(c[4]);
		c[4].setPrev(c[4]);
		assertWellFormed(false, g);
	}
	
	
	/// testCx: test groups of two cards

	public void testC0() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		assertWellFormed(false, g);
	}
	
	public void testC1() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		assertWellFormed(false, g);
	}
	
	public void testC2() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		assertWellFormed(true, g);
	}
	
	public void testC3() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[4].setPrev(c[3]);
		assertWellFormed(false, g);
	}
	
	public void testC4() {
		g = spy.newGroup(c[3], c[4], 1);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		assertWellFormed(false, g);
	}
	
	public void testC5() {
		g = spy.newGroup(c[3], c[4], 3);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		assertWellFormed(false, g);
	}
	
	public void testC6() {
		g = spy.newGroup(c[3], c[4], 2);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		c[3].setPrev(c[2]);
		c[2].setNext(c[3]);
		assertWellFormed(false, g);
	}
	
	public void testC7() {
		g = spy.newGroup(c[3], c[4], 3);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		c[3].setPrev(c[2]);
		c[2].setNext(c[3]);
		assertWellFormed(false, g);
	}

	public void testC8() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[5].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		c[5].setPrev(c[4]);
		c[4].setNext(c[5]);
		assertWellFormed(false, g);
	}

	public void testC9() {
		g = spy.newGroup(c[3], c[4], 3);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[5].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		c[5].setPrev(c[4]);
		c[4].setNext(c[5]);
		assertWellFormed(false, g);
	}
	
	
	/// testDx: more tests with 2 cards, including cycles
	
	public void testD0() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[3]);
		c[4].setPrev(c[3]);
		assertWellFormed(false, g);
	}
	
	public void testD1() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[3]);
		c[3].setPrev(c[3]);
		c[4].setPrev(c[3]);
		assertWellFormed(false, g);
	}
	
	public void testD2() {
		TestCard ks1 = spy.newCard(Rank.KING, Suit.DIAMOND);
		g = spy.newGroup(c[3], c[4], 2);
		ks1.setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		ks1.setNext(c[4]);
		c[4].setPrev(ks1);
		assertWellFormed(false, g);
	}
	
	public void testD3() {
		TestCard i4 = new TestCard(Rank.DEUCE,Suit.CLUB);
		g = spy.newGroup(c[3], c[4], 2);
		i4.setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(i4);
		i4.setPrev(c[3]);
		c[4].setPrev(c[3]);
		assertWellFormed(false, g);
	}
	
	public void testD4() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		c[4].setNext(c[3]);
		assertWellFormed(false, g);
		c[3].setPrev(c[4]);
		assertWellFormed(false, g);
	}
	
	public void testD5() {
		g = spy.newGroup(c[3], c[4], 2);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[3].setNext(c[4]);
		c[4].setPrev(c[3]);
		c[4].setNext(c[4]);
		assertWellFormed(false, g);
		c[4].setPrev(c[4]);
		assertWellFormed(false, g);
	}
	
	public void testD6() {
		TestCard ks1 = spy.newCard(Rank.KING, Suit.DIAMOND);
		TestCard ks2 = spy.newCard(Rank.KING, Suit.DIAMOND);
		g = spy.newGroup(ks1, ks2, 2);
		ks1.setGroup(g);
		ks2.setGroup(g);
		ks1.setNext(ks2);
		ks2.setPrev(ks1);
		assertWellFormed(true, g);
	}

	public void testD7() {
		TestCard ks1 = spy.newCard(Rank.KING, Suit.DIAMOND);
		TestCard ks2 = spy.newCard(Rank.KING, Suit.DIAMOND);
		g = spy.newGroup(ks1, ks2, 2);
		ks1.setGroup(g);
		ks2.setGroup(g);
		ks1.setNext(ks1);
		ks2.setPrev(ks2);
		assertWellFormed(false, g);
	}
	
	
	/// testEx: tests of three cards
	
	public void testE0() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[2]);
		assertWellFormed(true, g);
	}
	
	public void testE1() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[2]);
		assertWellFormed(false, g);
	}
	
	public void testE2() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[2]);
		assertWellFormed(false, g);
	}
	
	public void testE3() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[3].setPrev(c[2]);
		assertWellFormed(false, g);
	}
	
	public void testE4() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[2].setPrev(c[1]); 
		assertWellFormed(false, g);
	}
	
	public void testE5() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[1]);
		assertWellFormed(false, g);
	}

	public void testE6() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[1]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[2]);
		assertWellFormed(false, g);
		c[1].setPrev(c[2]);
		assertWellFormed(false, g);
	}

	public void testE7() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[2]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[2]);
		assertWellFormed(false, g);
	}

	public void testE8() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[2]);
		c[3].setNext(c[1]);
		assertWellFormed(false, g);
		c[1].setPrev(c[3]);
		assertWellFormed(false, g);
	}

	public void testE9() {
		g = spy.newGroup(c[1], c[3], 3);
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[1].setNext(c[2]); 
		c[2].setNext(c[3]);
		c[2].setPrev(c[1]); 
		c[3].setPrev(c[2]);
		c[3].setNext(c[2]);
		assertWellFormed(false, g);
		c[2].setPrev(c[3]);
		assertWellFormed(false, g);
	}


	/// testFx: longer lists
	
	public void testF0() {
		g = spy.newGroup(c[1], c[5], 5);
		
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[5].setGroup(g);
		
		c[1].setNext(c[2]);
		c[2].setNext(c[3]);
		c[3].setNext(c[4]);
		c[4].setNext(c[5]);
		
		c[5].setPrev(c[4]);
		c[4].setPrev(c[3]);
		c[3].setPrev(c[2]);
		c[2].setPrev(c[1]);
		
		assertWellFormed(true, g);
	}
	
	public void testF1() {
		g = spy.newGroup(c[1], c[5], 5);
		TestCard i3 = spy.newCard(Rank.KING, Suit.DIAMOND);
	
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[5].setGroup(g);
		i3.setGroup(g);
		
		c[1].setNext(c[2]);
		c[2].setNext(c[3]);
		c[3].setNext(c[4]); i3.setNext(c[4]);
		c[4].setNext(c[5]);
		
		c[5].setPrev(c[4]);
		c[4].setPrev(i3);
		c[3].setPrev(c[2]); i3.setPrev(c[2]);
		c[2].setPrev(c[1]);
		
		assertWellFormed(false, g);
	}

	public void testF2() {
		g = spy.newGroup(c[1], c[5], 5);
		
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[5].setGroup(g);
		
		c[1].setNext(c[2]);
		c[2].setNext(c[3]);
		c[3].setNext(c[4]);
		c[4].setNext(c[2]);
		
		c[5].setPrev(c[4]);
		c[4].setPrev(c[3]);
		c[3].setPrev(c[2]);
		c[2].setPrev(c[1]);
		
		assertWellFormed(false, g);
		c[2].setPrev(c[4]);
		assertWellFormed(false, g);
	}

	public void testF3() {
		g = spy.newGroup(c[1], c[5], 5);
		TestCard i5 = new TestCard(Rank.KING,Suit.HEART);
		
		c[1].setGroup(g);
		c[2].setGroup(g);
		c[3].setGroup(g);
		c[4].setGroup(g);
		c[5].setGroup(g);
		i5.setGroup(g);
		
		c[1].setNext(c[2]);
		c[2].setNext(c[3]);
		c[3].setNext(c[4]);
		c[4].setNext(i5);
		
		c[5].setPrev(c[4]); i5.setPrev(c[4]);
		c[4].setPrev(c[3]);
		c[3].setPrev(c[2]);
		c[2].setPrev(c[1]);
		
		assertWellFormed(false, g);
	}

}

