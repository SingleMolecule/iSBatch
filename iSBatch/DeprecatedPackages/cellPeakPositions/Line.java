package cellPeakPositions;

public class Line extends Vec {
	public Point orig;
	
	public Line(Point orig, Vec vec) {
		super(vec.u, vec.v);
		this.orig = orig;
	}
	
	public Line(Point orig, Point dest) {
		this(orig, new Vec(dest.x - orig.x, dest.y - orig.y));
	}
	
	public Point intersection(Line l) {
		double scalar = (u * (orig.y - l.orig.y) + v * (l.orig.x - orig.x)) / (u * l.v - l.u * v);
		
		return new Point(l.orig.x + scalar * l.u, l.orig.y + scalar * l.v);
	}
	
	public Point center() {
		return new Point(orig.x + 0.5 * u, orig.y + 0.5 * v);
	}
	
	@Override
	public String toString() {
		return orig + "-" + new Point(orig.x + u, orig.y + v);
	}

	
}

