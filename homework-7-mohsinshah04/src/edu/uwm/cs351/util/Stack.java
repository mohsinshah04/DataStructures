package edu.uwm.cs351.util;

import java.lang.reflect.Array;
import java.util.EmptyStackException;
import java.util.function.Consumer;


/**
 * A generic stack class with push/pop methods.
 * When an instance is created, one may optionally pass in a
 * class descriptor.  This makes the implementation more robust.
 * @param T element type of stack
 */
public class Stack<T> implements Cloneable {
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}


	private final Class<T> clazz; // initialize to null if necessary

	// TODO: Declare fields (for dynamic array data structure)
	private int size;
	private T[] contents;

	private Stack(boolean unused) { clazz = null; } // do not change this constructor

	// TODO: declare wellFormed
	private boolean wellFormed() 
	{
		if(contents==null)return report("contents is null");
		if(size>contents.length||size<0)return report("size and contents length is not same");
		return true;
	}
	// a helper method which you will find useful.
	@SuppressWarnings("unchecked")
	private T[] makeArray(int size) {
		if (clazz == null)
			return (T[])new Object[size]; // lying...
		else
			return (T[])Array.newInstance(clazz, size);
	}

	private static final int DEFAULT_CAPACITY = 1;

	/**
	 * Return the capacity of this stack.  The total number of
	 * elements it can hold before we need to allocate a larger array.
	 * This method should be used with care.
	 * @return number of elements that this stack can hold before 
	 * it needs to allocate more memory.
	 */
	public int getCapacity() {
		return contents.length; // TODO
	}

	// TODO: rest of class
	// You need two public constructors: one taking a class value (used by makeArray)
	// and one without such a value.  In the former case, makeArray
	// won't need to lie in its array creation.
	// Declare "ensureCapacity" as in Activity 2, except that
	// it will use makeArray to construct arrays.
	// Make sure to assert the invariant at end of each constructor
	// and at start (and end, if they mutate anything) of public methods.
	/**
	 * default constructor
	 */
	public Stack() //general constructor, in this case clazz is null
	{
		clazz=null;
		size=0;
		contents=makeArray(DEFAULT_CAPACITY);
		assert wellFormed() : "invariant broken by constructor";
	}
	/**
	 * specifying constructor
	 * @param c
	 */
	public Stack(Class<T> c)//in specifying, clazz is c and the rest is still the same, we are just initializing
	{
		clazz=c;
		size=0;
		contents=makeArray(DEFAULT_CAPACITY);
		assert wellFormed() : "invariant broken by specifying constructor";

	}
	/**
	 * 	ensure that the capacity is the int s and if it isnt then we need to make
	a new array that is length of int s
	 * @param s
	 */
	private void ensureCapacity(int s) 
	{ 
		if (s <= contents.length) return; 
		int newSize = contents.length*2;
		if (newSize < s) newSize = size; 
		T[] newArray = makeArray(newSize); 
		for (int i=0; i < contents.length; ++i) {
			newArray[i] = contents[i]; 
		}
		contents = newArray; 
	}
	
	/**
	 * removes the last index of the array and decrement size accordingly list cant be empty
	 * @return the last element
	 * @throws EmptyStackException if isEmpty
	 */
	public T pop() {
		assert wellFormed() : "invariant broken in pop";
		if(isEmpty())throw new EmptyStackException();
		T last=contents[size-1];
		--size;
		assert wellFormed() : "invariant broken by pop";
		return last;
		
	}
	/**
	 * same as pop but instead of changing anything we are just looking at it list cant be empty
	 * @return the last index without changing any values
	 */
	public T peek() {
		assert wellFormed() : "invariant broken in peek";
		if(isEmpty())throw new EmptyStackException();
		return contents[size-1];
	}
	/**
	 * 	add to the end of the list and increment size accordingly
	 * @param p
	 */
	public void push(T p) {
		assert wellFormed() : "invariant broken in push";
		++size;
		ensureCapacity(size);
		contents[size-1]=p;
		assert wellFormed() : "invariant broken by push";
	}
	/**
	 * 	return whether or not the list is empty or not
	 * @return if the list is empty or not
	 * if size is or not 0
	 */
	public boolean isEmpty() {
		assert wellFormed() : "invariant broken in isEmpty";
		boolean result=true;
		if(size!=0)result=false;
		return result;
	}
	/**
	 * 	set the size to 0 so you set everything else to unused and ignore it
	 * go back to the start
	 * you may not remove everything but it doesnt matter if u go back to start
	 */
	public void clear() {
		assert wellFormed() : "invariant broken in clear";
		size=0;
		assert wellFormed() : "invariant broken by clear";
	}
	/**
	 * create a stack of clone and similar to the last assignment i copied 
	the try and catch from homework 6 but changing references to this assignment
	im setting the contents of the stack clone to a new array that is the 
	contents array length, and then its assigning each point in the copy to the point
	in the previous.
	 *Create a deep copy of the stack
	 *copy everything over
	 */
	@SuppressWarnings("unchecked")
	public Stack<T> clone() 
	{
		assert wellFormed() : "invariant failed at start of clone";
		Stack<T> clone;

		try
		{
			clone = (Stack<T>) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			//you have to do this because then if you were to modify it then it
			//would be modifying the original array. and technically in that case
			//there is no second array
			throw new RuntimeException
			("This class does not implement Cloneable");
		}
		clone.contents= makeArray(contents.length);
		for(int i=0;i<size;i++) 
		{
			clone.contents[i]=contents[i];
		}
		assert wellFormed() : "invariant failed at end of clone";
		assert clone.wellFormed() : "invariant on answer failed at end of clone";
		return clone;
	}
	/**
	 * Class to enable data structure testing.  Do not modify this class.
	 * Any compiler errors here need to be fixed by changing the main class, not this one.
	 */
	public static class Spy<U> {
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
		 * Create a stack with the given data structure.
		 * @param c array to hold contents
		 * @param s size of stack
		 */
		public Stack<U> create(U[] c, int s) {
			Stack<U> result = new Stack<>(false);
			result.contents = c;
			result.size = s;
			return result;
		}

		/**
		 * Check the data structure of a stack.
		 * @param s stack to check, must not be null
		 * @return boolean indicating whether the data structure is good
		 */
		public boolean wellFormed(Stack<U> s) {
			return s.wellFormed();
		}
	}
}
