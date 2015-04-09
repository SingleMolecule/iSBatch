package cellPeakPositions;

public class Vec {

	public double u;
	public double v;

	public Vec(double u, double v) {
		this.u = u;
		this.v = v;
	}
	
	public double length() {
		return Math.sqrt(u * u + v * v);
	}
	
	public Vec normal() {
		return new Vec(v, -u);
	}
	
	@Override
	public String toString() {
		return String.format("[%f, %f]", u, v);
	}
	
}
