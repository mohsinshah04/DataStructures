package edu.uwm.cs351;

import java.net.URI;

/**
 * An object identified by an URI
 * with a fixed content stored locally.
 */
public class FixedObject extends Resource {

	private final Object contents;
	
	/**
	 * Create a resource with given URI and contents
	 * @param uri information for this object
	 * @param c contents of this object
	 */
	public FixedObject(URI uri, Object c) {
		super(uri);
		contents = c;
	}
	
	/**
	 * Get the contents for this object.
	 * @return contents (may be null if the contents was set to be null).
	 */
	public Object getContents() {
		return contents;
	}

	@Override
	public int hashCode() {
		if (uri == null) {
			if (contents == null) return 0;
			return contents.hashCode();
		}
		return uri.hashCode() ^ contents.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FixedObject)) return false;
		FixedObject other = (FixedObject)obj;
		return (uri == null ? other.uri == null : uri.equals(other.uri)) &&
				(contents == null ? other.contents == null : contents.equals(other.contents));
	}

	@Override
	public String toString() {
		return super.toString() + contents;
	}
}
