package edu.uwm.cs351.util;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * An implementation of priority queue in which elements
 * are kept in sorted order and we can insert at either end.
 */
public class CircularArrayPriorityQueue<E> extends AbstractQueue<E> {
	private static final int INITIAL_CAPACITY = 1;
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	/**
	 * Used to report an error found when checking the invariant.
	 * By providing a string, this will help debugging the class if the invariant should fail.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	@SuppressWarnings("unchecked")
	private E[] makeArray(int s) {
		return (E[]) new Object[s];
	}

	private E[] data;
	private Comparator<E> comparator;
	private int head; // index of first element in PQ (a legal index)
	private int rear; // index of space after last element in PQ (a legal index)
	private int version;


	private boolean wellFormed() {
		if (data == null) return report("data array is null");
		if (comparator == null) return report("comparator is null");
		if (head < 0 || head >= data.length) return report("head is bad: " + head);
		if (rear < 0 || rear >= data.length) return report("rear is bad: " + rear);
		E prev = null;
		for (int i=head; i != rear;) {
			if (i != head) {
				if (comparator.compare(prev,data[i]) > 0) return report("out of order: " + prev + " and " + data[i]);
			}
			prev = data[i];
			++i;
			if (i == data.length) i = 0;
		}
		return true;
	}
	public  CircularArrayPriorityQueue(){
		this(null);
	}
	@SuppressWarnings("unchecked")
	public  CircularArrayPriorityQueue(Comparator <E> c)
	{
		data=makeArray(INITIAL_CAPACITY);
		head=0;
		rear=0;
		version=0;
		if(c!=null)comparator=c;
		else comparator = (Comparator<E>) Comparator.naturalOrder();
		assert wellFormed() : "invariant broken by constructor";
	}
	/**
	 * Return whether the mid point is closer to the head than to the rear,
	 * with ties resolved in favor of the rear.
	 * @param mid mid index
	 * @return whether the mid index is closer to head than to rear
	 */
	private boolean inFirstHalf(int mid) {
		if(head<rear)
		{
			if(mid-head>rear-mid)return true;
		}
		else
		{
			if(head-mid>mid-rear)return true;
		}
		return false; // TODO
	}
	private int wrap(int i) {
		if(i>=data.length-1)return i%data.length;
		return i;
	}
	private int nextIndex(int i) {
		if (i == data.length-1) i = 0;
		else i++;
		return i;
	}
	private int prevIndex(int i) {
		if (i == 0) i = data.length-1;
		else --i;
		return i;
	}
	private int binary(E e) {
		int mid=wrap((head+size())/2);
		int hi=rear;
		int lo=head;
		while(hi!=lo)
		{
			if(comparator.compare(data[mid], e)<0)lo=mid;
			else hi=mid;
		}
		return mid;
		
	}

	// TODO: Body of class
	// "offer" is the most work
	//
	// The constructor taking a comparator may be annotated
	// @SuppressWarnings("unchecked")
	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		assert wellFormed() : "invariant broken in offer";
		if(e==null)throw new NullPointerException("offer: passing in null");
		if(nextIndex(rear)==head)//resize
		{
			E[] temp=makeArray(data.length*2);
			for (int i=head,j=0; i != rear;i=nextIndex(i)) 
			{
				temp[j]=data[i];
				j++;
			}
			rear=size();
			head=0;
			data=temp;
		}
		if(size()==0)
		{
			data[head]=e;
			rear=nextIndex(rear);
		}
		else
		{
			int index=binary(e);
			if(inFirstHalf(index))//shift left
			{
				
			}
			else//shift right
			{
				for (int i=rear; i != index;i=nextIndex(i)) 
				{
					data[i]=data[nextIndex(i)];
				}
				data[index]=e;
				rear=nextIndex(rear);
			}
		}
		assert wellFormed() : "invariant broken by offer";
		return true;
	}
	@Override
	public E poll() {
		// TODO Auto-generated method stub
		E temp=data[head];
		data[head]=null;
		head=nextIndex(head);
		return temp;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return data[head];
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new MyIterator();
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		if(rear>=head)return rear-head;
		else return (data.length-head)+rear;

	}
	private class MyIterator implements Iterator<E> {
		private int current = head;
		private boolean canRemove = false;
		private int colVersion = version;

		/// Data structure Design:
		// current is index of current element (if canRemove is true)
		// otherwise there is no current and "current" is the index of the next element.
		// It must always be between head and rear (inclusive) sees as indices in a circular array.
		// If current == rear then there is no current element (why not?)
		// and no next element either (why not?)

		private boolean wellFormed() {
			if (!CircularArrayPriorityQueue.this.wellFormed()) return false;
			if (version != colVersion) return true;
			if (current < 0 || current >= data.length) return report("current is bad index: " + current);
			if (head <= rear) {
				if (current < head || current > rear) return report("current is out of range: " + current + " not in [" + head + "," + rear +")");
			} else {
				if (current < head && current > rear) return report("current is out of range: " + current + " not in [" + head + "," + rear + ")"); 
			}
			if (canRemove) {
				if (rear == current) return report("canRemove but current == rear");
			}
			return true;
		}
		// TODO: Body of iterator class.
		// "remove" is the most work
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			Iterator.super.remove();
		}
	}

}
