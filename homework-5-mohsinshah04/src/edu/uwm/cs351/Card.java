package edu.uwm.cs351;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;



/**
 * Traditional playing cards.
 * @author boyland
 */
public class Card {
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private static boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	public enum Suit { CLUB, DIAMOND, HEART, SPADE };
	public enum Rank {
		ACE(1), DEUCE(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT (8), NINE(9), TEN(10),
		JACK(11), QUEEN(12), KING(13);

		private final int rank;
		private Rank(int r) {
			rank = r;
		}

		public int asInt() {
			return rank;
		}
	}


	/// FIelds for Card

	private final Suit suit;
	private final Rank rank;
	private Card prev, next;
	private Group group;

	/**
	 * COnstructor for a card
	 * @param r rank, probably should not be null
	 * @param s suit, probably should not be null
	 */
	public Card(Rank r, Suit s) {
		this.rank = r;
		this.suit = s;
	}

	// getters for all fields:

	/**
	 * Return the suit of this card
	 * @return the suit
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * Return the rank of this card
	 * @return the rank
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * Return the card before this in the group
	 * @return previous card
	 */
	public Card getPrev() {
		return prev;
	}

	/**
	 * Return the card after this one in the group
	 * @return next card
	 */
	public Card getNext() {
		return next;
	}

	/**
	 * Get the group that this card is in
	 * @return group owning this card
	 */
	public Group getGroup() {
		return group;
	}

	// no setters


	@Override
	/** Return true if the suit and rank are the same.
	 * Caution: do not use this method to check if you have the same card!
	 */
	public boolean equals(Object x) {
		if (!(x instanceof Card)) return false;
		Card other = (Card)x;
		return suit == other.suit && rank == other.rank;
	}

	@Override
	public String toString() {
		return rank + " of " + suit + "S";
	}


	/**
	 * An endogenous DLL of card objects.
	 */
	public static class Group {
		private Card first, last;
		private int size;

		/**
		 * Create an empty group.
		 */
		public Group() {
			first = last = null;
			size = 0;
		}

		public Card getFirst() { return first; }
		public Card getLast() { return last; }
		public int getSize() {return size;}	

		//worked on the well formed with tutors,Peter, and joey
		public boolean wellFormed() {
			// TODO: Implement the invariant:
			//check to see if there are any cylcles, i got this from homework 4
			if (first != null) {
				Card slow = first;
				Card fast = first.next;
				while (fast != null) {
					if (slow == fast) return report("Found cycle in list");
					slow = slow.next;
					fast = fast.next;
					if (fast != null) fast = fast.next;
				}
			}
			// do checks to see if the first or last is null
			if(getFirst()==null)
			{
				if(getLast()==null)//if they both are null then the size must be 0
				{
					if(size!=0)return report("there is no list");
				}
				else {
					return report("first cant be null if there is a last");//if there is a last then there must be a first
				}
			}
			if(getLast()==null)
			{
				if(getFirst()!=null)//have to make sure you arent calling the same one i called on line 154
				{
					return report("last cant be null if there is a first");
				}
			}
			//do a for loop from the start that checks if there is a group and if so 
			//if the hash code is correct or comparable because groups can change
			//and the c.next.prev should have the same hash as the current otherwise return a report
			int count =0;
			for(Card c =first;c!=null;c=c.next)
			{
				if(c.getGroup()==null)
				{
					return report("there is no group");
				}
				if(c.next!=null&&c.next.prev!=null)
				{
					if(c.hashCode()!=c.next.prev.hashCode())return report("groups dont relate");
				}
				++count;
			}
			if(count!=size)return report("incorrect count");//check if count is size
			//if the count is 1, then the hashcode for the first and last must match
			if(count==1)
			{
				if(first.hashCode()!=last.hashCode())return report("groups dont relate");

			}
			//same as the earlier for loop but this time you are going from the back incase you
			//set a point to the previous
			int countNew =0;
			for(Card c =last;c!=null;c=c.prev)
			{
				if(c.getGroup()==null)
				{
					return report("there is no group");
				}
				if(c.prev!=null&&c.prev.next!=null)//same as before but flip it
				{
					if(c.prev.next.hashCode()!=c.hashCode())return report("groups dont relate");

				}

				++countNew;
			}
			if(countNew!=size)return report("incorrect count");
			if(countNew==1)//said earlier
			{

				if(first.hashCode()!=last.hashCode())return report("groups dont relate");

			}
			// - The cards must be properly linked up in a doubly-linked list.
			// - All cards must have their group set to this.
			// This code must terminate and must not crash even if there are problems.
			return true;
		}

