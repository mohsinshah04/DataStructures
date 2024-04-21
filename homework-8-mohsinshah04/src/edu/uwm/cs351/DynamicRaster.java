// This is an assignment for students to complete after reading Chapter 3 of
// "Data Structures and Other Objects Using Java" by Michael Main.

package edu.uwm.cs351;

import java.awt.Point;
import java.util.function.Consumer;

/******************************************************************************
 * This class is a homework assignment;
 * A DynamicRaster is a sequence of Pixel objects in column-major order.
 * The raster can have a special "current element," which is specified and 
 * accessed through four methods that are available in the this class 
 * (start, getCurrent, advance and isCurrent).
 ******************************************************************************/
public class DynamicRaster implements Cloneable {
	// TODO: Declare the private static Node class.
	// It should have a constructor but no methods.
	// The constructor should take an Pixel.
	// The fields of Node should have "default" access (neither public, nor private)
	// and should not start with underscores.

	// TODO: Declare the private fields needed given the BST data structure

	//got the structure from the spy class and changed it accordingly
	private static class Node 
	{
		Node left,right;
		Pixel data;
		Node(Pixel d) // Specifying constructor
		{
			data = d;
			left=right=null;
		}
	}
	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };

	private static boolean report(String error) {
		reporter.accept(error);
		return false;
	}

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
		// TODO (use recursion)
		if (r == null)return 0; 
		//recursion until its empty
		return 1+countNodes(r.left)+countNodes(r.right);
	}

	/**
	 * Return the first node in a non-empty subtree.
	 * It doesn't examine the data in teh nodes; 
	 * it just uses the structure.
	 * @param r subtree, must not be null
	 * @return first node in the subtree
	 */
	private static Node firstInTree(Node r) {
		// TODO: non-recursive is fine
		if (r == null)return null;//throw new NullPointerException("firstintree is null"); // Base case: an empty subtree has 0 nodes.
		while(r.left!=null)r=r.left;
		return r;
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
	//in tutoring center, talked about this with tutors and peter, no sharing code was
	//involved but logic may be similar as our understanding was the same
	private static Node nextInTree(Node r, Point target, boolean acceptEqual, Node alt) {
		// TODO: recursion not required, but is simpler.
		if(r==null)return alt;
		// Compare the current with the target.
		if(target.equals(r.data.loc())&&acceptEqual)return r;
		//if target is before current
		else if(comesBefore(target,r.data.loc()))return nextInTree(r.left,target,acceptEqual,r);
		//if current is before target
		else return nextInTree(r.right,target,acceptEqual,alt);
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
	//got my initial logic down but talked with tutor on how to get it shorter, heres the result
	private static boolean allInRange(Node r, Point lo, Point hi) {
		// TODO
		if(r==null)return true;
		if(r.data==null)return report("data is null");
		if(lo!=null &&  !comesBefore(lo,r.data.loc())) return report("lo incorrect");
		if(hi!=null && !comesBefore(r.data.loc(),hi)) return report("hi incorrect");
		return allInRange(r.left,lo,r.data.loc()) && allInRange(r.right,r.data.loc(),hi);



	}

	public Node root,cursor;
	public int manyItems;

	private boolean wellFormed() {
		// Check the invariant.
		// Invariant:
		// 1. The number of nodes must match manyItems
		// 2. Every node's data must not be null and be in range.
		// 3. The cursor must be null or in the tree.

		// Implementation:
		// Make sure to check the second check first or else you risk
		// getting caught in a cycle. Also make sure not to double report
		// for a problem.  Each problem must be reported exactly once.
		// TODO: Use helper methods

		// 2. Every node's data must not be null and be in range.
		if(!allInRange(root, null, null))return false;

		// 1. The number of nodes must match manyItems
		if (countNodes(root) != manyItems)return report("count nodes != manyItems");

		// 3. The cursor must be null or in the tree.
		if (cursor != null && (cursor.data==null || nextInTree(root, cursor.data.loc(), true, null) != cursor))
			return report("Cursor not in tree");

		// If no problems found, then return true:
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private DynamicRaster(boolean testInvariant) { }

	/**
	 * Initialize an empty raster. 
	 **/   
	public DynamicRaster( )
	{
		// TODO: Implemented by student.
		manyItems=0;
		cursor=root=null;
		assert wellFormed() : "invariant failed at end of constructor";
	}

	/**
	 * Determine the number of elements in this raster.
	 * @return
	 *   the number of elements in this raster
	 **/ 
	public int size( )
	{
		assert wellFormed() : "invariant failed at start of size";
		// TODO: easy
		return manyItems;
	}

	/**
	 * Set the current element at the top-most pixel in
	 * the left-most column of this raster if there are any suc.
	 * Otherwise, do nothing (still no current element).
	 **/ 
	public void start( )
	{
		assert wellFormed() : "invariant failed at start of start";
		// TODO: Implemented by student.
		//you go to the first in tree, as its the leftest node
		cursor=firstInTree(root);
		assert wellFormed() : "invariant failed at end of start";
	}

	/**
	 * Accessor method to determine whether this raster has a specified 
	 *w current element that can be retrieved with the 
	 * getCurrent method. 
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent()
	{
		assert wellFormed() : "invariant failed at start of isCurrent";
		// TODO: Implemented by student.
		return cursor!=null;
	}

	/**
	 * Accessor method to get the current element of this raster. 
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this raster, never null
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public Pixel getCurrent( )
	{
		assert wellFormed() : "invariant failed at start of getCurrent";
		// TODO: Implemented by student.
		if(!isCurrent())throw new IllegalStateException("no current");
		return cursor.data;
	}

	/**
	 * Move forward, so that the current element will be the next element in
	 * this raster.
	 * @precondition
	 *   isCurrent() returns true. 
	 * @postcondition
	 *   If the current element was already the end element of this raster 
	 *   (with nothing after it), then there is no longer any current element. 
	 *   Otherwise, the new element is the element immediately after the 
	 *   original current element.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   advance may not be called.
	 **/
	public void advance( )
	{
		assert wellFormed() : "invariant failed at start of advance";
		// TODO: See homework description.
		// firstInTree and nextInTree are useful.
		if(!isCurrent())throw new IllegalStateException("no current");
		//setting acceptEqual to false so i go to the next point if any
		//reset the cursor because thats what we use for our advance indication
		cursor=nextInTree(root,cursor.data.loc(),false, null);
		assert wellFormed() : "invariant failed at end of advance";
	}

	/**
	 * Remove the current element from this raster.
	 * NB: Not supported in Homework #8
	 **/
	public void removeCurrent( )
	{
		assert wellFormed() : "invariant failed at start of removeCurrent";
		throw new UnsupportedOperationException("remove is not implemented");
	}

	/**
	 * Add a new pixel to this raster, in order.  If an equal Pixel is already
	 * in the raster, nothing is added, and we return false.
	 * If there was a pixel at the same location with a different color,
	 * it is replaced.  Otherwise it is added in column-major order. 
	 * Afterwards, whether or not a pixel was replaced or added,
	 * the pixel is the current element of the raster.
	 * @param element
	 *   the new element that is being added, must not be null.
	 * @return false if the pixel was already in the raster, otherwise true
	 * @exception IllegalArgumentException pixel being added has a negative x or negative y coordinate.
	 **/
	public boolean add(Pixel element)
	{
		assert wellFormed() : "invariant failed at start of add";
		boolean result = true;
		// TODO: Recursion, even with a helper method
		// is hard to do right.  We recommend a non-recursive solution.
		//Node e= new Node(element);//holder
		if(element==null)throw new NullPointerException("element must not be null.");
		if(element.loc().x<0||element.loc().y<0)
		{
			throw new IllegalArgumentException("x or y is negative");
		}
		if(root==null)//adding to an empty list
		{
			root = new Node(element);
			cursor=root;
			++manyItems;
		}
		else
		{
			Node r = root;
			while(r!=null)
			{
				//going left subtree
				if(comesBefore(element.loc(),r.data.loc()))
				{
					if(r.left==null)//if its null then we can add here 
					{
						r.left=new Node(element);
						++manyItems;
						cursor=r.left;
						break;
					}
					else r=r.left;//we have to keep going left until it is null
				}
				//going right subtree
				else if(comesBefore(r.data.loc(),element.loc()))
				{
					if(r.right==null)//if its null we can add here
					{
						r.right=new Node(element);
						++manyItems;
						cursor=r.right;
						break;
					}
					else r=r.right;//we have to keep going right until it is null
				}
				//same location
				else
				{
					cursor=r;
					if(element.color().equals(r.data.color()))//same color
					{
						result=false;
					}
					else//same loc but dif color
					{
						r.data=element;//refresh color
						//result is already true
					}
					break;
				}
			}
		}
		assert wellFormed() : "invariant failed at end of add";
		return result;
	}

	/** Get a pixel from the raster
	 * @param x x-coordinate, must not be negative
	 * @param y y-coordinate, must not be negative
	 * @return the pixel at x,y, or null if no pixel.
	 */
	public Pixel getPixel(int x, int y) {
		// TODO
		assert wellFormed() : "invariant failed at start of getPixel";
		if(x<0||y<0)throw new IllegalArgumentException("x or y is negative");
		Point target = new Point(x,y);//point at the x and y to reference target
		Node get =nextInTree(root,target,true,null);//look for that point
		if(get==null||!get.data.loc().equals(target))return null;
		return get.data;
	}

	// TODO: private recursive helper method for clone.
	// - Must be recursive
	// - Take the answer as a parameter so you can set the cloned cursor
	
	//got help on the logic for creating the method header by a tutor, talked with peter and tutor,
	//header is similar but logic behind the code is different
	private Node clone(Node r, DynamicRaster answer)
	{
		Node rootCopy=null;
		if(r!=null)
		{
			rootCopy=new Node(r.data);//create a node that will copy the r
			rootCopy.left=clone(r.left, answer);//recursion for the left points
			rootCopy.right=clone(r.right, answer);//recursion for the right points
			if(r==cursor)answer.cursor=rootCopy;//setting cursor whenever i get there

		}
		return rootCopy;//return the node with its left and right assignment
	}
	/**
	 * Generate a copy of this raster.
	 * @return
	 *   The return value is a copy of this raster. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	public DynamicRaster clone( ) { 
		assert wellFormed() : "invariant failed at start of clone";
		DynamicRaster answer;

		try
		{
			answer = (DynamicRaster) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}

		// TODO: copy the structure (use helper method)
		answer.root=clone(root, answer);//you are cloning at the point and all its sub
		//nodes are also copied by the recursion
		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	// don't change this nested class:
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
		public class Node extends DynamicRaster.Node {
			public Node(Pixel d, Node n1, Node n2) {
				super(a);
				data = d;
				left = n1;
				right = n2;
			}
			public void setLeft(Node l) {
				left = l;
			}
			public void setRight(Node r) {
				right = r;
			}
		}

		/**
		 * Create a node for testing.
		 * @param a pixel, may be null
		 * @param l left subtree, may be null
		 * @param r right subtree, may be null
		 * @return newly created test node
		 */	
		public Node newNode(Pixel a, Node l, Node r) {
			return new Node(a, l, r);
		}

		public DynamicRaster create(Node r, int m, Node c) {
			DynamicRaster result = new DynamicRaster(false);
			result.root = r;
			result.manyItems = m;
			result.cursor = c;
			return result;
		}


		/// relay methods for helper methods:

		public int countNodes(Node r) {
			return DynamicRaster.countNodes(r);
		}

		public boolean allInRange(Node r, Point lo, Point hi) {
			return DynamicRaster.allInRange(r, lo, hi);
		}

		public boolean wellFormed(DynamicRaster r) {
			return r.wellFormed();
		}

		public Node firstInTree(Node r) {
			return (Node)DynamicRaster.firstInTree(r);
		}

		public Node nextInTree(Node r, Point a, boolean acceptEquiv, Node alt) {
			return (Node)DynamicRaster.nextInTree(r, a, acceptEquiv, alt);
		}

	}
}

