import java.awt.Color;
import java.awt.Point;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import edu.uwm.cs.random.AbstractRandomTest;
import edu.uwm.cs.random.Command;
import edu.uwm.cs.util.TriFunction;
import edu.uwm.cs351.RasterMap;
import edu.uwm.cs351.test.MapReference;
import edu.uwm.cs351.util.DefaultEntry;

public class RandomTest extends AbstractRandomTest<MapReference<Point,Color>, RasterMap> {
	private static final int DEFAULT_MAX_TEST_LENGTH = 1000;
	private static final int MAX_TESTS = 3_000_000;

	private static Comparator<Point> comp = (p1, p2) -> {
		int diff1 = p1.x - p2.x;
		if (diff1 != 0) return diff1;
		return p1.y - p2.y;
	};

	@SuppressWarnings("unchecked")
	private final Class<Set<Entry<Point,Color>>> entrySetClass = (Class<Set<Entry<Point,Color>>>)(Class<?>)Set.class;
	private final RegisteredClass<Set<Entry<Point,Color>>, Set<Entry<Point,Color>>> entrySetDesc = super.registerMutableClass(entrySetClass, entrySetClass, "Set<Entry<Point,Color>>", "s");
	private final RegisteredClass<Iterator<Entry<Point,Color>>, Iterator<Entry<Point,Color>>> iteratorClass = super.registerMutableClass(iteratorClass(), iteratorClass(), "Iterator<Entry<Point,Color>>", "i");

	@SuppressWarnings("unchecked")
	protected RandomTest() {
		super((Class<MapReference<Point,Color>>)(Class<?>)MapReference.class, RasterMap.class, "RasterMap", "m", MAX_TESTS, DEFAULT_MAX_TEST_LENGTH);
	}

	protected Point p(Random r) {
		double d = r.nextDouble();
		if (d <= 1.0/25) return null;
		int n = (int)(1.0/d);
		return new Point(n,r.nextInt(10));
	}
	
	protected Color c(Random r) {
		return new Color(64*r.nextInt(4),64*r.nextInt(4),64*r.nextInt(4));
	}
	
	protected Entry<Point,Color> e(Random r) {
		int n = r.nextInt(32);
		if (n < 29) return new DefaultEntry<>(new Point(n%11,n%10),c(r));
		if (n < 30) return null;
		return new DefaultEntry<Point,Color>(null,c(r));
	}
		
	@Override
	public String toString(Object x) {
		if (x instanceof Entry<?,?>) {
			Entry<?,?> e = (Entry<?,?>)x;
			return "new DefaultEntry<>(" + toString(e.getKey()) + "," + toString(e.getValue()) + ")";
		} else if (x instanceof Point) {
			Point p = (Point)x;
			return "new Point("+p.x+","+p.y+")";
		} else if (x instanceof Color) {
			Color c = (Color)x;
			return "new Color(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
		}
		return super.toString(x);
	}

	private Command<?> newMapCommand = create(mainClass, () -> new MapReference<Point,Color>(comp), () -> new RasterMap());
	private Function<Integer,Command<?>> sizeMapCommand = build(lift(MapReference<Point,Color>::size), lift(RasterMap::size), "size"); 
	private Function<Integer,Command<?>> entrySetCommand = build(lift(entrySetDesc,MapReference::entrySet), lift(entrySetDesc,RasterMap::entrySet), "entrySet");
	private Function<Integer,Command<?>> clearMapCommand = build(lift(MapReference<Point,Color>::clear), lift(RasterMap::clear), "clear"); 
	private BiFunction<Integer, Point, Command<?>> getCommand = build(lift(MapReference<Point,Color>::get), lift(RasterMap::get), "get");
	private BiFunction<Integer, Point, Command<?>> containsKeyCommand = build(lift(MapReference<Point,Color>::containsKey), lift(RasterMap::containsKey), "containsKey");
	private BiFunction<Integer, Point, Command<?>> removeKeyCommand = build(lift((MapReference<Point,Color> m, Point k) -> m.remove(k)), lift((RasterMap m, Point k) -> m.remove(k)), "remove");
	private TriFunction<Integer, Point, Color, Command<?>> putCommand = build(lift(MapReference<Point,Color>::put), lift(RasterMap::put), "put");

