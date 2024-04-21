package edu.uwm.cs351;

import java.util.Comparator;
import java.util.function.Consumer;
/******************************************************************************
 * This class is a homework assignment;
 * An SortedSequence is a sequence of values in sorted order.
 * It can have a special "current element," which is specified and 
 * accessed through four methods that are available in the this class 
 * (start, getCurrent, advance and isCurrent).
 ******************************************************************************/
public class SortedSequence<E> implements Cloneable {

	// TODO: Declare the private static generic Node class with fields data and next.
	// The class should be private, static and generic.
	// Please use a different name for its generic parameter.
	// It should have a constructor or two (at least the default constructor) but no methods.
	// The no-argument constructor can construct a dummy node if you would like.
	// The fields of Node should have "default" access (neither public, nor private)

	private static class Node<T> 
	{
		T data;
		Node<T> next;
		Node() // Default constructor
		{
			data =(T) this;
			next=this;
		}

		Node(T data,Node<T> n) // Specifying constructor
		{
			this.data = data;
			this.next=n;
		}
	}

	// TODO: Declare the private fields of SortedSequence:
	// One for the tail, one for manyItems and one for the precursor.
	// Finally, one for the comparator.
	// Do not declare any other fields.
	// In particular do *NOT* declare a "dummy" field.  The dummy should be a model field.
	// NB: You must use generics correctly: no raw types!
	private int manyItems;
	private Node<E>tail;
	private Node<E>precursor;
	private Comparator<E>comparator;
	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	private boolean wellFormed() {
		// Check the invariant.
		//		all in one loop
		//		if(tail==null)return report("tail is null");
		//		if(precursor==null)return report("precursor is null");
		//		boolean boo=false;
		//		int c=0;
		//		for(Node<E> n=tail.next.next;n!=tail.next;n=n.next)
		//		{
		//			if(n==precursor)boo=true;
		//			if(n.next==null)return report("“next” link is null");
		//			++c;
		//			if(n.data==null)return report("null data field");
		//		}
		//		if(!boo)return report("there is no precursor");
		//		if(manyItems!=c)return report("many Items is incorrect");

		// TODO: Implement conditions.
		//		1. The comparator may not be null
		//check to see if there are any cylcles, i got this from homework 4
		if (tail != null) {
			Node<E> slow = tail.next;
			Node<E> fast = tail.next.next;
			while (fast != null) {
				if(fast==tail)break;
				if (slow == fast) return report("Found cycle in list");
				slow = slow.next;
				fast = fast.next;
				if(fast==tail)break;
				if (fast.next != tail) fast = fast.next;
			}
		}
		if(comparator==null)return report("comparator is null");
		//		2. The tail may not be null;
		if(tail==null)return report("tail is null");
		//		3. The node must be linked in a cycle beginning and ending with the tail, and in particular
		//		none of the “next” links in any of the nodes may be null;
		for(Node<E> n=tail.next;n!=tail;n=n.next)
		{
			if(n==null)return report("“next” link is null");
		}	
		//		4. The node after the tail node must be a “dummy” node: one in which the data field
		//		refers to itself;
		if(tail.next.data!=tail.next)return report("dummy not in list");

		//		5. The “precursor” field points at a node in the cycle (possibly the dummy) and in
		//		particular, the precursor may never be null;
		if(precursor==null)return report("precursor is null");
		if(precursor!=tail.next)
		{
			boolean boo=false;
			for(Node<E> n=tail.next.next;n!=tail.next;n=n.next)
			{
				if(n==precursor)boo=true;
			}
			if(!boo)return report("there is no precursor");
		}
		//		6. The “manyItems” field must refer to the actual number of values (the number of non-
		//		dummy nodes) in the sorted sequence;
		int c=0;
		for(Node<E> n=tail.next.next;n!=tail.next;n=n.next)
		{
			++c;
		}
		if(manyItems!=c)return report("many Items is incorrect");
		//		7. No node in the linked list may have a null “data” field.
		for(Node<E> n=tail.next.next;n!=tail.next;n=n.next)
		{
			if(n.data==null)return report("null data field");
		}
		//		8. The data fields of the linked list (other than the dummy node) must be in increasing
		//		order according to the comparator.
		for(Node<E> n=tail.next.next;n!=tail;n=n.next)
		{
			if(comparator.compare(n.data, n.next.data)>=0)return report("not in inc order");;
		}

		// If no problems found, then return true:
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private SortedSequence(boolean testInvariant) { }

	/**
	 * Initialize an empty sorted sequence 
	 * using natural ordering (compareTo)
	 **/   
	public SortedSequence( )
	{
		this (null);
	}

	/**
	 * Initialize an empty sorted sequence
	 * @param comp comparator to use.  if null, then use
	 * natural ordering (compareTo).
	 */

	//obtained from chat gpt, specifically the line comp = (cur, next) -> ((Comparable<E>) cur).compareTo(next);
	@SuppressWarnings("unchecked")// for the comparator creation
	public SortedSequence(Comparator<E> comp) {
		if (comp == null) {
			// TODO: create a new Comparator
			// in which you cast the first argument to Comparable<E>
			// so that you use compareTo.
			// (Lambda syntax will make the code shorter, but is not required.)
			// Create a new Comparator using a lambda expression
			comp = (cur, next) -> ((Comparable<E>) cur).compareTo(next);
		}
		comparator=comp;
		tail=new Node<E>();
		precursor=tail;
		manyItems=0;
		tail.data=(E) tail;

		// TODO: Implemented by student.
		assert wellFormed() : "invariant failed at end of constructor";
	}

	// TODO: Implement size and the cursor methods here.
	public int size() 
	{
		assert wellFormed() : "invariant broken in size";
		return manyItems;
	}

	private Node<E> getCursor() 
	{
		return precursor.next;
	}
	public void start() {
		assert wellFormed() : "invariant broken in isCurrent";
		precursor=tail.next;
	}
	public boolean isCurrent() {
		assert wellFormed() : "invariant broken in isCurrent";
		// TODO: one liner since we can assume the invariant
		return precursor.next!=tail.next;
	}
	public E getCurrent() 
	{
		assert wellFormed() : "invariant broken in getCurrent";
		// TODO: simple since we can assume the invariant
		if(!isCurrent())throw new IllegalStateException("there is no current data");
		return getCursor().data;
	}


	public void advance() {
		assert wellFormed() : "invariant broken in advance";
		if(!isCurrent())throw new IllegalStateException("there is no current data");
		precursor=precursor.next;
		assert wellFormed() : "invariant broken by advance";
	}

	public void removeCurrent() 
	{
		assert wellFormed() : "invariant broken in remove current";
		if(!isCurrent())throw new IllegalStateException("there is no current data");
		if(getCursor()==tail)tail=precursor;
		precursor.next=getCursor().next;
		--manyItems;
		assert wellFormed() : "invariant broken by remove current";
	}
	/**
	 * Add a element to this sorted sequence. Return whether a change was made.
	 * If the given element is equal to an old element in this sorted sequence, return false.
	 * Otherwise, if it is equivalent to an existing element element, 
	 * replace the old element in this node with this new element, and return true.
	 * Finally, if it not equivalent to an existing element, insert it into the sequence 
	 * in the position indicated by the order and return true.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @return
	 *   whether a change was made to a sorted sequence.
	 **/

	//recieved help from peter, he explained the concept to me and showed me what i did wrong 
	//but we did not share code
	public boolean add(E element)
	{
		assert wellFormed() : "invariant failed at start of add";
		boolean result = true;
		// TODO
		if(element==null)throw new NullPointerException("element must not be null.");
		Node<E> e=new Node<E>(element, null);
		Node<E> first=tail.next.next;
		if (manyItems == 0) {
			tail.next = e;
			e.next = tail;
			tail = e;
			++manyItems;
		}
		else if(comparator.compare(element, first.data)<0) 
		{
			
			e.next=first;
			tail.next.next=e;
			++manyItems;
		}
		else if(comparator.compare(e.data, tail.data)>0) 
		{
			precursor=tail;
			e.next=tail.next;
			//precursor.next=e;
			tail.next=new Node<E> (element, e.next);
			tail=tail.next;
			++manyItems;
		}
		else
		{
			Node<E>lag=tail.next;
			for(Node<E> n=tail.next.next;n!=tail.next;lag=n,n=n.next)
			{
				if(comparator.compare(element, n.data)>0&&comparator.compare(element, n.next.data)<0)
				{
					e.next=n.next;
					n.next=e;
					precursor=n;
					precursor.next=e;
					++manyItems;
					result=true;
					break;
				}
				else if(comparator.compare(element, n.data)==0)
				{
					if(element!=n.data)n.data=element;
					else result=false; 
					precursor=lag;
					break;
				}
			}
		}
		assert wellFormed() : "invariant failed at end of add";
		return result;
	}

	/**
	 * Generate a copy of this sorted sequence.
	 * @return
	 *   The return value is a copy of this sorted sequence. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	@SuppressWarnings("unchecked")
	public SortedSequence<E> clone( ) { 
		assert wellFormed() : "invariant failed at start of clone";
		SortedSequence<E> answer;

		try
		{
			answer = (SortedSequence<E>) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}

		// Somewhat similar to Homework #4 :
		// (Create a new dummy node outside of loop).
		// TODO: Copy the list and update answer fields
		answer=new SortedSequence<E>(comparator);
		Node<E> p=tail.next.next;
		while(p!=tail.next)
		{
			answer.add(p.data);
			p=p.next;

		}
		Node<E> curr=answer.tail.next.next;
		if(precursor==tail.next)answer.precursor=answer.tail.next;
		else {
			for(Node<E> n=tail.next.next;n!=tail.next;n=n.next)
			{
				if(n==precursor)
				{
					answer.precursor=curr;
					break;
				}
				curr=curr.next;
			}
		}
		


		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	// don't change this nested class:
	public static class Spy<U> {
		public static class Node<V> extends SortedSequence.Node<V> {
			public Node(V d, Node<V> n) {
				super();
				data = d;
				next = n;
			}

			@SuppressWarnings("unchecked")
			public void setData(Object d) {
				data = (V)d;
			}

			public void setNext(Node<V> n) {
				next = n;
			}
		}

		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public static Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public static void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a node that points to itself
		 */
		@SuppressWarnings("unchecked")
		public Node<U> createNode() {
			Node<U> result = new Node<>(null,null);
			result.data = (U)result;
			result.next = result;
			return result;
		}

		/**
		 * Create a node for testing.
		 * @param U type of elements
		 * @param u data element
		 * @param n next pointer
		 * @return new node with the given pieces
		 */
		public Node<U> createNode(U u, Node<U> n) {
			Node<U> result = new Node<>(u, n);
			return result;
		}

		/**
		 * Create a sorted sequence for testing with the given data structure.
		 * @param U type of elements
		 * @param t tail pointer
		 * @param p precursor pointer
		 * @param s manyItems value
		 * @param c comparator
		 * @return structure
		 */
		public SortedSequence<U> create(Node<U> t, Node<U> p, int s, Comparator<U> c) {
			SortedSequence<U> result = new SortedSequence<U>(false);
			result.tail = t;
			result.precursor = p;
			result.manyItems = s;
			result.comparator = c;
			return result;
		}

		public boolean wellFormed(SortedSequence<U> seq) {
			return seq.wellFormed();
		}
	}


}

