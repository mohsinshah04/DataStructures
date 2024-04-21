package edu.uwm.cs351;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uwm.cs351.Resource.Table;

//worked with peter, jack, george and tutors
//explained concepts to ademar and joey
/**
 * A class to crawl the web from a starting URL.
 */
public class BreadthFirstWebCrawler {

	// TODO: add your own fields.
	private Queue<URL> all;
	private Table dupes;
	// If errors found, log with this logger:
	private final Logger logger = Logger.getLogger("edu.uwm.cs351.xml");

	/**
	 * Create a breadth-first web crawler starting at the given URL
	 * @param base initial URL
	 */
	public BreadthFirstWebCrawler(URL base) {
		//TODO
		if(base==null)logger.log(Level.WARNING,"url base is null");
		all=new LinkedList<URL>();
		dupes=new Table();
		all.add(removeFragment(base));

	}

	/**
	 * Crawl the web from where last left off
	 * in a breadth-first fashion.  Each URL has its fragment removed
	 * before being used to fetch a resource.  We visit no more than the indicated
	 * number of new URLs.  
	 * A URL that has been dupes before or cannot be successfully converted to URIs 
	 * will not be counted. 
	 * If all links all exhausted, we may stop crawling
	 * before reaching the number requested, and return the remaining count.
	 * Otherwise, we return zero.
	 * @param count maximum number of URLs to read
	 * @param listener is called for each URL that is successfully read
	 * @return remaining count that were not dupes.
	 */
	public int crawl(int count, Consumer<URLObject> listener) {
		// TODO: crawl the web, decrementing count after each successful unique read.
		// Using try-catch to handle the exceptions (URISyntaxException, IOException) for converting URL to URI,
		// and logging exceptions as WARNING.
		while (count > 0 && all.size()>0) {
			try 
			{
				URL poll=all.remove();
				URL cleanedPoll=removeFragment(poll);
				URLObject main = new URLObject(cleanedPoll);
				if(dupes.contains(main))continue;
				listener.accept(main);
				dupes.add(main);
				--count;
				for(URL sub: main)
				{
					URL cleanedSub= removeFragment(sub);
					all.add(cleanedSub);
				}
			}catch (IOException | URISyntaxException e) {
				logger.log(Level.WARNING, "Exception in crawl");
			}
		}
		return count;
	}
	/**
	 * Remove the "fragment" from a URL
	 * so that we don't see it as a novel URL.
	 * @param u URL to remove fragment (if any) from
	 * @return URl without a fragment.
	 */
	protected URL removeFragment(URL u) {
		String s= u.toString();
		int i = s.indexOf('#');
		if (i >= 0)
			try {
				return new URL(s.substring(0,i));
			} catch (MalformedURLException e) {
				// this should never happen:
				throw new RuntimeException(e.toString());
			}
		return u;
	}
}
