package edu.uwm.cs351;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A resource with a given location (URL).
 * The contents are fetched immediately and a simple
 * hash is computed over the contents.
 */
public class URLObject extends Resource {
	protected final int contentHash;
	
	/**
	 * Create a web object at a particular web address
	 * (which will be accessed in this constructor)
	 * @param url location of object, must not be null
	 * @throws URISyntaxException in the unlikely event the URL is malformed
	 * @throws IOException if the resource cannot be accessed successfully
	 */
	public URLObject(URL url) throws URISyntaxException, IOException {
		super(url.toURI());
		contentHash = CRC32.ofStream(new BufferedInputStream(url.openStream()));
	}
	
	/**
	 * Create a web object at the given URL (given as a string)
	 * and accessing the content.
	 * @param url location of object, must not be null
	 * @throws URISyntaxException if the created URL is invalid (very unlikely)
	 * @throws IOException if the URL is malformed or if the object cannot be accessed.
	 */
	public URLObject(String url) throws URISyntaxException, IOException {
		this(new URL(url));
	}

	@Override
	public String toString() {
		return super.toString() + "@" + contentHash;
	}

	@Override
	public int hashCode() {
		return uri.hashCode() ^ contentHash;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof URLObject)) return false;
		URLObject other = (URLObject)obj;
		return uri.equals(other.uri) && contentHash == other.contentHash;
	}
}
