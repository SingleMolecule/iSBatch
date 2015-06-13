/*
 * 
 */
package analysis;

import java.awt.Color;
import java.util.Arrays;

import table.ResultsTableSorter;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

// TODO: Auto-generated Javadoc
/**
 * The Class MeanSquareDisplacement2.
 */
public class MeanSquareDisplacement2 implements PlugIn {

	/** The time interval. */
	private double timeInterval = 0.1;
	
	/** The pixel size. */
	private double pixelSize = 0.1;
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	@Override
	public void run(String arg0) {
		
		ResultsTable table = Analyzer.getResultsTable();

		if (table == null) {
			IJ.showMessage("This plugin requires a results table");
			return;
		}

		GenericDialog dialog = new GenericDialog("Mean Square Displacement");
		dialog.addNumericField("Time_interval", timeInterval, 6, 10, "s");
		dialog.addNumericField("Pixel_size", pixelSize, 6, 10, " µm");
		dialog.addCheckbox("Show_square_displacements", false);
		dialog.addCheckbox("Show_mean_square_displacements", false);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;

		double timeInterval = dialog.getNextNumber();
		double pixelSize = dialog.getNextNumber();
		boolean showSDValues = dialog.getNextBoolean();
		boolean showMSDValues = dialog.getNextBoolean();
		
		ResultsTable sdTable = new ResultsTable();
		
		for (int r1 = 0; r1 < table.getCounter(); r1++) {
			
			for (int r2 = r1 + 1; r2 < table.getCounter(); r2++) {
				
				int t1 = (int)table.getValue("trajectory", r1);
				int t2 = (int)table.getValue("trajectory", r2);
				
				if (t1 == -1 || t2 == -1 || t1 != t2)
					break;
				
				int s1 = (int)table.getValue("slice", r1);
				int s2 = (int)table.getValue("slice", r2);
				double x1 = table.getValue("x", r1);
				double y1 = table.getValue("y", r1);
				double x2 = table.getValue("x", r2);
				double y2 = table.getValue("y", r2);
				
				int dt = s2 - s1;
				double dx = x2 - x1;
				double dy = y2 - y1;
				double sd = dx * dx + dy * dy;
				
				sdTable.incrementCounter();
				sdTable.addValue("trajectory", t1);
				sdTable.addValue("dt", dt * timeInterval);
				sdTable.addValue("sd", sd * pixelSize * pixelSize);
			}
			
		}
		
		// sort table on delta t
		ResultsTableSorter.sort(sdTable, true, "dt");
		
		// calculate mean square displacement
		ResultsTable msdTable = new ResultsTable();
		
		double msd = 0;
		int n = 0;
		int from = 0;
		
		for (int row = 0; row < sdTable.getCounter(); row++) {
			
			double dt = sdTable.getValue("dt", row);
			double sd = sdTable.getValue("sd", row);
			
			msd += sd;
			n++;
			
			// last row or new delta t
			if (row + 1 >= sdTable.getCounter() || dt != sdTable.getValue("dt", row + 1)) {
				
				msd /= n;
				msdTable.incrementCounter();
				msdTable.addValue("dt", dt);
				msdTable.addValue("msd", msd);
				
				// calculate standard deviation
				double stdDev = 0;
				
				for (int r = from; r < row; r++) {
					sd = sdTable.getValue("sd", r);
					double d = sd - msd;
					stdDev += d * d;
				}
				
				stdDev = Math.sqrt(stdDev / n);
				msdTable.addValue("stdDev", stdDev);
				
				msd = 0;
				n = 0;
				from = row;
			}
			
		}

		// plot all mean square displacement values
		double[] x = new double[msdTable.getCounter()];
		double[] y = new double[msdTable.getCounter()];
		double[] error = new double[msdTable.getCounter()];
		
		for (int row = 0; row < msdTable.getCounter(); row++) {
			x[row] = msdTable.getValue("dt", row);
			y[row] = msdTable.getValue("msd", row);
			error[row] = msdTable.getValue("stdDev", row);
		}
		
		Plot plot = new Plot();
		plot.addScatterPlot(x, y, Color.BLACK, 2);
		plot.addErrorBars(x, y, error, Color.GRAY, 1);
		plot.showPlot("mean square displacements");
		
		
		// fit linear part of the plot
		dialog = new GenericDialog("Fit Mean Square Displacement"); 
		dialog.addNumericField("Fit_until_time", timeInterval * 10, 6, 10, "s");
		dialog.showDialog();
		
		double fitUntil = dialog.getNextNumber();
		
		for (int i = 0; i < x.length; i++) {
			if (x[i] > fitUntil) {
				x = Arrays.copyOf(x, i);
				y = Arrays.copyOf(y, i);
				error = Arrays.copyOf(error, i);
				break;
			}
		}
		
		
		// fit data
		LevenbergMarquardt lm = new LevenbergMarquardt() {
			
			@Override
			public double getValue(double[] x, double[] p, double[] dyda) {
				dyda[0] = 4 * x[0];
				return 4 * p[0] * x[0];
			}
		};
		
		double[][] xx = new double[x.length][1];
		
		for (int i = 0; i < x.length; i++)
			xx[i][0] = x[i];
		
		msd = y[y.length - 1];
		double t = x[x.length - 1];
		
		double[] p = new double[]{msd / (4 * t)};
		double[] e = new double[1];
		
		lm.solve(xx, y, error, xx.length, p, null, e, 0.001);
		
		double[] fx = new double[2];
		double[] fy = new double[2];
		
		fx[0] = 0;
		fy[0] = 0;
		
		fx[1] = t;
		fy[1] = 4 * p[0] * t;
		
		plot = new Plot();
		plot.addErrorBars(x, y, error, Color.GRAY, 1f);
		plot.addScatterPlot(x, y, Color.BLACK, 1f);
		plot.addLinePlot(fx, fy, Color.RED, 1f);
		plot.setCaption("D = " + p[0] + " µm^2/s  fitting error = " + e[0]);
		plot.setxAxisLabel("Time Step (s)");
		plot.setyAxisLabel("Mean Square Displacement (µm)");
		plot.showPlot("Mean Square Displacement");
		
		// show tables (optional)
		if (showSDValues)
			sdTable.show("Square Displacement");
		
		if (showMSDValues)
			msdTable.show("Mean Square Displacement");

	}

}
