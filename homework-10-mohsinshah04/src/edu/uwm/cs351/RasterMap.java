package edu.uwm.cs351;

import java.awt.Color;
import java.awt.Point;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

import edu.uwm.cs351.util.AbstractEntry;


//worked with peter and tutoring center
//explained concepts to hassan and jack
/**
 * An extensible Raster that satisfies {@link java.util.Map} 
 * and uses binary search trees internally.
 */
public class RasterMap 
extends AbstractMap<Point,Color> implements Cloneable, Map<Point, Color> // TODO: change this!
{
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private static boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	private static class Node {
		Pixel data;
		Node left, right;
		Node next;

		Node(Pixel a) { data = a; }

		@Override //implementation
		public String toString() {
			return "Node(" + data + ")"; 
		}
	}

	private Node dummy;
	private int size;
	private int version;

	private Node getRoot() {
		return dummy.right;
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

	private boolean wellFormed() {
		// Check the invariant.
		// 1. the dummy is set up correctly
		if (dummy == null) return report("dummy is null");

		// 2. the dummy's data is not null
		if (dummy.data != null) return report("dummy has real data");

		// 3. the dummy's let subtree is not null
		if (dummy.left != null) return report("dummy's left is not null");

		// 4. the elements in the tree are in range
		if (!allInRange(getRoot(), null, null)) return false; // already reported

		// 5. the count is correct
		if (countNodes(getRoot()) != size) return report("manyItems is inconsisrent: " + size);

		// 6. All nodes in the chain from the dummy and in the tree correctly record
		if (size == 0) {
			if (dummy.next != null) return report("first node cannot exist in empty tree");
		} else {
			Node p = dummy.right;
			while (p.left != null) p = p.left;
			if (dummy.next != p) return report("dummy's next points at wrong node " + dummy.next);
			while (p != null) {
				Node n = nextInTree(getRoot(), p.data.loc(), false, null);
				if (p.next != n) return report("next for " + p + " is wrong: " + p.next);
				p = n;
			}
		}

		// If no problems discovered, return true
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private RasterMap(boolean testInvariant) { }

	/**
	 * Create an empty raster.
	 */
	public RasterMap() {
		dummy = new Node(null);
		assert wellFormed() : "invariant broken by constructor";
	}

	/** Get a pixel from the raster.
	 * @param x pixel from the left (zero based), must not be negative
	 * @param y pixel from the top (zero based), must not be negative
	 * @return the pixel at x,y, or null if no pixel.
	 */
	public Pixel getPixel(int x, int y) {
		assert wellFormed() : "invariant broken at start of getPixel()";
		if(x < 0)
			throw new IllegalArgumentException("column cannot be negative");
		if(y < 0)
			throw new IllegalArgumentException("row cannot be negative");
		Point pt = new Point(x,y);
		Node n = nextInTree(getRoot(), pt, true, null);
		if (n != null && n.data.loc().equals(pt)) return n.data;
		return null;
	}

	/**
	 * Insert a node into the subtree and return the (modified) subtree.
	 * @param r root of subtree, may be null
	 * @param p pixel to add to tree, must not be null and must not be in tree
	 * @param before the last node before the ones in the subtree, never null
	 * @return root of new subtree
	 */
	private Node doAdd(Node r, Pixel p, Node before) {
		if (r == null) {
			r = new Node(p);
			r.next = before.next;
			before.next = r;
		} else if (comesBefore(p.loc(), r.data.loc())) {
			r.left = doAdd(r.left, p, before);
		} else {
			r.right = doAdd(r.right, p, r);
		}
		return r;
	}


	/**
	 * Set a pixel in the raster.  Return whether a change was made.
	 * If a pixel with the same coordinate was in the raster,
	 * then the new pixel replaces this one.
	 * @param p pixel to add, must not be null
	 * @return whether a change was made to a pixel.
	 */
	public boolean add(Pixel element)
	{
		assert wellFormed() : "invariant failed at start of add";
		boolean result = true;
		if (element.loc().x < 0 || element.loc().y < 0) throw new IllegalArgumentException("Cannot add pixel with negative coords");
		Point pt = element.loc();
		Node n = nextInTree(getRoot(), pt, true, null);
		if (n != null && n.data.loc().equals(pt)) {
			if (n.data.equals(element)) {
				result = false;
			} else {
				n.data = element;
			}
		} else {
			dummy.right = doAdd(getRoot(), element, dummy);
			++size;
			++version;
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
	private Node doRemove(Node r, Point pt, Node before) {
		if (r != null) {
			Point here = r.data.loc();
			if (here.equals(pt)) {
				if (r.left == null) {
					before.next = r.next;
					--size;
					++version;
					return r.right;
				}
				Node pre = r.left;
				while (pre.right != null) pre = pre.right;
				assert pre.next == r;
				pre.next = pre; // temporarily
				r.left = doRemove(r.left, pre.data.loc(), before);
				pre.left = r.left;
				pre.right = r.right;
				pre.next = r.next;
				return pre;
			} else if (comesBefore(pt, here)) {
				r.left = doRemove(r.left, pt, before);
			} else {
				r.right = doRemove(r.right, pt, r);
			}
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
		dummy.right = doRemove(dummy.right, new Point(x, y), dummy);
		assert wellFormed() : "invariant broken by clearAt";
		return true;
	}

	/**
	 * Return the number of pixels in the raster.
	 * @return number of (non-null) pixels
	 */
	@Override // required
	public int size() {
		assert wellFormed() : "invariant broken in size";
		return size;
	}


	public Iterator<Pixel> iterator() {
		assert wellFormed() : "invariant broken in iterator";
		return new MyIterator();
	}
	//not overriden anymore
	public boolean contains(Object x) {
		assert wellFormed() : "Invariant broken in contains";
		if (!(x instanceof Pixel)) return false;
		Pixel p = (Pixel)x;
		return p.equals(getPixel(p.loc().x, p.loc().y));
	}

	@Override //efficiency
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		if (!(key instanceof Point)) return false;
		Point p = (Point)key;
		Pixel get=getPixel(p.x,p.y);
		return get!=null;
	}

	@Override // efficiency
	public Color remove(Object x) {
		assert wellFormed() : "Invariant broken in remove";
		if (!(x instanceof Point)) return null;
		Point pt = ((Point)x);
		Pixel p=getPixel(pt.x, pt.y);
		clearAt(pt.x, pt.y);
		if(p==null)return null;
		return p.color();
	}

	/**
	 * Clone the given subtree
	 * @param r subtree to clone, may be null
	 * @return cloned subtree
	 */
	private Node doClone(Node r) {
		if (r == null) return null;
		Node copy = new Node(r.data);
		copy.left = doClone(r.left);
		copy.right = doClone(r.right);
		return copy;
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
	private Node doLink(Node r, Node after) {
		if (r == null) return after;
		r.next = doLink(r.right, after);
		return doLink(r.left, r);
	}

	@Override // decorate (we use the superclass implementation, but do more)
	public RasterMap clone() {
		assert wellFormed() : "invariant broken in clone";
		RasterMap result;
		try {
			result = (RasterMap) super.clone();
		} catch(CloneNotSupportedException ex) {
			throw new IllegalStateException("did you forget to implement Cloneable?");
		}
		result.dummy = doClone(dummy);
		doLink(result.dummy, null);

		assert result.wellFormed() : "invariant faield for new clone";
		return result;
	}

	private class MyIterator implements Iterator<Pixel>
	{
		Node precursor;
		boolean hasCurrent;
		int colVersion;

		Node getCursor() {
			if (hasCurrent) return precursor.next;
			else return precursor;
		}

		private boolean wellFormed() {
			if (!RasterMap.this.wellFormed()) return false;
			if (version != colVersion) return true;
			if (precursor == null) return report("precursor is null");
			if (precursor != dummy) {
				if (precursor.data == null) return report("precursor has null data");
				if (precursor != nextInTree(dummy.right, precursor.data.loc(), true, null)) {
					return report("Precursor is not in tree");
				}
			}
			if (hasCurrent && precursor.next == null) return report("Cannot have current at end");
			return true;
		}

		MyIterator(boolean unused) {} // do not changethis iterator

		MyIterator() {
			precursor = dummy;
			hasCurrent = false;
			colVersion = version;
			assert wellFormed() : "invariant broken by start";
		}

		private void checkVersion() {
			if (version != colVersion) throw new ConcurrentModificationException("sale!");
		}

		@Override // required
		public boolean hasNext() {
			assert wellFormed() : "invariant broken in isCurrent";
			checkVersion();
			return getCursor().next != null;
		}

		@Override // required
		public Pixel next() {
			assert wellFormed() : "invariant broken in getCurrent";
			if (!hasNext()) throw new NoSuchElementException("no current!");
			if (hasCurrent) precursor = precursor.next;
			else hasCurrent = true;
			assert wellFormed();
			return getCursor().data;
		}

		@Override // implementation
		public void remove() {
			assert wellFormed() : "invariant broken in remove";
			checkVersion();
			if (!hasCurrent) throw new IllegalStateException("no current to remove");
			dummy.right = doRemove(dummy.right, getCursor().data.loc(), dummy);
			hasCurrent = false;
			colVersion = version;
			assert wellFormed() : "invariant broken by remove";
		}
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
		public class Node extends RasterMap.Node {
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
		public RasterMap create(Node r, int s, int v) {
			RasterMap result = new RasterMap(false);
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
		public Iterator<Pixel> newIterator(RasterMap outer, Node p, boolean c, int cv) {
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
		public boolean wellFormed(RasterMap s) {
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
	@Override//implementation
	public Color put(Point key, Color value) {
		// TODO Auto-generated method stub
		Pixel p=getPixel(key.x,key.y);
		add(new Pixel(key,value));
		if(p==null)return null;
		return p.color();
	}
	@Override//efficiency
	public Color get(Object key) {
		// TODO Auto-generated method stub
		if (!(key instanceof Point)||key==null) return null;
		Point k=(Point)key;
		Pixel p=getPixel(k.x,k.y);
		if(p==null)return null;
		return p.color();
	}
	private Set<Point> keySet;
	@Override//Efficiency
	public Set<Point> keySet() {
		// TODO Auto-generated method stub
		if(keySet==null)keySet=new AbstractSet<Point>()
		{
			Set<Point> set= RasterMap.super.keySet();
			@Override //required
			public Iterator<Point> iterator() {
				// TODO Auto-generated method stub
				return set.iterator();
			}

			@Override //required
			public int size() {
				// TODO Auto-generated method stub
				return set.size();
			}

			@Override//efficiency
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				if (!(o instanceof Point)) return false;
				return clearAt(((Point)o).x,((Point)o).y);
			}
			@Override//efficiency
			public boolean contains(Object o) {
				// TODO Auto-generated method stub
				if (!(o instanceof Point)) return false;
				return RasterMap.this.containsKey(o);
			}
		};
		return keySet;
	}

	private EntrySet entrySet;
	@Override//required
	public Set<Entry<Point, Color>> entrySet() {
		// TODO Auto-generated method stub
		if(entrySet==null)entrySet= new EntrySet();
		return entrySet;
	}
	private class EntrySet extends AbstractSet<Entry<Point,Color>> {

		@Override//required
		public Iterator<Entry<Point,Color>> iterator() 
		{
			// TODO Auto-generated method stub
			return new Iterator<Entry<Point,Color>>()
			{
				MyIterator it=new MyIterator();
				@Override//required
				public boolean hasNext() 
				{
					// TODO Auto-generated method stub
					return it.hasNext();
				}

				@Override //required
				public Entry<Point, Color> next() 
				{
					it.next();
					// TODO Auto-generated method stub
					AbstractEntry<Point, Color> ab=new AbstractEntry<Point, Color>() 
					{
						@Override //required
						public Point getKey() 
						{
							// TODO Auto-generated method stub
							return it.getCursor().data.loc();
						}

						@Override //required
						public Color getValue() 
						{
							// TODO Auto-generated method stub
							return it.getCursor().data.color();
						}

						@Override //implementation
						public Color setValue(Color v) 
						{
							// TODO Auto-generated method stub
							return put(it.getCursor().data.loc(),v);
						}
					};
					return ab;
				}@Override //required
				public void remove() 
				{
					it.remove();
				}

			} ;

		}

		@Override//required
		public int size() {
			// TODO Auto-generated method stub
			return size;
		}
		@Override //efficiency
		public boolean contains(Object x) {
			assert wellFormed() : "Invariant broken in contains";
			if (!(x instanceof Map.Entry<?, ?>)) return false;
			Map.Entry<?, ?> p = (Map.Entry<?, ?>)x;
			if(!(p.getKey() instanceof Point)||!(p.getValue() instanceof Color))return false;
			Point pt = ((Point)p.getKey());
			Color c=((Color)p.getValue());
			Pixel pc=new Pixel(pt, c);
			return RasterMap.this.contains(pc);
		}
		@Override //efficiency
		public boolean remove(Object x) {
			assert wellFormed() : "Invariant broken in remove";
			if (!(x instanceof Map.Entry<?, ?>)) return false;
			Map.Entry<?, ?> p = (Map.Entry<?, ?>)x;
			if(!(p.getKey() instanceof Point)||!(p.getValue() instanceof Color))return false;			
			return RasterMap.this.remove(p.getKey(),p.getValue());
		}
	}

}
