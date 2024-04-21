package edu.uwm.cs351;

import java.awt.Point;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

//worked with peter and ademar
/**
 * A resource with an identifier.
 */
public abstract class Resource {
	public final URI uri;
	private Resource next;
	private Resource.Table table;

	/** Create a resource with the given identifier
	 * @param u URI, may be null
	 */
	public Resource(URI u) {
		uri = u;
	}

	/**
	 * Create a resource.
	 * @param s string to be converted to a URI, must not be null
	 * @throws URISyntaxException if string is not a legal URI
	 */
	public Resource(String s) throws URISyntaxException {
		this(new URI(s));
	}

	@Override
	public String toString() {
		return "<" + uri + ">";
	}

	/**
	 * A table that includes resources, indexed by their URI.
	 * The table is endogenous: a resource can only be in one table.
	 * Neither null resources, nor resources with null URIs may be added to the table.
	 * The table can only include at most one resource for each URI.
	 */
	public static class Table extends AbstractSet<Resource> {
		private static final int DEFAULT_SIZE = 7;

		// DESIGN: we have non-null table
		// in which each resource is in an endogenous list starting at table[i]
		// where i is the modulus of the resource's URI with respects to the length of the table.
		// No two resources in the table can have the same URI.
		// Resources with null URIs are not present in the table.
		// The "table" field of each resource refers to the table it is in.
		// The "size" field represents the number of resources.
		// There are never any more resources than the size of the table.
		// The "version" field is used for fail-fast iterators.
		// There are no other fields.
		private Resource[] table;
		private int size;
		private int version;

		/**
		 * Hash a URI to a modulus table index
		 * @param u URI to hash, must not be null
		 * @return modulus hash.
		 */
		private int hash(URI u) {
			int h = u.hashCode();
			int n = table.length;
			h %= n;
			if (h < 0) h += n;
			return h;
		}

		private static boolean doReport = true; // don't change this

		private boolean report(String s) {
			if (doReport) System.out.println("Invariant error: " + s);
			return false;
		}

		private boolean wellFormed() {

			int count = 0;
			if (table == null) return report("null table");
			if (size > table.length) return report("table too full");
			int n = table.length;
			for (int i=0; i < n; ++i) {
				Resource o = table[i];
				while (o != null) {
					if (o.uri == null) return report("null URI in table");
					if (o.table != this) return report("table field is wrong for " + o);
					int h = o.uri.hashCode() % n;
					if (h < 0) h = n+h;
					if (h != i) return report("object " + o + " in wrong bucket: " + i);
					if (++count > size) return report("encountered more than " + size + " objects");
					for (Resource p = table[i]; p != o; p = p.next) {
						if (p.uri.equals(o.uri)) return report("duplicate object for " + p.uri);
					}
					o = o.next;
				}
			}
			if (count != size) return report("encountered fewer than " + size  + " objects.");
			return true;
		}

		/**
		 * Create an empty table.
		 */
		public Table() {

			table = new Resource[DEFAULT_SIZE];

			assert wellFormed() : "invariant broken at end of constructor";
		}

		@Override // required
		public int size() {
			assert wellFormed() : "invariant broken in size()";
			return size; // we did this for you
		}

		/**
		 * Return the resource in this table with the given uri.
		 * If there is no such, return null.
		 * NB: If the URI is null, this method returns null, since no resource in the table
		 * has a null URI.
		 * @param u URI to use, may be null
		 * @return resource with this URI from this table (if any)
		 */
		public Resource get(URI u) {
			assert wellFormed() : "invariant broken at start of get()";
			// TODO: complete get method, return resource with this URI
			if(u==null)return null;
			Resource getter=table[hash(u)];
			while(getter!=null&&!getter.uri.equals(u))getter=getter.next;
			return getter;
		}

		//Helper method
		private void rehash() {
			Resource[] old = table;
			table = new Resource[size*2];
			for (Resource o : old) {
				while (o != null) {
					Resource n = o.next;
					int i = hash(o.uri);
					o.next = table[i];
					table[i] = o;
					o = n;
				}
			}
		}

		// TODO: Override contains, add, remove, clear
		// Hint: Using the helper method rehash() to complete add().


		@Override // required
		public Iterator<Resource> iterator() {
			assert wellFormed() : "invariant broken in iterator()";
			return new MyIterator();
		}

		@Override//efficiency
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			assert wellFormed() : "Invariant broken in contains";
			if (!(o instanceof Resource)) return false;
			Resource c=(Resource)o;
			return (get(c.uri)!=null&&get(c.uri).equals(o));
		}

