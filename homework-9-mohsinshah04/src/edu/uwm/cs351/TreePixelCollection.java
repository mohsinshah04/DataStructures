package edu.uwm.cs351;

import java.awt.Point;
import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;


/**
 * An extensible Raster that satisfies {@link java.util.Collection} 
 * and uses binary search trees internally.
 */
public class TreePixelCollection extends AbstractCollection<Pixel> implements Cloneable
// TODO: We need to implement something so that super.clone will work.
{
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private static boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	private static class Node {
		Pixel data;
		Node left, right,next;

		Node(Pixel a) { data = a; }

		@Override //implementation
		public String toString() {
			return "Node(" + data + ")"; 
		}
	}
	// TODO: You will need to add a 'next" field to the node class

	// TODO: Declare the private fields needed given the BST data structure
	private Node dummy;
	private int size;
	private int version;
	// TODO: define private getter for model field "root"
	private Node getRoot() {return dummy.right;}

	/** Compare two points in column-major order.
	 * @param p1 first point, must not be null
	 * @param p2 second point, must not be null
	 * @return whether the first point comes before the second in
	 * column-major order (first column, then row).
	 */
	private static boolean comesBefore(Point p1, Point p2) {
		return p1.x < p2.x || (p1.x == p2.x && p1.y < p2.y);		
	}

	/**
	 * Return the number of nodes in a subtree that has no cycles.
	 * @param r root of the subtree to count nodes in, may be null
	 * @return number of nodes in subtree
	 */
	private static int countNodes(Node r) {
		if (r == null) return 0;
		return 1 + countNodes(r.left) + countNodes(r.right);
	}
	/**
	 * Find the node that has the point (if acceptEqual) or the first thing
	 * after it.  Return that node.  Return the alternate if everything in the subtree
	 * comes before the given point.
	 * @param r subtree to look into, may be null
	 * @param target point to look for, must not be null
	 * @param acceptEqual whether we accept something with this point.  Otherwise, only
	 * Pixels after the point are accepted.
	 * @param alt what to return if no node in subtree is acceptable.
	 * @return node that has the first element equal (if acceptEqual) or after
	 * the point.
	 */
	private static Node nextInTree(Node r, Point target, boolean acceptEqual, Node alt) {
		if (r == null) return alt;
		Point p = r.data.loc();
		if (p.equals(target) && acceptEqual) return r;
		if (comesBefore(target, p)) return nextInTree(r.left, target, acceptEqual, r);
		return nextInTree(r.right, target, acceptEqual, alt);
	}

	/**
	 * Return whether all the data in nodes in the subtree are non-null
	 * and in the given range, and also in their respective subranges.
	 * If there is a problem, one problem should be reported.
	 * @param r root of subtree to check, may be null
	 * @param lo exclusive lower bound, may be null (no lower bound)
	 * @param hi exclusive upper bound, may be null (no upper bound)
	 * @return whether any problems were found.
	 * If a problem was found, it has been reported.
	 */
	private static boolean allInRange(Node r, Point lo, Point hi) {
		if (r == null) return true;
		if (r.data == null) return report("found null data in tree");
		Point pt = r.data.loc();
		if (lo != null && !comesBefore(lo, pt)) return report("Out of bound: " + r.data + " is not >= " + lo);
		if (hi != null && !comesBefore(pt, hi)) return report("Out of bound: " + r.data + " is not < " + hi);
		return allInRange(r.left, lo, pt) && allInRange(r.right, pt, hi);
	}

	//worked with ademar and tutors
	private boolean wellFormed() {
		// TODO: Read Homework description
		if(dummy==null)return report("dummy is null");
		if(dummy.data!=null)return report("dummy data not null");
		if(dummy.left!=null)return report("dummy left not null");
		if(!allInRange(getRoot(), null, null))return false;
		if (countNodes(getRoot()) != size)return report("count nodes != size");
		int c=0;
		for(Node n=dummy.next;n!=null;n=n.next)
		{
			++c;
			if(c>size)break;
		}
		if(size!=c)return report("many nodes is incorrect");
		Node r=getRoot();
		if (r != null)while(r.left!=null)r=r.left;
		if(dummy.next!=r)return report("dummy next incorect");

		while(r!=null) {
			if(r.next!=nextInTree(getRoot(), r.data.loc(), false, null))return report("next pointers incorrect");
			r=r.next;
		}
		// If no problems discovered, return true
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private TreePixelCollection(boolean testInvariant) { }

	/**
	 * Create an empty raster.
	 */
	public TreePixelCollection() {
		// TODO: Implement the main constructor
		size=0;
		version=0;
		dummy=new Node(null);
		assert wellFormed() : "invariant failed at end of constructor";
	}
	/**
	 * Determine the number of elements in this linked list.
	 * @return
	 *   the number of elements in this linked list
	 **/ 
	@Override //required
	public int size( )
	{
		assert wellFormed() : "invariant failed at start of size";
		// TODO: easy
		return size;
	}
	/** Get a pixel from the raster.
	 * @param x pixel from the left (zero based), must not be negative
	 * @param y pixel from the top (zero based), must not be negative
	 * @return the pixel at x,y, or null if no pixel.
	 */
	//got from homework 8 just changed root to dummy.right
	public Pixel getPixel(int x, int y) {
		// TODO: Copy from Homework #8, but use root model field
		assert wellFormed() : "invariant failed at start of getPixel";
		if(x<0||y<0)throw new IllegalArgumentException("x or y is negative");
		Point target = new Point(x,y);//point at the x and y to reference target
		Node get =nextInTree(getRoot(),target,true,null);//look for that point
		if(get==null||!get.data.loc().equals(target))return null;
		return get.data;
	}

	/**
	 * Insert a node into the subtree and return the (modified) subtree.
	 * @param r root of subtree, may be null
	 * @param p pixel to add to tree, must not be null and must not be in tree
	 * @param before the last node before the ones in the subtree, never null
	 * @return root of new subtree
	 */
	//got logic from activity just changed comparator to comesbefore
	private Node doAdd(Node r, Pixel p, Node before) {
		// TODO: recommended
		// Very similar to the recursive doAdd done in lecture,
		// but we add the "before" parameter to recursive calls,
		// and then use it when we hit a null: "before" will be the node
		// before where we need to be in the linked list.
		if (r == null) {
			r = new Node(p);
			r.next=before.next;
			before.next=r;
		}
		else if(comesBefore(p.loc(),r.data.loc()))
			r.left = doAdd(r.left, p, before);
		else if(comesBefore(r.data.loc(),p.loc()))
			r.right = doAdd(r.right, p, r);
		return r;
	}

	@Override // implementation
	/**
	 * Set a pixel in the raster.  Return whether a change was made.
	 * If a pixel with the same coordinate was in the raster,
	 * then the new pixel replaces this one.
	 * @param p pixel to add, must not be null
	 * @return whether a change was made to a pixel.
	 */
	//worked in tutoring center, got help on little debug fixes
	public boolean add(Pixel element)
	{
		assert wellFormed() : "invariant failed at start of add";
		if(element==null)throw new NullPointerException("element must not be null.");
		if(element.loc().x<0||element.loc().y<0)
		{
			throw new IllegalArgumentException("x or y is negative");
		}
		boolean result = true;
		// TODO: First see if there is a node already with same point,
		// otherwise use the helper method
		Node nit=nextInTree(getRoot(),element.loc(),true,null); 
		if(nit!=null&&nit.data.loc().equals(element.loc()))
		{
			if(element.color().equals(nit.data.color()))result=false;
			else 
			{
				nit.data=element;
			}
		}
		else
		{
			dummy.right=doAdd(getRoot(),element,dummy);
			++version;
			++size;
		}

		assert wellFormed() : "invariant failed at end of add";
		return result;
	}
	/**
	 * Remove the node from the BST that has a pixel with the given point.
	 * This helper method will update size and version if a node is removed,
	 * otherwise not.
	 * @param r root of subtree to remove from, may be null
	 * @param pt point to look for, must not be null
	 * @param before last node before all nodes in subtree, must not be null
	 * @return new subtree (without node with given point), may be null
	 */
	//got this from lecture, changed implementation to match but its still similar to lecture
	//got help on little nitpicky things from tutors(code readability
	private Node doRemove(Node r, Point pt, Node before) {
		// TODO: implement this helper method
		if(r==null)return r;
		if(r.data.loc().equals(pt))
		{
			if(r.left==null)
			{
				//easy
				--size;++version;
				before.next=r.next;
				return r.right;
			}
			else if(r.right==null)
			{
				//medium
				--size;++version;
				Node left=r.left;
				while(left.right!=null)left=left.right;
				left.next=r.next;
				return r.left;
			}
			Node newR=r.next;
			r.data=newR.data;
			r.right=doRemove(r.right,r.data.loc(),r);
		}
		else if(comesBefore(pt,r.data.loc()))
		{
			r.left=doRemove(r.left,pt,before);
		}
		else
		{
			r.right=doRemove(r.right,pt,r);
		}
		return r;
	}
	/**
	 * Remove the pixel, if any, at the given coordinates.
	 * Returns whether there was a pixel to remove.
	 * @param x x-coordinate, must not be negative
	 * @param y y-coordinate, must not be negative
	 * @return whether anything was removed.
	 */
	public boolean clearAt(int x, int y) {
		assert wellFormed() : "invariant broken in clearAt";
		// conveniently getPixel checks the arguments for us.
		if (getPixel(x,y) == null) return false; 
		// TODO: use helper method
		dummy.right=doRemove(getRoot(),getPixel(x,y).loc(),dummy);

		assert wellFormed() : "invariant broken by clearAt";
		return true;
	}
	@Override //efficiency
	//reset everything but since structure changes increment version
	public void clear() {
		assert wellFormed() : "invariant broken in clear";
		dummy.right=null;
		dummy.next=null;
		size=0;
		++version;
		assert wellFormed() : "invariant broken by clear";
	}
	// TODO: Some Collection overridings.
	// Make sure to comment reasons for any overrides.
	@Override //efficiency
	//worked with ademar got help on the get null check
	public boolean contains(Object o) {
		assert wellFormed():"error in contains";
		if((o)instanceof Pixel)
		{
			Pixel temp=(Pixel)o;
			Node get =nextInTree(getRoot(),temp.loc(),true,null);
			if(get!=null)return temp.equals(get.data);
		}
        return false;
	}
	@Override //efficiency
	//worked with ademar on the checks before i actually return clearAt
	public boolean remove(Object o) {
		assert wellFormed():"error in remove";
		if((o)instanceof Pixel)
		{
			Pixel temp=(Pixel)o;
			Pixel clear=getPixel(temp.loc().x,temp.loc().y);
			if(clear!=null&&clear.color().equals(temp.color()))return clearAt(temp.loc().x,temp.loc().y);
		}
		assert wellFormed():"error by remove";
        return false;
    }
	/**
	 * Clone the given subtree
	 * @param r subtree to clone, may be null
	 * @return cloned subtree
	 */
	//copied from homework 8 but remove the cursor update
	private Node doClone(Node r) {
		// TODO: Similar to but easier than Homework #8
		Node rootCopy=null;
		if(r!=null)
		{
			rootCopy=new Node(r.data);//create a node that will copy the r
			rootCopy.left=doClone(r.left);//recursion for the left points
			rootCopy.right=doClone(r.right);//recursion for the right points

		}
		return rootCopy;
	}

	/**
	 * Link up the nodes in the subtree and return the first
	 * one (or the "after" if there are no nodes).
	 * This will set the "next" fields in all the nodes
	 * of the subtree.
	 * @param r subtree to work on
	 * @param after node closest after the subtree
	 * @return the first node in subtree (or the after node if none)
	 */
	//worked with ademar and peter
	private Node doLink(Node r, Node after) {
		// TODO
		if(r!=null)
		{
			Node start=doLink(r.left,r);
			r.next=doLink(r.right,after);
			return start;
		}
		return after;
	}

	@Override // decorate (we use the superclass implementation, but do more)
	//worked with peter and tutors
	public TreePixelCollection clone() {
		assert wellFormed() : "Invariant broken in clone";
		TreePixelCollection result;
		try {
			result = (TreePixelCollection) super.clone();
		} catch(CloneNotSupportedException ex) {
			throw new IllegalStateException("did you forget to implement Cloneable?");
		}
		// TODO: Work to do.
		// 1. Create new tree (as in Homework #8)
		// 2. Link together all the nodes in the result.
		//1. 
		result.dummy=new Node(null);
		result.dummy.right=doClone(getRoot());
		//2. 
		result.dummy.next=doLink(result.getRoot(),null);

		assert result.wellFormed() : "invariant faield for new clone";
		assert wellFormed():"error by clone";
		return result;
	}
	@Override //required
	public Iterator<Pixel> iterator()
	{
		return new MyIterator();

	}
	private class MyIterator implements Iterator<Pixel>
	{
		Node precursor;
		boolean hasCurrent;
		int colVersion;

		// TODO define getCursor() for model field 'cursor'
		private Node getCursor()
		{
			return precursor.next;
		}
		//worked with tutors
		private boolean wellFormed() {
			// First check outer invariant, and if that fails don't proceed further
			if(!TreePixelCollection.this.wellFormed())return false;
			// Next, if the versions don't match, pretend there are no problems.
			if(version!=colVersion)return true;
			// (Any problems could be due to being stale, which is not our fault.)
			// Then check the remaining parts of the invariant.  (See Homework description.)
			if(precursor==null)return report("precursor is null");

			if(precursor.data==null&&!precursor.equals(dummy))return report("precursor should be dummy");

			if(precursor.data!=null&&nextInTree(getRoot(), precursor.data.loc(), true, null)!=precursor)
				return report("precursor not in tree");
			if(hasCurrent==true&&getCursor()==null)return report("hascurrent and precursor next is null");

			return true;
		}

		MyIterator(boolean unused) {} // do not change this iterator

		MyIterator() {
			// Implement this constructor.  Don't forget to assert the invariant
			precursor=dummy;
			colVersion=version;
			hasCurrent=false;
			assert wellFormed():"error by iterator constructor";
		}
		//recieved logic from tutoring center george helped me with simplifying my checks to not be redundant
		@Override //required
		public boolean hasNext() {
			// TODO Auto-generated method stub
			assert wellFormed():"error in has next";
			if(colVersion!=version)throw new ConcurrentModificationException("colVersion!=version");

			if(!hasCurrent)
			{
				if(precursor.next!=null)return true;
				else return false;
			}
			else
			{
				if(precursor.next.next!=null)return true;
				else return false;
			}
		}
		//my next was initially returning the precursor.next at all cases
		//but got help by tutor when i debugged it
		//tutoring center helped me
		@Override //required
		public Pixel next() {
			assert wellFormed():"error in next";
			if (!hasNext()) throw new NoSuchElementException("no more");
			if(colVersion!=version)throw new ConcurrentModificationException("colVersion!=version");
			if(hasCurrent)precursor=precursor.next;
			hasCurrent=true;
			assert wellFormed():"error by next";
			return precursor.next.data;
		}
		@Override //required
		//worked with ademar and tutors
		public void remove() {
			// TODO Auto-generated method stub
			assert wellFormed() : "invariant broken in clearAt";
			if(colVersion!=version)throw new ConcurrentModificationException("colVersion!=version");
			if (!hasCurrent) throw new IllegalStateException("no current");
			dummy.right=doRemove(getRoot(),precursor.next.data.loc(),dummy);
			hasCurrent=false;
			colVersion=version;
			assert wellFormed() : "invariant broken by clearAt";
		}

		// TODO iterator methods
	}

	/**
	 * Class for internal testing.
	 * Do not use in client/application code.
	 * Do not change anything in this class.
	 */
	public static class Spy {
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

		private static Pixel a = new Pixel(0, 0);

		/**
		 * Class of nods for testing purposes.
		 */		
		public class Node extends TreePixelCollection.Node {
			public Node(Pixel d, Node n1, Node n2, Node n3) {
				super(a);
				data = d;
				left = n1;
				right = n2;
				next = n3;
			}
			public void setLeft(Node l) {
				left = l;
			}
			public void setRight(Node r) {
				right = r;
			}
			public void setNext(Node n) {
				next = n;
			}
		}

		/**
		 * Create a node for testing.
		 * @param a pixel, may be null
		 * @param l left subtree, may be null
		 * @param r right subtree, may be null
		 * @param n next node, may be null
		 * @return newly created test node
		 */	
		public Node newNode(Pixel a, Node l, Node r, Node n) {
			return new Node(a, l, r, n);
		}

		/**
		 * Create an instance of the ADT with give data structure.
		 * This should only be used for testing.
		 * @param d data array
		 * @param s size
		 * @param v current version
		 * @return instance of the ADT with the given field values.
		 */
		public TreePixelCollection create(Node r, int s, int v) {
			TreePixelCollection result = new TreePixelCollection(false);
			result.dummy = r;
			result.size = s;
			result.version = v;
			return result;
		}

		/**
		 * Create an iterator for testing purposes.
		 * @param outer outer object to create iterator for
		 * @param p precursor of iterator
		 * @param c whether the iterator has a current
		 * @param cv version of collection this iterator is for
		 * @return iterator to the raster
		 */
		public Iterator<Pixel> newIterator(TreePixelCollection outer, Node p, boolean c, int cv) {
			MyIterator result = outer.new MyIterator(false);
			result.precursor = p;
			result.hasCurrent = c;
			result.colVersion = cv;
			return result;
		}

		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s transaction seq to check.
		 * @return
		 */
		public boolean wellFormed(TreePixelCollection s) {
			return s.wellFormed();
		}

		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s transaction seq to check.
		 * @return
		 */
		public boolean wellFormed(Iterator<Pixel> it) {
			MyIterator myit = (MyIterator)it;
			return myit.wellFormed();
		}

	}
}
