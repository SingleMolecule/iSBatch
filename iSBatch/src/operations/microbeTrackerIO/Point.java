/*
 * 
 */
package operations.microbeTrackerIO;


// TODO: Auto-generated Javadoc
/**
 * The Class Point.
 */
public class Point {
	
	/** The y. */
	public double x, y;

	/**
	 * Instantiates a new point.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Distance to.
	 *
	 * @param p the p
	 * @return the double
	 */
	public double distanceTo(Point p) {
		double dx = p.x - x;
		double dy = p.y - y;
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	// http://stackoverflow.com/questions/1560492/how-to-tell-whether-a-point-is-to-the-right-or-left-side-of-a-line
	/**
	 * Checks if is left of.
	 *
	 * @param l the l
	 * @return true, if is left of
	 */
	public boolean isLeftOf(Line l) {
		
		Vec vec = new Vec(x - l.orig.x, y - l.orig.y);
		
		return l.u * vec.v - l.v * vec.u > 0;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
	
}
