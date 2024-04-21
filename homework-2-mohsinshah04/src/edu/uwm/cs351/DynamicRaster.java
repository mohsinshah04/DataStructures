package edu.uwm.cs351;

import java.awt.Color;
import java.util.function.Consumer;

/**
 * A collection of pixels on the screen, typically arranged 
 * in a rectangular form.
 */
public class DynamicRaster {
	private static final int DEFAULT_INITIAL_WIDTH=1;
	private static final int DEFAULT_INITIAL_HEIGHT=0;

	private Color[][] pixels;
	private int size; // number of non-transparent (null) pixels
	// the cursor:
	private int x;
	private int y;

	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
	//collaborated with a few classmates in the tutoring ssc for number 7 in the wellFormed
	private boolean wellFormed() {
		// Check the invariant.
		// 1. the (outer) array is never null
		if (pixels == null) return report("pixels is null"); // test the NEGATION of the condition

		// 2. The current x is always a legal index in the array.
		// (Implicitly, the array length can never be zero.)
		// TODO
		if(pixels.length<=x||this.x<0)return report("Array length is 0");

		// 3. the subarray at index x is never null.
		// TODO
		if(pixels[x]==null)return report("Sub array is null");

		// 4. the y element must be a legal index or else equal to the length of the array
		// TODO
		if(this.y<0||this.y > pixels[x].length) return report("Y isnt legal index");

		// 5. The current pixel (if there is one) is not null
		// TODO
		if(this.y<pixels[x].length&&pixels[x][y]==null)return report("Current Pixel is null");

		// 6. The "size" field is correct -- it is the number
		// of non-null pixels in the raster.
		// TODO
		int count=0;
		for(Color[]a:pixels)//for loop for the x's
		{

			if(a==null)continue;
				for(Color b:a)//for loop for the y's
				{
					if (b!=null)++count;
			}

		}
		if(count!=size)return report("count is not size");


		// 7. If y is not a legal array index, then
		// x,y must refer to a point in the raster after *all* of the 
		// (non-null) pixels in the raster.  (Because "isCurrent"
		// can only be false if there are no more pixels in the raster.)
		// TODO
		if(this.y==this.pixels[x].length) 
		{
			for(int i=x+1;i<pixels.length;++i)//for loop for the y's
			{
				if(pixels[i]==null)continue;
					for(int o=0;o<pixels[i].length;++o)
					{
						if(pixels[i][o]!=null)return report("cant point to a cord in raster");
					}
			}
		}
		//		if(size!=0&&this.y>pixels[x].length) {
		//			if(x<i||y<j)return report("count is not size");
		//		}
		//		{
		//testing if y is legal
		//			this.y=pixels[x].length;
		//		}
		// If no problems discovered, return true
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private DynamicRaster(boolean testInvariant) { }

	/**
	 * Create a raster with no initial size, or rather
	 * using a default initial size, which is the smallest possible.
	 */
	public DynamicRaster() {
		this(DEFAULT_INITIAL_WIDTH, DEFAULT_INITIAL_HEIGHT);
	}

	/**
	 * Create a raster with an initial size.
	 * @param w width, must be positive
	 * @param h height, must not be negative
	 */
	public DynamicRaster(int w, int h) {
		//TODO.  Make sure that you assert the invariant at the end only
		if(w<=0||h<0)
		{
			throw new IllegalArgumentException("x or y is negative");
		}
		this.pixels=new Color[w][h];
		this.x=0;
		this.y=h;
		assert wellFormed() : "Invariant failed at end";
	}

	@Override // implementation
	public String toString() {
		// don't assert invariant, so we can use this for testing/debugging
		StringBuilder sb = new StringBuilder();
		if (pixels == null) sb.append("null");
		else {
			sb.append("[");
			for (int i=0; i < pixels.length; ++i) {
				if (i != 0) sb.append(",");
				if (pixels[i]== null) continue;
				sb.append(pixels[i].length);
			}
			sb.append("]");
		}
		sb.append(":" + size + "@(" + x + "," + y + ")");
		return sb.toString();
		// A square bracket delimited list of the lengths of each row, or blank is null
		// e.g. if the outer array has six elements
		// and the subarrays at index 3 and 5 are null, and the others
		// have length 4, 10, 0 and 3, then the first part is "[4,10,0,,3,]"
		// To this we append size, x and y.
	}

	/** Get a pixel from the raster
	 * @param x x-coordinate, must not be negative
	 * @param y y-coordinate, must not be negative
	 * @return the pixel at x,y, or null if no pixel.
	 */
	public Pixel getPixel(int x, int y) 
	{
		//TODO return the pixel at x, y
		//Throw IllegalArgumentException if either x or y is negative
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be non-negative.");
		}
		if (x >= pixels.length || pixels[x]==null||pixels[x].length<=y)return null;
		if(pixels[x][y]==null)return null;
		return new Pixel(x,y,pixels[x][y]);
	}

