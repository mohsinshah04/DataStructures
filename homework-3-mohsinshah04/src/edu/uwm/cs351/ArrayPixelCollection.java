package edu.uwm.cs351;

import java.awt.Color;
import java.awt.Point;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
//in my code, i got slight help from hassan khan to check on some of my methods
/**
 * An extensible Raster that satisfies {@link java.util.Collection} 
 * and uses array lists internally.
 */
public class ArrayPixelCollection extends AbstractCollection<Pixel>
// TODO: how do we indicate that this is a collection?  
// And what is it a collection of?
// There is something else we need to declare as well.
{
	private static final int DEFAULT_INITIAL_WIDTH=1;
	private static final int DEFAULT_INITIAL_HEIGHT=0;

	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	// TODO: Data structure: List of Lists, a size and a version
	private List<List<Color>> pixels;
	private int size;
	private int version;

	private boolean wellFormed() {
		// TODO
		// Check the invariant.
		// 1. the (outer) array is never null
		if (pixels == null) return report("pixels is null"); // test the NEGATION of the condition
		int count=0;
		for(int i=0;i<pixels.size();i++)
		{
			if(pixels.get(i)!=null)
			{
				for(int j=0;j<pixels.get(i).size();j++)
				{
					if(pixels.get(i).get(j)!=null) count++;
				}
			}
		}
		if(count!=size) return report("No size");

		// If no problems discovered, return true
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private ArrayPixelCollection(boolean testInvariant) { }

	public ArrayPixelCollection() {
		this(DEFAULT_INITIAL_WIDTH, DEFAULT_INITIAL_HEIGHT);
	}

	public ArrayPixelCollection(int w, int h) {
		// TODO: Implement the main constructor
		if(w<=0||h<0)
		{
			throw new IllegalArgumentException("x or y is negative");
		}
		pixels=new ArrayList<>();
		size=0;
		version=0;
		for (int i=0; i < w; ++i) 
		{
			List<Color> newPixels = null;
			if (h >= 0) {
				newPixels = new ArrayList<>();
				for (int j=0; j < h; ++j) {
					newPixels.add(null);
				}
			}
			pixels.add(newPixels);
		}
	}

	// TODO: methods need to be implemented.
	// Several Raster-specific methods and then
	// some Collection overridings.
	// Make sure to comment reasons for any overrides,
	// and to provide full documentation for public methods that do not override.
	// You are expect to copy documentation from the
	// DynamicRaster class, with changes relevant to the lack of cursors.
	@Override //required
	public boolean add(Pixel p)
	{
		assert wellFormed() : "invariant broken in add";

		boolean result =false;
		int newx=p.loc().x;
		int newy=p.loc().y;
		Color c =p.color();

		if(newx<0||newy<0)
		{
			throw new IllegalArgumentException("x or y is negative");
		}
		if(newx<pixels.size()&&pixels.get(newx)==null)
		{
			pixels.set(newx, new ArrayList<>());
		}
		while(newx>pixels.size())
		{
			pixels.add(null);
		}
		if(newx==pixels.size())
		{
			pixels.add(new ArrayList<>());
		}
		List<Color> nextList = pixels.get(newx);
		while(newy>=nextList.size())
		{
			nextList.add(null);
		}
		if(nextList.get(newy)==null)
		{
			nextList.set(newy, c);
			++size; ++version;
			result=true;
		}
		else if(!nextList.get(newy).equals(c))
		{
			nextList.set(newy, c);
			result=true;

		}
		//		if(newx>=pixels.size())
		//		{
		//			for(int i=pixels.size()-1;i<=newx;++i)//for loop for the x's
		//			{
		//				
		//				//pixels[i]=old[i];
		//				pixels.add(null);
		//
		//			}
		//			while((newy>pixels.get(newx).size()))
		//			{
		//				for(int i=pixels.get(newx).size();i<p.loc().y+1;++i)//for loop for the x's
		//				{
		//					//pixels[i]=old[i];
		//					pixels.get(i).add(null);
		//				}
		//			}
		//			pixels.get(newx).add(c);
		//			size++;
		//			result=true;
		//		}
		//		if(pixels.get(newx)==null)
		//		{
		//			int newHeight=newy+1;
		//			pixels.snewx=new ArrayList<>(newHeight);
		//		}
		//		if(pixels.get(newx).get(newy)==null)
		//		{
		//			++size;
		//		}else if(pixels[newx][newy].equals(c)){
		//			result=false;
		//			
		//		}
		//		x=newx;
		//		y=newy;
		assert wellFormed() : "invariant broken by add";
		return result;
	}
	@Override //required
	public boolean contains(Object r)
	{
		if(r instanceof Pixel)
		{
			Pixel p=(Pixel)r;
			int newx=p.loc().x;
			int newy=p.loc().y;
			if(newx>=0&&newx<pixels.size()&&pixels.get(newx)!=null&&newy>=0&&newy<pixels.get(newx).size())
				return p.color().equals(pixels.get(newx).get(newy));
		}
		return false;
	}
	@Override //required
	public int size()
	{
		return size;
	}
	public boolean clearAt(int x, int y)//homework 2 gave me this(most of it)
	{
		assert wellFormed() : "invariant broken in clearAt";
		if(getPixel(x,y)==null)return false;
		Pixel pixel= new Pixel(x,y);
		if(x==pixel.loc().x&&y==pixel.loc().y)
		{
			pixels.get(x).set(y, null);
			--size; ++version;
		}
		assert wellFormed() : "invariant broken by clearAt";
		return true;
	}
	@Override //required
	public boolean remove(Object r)
	{
		if(r==null||!(r instanceof Pixel))return false;
		Pixel p=(Pixel)r;
		int newx=p.loc().x;
		int newy=p.loc().y;
		if(newy>=pixels.get(newx).size()||pixels.get(newx)==null||newx>=pixels.size()||newx<0||newy<0)return false;
		if(pixels.get(newx).get(newy)!=null)
		{
			if(pixels.get(newx).get(newy).equals(p.color()))
			{
				pixels.get(newx).set(newy, null);
				--size; ++version;
				return true;
			}
		}
		return false;
	}
	@Override //required
	public String toString()//got help on this in tutoring, i understand it 
	{
		StringBuilder sb = new StringBuilder();
		if(pixels==null)sb.append("null");
		else 
		{
			sb.append("[");
			for(int j=0; j<pixels.size();++j)
			{
				if(j!=0)sb.append(",");
				if(pixels.get(j)==null)
				{
					sb.append("");
				}
				else
				{
					sb.append(pixels.get(j).size());
				}

			}
			sb.append("]");
		}
		sb.append(":"+size);
		return sb.toString();

	}
	@Override //required
	public Iterator<Pixel> iterator()
	{
		return new MyIterator();

	}
	public Pixel getPixel(int x, int y)
	{
		assert wellFormed() : "invariant broken in getPixel";

		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be non-negative.");
		}
		if (x >= pixels.size() || pixels.get(x)==null||pixels.get(x).size()<=y)return null;
		if(pixels.get(x).get(y)==null)return null;
		assert wellFormed() : "invariant broken by getPixel";

		return new Pixel(x,y,pixels.get(x).get(y));
	}

	@Override // decorate (we use the superclass implementation, but do more)
	public ArrayPixelCollection clone() {
		ArrayPixelCollection result;
		try {
			result = (ArrayPixelCollection) super.clone();
		} catch(CloneNotSupportedException ex) {
			throw new IllegalStateException("did you forget to implement Cloneable?");
		}
		// TODO: Work to do.
		result.pixels =new ArrayList<>(pixels.size());
		for(List<Color>newList:pixels)
		{
			if(newList==null)
			{
				result.pixels.add(null);
			}
			else
			{
				result.pixels.add(new ArrayList<>(newList));
			}
		}
		return result;
	}

	private class MyIterator implements Iterator<Pixel>
	// TODO: How do we indicate that this is our iterator implementation ?  WHat is this an iterator of?
	{
		int x, y;
		int colVersion;
		int remaining;

		private boolean wellFormed() {
			// First check outer invariant, and if that fails don't proceed further
			if(!ArrayPixelCollection.this.wellFormed())return false;
			// Next, if the versions don't match, pretend there are no problems.
			if(version!=colVersion)return true;
			// (Any problems could be due to being stale, which is not our fault.)
			// Then check that (except for the special case x=0, y = -1),
			// x and y refer to an actually place in the list of lists,
			if(x<0||x>=pixels.size()||y<0||pixels.get(x)==null||y>=pixels.get(x).size())
			{
				if(x!=0||y!=-1)
				{
					return report("not valid range");		
				}
			}
			// and then that "remaining" correctly counts any pixels *after* the current one.
			int count=0;
			for(int i=x;i<pixels.size();i++)
			{
				if(pixels.get(i)!=null)
				{
					for(int j=0;j<pixels.get(i).size();j++)
					{
						if(i!=x||j>y)
						{
							if(pixels.get(i).get(j)!=null) ++count;

						}
					}
				}
			}
			if(count!=remaining) return report("No size");
			return true;
		}

		MyIterator(boolean unused) {} // do not change this iterator

		MyIterator() 
		{
			// Implement this constructor.  Don't forget to assert the invariant
			this.x=0;//start at the beginning
			this.y=-1;//we start here because of our special case
			this.colVersion=version;//Initialize
			this.remaining=size;//set remaining to the whole size
			assert wellFormed():"error in MyIterator constructor";
		}

		// TODO iterator methods
		public boolean hasNext()// this one was simple, did it myself
		{
			assert wellFormed():"error in has next";
			if(colVersion!=version)throw new ConcurrentModificationException("has next");
			return remaining>0;
		}
		public Pixel next()// got help in tutoring and some chatgpt for trouble shooting
		{
			assert wellFormed():"error in next";
			if (!hasNext()) throw new NoSuchElementException("no more");
			if(colVersion!=version)throw new ConcurrentModificationException("has next");

			while(++y<pixels.get(x).size()&&pixels.get(x).get(y)==null) {}
			if(y==pixels.get(x).size())
			{
				outer:for(int j=x+1;j<pixels.size();++j)
				{
					if(pixels.get(j)==null)continue;
					for(int k=0;k<pixels.get(j).size();++k)
					{
						if(pixels.get(j).get(k)!=null)
						{
							x=j;y=k;
							break outer;
						}
					}
				}
			}
			if(y==pixels.get(x).size())
			{
				outer:for(int j=x+1;j<pixels.size();++j)
				{
					if(pixels.get(j)==null)continue;
					for(int k=0;k<pixels.get(j).size();++j)
					{
						if(pixels.get(j).get(k)!=null)
						{
							x=j;y=k;
							break outer;
						}
					}
				}
			}
			--remaining;
			assert wellFormed():"error by next";
			return new Pixel(x,y,pixels.get(x).get(y));

		}
		@Override //required
		public void remove() //got help in tutoring
		{
			assert wellFormed():"error in remove";
			if(colVersion!=version)throw new ConcurrentModificationException("has next");
			if((y==-1&&x==0)||pixels.get(x).get(y)==null)throw new IllegalStateException("element not there or havent called next yet");
			if(x>=0||y>-1||size!=0||x<pixels.size()||y<pixels.get(x).size())
			{
				pixels.get(x).set(y, null);
				--size; ++version;
				colVersion=version;
			}

			assert wellFormed():"error by remove";

		}
	}

	/**
	 * Class for internal testing.
	 * Do not use in client/application code
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

		/**
		 * Create an instance of the ADT with give data structure.
		 * This should only be used for testing.
		 * @param d data array
		 * @param s size
		 * @param v current version
		 * @return instance of DynamicRaster with the given field values.
		 */
		public ArrayPixelCollection create(List<List<Color>> d, int s, int v) {
			ArrayPixelCollection result = new ArrayPixelCollection(false);
			result.pixels = d;
			result.size = s;
			result.version = v;
			return result;
		}

		/**
		 * Create an iterator for testing purposes.
		 * @param outer outer object to create iterator for
		 * @param x x coordinate of current
		 * @param y y coordinate of current
		 * @param r remaining pixels after current
		 * @param cv version of collection this iterator is for
		 * @return iterator to the raster
		 */
		public Iterator<Pixel> newIterator(ArrayPixelCollection outer, int x, int y, int r, int cv) {
			MyIterator result = outer.new MyIterator(false);
			result.x = x;
			result.y = y;
			result.remaining = r;
			result.colVersion = cv;
			return result;
		}

		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s transaction seq to check.
		 * @return
		 */
		public boolean wellFormed(ArrayPixelCollection s) {
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
