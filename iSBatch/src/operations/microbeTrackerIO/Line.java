/*
 * 
 */
package operations.microbeTrackerIO;

// TODO: Auto-generated Javadoc
/**
 * The Class Line.
 */
public class Line extends Vec {
	
	/** The orig. */
	public Point orig;
	
	/**
	 * Instantiates a new line.
	 *
	 * @param orig the orig
	 * @param vec the vec
	 */
	public Line(Point orig, Vec vec) {
		super(vec.u, vec.v);
		this.orig = orig;
	}
	
	/**
	 * Instantiates a new line.
	 *
	 * @param orig the orig
	 * @param dest the dest
	 */
	public Line(Point orig, Point dest) {
		this(orig, new Vec(dest.x - orig.x, dest.y - orig.y));
	}
	
	/**
	 * Intersection.
	 *
	 * @param l the l
	 * @return the point
	 */
	public Point intersection(Line l) {
		double scalar = (u * (orig.y - l.orig.y) + v * (l.orig.x - orig.x)) / (u * l.v - l.u * v);
		
		return new Point(l.orig.x + scalar * l.u, l.orig.y + scalar * l.v);
	}
	
	/**
	 * Center.
	 *
	 * @return the point
	 */
	public Point center() {
		return new Point(orig.x + 0.5 * u, orig.y + 0.5 * v);
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return orig + "-" + new Point(orig.x + u, orig.y + v);
	}

	
}

