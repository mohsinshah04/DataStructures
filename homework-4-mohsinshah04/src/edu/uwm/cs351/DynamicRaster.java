//Mohsin Shah
package edu.uwm.cs351;

import java.awt.Color;
import java.awt.Point;
import java.util.function.Consumer;

/**
 * A collection of pixels on the screen, typically arranged 
 * in a rectangular form.
 */
public class DynamicRaster {
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
	private Node head;
	private Node precursor;
	private int manyNodes;
	// TODO data structure: Node class and fields
	private static class Node
	{
		Pixel data;
		Node next;
		Node(Pixel p)
		{
			this.data=p;
			next=null;
		}
	}

	private boolean comesBefore(Point p1, Point p2) {

		if(p1.x==p2.x)//if p1 comes before p2
		{
			if(p1.y>=p2.y)return false;
		}
		if(p1.x>p2.x)
		{
			return false;
		}
		return true; // TODO

	}

	private boolean wellFormed() {
		// Check the invariant.
		// 1. The list has no cycles
		// 2. The precursor field is null or points to a node in the list.
		// 3. manyNodes accurately contains the number of nodes in the list (starting at head).
		// 4. No pixel is null
		// 5. No pixel has negative coordinates
		// 6. The pixels are in column major order

		// The first check is given to you:
		// It uses Floyd's Tortoise & Hare algorithm
		if (head != null) {
			Node slow = head;
			Node fast = head.next;
			while (fast != null) {
				if (slow == fast) return report("Found cycle in list");
				slow = slow.next;
				fast = fast.next;
				if (fast != null) fast = fast.next;
			}
		}

		// TODO
		// 2. The precursor field is null or points to a node in the list.
		boolean res=false;
		if(precursor!=null)
		{
			for(Node n=head;n!=null;n=n.next)
			{
				if(precursor==n) res=true;
			}
			if(res==false)return report("precursor field is not null or doesnt point to a node in the list");
		}

		// 3. manyNodes accurately contains the number of nodes in the list (starting at head).
		int c=0;
		for(Node n=head;n!=null;n=n.next)
		{
			++c;
		}
		if(manyNodes!=c)return report("many nodes is incorrect");
		// 4. No pixel is null
		for(Node n=head;n!=null;n=n.next)
		{
			if(n.data==null)return report("pixel is null");
		}		
		// 5. No pixel has negative coordinates
		int newx;
		int newy;
		for(Node n=head;n!=null;n=n.next)
		{
			newx=n.data.loc().x;
			newy=n.data.loc().y;
			if(newx<0||newy<0)return report("the x or y are negative");
		}	
		// 6. The pixels are in column major order
		int nextx;
		int nexty;
		for(Node n=head;n!=null;n=n.next)
		{
			if(n.next==null)break;
			newx=n.data.loc().x;
			newy=n.data.loc().y;
			nextx=n.next.data.loc().x;
			nexty=n.next.data.loc().y;
			if(newx==nextx)
			{
				if(newy>=nexty)return report("y>nexty or x and y are dupes");
			}
			if(newx>nextx)
			{
				return report("x>nextx");
			}
		}
		// If no problems discovered, return true
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private DynamicRaster(boolean testInvariant) { }

	/**
	 * Create an empty raster..
	 */
	public DynamicRaster() {
		//TODO.  Make sure that you assert the invariant at the end only
		head=null;//set everything to null at the start
		precursor=null;
		manyNodes=0;
		assert wellFormed() : "invariant broken in Constructor";
	}

	@Override // implementation
	public String toString() {
		// don't assert invariant, so we can use this for testing/debugging
		StringBuilder sb = new StringBuilder();
		boolean foundPre = precursor == null;
		Node lag = null;
		Node fast = head;
		sb.append("[");
		for (Node p=head; p != null; p = p.next) {
			if (p == precursor) foundPre = true;
			if (fast != null) fast = fast.next;
			if (p != head) sb.append(", ");
			if (lag == precursor) {
				sb.append("*");
				foundPre = true;
			}
			sb.append(p.data);
			if (p == fast) {
				sb.append(" ???");
				break;
			}
			lag = p;
			if (fast != null) fast = fast.next;
		}
		sb.append("]:" + manyNodes + (foundPre ? "" : "*?"));
		return sb.toString();
	}

	/** Get a pixel from the raster
	 * @param x x-coordinate, must not be negative
	 * @param y y-coordinate, must not be negative
	 * @return the pixel at x,y, or null if no pixel.
	 */
	public Pixel getPixel(int x, int y) {
		//TODO return the pixel at x, y
		//Throw IllegalArgumentException if either x or y is negative
		assert wellFormed() : "invariant broken in getPixel";
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be non-negative.");
		}
		Pixel p = new Pixel(x,y);
		if(p!=null)//pixel cant be null
		{
			if(head!=null)//there has to be a list
			{
				for(Node m=head;m!=null;m=m.next)//TEST IFFECIENCY TEST 2
				{	//when you get to the point then you return the point and the color
					if(p.loc().x==m.data.loc().x&&p.loc().y==m.data.loc().y)return new Pixel(x,y,m.data.color());
				}
			}
		}
		assert wellFormed() : "invariant broken by getPixel";
		return null;
	}

