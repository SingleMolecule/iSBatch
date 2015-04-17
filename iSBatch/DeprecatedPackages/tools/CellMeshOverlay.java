// this script creates an average image of all the cells defined by the meshes from a results table

// the results table is created by MicrobeTracker (with some added functions)

// c.m.punter@rug.nl

// 2014/2/3



// 2014/2/6 Added from/to slice option (c.m.punter@rug.nl)



// 2014/2/12 Converted macro to Java code (c.m.punter@rug.nl)



package tools;



import ij.IJ;

import ij.ImagePlus;

import ij.ImageStack;

import ij.WindowManager;

import ij.gui.GenericDialog;

import ij.measure.ResultsTable;

import ij.plugin.PlugIn;

import ij.plugin.filter.Analyzer;

import ij.process.ImageProcessor;



public class CellMeshOverlay implements PlugIn {



	private void getProfileLine(ImageProcessor ip, double x0, double y0, double x1, double y1, double[] xpoints, double[] ypoints) {

		

		double dx = x1 - x0;

		double dy = y1 - y0;

		double length = Math.sqrt(dx * dx + dy * dy);

		

		dx /= xpoints.length;

		dy /= xpoints.length;

		double step_size = length / xpoints.length;

		

		for (int i = 0; i < xpoints.length; i++) {

			

			double x = x0 + i * dx;

			double y = y0 + i * dy;

			

			xpoints[i] = (i * step_size) - (length / 2.0);

			ypoints[i] = ip.getInterpolatedPixel(x, y);

			

		}

		

	}

	

	private String[] getImageTitles() {



		String[] titles = new String[WindowManager.getImageCount()];

		

		for (int i = 1; i <= titles.length; i++) {

			ImagePlus imp = WindowManager.getImage(i);

			titles[i - 1] = imp.getTitle();

		}

		

		return titles;

		

	}

	

	public void run(String arg0) {



		

		ResultsTable table = Analyzer.getResultsTable();

		

		if (table == null)

			IJ.error("plugin requires results table");

		

		ImagePlus imp = IJ.getImage();

		

		if (imp == null)

			IJ.error("plugin requires image");

		

		

		

		// all in pixels

		int width = 750;

		int height = 150;

		String[] titles = getImageTitles();



		GenericDialog dialog = new GenericDialog("Average all cells");

		dialog.addNumericField("width", width, 0, 5, "pixels");

		dialog.addNumericField("height", height, 0, 5, "pixels");

		dialog.addChoice("image", titles, titles[0]);

		dialog.addNumericField("from_slice", 1, 0);

		dialog.addNumericField("to_slice (inclusive)", imp.getNSlices(), 0);

		dialog.showDialog();

		

		if (dialog.wasCanceled())

			return;



		width = (int)dialog.getNextNumber();

		height = (int)dialog.getNextNumber();

		String image = dialog.getNextChoice();

		int from_slice = (int)dialog.getNextNumber();

		int to_slice = (int)dialog.getNextNumber();



		imp = WindowManager.getImage(image);



		double[] sum = new double[width * height];

		int[] cnt = new int[width * height];



		int points = height * 4;	// should always be more than the height

		double[] xpoints = new double[points];

		double[] ypoints = new double[points];



		ImageStack stack = imp.getStack();

		

		for (int row = 0; row < imp.getNSlices(); row++) {

			

			int slice = (int)table.getValue("slice", row);

			//int cell = (int)table.getValue("cell", row);

			double x0 = table.getValue("x0", row);

			double y0 = table.getValue("y0", row);

			double x1 = table.getValue("x1", row);

			double y1 = table.getValue("y1", row);

			double length = table.getValue("length", row);

			double max_length = table.getValue("max_length", row);

			double max_width = table.getValue("max_width", row);

			

			if (slice >= from_slice && slice <= to_slice) {

			

				ImageProcessor ip = stack.getProcessor(slice);

				getProfileLine(ip, x0, y0, x1, y1, xpoints, ypoints);

				

				// xpoints gives us the y coordinate

				// ypoinys gives us the x coordinate

				// both need to be scaled

				

				double x_scale = (width - 1) / max_length;	// pixels per step

				double y_scale = (height - 1) / max_width;

				

				int x_from = (int)(length * x_scale);

				int x_to = (int)((length + 1) * x_scale);

				

				for (int x = x_from; x < x_to; x++) {

				

					for (int i = 0; i < xpoints.length; i++) {

					

						int y = (int)((height / 2.0) + (xpoints[i] * y_scale));

						

						int index = x + y * width;

						

						sum[index] += ypoints[i];

						cnt[index] += 1;

						

					}

					

				}

				

			}

		}





		// create the image with all the average values



		ImagePlus imp2 = IJ.createImage("average of all cells", "32-bit", width, height, 1);

		ImageProcessor ip2 = imp2.getProcessor();



		int index = 0;



		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

			

				ip2.setf(x, y, (float)(sum[index] / cnt[index]));

				index++;

				

			}

		}



		imp2.show();

		

		IJ.run("Enhance Contrast", "saturated=0.35");

	}



}