	private Function<Integer,Command<?>> sizeSetCommand = build(entrySetDesc,lift(Set<Entry<Point,Color>>::size), "size");
	private Function<Integer,Command<?>> clearSetCommand = build(entrySetDesc,lift(Set<Entry<Point,Color>>::clear), "clear");
	private Function<Integer,Command<?>> iteratorCommand = build(entrySetDesc,lift(iteratorClass, Set<Entry<Point,Color>>::iterator), "iterator");
	private BiFunction<Integer, Entry<Point,Color>, Command<?>> containsCommand = build(entrySetDesc,lift(Set<Entry<Point,Color>>::contains), "contains");
	private BiFunction<Integer, Entry<Point,Color>, Command<?>> removeEntryCommand = build(entrySetDesc,lift(Set<Entry<Point,Color>>::remove), "remove");

	private Function<Integer,Command<?>> hasNextCommand = build(iteratorClass,lift(Iterator<Entry<Point,Color>>::hasNext),"hasNext");
	private Function<Integer,Command<?>> nextCommand = build(iteratorClass,lift(Iterator<Entry<Point,Color>>::next),"next");
	private Function<Integer,Command<?>> removeCommand = build(iteratorClass,lift(Iterator<Entry<Point,Color>>::remove),"remove");

	@Override
	protected Command<?> randomCommand(Random r) {
		int n = mainClass.size();
		if (n == 0) return newMapCommand;
		int ni = iteratorClass.size();
		int ns = entrySetDesc.size();
		int index = r.nextInt(n);
		switch (r.nextInt(42)) {
		default:
		case 0: return newMapCommand;
		case 1: return clearMapCommand.apply(index);
		case 2:
		case 3: return sizeMapCommand.apply(index);
		case 4:
		case 5:
		case 6: return putCommand.apply(index, p(r), c(r));
		case 7:
		case 8:
		case 9: return getCommand.apply(index, p(r));
		case 10:
		case 11:
		case 12: return entrySetCommand.apply(index);
		case 13:
		case 14:
		case 15: return containsKeyCommand.apply(index, p(r));
		case 16:
		case 17:
		case 18: return removeKeyCommand.apply(index, p(r));
		// the next cases all fall through to create an iterator
		case 19: 
		case 20: if (ni > 0) return removeCommand.apply(r.nextInt(ni));
		case 21: case 22: case 23: case 24:
		case 25: if (ni > 0) return hasNextCommand.apply(r.nextInt(ni));
		case 26: case 27: case 28: case 29:
		case 30: if (ni > 0) return nextCommand.apply(r.nextInt(ni));
		// the next cases all fall through to create an entry set
		case 31: if (ns > 0) return iteratorCommand.apply(r.nextInt(ns));
		case 32: if (ns > 0) return clearSetCommand.apply(r.nextInt(ns));
		case 33:
		case 34: if (ns > 0) return sizeSetCommand.apply(r.nextInt(ns));
		case 35:
		case 36:
		case 37: if (ns > 0) return containsCommand.apply(r.nextInt(ns), e(r));
		case 38:
		case 39:
		case 40: if (ns > 0) return removeEntryCommand.apply(r.nextInt(ns), e(r));		
		case 41: return entrySetCommand.apply(index);
		}
	}

	@Override
	protected void printImports() {
		super.printImports();
		System.out.println("import java.awt.Color;");
		System.out.println("import java.awt.Point;\n");
		System.out.println("import java.util.Iterator;");
		System.out.println("import java.util.Map.Entry;");
		System.out.println("import java.util.Set;\n");
		System.out.println("import edu.uwm.cs351.util.DefaultEntry;");
		System.out.println("import edu.uwm.cs351.RasterMap;");
	}
	
	public static void main(String[] args) {
		RandomTest test = new RandomTest();
		test.run();
	}
}