	/**
	 * Set a pixel in the raster.  Return whether a change was made.
	 * This pixel is now current.
	 * @param p pixel to add, must not be null
	 * @return whether a change was made to a pixel.
	 * But the pixel will be current whetehr or not it was newly added.
	 */
	public boolean add(Pixel p) {
		assert wellFormed() : "invariant broken in add";
		boolean result = true;
		if(p==null)throw new NullPointerException("Pixel must not be null.");
		int px=p.loc().x;
		int py=p.loc().y;
		if(px<0||py<0)
		{
			throw new IllegalArgumentException("x or y is negative");
		}
		Node n=new Node(p);
		Node lag=null;
		//++manyNodes;
		if(head==null)//creating from scratch
		{
			head=n;
			//precursor=null;
			++manyNodes;
		}
		else if(comesBefore(p.loc(), head.data.loc()))//adding before head
		{
			Node temp=head;
			head=n;
			n.next=temp;
			++manyNodes;
			precursor=null;
			return true;

		}
		else {
			for(Node m=head;m!=null;lag=m,m=m.next)
			{
				if(m.data.loc().equals(p.loc()))//adding in the same spot
				{
					if(m.data.color().equals(p.color()))//check to see if the colors are the same
					{
						precursor=lag;
						return false;
					}
					m.data=p;
					//precursor=m; 
					precursor=lag;
					return true;
				}
				if(!(comesBefore(m.data.loc(), p.loc())))//adding in between 2 nodes
				{
					if(lag!=null)
					{
						lag.next=n;
					}
					n.next=m;
					++manyNodes;
					precursor=lag;
					return true;
				}
			}
			lag.next=n;
			precursor=lag;
			++manyNodes;
		}
		assert wellFormed() : "invariant broken by add";
		return result;
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
		// TODO: easy if you use getPixel to avoid lots of work.
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be non-negative.");
		}
		if(head==null)
		{
			return false;
		}
		Pixel p =getPixel(x,y);//create something for us to use
		Node lag=null;
		if(p==null) 
		{
			return false;
		}
		else {
			//remove pixel
			for(Node m=head;m!=null;lag=m,m=m.next)
			{
				if(p.equals(m.data))
				{
					if(head==m)
					{
						if(head==precursor)
						{
							precursor=null;
						}
						head=m.next;
					}
					else{
						Node temp=lag.next;
						lag.next=m.next;
						if(precursor==temp)
						{
							precursor=getTail();
						}

					}
					break;

				}
			}
			--manyNodes;
		}
		assert wellFormed() : "invariant broken by clearAt";
		return true;
	}

	/**
	 * Return the number of pixels in the raster.
	 * @return number of (non-null) pixels
	 */
	public int size() {
		assert wellFormed() : "invariant broken in size";
		return manyNodes;
	}


	/// Cursor methods

	// TODO Implement model field "cursor" (i.e., "getCursor()")
	private Node getCursor() 
	{
		return precursor==null ? head : precursor.next;
	}
	private Node getTail() 
	{
		for(Node m=head;m!=null;m=m.next)
		{		
			if(m.next==null)return m;
		}
		return null;
	}
	/**
	 * Move the cursor to the beginning, first pixel in the raster,
	 * if any.
	 */
	public void start() {
		// TODO: don't forget to check invariant
		assert wellFormed() : "invariant broken in isCurrent";
		precursor=null;
	}

	/**
	 * Return whether we have a current pixel
	 * @return whether there is a current pixel.
	 */
	public boolean isCurrent() {
		assert wellFormed() : "invariant broken in isCurrent";
		// TODO: one liner since we can assume the invariant
		return getCursor()!=null;
	}

	/**
	 * Return the current pixel.
	 * @exception IllegalStateException if there is no current pixel
	 * @return the current pixel, never null.
	 */
	public Pixel getCurrent() {
		assert wellFormed() : "invariant broken in getCurrent";
		// TODO: simple since we can assume the invariant
		if(!isCurrent())throw new IllegalStateException("there is no current pixel");
		return getCursor().data;
	}

	/**
	 * Move on to the next pixel, if any.  The pixels are organized 
	 * left to right and top-to-bottom in each column.
	 * If there are no more pixels, then afterwards, {@link #isCurrent()}
	 * will return false.
	 * @throws IllegalStateException if there is no current pixel before this operation starts
	 */
	public void advance() {
		// TODO: Don't forget to check the invariant before and after!
		assert wellFormed() : "invariant broken in advance";
		if(!isCurrent())throw new IllegalStateException("there is no current pixel");
		precursor=getCursor();
		assert wellFormed() : "invariant broken by advance";
	}

	/**
	 * Remove the current pixel, advancing the cursor to the next pixel.
	 * @throws IllegalStateException if there is no current pixel.
	 */
	public void removeCurrent() {
		// TODO: Don't forget to check the invariant before and after!
		// Hint: let "advance" do most of the work.  Rely on the invariant.
		assert wellFormed() : "invariant broken in remove current";
		if(!isCurrent())throw new IllegalStateException("there is no current pixel");
		clearAt(getCurrent().loc().x,getCurrent().loc().y);
		assert wellFormed() : "invariant broken by remove current";
	}

	/**
	 * Class for internal testing.  Do not modify.
	 * Do not use in client/application code
	 */
	public static class Spy {
		/**
		 * A public versio of the data structure's internal node class.
		 * This class is only used for testing.
		 */
		public static class Node extends DynamicRaster.Node {
			/**
			 * Create a node with null data and null next fields.
			 */
			public Node() {
				this(null, null);
			}
			/**
			 * Create a node with the given values
			 * @param p data for new node, may be null
			 * @param n next for new node, may be null
			 */
			public Node(Pixel p, Node n) {
				super(null);
				this.data = p;
				this.next = n;
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
		 * Create a node for testing.
		 * @param p pixel, may be null
		 * @param n next node, may be null
		 * @return newly ceated test node
		 */
		public Node newNode(Pixel p, Node n) {
			return new Node(p, n);
		}

		/**
		 * CHange a node's next field
		 * @param n1 node to change, must not be null
		 * @param n2 node to point to, may be null
		 */
		public void setNext(Node n1, Node n2) {
			n1.next = n2;
		}

		/**
		 * Create an instance of the ADT with give data structure.
		 * This should only be used for testing.
		 * @param h head of linked list
		 * @param s size
		 * @param x current x
		 * @param y current y
		 * @return instance of DynamicRaster with the given field values.
		 */
		public DynamicRaster create(Node h, int s, Node p) {
			DynamicRaster result = new DynamicRaster(false);
			result.head = h;
			result.manyNodes = s;
			result.precursor = p;
			return result;
		}

		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s transaction seq to check.
		 * @return
		 */
		public boolean wellFormed(DynamicRaster s) {
			return s.wellFormed();
		}


	}
}
