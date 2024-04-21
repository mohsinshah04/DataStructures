package edu.uwm.cs351;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Starting from a given URL, try
 * to see if there are duplicate resources ---
 * resources with different paths that have the same content hash.
 * <p>
 * When run, it crawls the web from the given URL, computing a content
 * hash of each resource it finds.  At the end, it prints how many duplicate
 * resources it found and what percentage this is of all the resources found.
 * Then, starting with the most duplicative, it gives the resources
 * with the same content hash (presumed duplicates).
 */
public class DuplicateResourceFinder {
	private static final String COUNT_OPTION = "--count=";
	private static final int DEFAULT_COUNT = 100;
	
	
	private int count;
	private BreadthFirstWebCrawler crawler;
	private Map<Integer,List<URLObject>> contentTable = new HashMap<>();
	
	
	private DuplicateResourceFinder(int c, String base) throws MalformedURLException {
		
		count = c;
		crawler = new BreadthFirstWebCrawler(new URL(base)); 
		
	}
	
	public void run() {
		//TODO: Crawl the web by calling the helper method gotObject(), and declare n, u:
		// n: the number of resources successfully read
		// u: the number of unique resources (assuming contentHash is unique).
		// When crawling the web, each URL is consumed as it is read.
		int n=count-crawler.crawl(count, this::gotObject);
		int u=contentTable.size();
		
		if (u == 0) {
			System.out.println("No URLs successfully read");
			return;
		}
		System.out.println((n-u)+" (potential) duplicates found, for a waste of " + (n-u)*100.0/n + "%");
		// TODO:
		//  1. Get a list of lists of URL objects (called "groups")
		//     where the inner lists are the objects with the same content hashes
		//  2. sort them using a comparator so that larger groups come first
		// In our solution, each is one line of code.
		ArrayList<List<URLObject>> groups=new ArrayList<>(contentTable.values());
		groups.sort((List<URLObject> first,List<URLObject> second)->second.size()-first.size());
		for (List<URLObject> g : groups) {
			if (g.size() == 1) continue;
			System.out.println("Duplicate resources at:");
			for (URLObject obj : g) {
				System.out.println("  " + obj.uri);
			}
		}
	}
	
	private void gotObject(URLObject x) {
		System.out.println("Read " + x);
		int h = x.contentHash();
		List<URLObject> l = contentTable.get(h);
		if (l == null) {
			l= new ArrayList<>();
			contentTable.put(h,l);
		}
		l.add(x);
		
	}
	
	public static void main(String[] args) throws MalformedURLException {
		if (args.length == 0) {
			System.err.println("Please run this program with a base URL to check");
		}
		int count = DEFAULT_COUNT;
		for (String arg:args) {
			if (arg.startsWith(COUNT_OPTION)) {
				count = Integer.parseInt(arg.substring(COUNT_OPTION.length()));
			} else new DuplicateResourceFinder(count,arg).run();
		}
	}
}
