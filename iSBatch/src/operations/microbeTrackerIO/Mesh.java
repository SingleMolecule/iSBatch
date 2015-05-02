/*
 * 
 */
package operations.microbeTrackerIO;

import ij.gui.PolygonRoi;
import ij.gui.Roi;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Mesh.
 */
public class Mesh {
	
	/** The segments. */
	private ArrayList<Segment> segments = new ArrayList<Segment>();
	
	/** The total length. */
	private double totalLength;
	
	/** The slice. */
	private int slice;
	
	/** The cell. */
	private int cell;
	
	/** The area. */
	private double area;
	
	/** The volume. */
	private double volume;
	
	/** The outline. */
	private ArrayList<Point> outline = new ArrayList<Point>();
	
	
	/**
	 * Gets the outline.
	 *
	 * @return the outline
	 */
	public ArrayList<Point> getOutline(){
		return outline;
	}
	
	/**
	 * Instantiates a new mesh.
	 *
	 * @param slice the slice
	 * @param cell the cell
	 * @param area the area
	 * @param volume the volume
	 */
	public Mesh(int slice, int cell, double area, double volume) {
		this.slice = slice;
		this.cell = cell;
		this.area = area;
		this.volume = volume;
	}
	
	/**
	 * Instantiates a new mesh.
	 *
	 * @param slice the slice
	 * @param cell the cell
	 * @param area the area
	 * @param volume the volume
	 * @param outline the outline
	 */
	public Mesh(int slice, int cell, double area, double volume,  ArrayList<Point> outline ) {
		this.slice = slice;
		this.cell = cell;
		this.area = area;
		this.volume = volume;
		this.outline = outline;
		
	}

	/**
	 * Adds the segment.
	 *
	 * @param s the s
	 */
	public void addSegment(Segment s) {
		s.setLengthOffset(totalLength);
		segments.add(s);
		totalLength += s.length();
	}
	
	/**
	 * Distance to.
	 *
	 * @param p the p
	 * @return the double
	 */
	public double distanceTo(Point p) {
		return closestSegmentTo(p).distanceTo(p);
	}

	/**
	 * Projection of.
	 *
	 * @param p the p
	 * @return the point
	 */
	public Point projectionOf(Point p) {
		return closestSegmentTo(p).projectionOf(p);
	}
	
	/**
	 * Closest segment to.
	 *
	 * @param p the p
	 * @return the segment
	 */
	public Segment closestSegmentTo(Point p) {
		
		Segment closestSegment = null;
		double minDistance = Double.POSITIVE_INFINITY;
		
		for (Segment s: segments) {
			
			double distance = s.distanceTo(p);
			
			if (distance < minDistance) {
				minDistance = distance; 
				closestSegment = s;
			}
			
		}
		
		return closestSegment;
	}

	/**
	 * Gets the segments.
	 *
	 * @return the segments
	 */
	public ArrayList<Segment> getSegments() {
		return segments;
	}

	/**
	 * Gets the total length.
	 *
	 * @return the total length
	 */
	public double getTotalLength() {
		return totalLength;
	}

	/**
	 * Gets the slice.
	 *
	 * @return the slice
	 */
	public int getSlice() {
		return slice;
	}

	/**
	 * Gets the cell.
	 *
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public double getArea() {
		return area;
	}

	/**
	 * Gets the volume.
	 *
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}
	
	/**
	 * Gets the roi.
	 *
	 * @param m the m
	 * @return the roi
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private static Roi getRoi(Mesh m) {
		
		ArrayList<Point> points = m.getOutline();
		
		int height = points.size();
		int[] x = new int[height];
		int[] y = new int[height];
		
		for (int i=0; i<points.size(); i++) {
			x[i] = (int)Math.round(points.get(i).x);
			y[i] = (int)Math.round(points.get(i).y);
		}
		
		Roi roi = new PolygonRoi(x, y, height, null, Roi.FREEROI);
		if (roi.getLength()/x.length>10)
			roi = new PolygonRoi(x, y, height, null, Roi.POLYGON); // use "handles"
		
		return roi;
	}
	
}
