package operations.microbeTrackerIO;

import java.util.ArrayList;

public class Mesh {
	
	private ArrayList<Segment> segments = new ArrayList<Segment>();
	private double totalLength;
	private int slice;
	private int cell;
	private double area;
	private double volume;
	private ArrayList<Point> outline = new ArrayList<Point>();
	
	
	public ArrayList<Point> getOutline(){
		return outline;
	}
	public Mesh(int slice, int cell, double area, double volume) {
		this.slice = slice;
		this.cell = cell;
		this.area = area;
		this.volume = volume;
	}
	
	public Mesh(int slice, int cell, double area, double volume,  ArrayList<Point> outline ) {
		this.slice = slice;
		this.cell = cell;
		this.area = area;
		this.volume = volume;
		this.outline = outline;
		
	}

	public void addSegment(Segment s) {
		s.setLengthOffset(totalLength);
		segments.add(s);
		totalLength += s.length();
	}
	
	public double distanceTo(Point p) {
		return closestSegmentTo(p).distanceTo(p);
	}

	public Point projectionOf(Point p) {
		return closestSegmentTo(p).projectionOf(p);
	}
	
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

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	public double getTotalLength() {
		return totalLength;
	}

	public int getSlice() {
		return slice;
	}

	public int getCell() {
		return cell;
	}

	public double getArea() {
		return area;
	}

	public double getVolume() {
		return volume;
	}
	
}
