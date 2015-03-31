package analysis;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;

import table.ResultsTableSorter;

public class ParticleTracker implements PlugIn {

	private int lookAhead = 1;
	private double maxStepSize = 8;
	private double minimumWidth = 0;
	private double minimumHeight = 0;
	private boolean showTrajectories = true;
	
	@Override
	public void run(String arg0) {
		
		ResultsTable table = Analyzer.getResultsTable();
		
		if (table == null) {
			IJ.showMessage("This plugin requires a results table");
			return;
		}
		
		GenericDialog dialog = new GenericDialog("Particle Tracker");
		dialog.addNumericField("slice_to_look_ahead (for blinking)", lookAhead, 0);
		dialog.addNumericField("max_step_size (in pixels)", maxStepSize, 2);
		dialog.addNumericField("minimum_width (minimum movement on x-axis)", minimumWidth, 2);
		dialog.addNumericField("minimum_height (minimum movement on y-axis)", minimumHeight, 2);
		dialog.addCheckbox("show_trajectories", showTrajectories);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		lookAhead = (int)dialog.getNextNumber();
		maxStepSize = dialog.getNextNumber();
		minimumWidth = dialog.getNextNumber();
		minimumHeight = dialog.getNextNumber();
		
		// sort on slice
		ResultsTableSorter.sort(table, true, "slice");
		
		// initialize trajectory column and set step size to 0
		for (int row = 0; row < table.getCounter(); row++) {
			table.setValue("trajectory", row, -1);
			table.setValue("step_size", row, 0);
		}
		
		// determine which rows belong to which slice
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		offsets.add(0);
		
		for (int row = 1; row < table.getCounter(); row++) {
			int s0 = (int)table.getValue("slice", row - 1);
			int s1 = (int)table.getValue("slice", row);
			
			if (s0 != s1)
				offsets.add(row);
		}
		
		offsets.add(table.getCounter());
		
		
		// find all trajectories
		int trajectoryCount = 0;
		
		for (int i = 0; i < offsets.size() - 1; i++) {
			
			int from = offsets.get(i);
			int to = offsets.get(i + 1);
			
			// make a list of all possible links
			ArrayList<double[]> links = new ArrayList<double[]>();
			
			for (int row1 = from; row1 < to; row1++) {
				
				int s1 = (int)table.getValue("slice", row1);
				double x1 = table.getValue("x", row1);
				double y1 = table.getValue("y", row1);
				
				for (int row2 = to; row2 < table.getCounter(); row2++) {
					
					int s2 = (int)table.getValue("slice", row2);
					double x2 = table.getValue("x", row2);
					double y2 = table.getValue("y", row2);
					
					if (s2 - s1 > lookAhead)
						break;
					
					double dx = x2 - x1;
					double dy = y2 - y1;
					double dsq = dx * dx + dy * dy;
					
					if (dsq < maxStepSize * maxStepSize)
						links.add(new double[]{s2 - s1, dsq, row1, row2, dx, dy});
					
				}
				
			}
			
			// sort all possible links on distance (or slice number)
			Collections.sort(links, new Comparator<double[]>(){

				@Override
				public int compare(double[] o1, double[] o2) {
					if (o1[0] != o2[0])
						return Double.compare(o1[0], o2[0]);
					
					return Double.compare(o1[1], o2[1]);
				}
				
			});
			
			// filter out all links that are not possible
			Set<Integer> linked = new HashSet<Integer>();
			
			for (double[] link: links) {
				
				int r1 = (int)link[2];
				int r2 = (int)link[3];
				int t1 = (int)table.getValue("trajectory", r1);
				
				if (!linked.contains(r1) && !linked.contains(r2)) {

					if (t1 == -1) {
						t1 = trajectoryCount++;
						table.setValue("trajectory", r1, t1);						
					}
					
					table.setValue("trajectory", r2, t1);
					table.setValue("dx", r2, link[4]);
					table.setValue("dy", r2, link[5]);
					table.setValue("step_size", r2, Math.sqrt(link[1]));
					table.setValue("displacement_sq", r2, link[1]);
					
					linked.add(r1);
					linked.add(r2);
				}
				
			}
			
		}
		
		// sort on slice column
		ResultsTableSorter.sort(table, true, "slice", "trajectory");
		
		
		// filter out trajectories based on minimum/maximum bounding box
		int from = table.getCounter() - 1;
		double x = table.getValue("x", from);
		double y = table.getValue("y", from);
		Rectangle2D.Double boundingBox = new Rectangle2D.Double(x, y, 0, 0);
		
		for (int row = from - 1; row >= 0; row--) {
			
			x = table.getValue("x", row);
			y = table.getValue("y", row);
			
			if (table.getValue("trajectory", row) == table.getValue("trajectory", row + 1)) {
				boundingBox.add(x, y);
			}
			else {
				
				if (boundingBox.getWidth() < minimumWidth || boundingBox.getHeight() < minimumHeight) {
					
					// delete rows
					for (int r = from; r > row; r--)
						table.deleteRow(r);
					
				}
				
				from = row;
				boundingBox = new Rectangle2D.Double(x, y, 0, 0);
			}
			
		}
		
		// delete particles (rows) that don't belong to any trajectory
		for (int row = table.getCounter() - 1; row >= 0; row--) {
			if (table.getValue("trajectory", row) == -1)
				table.deleteRow(row);
		}
		
		if (showTrajectories) {
			
			RoiManager roiManager = RoiManager.getInstance();
			
			if (roiManager == null)
				roiManager = new RoiManager();
			
			Polygon poly = new Polygon();
			x = table.getValue("x", 0);
			y = table.getValue("y", 0);
			poly.addPoint((int)x, (int)y);
			
			for (int row = 1; row < table.getCounter(); row++) {

				x = table.getValue("x", row);
				y = table.getValue("y", row);

				if (table.getValue("trajectory", row) == table.getValue("trajectory", row - 1)) {
					poly.addPoint((int)x, (int)y);
				}
				else {
					roiManager.addRoi(new PolygonRoi(poly, PolygonRoi.POLYLINE));
					
					poly = new Polygon();
					poly.addPoint((int)x, (int)y);
				}
			}
			
			roiManager.addRoi(new PolygonRoi(poly, PolygonRoi.POLYLINE));
			roiManager.run("Show All");
			
		}
		
		table.show("Results");
		
	}

}