		/**
		 * Return true if there are no cards,
		 * that is, if and only if getFirst() == null. O(1)
		 */
		public boolean isEmpty() {
			assert wellFormed() : "invariant false on entry to isEmpty()";
			if(getFirst()==null)return true;//if theres no first then theres nothing to be added
			return false; // TODO
		}

		/**
		 * Return the number of cards in this pile. O(1)
		 */
		public int count() {
			assert wellFormed() : "invariant false on entry to count()";
			return size; // TODO// you just need to return size;
		}

		/**
		 * Add a card to the end of this pile/hand. O(1)
		 * @param c card to add, must not be null or in a group already.
		 * @throws IllegalArgumentException if the card is in a group already.
		 */
		public void add(Card c) {
			// TODO
			// No loops allowed!
			// Make sure to test invariant at start and before returning.
			assert wellFormed() : "invariant error on add";
			if(c==null)throw new IllegalStateException();
			if(c.group!=null)throw new IllegalArgumentException();
			if(last!=null)
			{
				last.next=c;c.prev=last;last=c;

			}
			else {
				first=c;
				last=c;
			}
			c.group=this;
			++size;
			assert wellFormed() : "invariant error by add";

		}

		/**
		 * Remove the first card and return it.
		 * The group must not be empty.  The resulting card
		 * will not belong to any group afterwards. O(1)
		 *@throws IllegalStateException if group empty
		 */
		public Card draw() {
			// TODO
			// No loops allowed!
			// Make sure to test invariant at start and before returning.
			assert wellFormed() : "invariant error in draw";
			if(isEmpty())throw new IllegalStateException();
			Card c=first;
			if(c==last)last=null;//if there is only 1 card in the list then you need to set last to null
			first=first.next;
			if(first!=null)//if the first isnt null the previous must be null
			{
				first.prev=null;
			}
			c.next=null;
			c.group=null;
			--size;
			assert wellFormed() : "invariant error by draw";
			return c;
		}

		/**
		 * Remove the given card from this group.
		 * Afterwards the card is not in this group. O(1)
		 * @param c, card in this group, must not be null
		 * @throws IllegalArgumentException if c is not in this group
		 */
		public void remove(Card c) {
			// TODO.  
			// No loops allowed!
			// Make sure to test invariant!
			assert wellFormed() : "invariant error in remove";
			if(c==null)throw new NullPointerException();
			if(c.group!=this)throw new IllegalArgumentException();
			if(c==first)draw();//check if its the first in the list or of its the first and the last
			else if(c==last)//remove at the end
			{
				last=last.prev;
				last.next=null;
				c.prev=null;
				c.group=null;
				--size;
			}
			else //remove in the middle
			{
				c.prev.next=c.next;
				c.next.prev=c.prev;
				c.next=null;
				c.prev=null;
				c.group=null;
				--size;
			}
			assert wellFormed() : "invariant error by remove";

		}