		@Override //implementation
		public boolean add(Resource e) {
			// TODO Auto-generated method stub
			assert wellFormed() : "invariant failed at start of add";
			boolean res=false;			
			if(e.uri==null||(e.table!=null&&e.table!=this))throw new IllegalArgumentException("cant add a null or to an existing table");
			e.table=this;

			if(e==get(e.uri))return false;
			if(table[hash(e.uri)]==null)//add, the array index is empty
			{
				table[hash(e.uri)]=e;
				++version;
				++size;
				res=true;

			}
			else//add to correct spot inside the array index or reupdate and dont change
			{
				Resource getter=get(e.uri);

				if(getter==null)
				{
					e.next=table[hash(e.uri)];
					table[hash(e.uri)]=e;
					++version;
					++size;
					res=true;

				}
				else
				{
					remove(getter);
					e.next=table[hash(e.uri)];
					table[hash(e.uri)]=e;
					++version;
					++size;

				}
				if(size>table.length)rehash();

			}
			if(size>table.length)rehash();
			assert wellFormed() : "invariant failed at end of add";
			return res;
		}

		@Override //efficiency
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			assert wellFormed() : "Invariant broken in remove";
			if (!(o instanceof Resource)) return false;
			if(!contains((Resource)o))return false;
			Resource ob=(Resource)o;
			Resource prev=null;
			for(Resource cur=table[hash(ob.uri)];cur!=null;prev=cur,cur=cur.next)
			{
				if(ob.equals(cur))
				{
					if(prev==null)
					{
						table[hash(ob.uri)]=table[hash(ob.uri)].next;
					}
					else
					{
						prev.next=cur.next;
					}
					cur.next=null;
					cur.table=null;
					--size;++version;
					break;
				}
			}
			assert wellFormed() : "Invariant broken by remove";
			return true;
		}

		@Override //implementation
		public void clear() {
			// TODO Auto-generated method stub
			Iterator<Resource> it = iterator();//got from the super class
	        while (it.hasNext()) {
	            it.next();
	            it.remove();
	        }
	        Resource[] table2=new Resource[DEFAULT_SIZE];
	        table=table2;
		}

		private class MyIterator implements Iterator<Resource> {
			private int nextIndex;
			private Resource current, next;
			private int myVersion = version;

			private boolean wellFormed() {
				if (!Table.this.wellFormed()) return false;
				if (version != myVersion) return true;
				// check nextIndex and next
				if (nextIndex == table.length) {
					if (next != null) return report("not any more at end");
				} else {
					if (nextIndex < 0 || nextIndex >= table.length) return report("tableIndex bad: " + nextIndex);
					if (next == null) return report("next is null but not at end");
					Resource o = table[nextIndex];
					while (o != null && o != next) {
						o = o.next;
					}
					if (o != next) return report("next cannot be fiund at index " + nextIndex);
				}
				// check current
				if (current != next) {
					int currentIndex = nextIndex;
					if (next == null || table[nextIndex] == next) {
						while (--currentIndex >= 0) {
							if (table[currentIndex] != null) break;
						}
					}
					if (currentIndex < 0) return report("nothing before next");
					Resource o = table[currentIndex];
					while (o.next != null && o.next != next) {
						o = o.next;
					}
					if (current != o) return report("current isn't just before next");
				}
				return true;
			}

			// Helper method
			private void moveNextIndex() {
				while (++nextIndex < table.length) {
					if (table[nextIndex] != null) {
						next = table[nextIndex];
						break;
					}
				}
			}


			MyIterator() {
				// TODO complete MyIterator ()
				// Hint: Using the helper method moveNextIndex().
				if(table[0]==null)moveNextIndex();
				else next=table[0];
				current=next;
				myVersion=version;
				assert wellFormed() : "invariant broken in constructor";
			}

			private void checkVersion() {
				if (version != myVersion) throw new ConcurrentModificationException("stale");
			}

			@Override // required
			public boolean hasNext() {
				assert wellFormed() : "invariant broken at start of hasNext()";
				checkVersion();
				return next != null;
			}

			@Override // required
			public Resource next() {
				assert wellFormed() : "invariant broken at start of next()";
				if (!hasNext()) throw new NoSuchElementException("no more");
				checkVersion();
				current = next;
				// TODO: find next
				// Hint: Using the helper method moveNextIndex to complete next()
				if(next.next!=null||nextIndex>=table.length)next=next.next;//can move
				else 
				{
					moveNextIndex();
					if(nextIndex==table.length) 
					{
						next = null; // Indicates the end of iteration
					}
				}

				assert wellFormed() : "invariant broken at end of next()";
				return current;
			}

			@Override // required/implementation
			public void remove() {
				assert wellFormed() : "invariant broken at start of remove()";
				checkVersion();
				if (current == next) throw new IllegalStateException("nothing to remove");
				Table.this.remove(current);
				current = next;
				myVersion = version;
				assert wellFormed() : "invariant broken at end of remove()";
			}
		}
	}

}
