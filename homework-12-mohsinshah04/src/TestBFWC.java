import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.BreadthFirstWebCrawler;
import edu.uwm.cs351.URLObject;


public class TestBFWC extends LockedTestCase {

	protected BreadthFirstWebCrawler wc;
	protected List<URLObject> results;
	
	@Override
	protected void setUp() {
		results = new ArrayList<>();
	}
	
	protected URL create(String s) {
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			// This shouldn't happen!
			e.printStackTrace();
			return null;
		}
	}
	
	public void testA0() {
		wc = new BreadthFirstWebCrawler(create("http://broken-link.com/no"));
		assertEquals(10,wc.crawl(13, results::add));
		assertEquals(3,results.size());
	}
	
	public void testA1() {
		wc = new BreadthFirstWebCrawler(create("file:resources/adip.html"));
		assertEquals(0,wc.crawl(0, results::add));
		assertEquals(0,results.size());
	}
	
	public void testB0() {
		wc = new BreadthFirstWebCrawler(create("file:resources/dolorum.html"));
		// this URL has no links in it.
		assertEquals(Ti(146914124),wc.crawl(10, results::add));
		assertEquals(Ti(1794802188),results.size());
		assertEquals(Ts(1738008981),results.get(0).uri.toString());
	}
	
	public void testB1() {
		wc = new BreadthFirstWebCrawler(create("file:resources/adip.html"));
		assertEquals(0,wc.crawl(1, results::add));
		assertEquals(1,results.size());
		assertEquals("file:resources/adip.html",results.get(0).uri.toString());
	}
	
	public void testB2() {
		wc = new BreadthFirstWebCrawler(create("file:resources/incid.html"));
		assertEquals(0,wc.crawl(1, results::add));
		assertEquals(1,results.size());
		assertEquals("file:resources/incid.html",results.get(0).uri.toString());
	}
	
	public void testB3() {
		wc = new BreadthFirstWebCrawler(create("file:resources/panther.html"));
		assertEquals(1,wc.crawl(2, results::add));
		assertEquals(1,results.size());
		assertEquals("file:resources/panther.html",results.get(0).uri.toString());
	}
	
	public void testC0() {
		wc = new BreadthFirstWebCrawler(create("file:resources/adip.html"));
		assertEquals(0,wc.crawl(2, results::add));
		assertEquals(2,results.size());
		assertEquals("file:resources/adip.html",results.get(0).uri.toString());
		assertEquals("file:resources/test1.html",results.get(1).uri.toString());
	}
	
	public void testC1() {
		wc = new BreadthFirstWebCrawler(create("file:resources/adip.html#fragment"));
		assertEquals(0,wc.crawl(2, results::add));
		assertEquals(2,results.size());
		assertEquals("file:resources/adip.html",results.get(0).uri.toString());
		assertEquals("file:resources/test1.html",results.get(1).uri.toString());
	}
	
	public void testC2() {
		wc = new BreadthFirstWebCrawler(create("file:resources/about_adip.html"));
		assertEquals(0,wc.crawl(3, results::add));
		assertEquals(3,results.size());
		assertEquals("file:resources/about_adip.html",results.get(0).uri.toString());
		assertEquals("file:resources/adip.html",results.get(1).uri.toString());
		assertEquals("file:resources/dolorum.html",results.get(2).uri.toString());
	}
	
	public void testC3() {
		wc = new BreadthFirstWebCrawler(create("file:resources/about_adip.html"));
		assertEquals(0,wc.crawl(4, results::add));
		assertEquals(4,results.size());
		assertEquals("file:resources/about_adip.html",results.get(0).uri.toString());
		assertEquals("file:resources/adip.html",results.get(1).uri.toString());
		assertEquals("file:resources/dolorum.html",results.get(2).uri.toString());
		assertEquals("file:resources/test1.html",results.get(3).uri.toString());
	}
	
	public void testC4() {
		wc = new BreadthFirstWebCrawler(create("file:resources/incid.html"));
		assertEquals(0,wc.crawl(3, results::add));
		assertEquals(3,results.size());
		assertEquals("file:resources/incid.html",results.get(0).uri.toString());
		assertEquals("file:resources/test1.html",results.get(1).uri.toString());
		assertEquals("file:resources/adip.html",results.get(2).uri.toString());
	}
	
	public void testC5() {
		wc = new BreadthFirstWebCrawler(create("file:resources/test1.html"));
		assertEquals(0,wc.crawl(4, results::add));
		assertEquals("file:resources/test1.html",results.get(0).uri.toString());
		assertEquals("file:resources/adip.html",results.get(1).uri.toString());
		assertEquals("file:resources/incid.html",results.get(2).uri.toString());
		assertEquals("file:resources/test2.html",results.get(3).uri.toString());
	}
	
	public void testD0() {
		wc = new BreadthFirstWebCrawler(create("file:resources/test2.html"));
		assertEquals(0,wc.crawl(5, results::add));
		assertEquals("file:resources/test2.html",results.get(0).uri.toString());
		assertEquals("file:resources/test1.html",results.get(1).uri.toString());
		assertEquals("file:resources/dolorum.html",results.get(2).uri.toString());
		assertEquals("https://en.wikipedia.org/wiki/Lorem_ipsum",results.get(3).uri.toString());
		assertEquals("file:resources/adip.html",results.get(4).uri.toString());
	}
	
	public void testD1() {
		wc = new BreadthFirstWebCrawler(create("file:resources/adip.html"));
		assertEquals(0,wc.crawl(6, results::add));
		assertEquals(6,results.size());
		assertEquals("file:resources/adip.html",results.get(0).uri.toString());
		assertEquals("file:resources/test1.html",results.get(1).uri.toString());
		assertEquals("file:resources/incid.html",results.get(2).uri.toString());
		assertEquals("file:resources/test2.html",results.get(3).uri.toString());
		assertEquals("file:resources/panther.html",results.get(4).uri.toString());
		assertEquals("http://broken-link.com/checkerchecker",results.get(5).uri.toString());
	}
}
