package operations.microbeTrackerIO;

// Adapted from http://rsb.info.nih.gov/ij/plugins/download/ROI_Importer.java

import ij.*;
import ij.process.*;
import ij.gui.*;

import java.awt.*;
import java.util.ArrayList;

import ij.plugin.*;
import ij.measure.*;
import ij.plugin.TextReader;

public class ROIBuilder implements PlugIn {
	private ArrayList<Point> points;
	private Roi roi;
	private int numberOfPoints;
	int maxIntX;
	int maxIntY;
	
	public ROIBuilder(ArrayList<Point> points) {
		this.points = points;
		this.numberOfPoints = points.size();
		
		createROI();
		
	}

	private void createROI() {
		//Get maximun X Value
		setMax();
		int maxIntX = getMax(points, 1);
		
		//Get Maxium Y Value
		int maxIntY = getMax(points, 2);
		
		// Create a image with values 0
//		ImagePlus imp2 = new ImagePlus
//		ImagePlus imp = new ImagePlus("dummy"), new ByteProcessor(Math.abs(r.x)
//				+ r.width + 10, Math.abs(r.y) + r.height + 10));
//		
		
		
		
		
	}

	private void setMax() {
		for(Point point : points){
			if (point.x >= maxIntX){
				maxIntX = (int)Math.round(point.x);
			}
			
			if (point.y >= maxIntY){
				maxIntY = (int)Math.round(point.y);
			}
		}
		
	}

	private int getMax(ArrayList<Point> points, int i) {


		
		
		
		return 0;
	}

	@SuppressWarnings("deprecation")
	public void run(String arg) {
		if (IJ.versionLessThan("1.26f"))
			return;
		TextReader tr = new TextReader();
		ImageProcessor ip = tr.open();
		if (ip == null)
			return;
		// int width = ip.getWidth();
		// int height = ip.getHeight();
		// if (width!=2 || height<3) {
		// IJ.showMessage("ROI Importer", "Two column text file required");
		// return;
		// }
		double d = ip.getPixelValue(0, 0);
		if (d != (int) d) {
			IJ.showMessage("ROI Importer", "Integer coordinates required");
			return;
		}
		int[] x = new int[numberOfPoints];
		int[] y = new int[numberOfPoints];
		for (int i = 0; i < numberOfPoints; i++) {
			x[i] = (int) Math.round(ip.getPixelValue(0, i));
			y[i] = (int) Math.round(ip.getPixelValue(1, i));
		}

		roi = new PolygonRoi(x, y, numberOfPoints, null, Roi.FREEROI);
		if (roi.getLength() / x.length > 10)
			roi = new PolygonRoi(x, y, numberOfPoints, null, Roi.POLYGON); // use
																			// "handles"
//		@SuppressWarnings("deprecation")
//		Rectangle r = roi.getBoundingRect();
//		ImagePlus imp = WindowManager.getCurrentImage();
//		if (imp == null || imp.getWidth() < r.x + r.width
//				|| imp.getHeight() < r.y + r.height) {
//			new ImagePlus(tr.getName(), new ByteProcessor(Math.abs(r.x)
//					+ r.width + 10, Math.abs(r.y) + r.height + 10)).show();
//			imp = WindowManager.getCurrentImage();
//		}
//		if (imp != null)
//			imp.setRoi(roi);
	}

	public Roi getRoi() {
		// TODO Auto-generated method stub
		return roi;
	}
	
	

}