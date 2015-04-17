package cellPeakPositions;

public class Segment extends Line {

	private double lengthOffset = 0;
	
	public Segment(Line l1, Line l2) {
		super(l1.center(), l2.center());
	}
	
	public double distanceTo(Point p) {
		return center().distanceTo(p);
	}
	
	public Point projectionOf(Point p) {
		Point i = intersection(new Line(p, this.normal()));
		double orientation = p.isLeftOf(this) ? 1 : -1;
		
		return new Point(i.distanceTo(orig) + lengthOffset, i.distanceTo(p) * orientation);
	}
	
	public void setLengthOffset(double lengthOffset) {
		this.lengthOffset = lengthOffset;
	}
	
	public static void main(String[] args) {
		Segment s = new Segment(new Line(new Point(0,0), new Point(0, 10)), new Line(new Point(10, 10), new Point(10, 20)));
		Point p = new Point(5, 15);
		
		System.out.println(s.projectionOf(p));
	}
}