	/**
	 * Set a pixel in the raster.  Return whether a change was made.
	 * This pixel is now current.
	 * @param p pixel to add, must not be null
	 * @return whether a change was made to a pixel.
	 * But the pixel will be current whetehr or not it was newly added.
	 */
	public boolean add(Pixel p) {
		assert wellFormed() : "invariant broken in setAt";
		boolean result = true;
//		if (p == null) 
//		{
//			throw new IllegalArgumentException("Pixel must not be null.");
//		}
//		else 
//		{
//			p=getCurrent();
//			result=true;
//		}
//		// TODO: complex, including two cases of expanding arrays
//		if(pixels[x][y]!=null)
//		{
//			size=size*2;
//		}
		int newx=p.loc().x;
		int newy=p.loc().y;
		Color c=p.color();
		if(newx<0||newy<0)
		{
			throw new IllegalArgumentException("x or y is negative");
		}
		if(newx>=pixels.length)
		{
			Color [][] old=pixels;
			int newWidth=pixels.length*2;
			if(newWidth<=newx)newWidth=newx+1;
			pixels= new Color[newWidth][];
			for(int i=0;i<pixels.length;++i)//for loop for the x's
			{
				pixels[i]=old[i];

			}
		}
		if(pixels[newx]==null)
		{
			int newHeight=newy+1;
			pixels[newx]=new Color[newHeight];
		}
		if(pixels[newx][newy]==null)
		{
			++size;
		}else if(pixels[newx][newy].equals(c)){
			result=false;
			
		}
		x=newx;
		y=newy;
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
		if(getPixel(x,y)==null)return false;
		if(x==this.x&&y==this.y)advance();
		pixels[x][y]=null;
		--size;
		assert wellFormed() : "invariant broken by clearAt";
		return true;

	}

	/**
	 * Return the number of pixels in the raster.
	 * @return number of (non-null) pixels
	 */
	public int size() {
		assert wellFormed() : "invariant broken in size";
		int size=0;
		for(int x=0;x<pixels.length;x++)//for loop for the x's
		{
			for(int y=0;y<pixels[x].length;y++)//for loop for the y's
			{
				if (pixels[x][y] != null) //if the point isnt null
				{
					size++;
				}
			}

		}
		assert wellFormed() : "invariant broken by size";
		return size;
	}


	/// Cursor methods

	// We use a private helper method
	private void findNext()
	{
		while(++y<pixels[x].length&&pixels[x][y]==null){
			//nothing
		}
		if(y==pixels[x].length)
		{
			outer:for(int i=x+1;i<pixels.length;++i) {
				if(pixels[i]==null)continue;
				for(int j=0;j<pixels[i].length;++j)
				{
					x=i;
					y=j;
					break outer;
				}
			}
		}
	}
	/**
	 * Move the cursor to the beginning, first pixel in the raster,
	 * if any.
	 */
	public void start() {
		// TODO: don't forget to check invariant
		assert wellFormed() : "Invariant failed at start";
		this.x=0;this.y=-1;
		findNext();
		assert wellFormed() : "Invariant failed at end";
	}

	/**
	 * Return whether we have a current pixel
	 * @return whether there is a current pixel.
	 */
	public boolean isCurrent() {
		assert wellFormed() : "invariant broken in isCurrent";
		// TODO: one liner since we can assume the invariant
		return y<pixels[x].length;
	}

	/**
	 * Return the current pixel.
	 * @exception IllegalStateException if there is no current pixel
	 * @return the current pixel, never null.
	 */
	public Pixel getCurrent() {
		assert wellFormed() : "invariant broken in getCurrent";
		// TODO: simple since we can assume the invariant
		//return pixels[x][y];
		if(!(isCurrent()))throw new IllegalStateException("there is no current pixel");
		//create a pixel with those points and the color
		return new Pixel(x,y,pixels[x][y]);// get the color at [x]and[y]
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
		assert wellFormed() : "Invariant failed at start";
		if(!isCurrent())throw new IllegalStateException("there is no current pixel");
		findNext();
		assert wellFormed() : "Invariant failed at end";

	}

	/**
	 * Remove the current pixel, advancing the cursor to the next pixel.
	 * @throws IllegalStateException if there is no current pixel.
	 */
	public void removeCurrent() {
		// TODO: Don't forget to check the invariant before and after!
		// Hint: let "advance" do most of the work.  Rely on the invariant.
		assert wellFormed() : "Invariant failed at start";
		if(!isCurrent())throw new IllegalStateException("there is no current pixel");
		advance();
		int newx=x;
		int newy=y;
		pixels[newx][newy]=null;
		assert wellFormed() : "Invariant failed at end";
	}

	/**
	 * Class for internal testing.  Do not modify.
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
		 * @param x current x
		 * @param y current y
		 * @return instance of DynamicRaster with the given field values.
		 */
		public DynamicRaster create(Color[][] d, int s, int x, int y) {
			DynamicRaster result = new DynamicRaster(false);
			result.pixels = d;
			result.size = s;
			result.x = x;
			result.y = y;
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
