package operations.microbeTrackerIO;


public class Point {
	public double x, y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double distanceTo(Point p) {
		double dx = p.x - x;
		double dy = p.y - y;
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	// http://stackoverflow.com/questions/1560492/how-to-tell-whether-a-point-is-to-the-right-or-left-side-of-a-line
	public boolean isLeftOf(Line l) {
		
		Vec vec = new Vec(x - l.orig.x, y - l.orig.y);
		
		return l.u * vec.v - l.v * vec.u > 0;
	}
	
	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
	
}