		/**
		 * Sort the cards using the given comparison, so that
		 * after sorting for all cards c in the group that is not last
		 * <code>cmp.compare(c,c.next)</code> is never positive.
		 * This code must use insertion sort so that it is efficient
		 * on (mostly) sorted lists.
		 * @param cmp comparator to use for sorting.  Must not be null.
		 * The comparator should work correctly, or the final result is undefined.
		 */
		//recieved guidance from Peter Li, he explained the method to me and we drew it out on the board
		public void sort(Comparator<Card> cmp) {
			assert wellFormed() : "invariant false on entry to sort()";
			// TODO
			// Implement insertion sort efficiently.
			// You may find it helpful to use "remove" (but watch about size!).
			// DO NOT use anything in the CardUtil class or any java.util class

			if(size>1)
			{
				for(Card c =first.next;c!=null;c=c.next) 
				{
					Card temp=c.prev;
					Card now=c;
					if(cmp.compare(temp, now)>0)//have to make sure you can even compare
					{
						while (temp.prev !=null && cmp.compare(temp.prev, now)>0)
						{//check to see if you have to keep going back until temp.prev is just bigger than current
							temp=temp.prev;
						}

						//if temp.prev is null then you have to add to the start
						if(temp.prev==null)
						{
							remove(now);
							now.prev=null;
							now.next=temp;
							temp.prev=now;
							now.group=this;
							first=now;
							++size;
						}
						else
						{
							remove(now);
							now.prev=temp.prev;
							now.next=temp;
							temp.prev.next=now;
							temp.prev=now;
							now.group=this;
							++size;
						}
					}
				}
			}

			assert wellFormed() : "invariant false on exit to sort()";
		}
		//		ACtivity
		//private void insertionSort(E[] array) {
		//				042     int n = array.length;
		//				043     for (int u=1; u < n; ++u) { // u = next unsorted index
		//				044         E current = array[u];
		//				045         int hole = u;
		//				046         while (hole > 0 && current.compareTo(array[hole-1]) < 0) {
		//				047             array[hole] = array[hole-1];
		//				048             --hole;
		//				049         }
		//				050         array[hole] = current;
		//				051     }
		//				052 }


		/**
		 * Randomize the order of the cards in this group.
		 */
		public void shuffle() {
			/*
			 * This is very different from the sort method because:
			 * @ we decant the cards into an array list;
			 * @ we use a library function to do the work;
			 * The implementation you write for the sort method should
			 * have *neither* of these characteristics.
			 */
			List<Card> cards = new ArrayList<Card>();
			while (!isEmpty()) {
				cards.add(draw());
			}
			Collections.shuffle(cards);
			for (Card c: cards) {
				add(c);
			}
		}
	}


	/** Create and return a fresh pack of cards.
	 * A "static" method is a class method.  It is invoked using
	 * the class, not an instance.
	 * @return a fresh pack of 52 cards
	 */
	public static Group newDeck() {
		Group g = new Group();
		for (Suit s : Suit.values()) {
			for (Rank r : Rank.values()) {
				Card c = new Card(r,s);
				g.add(c);
			}
		}
		return g;
	}


	/** 
	 * Test to use for testing internal structures of the Card and Group classes.
	 * Do not change this class!
	 */
	public static class Spy {
		public static class TestCard extends Card {
			public TestCard(Rank r, Suit s) {
				super(r,s);
			}

			public void setPrev(Card c) {
				super.prev = c;
			}

			public void setNext(Card c) {
				super.next = c;
			}

			public void setGroup(Card.Group g) {
				super.group = g;
			}
		}

		public static class Group extends Card.Group {
			public void setFirst(Card c) {
				super.first = c;
			}

			public void setLast(Card c) {
				super.last = c;
			}

			public void setSize(int s) {
				super.size = s;
			}
		}

		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a Card for testing.
		 * @param r rank, may be null
		 * @param s suit may be null
		 * @return newly created card
		 */
		public TestCard newCard(Rank r, Suit s) {
			return new TestCard(r, s);
		}

		/**
		 * Create a group for testing
		 * @param f first card, may be null
		 * @param l last card, may be null
		 * @param s declared size (may be wrong)
		 * @return group with these parts.
		 */
		public Group newGroup(Card f, Card l, int s) {
			Group g = new Group();
			g.setFirst(f);
			g.setLast(l);
			g.setSize(s);
			return g;
		}

		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s transaction seq to check.
		 * @return whether well formed works.
		 */
		public boolean wellFormed(Card.Group g) {
			return g.wellFormed();
		}

	}
}
