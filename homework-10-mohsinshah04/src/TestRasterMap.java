import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import edu.uwm.cs351.RasterMap;

public class TestRasterMap extends AbstractTestMap<Point,Color> {

	@Override
	protected Map<Point,Color> create() {
		return new RasterMap();
	}

	protected Color clone(Color c) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	
	@Override
	protected void initMapElements() {
		super.permitNulls = false;
		super.sorted = true;
		super.failFast = true;
		super.hasRemove = true;
		k = new Point[] { 
				new Point(0,0), new Point(0,1), new Point(1,3), new Point(1,6),
				new Point(2,0), new Point(2,1), new Point(2,2), new Point(2,5),
				new Point(4,1), new Point(4,4)
		};
		l = new Point[] { 
				new Point(0,0), new Point(0,1), new Point(1,3), new Point(1,6),
				new Point(2,0), new Point(2,1), new Point(2,2), new Point(2,5),
				new Point(4,1), new Point(4,4)
		};
		v = new Color[] {
				Color.WHITE, Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, 
				Color.PINK, Color.CYAN, Color.YELLOW, Color.MAGENTA,
				Color.GRAY, Color.ORANGE
		};
		w = new Color[v.length];
		for (int i=0; i < v.length; ++i) {
			w[i] = clone(v[i]);
		}
	}

	private final String[] COLOR_NAMES = {
		"WHITE", "BLACK", "RED", "BLUE", "GREEN",
		"PINK", "CYAV", "YELLOW", "MAGENTA",
		"GRAY", "ORANGE"
	};
	
	/// locked tests
	
	protected String string(Supplier<?> supp) {
		String result;
		try {
			result = "" + supp.get();
		} catch(RuntimeException ex) {
			return ex.getClass().getSimpleName();
		}
		for (int i=0; i < v.length; ++i) {
			result = result.replace(v[i].toString(), COLOR_NAMES[i]);
		}
		return result;
	}
	
	protected String string(Runnable r) {
		return string(() -> {
			r.run();
			return "void";
		});
	}

	public void test490() {
		// In the what follows, "string" converts the result to a string,
		// where a void return gets "void" and if an exception
		// happens, we have the name of the exception.
		// You should know that an entry prints as key=value
		// (an equals sign in between and no spaces).
		// Java points print as "java.awt.Point[x=X,y=Y]" where X and Y are the coordinates.
		// We arrange for Colors to print with their UPPERCAE names (without the "Color." prefix).
		// And recall that pixels print as <X,Y,COLOR> where X, Y and COLOR are replaced
		// y the coordinates and color respectively.
		m.put(new Point(4,0), Color.RED);
		m.put(new Point(1,3), Color.BLUE);
		m.put(new Point(9,9), Color.GREEN);
		Iterator<Map.Entry<Point,Color>> it = m.entrySet().iterator();
		assertEquals(Ts(1637579151), string(() -> it.next())); // what does "next" return ?
		assertEquals(Ts(239007557), string(() -> m.remove(new Point(4,0))));
		assertEquals(Ts(695998347), string(() -> it.remove()));
		Map.Entry<Point, Color> e = m.entrySet().iterator().next();
		assertEquals(Ts(2056831811), string(() -> m.entrySet().remove(e)));
	}

	
	/// these tests will be out of order but will still be graded as if they were in the correct order
	
	public void test449() {
		Set<Point> s = m.keySet();
		m.put(k[4], v[4]);
		Point p = s.iterator().next();
		p.x = 45;
		p.y = 10;
		assertFalse(s.contains(p));
	}
}
