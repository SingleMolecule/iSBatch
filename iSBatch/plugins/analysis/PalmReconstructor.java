package analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;

public class PalmReconstructor implements PlugIn {
	
	private double zBinning = 0;
	private double magnification = 4;
	
	@Override
	public void run(String arg0) {

		ResultsTable table = Analyzer.getResultsTable();
		
		int xColumn = table.getColumnIndex("x");
		int yColumn = table.getColumnIndex("y");
		int zColumn = table.getColumnIndex("z");
		
		int xErrorColumn = table.getColumnIndex("error_x");
		int yErrorColumn = table.getColumnIndex("error_y");
		int zErrorColumn = table.getColumnIndex("error_z");
		
		double[] x = table.getColumnAsDoubles(xColumn);
		double[] y = table.getColumnAsDoubles(yColumn);
		double[] z = table.getColumnAsDoubles(zColumn);
		
		double[] xError = table.getColumnAsDoubles(xErrorColumn);
		double[] yError = table.getColumnAsDoubles(yErrorColumn);
		double[] zError = table.getColumnAsDoubles(zErrorColumn);
		
		double xMin = x[0];
		double xMax = x[0];
		double yMin = y[0];
		double yMax = y[0];
		double zMin = z[0];
		double zMax = z[0];
		
		for (int i = 1; i < x.length; i++) {
			if (x[i] < xMin) xMin = x[i];
			if (x[i] > xMax) xMax = x[i];
			if (y[i] < yMin) yMin = y[i];
			if (y[i] > yMax) yMax = y[i];
			if (z[i] < zMin) zMin = z[i];
			if (z[i] > zMax) zMax = z[i];
		}
		
		xMin = Math.floor(xMin);
		xMax = Math.ceil(xMax);
		yMin = Math.floor(yMin);
		yMax = Math.ceil(yMax);
		zMin = Math.floor(zMin);
		zMax = Math.ceil(zMax);
		
		// show dialog
		
		GenericDialog dialog = new GenericDialog("Palm Reconstruction");
		dialog.addNumericField("x_min", xMin, 2);
		dialog.addNumericField("x_max", xMax, 2);
		dialog.addNumericField("y_min", yMin, 2);
		dialog.addNumericField("y_max", yMax, 2);
		dialog.addNumericField("z_min", zMin, 2);
		dialog.addNumericField("z_max", zMax, 2);
		dialog.addNumericField("z_binning", zBinning, 2);
		dialog.addNumericField("magnification", magnification, 2);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		xMin = dialog.getNextNumber();
		xMax = dialog.getNextNumber();
		yMin = dialog.getNextNumber();
		yMax = dialog.getNextNumber();
		zMin = dialog.getNextNumber();
		zMax = dialog.getNextNumber();
		zBinning = dialog.getNextNumber();
		magnification = dialog.getNextNumber();
		
		int slices = (zBinning == 0) ? 1 : (int)((zMax - zMin) / zBinning) + 1;
		int width = (int)Math.ceil((xMax - xMin) * magnification);
		int height = (int)Math.ceil((yMax - yMin) * magnification);
		
		ImagePlus imp = IJ.createImage("reconstruction", "32-bit", width, height, slices);
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice <= slices; slice++) {
			
			ImageProcessor ip = stack.getProcessor(slice);
			
			// the z position is in the middle
			double z0 = zMin + ((slice - 1) + 0.5) * zBinning;
			
			IJ.showStatus("slice : " + slice + " z : " + z0);
			
			for (int i = 0; i < x.length; i++) {

				int x0 = (int)((x[i] - xMin) * magnification);
				int y0 = (int)((y[i] - yMin) * magnification);
				double sigma = (Math.sqrt(xError[i] * xError[i] + yError[i] * yError[i] + zError[i] * zError[i]) / 3) * magnification;
				
				if (zBinning == 0 || (z[i] > z0 - (zBinning + sigma) && z[i] < z0 + (zBinning + sigma))) { 
					// 20 x 20 pixel gaussians
					for (int y1 = -10; y1 <= 10; y1++) {
						for (int x1 = -10; x1 <= 10; x1++) {
							
							double dz = zBinning == 0 ? 0 : z0 - z[i];
							
							double d = Math.sqrt(x1 * x1 + y1 * y1 + dz * dz);
							double value = normalDistribution3D(d, sigma);
							float f = ip.getPixelValue(x0 + x1, y0 + y1);
							
							ip.putPixelValue(x0 + x1, y0 + y1, f + value);
						}
					}
				}
				
				IJ.showProgress(i, x.length);
				
			}	// end for x, y, z values
			
		}	// end for (through all the z slices)
		
		imp.show();
	}
	
	public static double normalDistribution3D(double x, double s) {
		double d = s * Math.sqrt(2.0 * Math.PI);
		
		// 3 dimensional
		return 1.0 / (d * d * d) * Math.exp(-(x * x) / (2.0 * s * s));
	}
	
	public static void main(String[] args) {
		
		double s = Math.sqrt(1);
		double sum = 0;
		
		for (double x = -5; x <= 5; x += 0.1) {
			for (double y = -5; y <= 5; y += 0.1) {
				for (double z = -5; z <= 5; z += 0.1) {
					double d = Math.sqrt(x * x + y * y + z * z);
					
					sum += normalDistribution3D(d, s) * 0.001; 
				}
			}
		}
		
		System.out.println(sum);
	}
}
