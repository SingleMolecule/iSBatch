/*
 * 
 */
package operations.microbeTrackerIO;

// TODO: Auto-generated Javadoc
/**
 * The Class Vec.
 */
public class Vec {

	/** The u. */
	public double u;
	
	/** The v. */
	public double v;

	/**
	 * Instantiates a new vec.
	 *
	 * @param u the u
	 * @param v the v
	 */
	public Vec(double u, double v) {
		this.u = u;
		this.v = v;
	}
	
	/**
	 * Length.
	 *
	 * @return the double
	 */
	public double length() {
		return Math.sqrt(u * u + v * v);
	}
	
	/**
	 * Normal.
	 *
	 * @return the vec
	 */
	public Vec normal() {
		return new Vec(v, -u);
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return String.format("[%f, %f]", u, v);
	}
	
}
