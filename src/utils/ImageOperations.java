package utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import operations.LevenbergMarquardt;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.gui.PointRoi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

public class ImageOperations {
	public static final double SIGMA_TO_FWHM = 2.0 * Math.sqrt(2.0 * Math.log(2));
	private boolean useDiscoidalFilter = true;
	private int innerRadius = 7;
	private int outerRadius = 9;
	private double threshold = 1;
	private boolean useStandardDeviation = true;
	private int minimumDistance = 10;
	private int fitRadius = 7;
	private double fwhm = 6;
	private double magnification = 4;
	private double fittingThreshold = 8;
	private double[] maxError = new double[] {
			5000,
			5000,
			5,
			5,
			5,
			5
	};
	
	/** The lm. */
	private LevenbergMarquardt lm = new LevenbergMarquardt() {
		
		@Override
		public double getValue(double[] x, double[] parameters) {
			
			double dx = x[0] - parameters[2];
			double dy = x[1] - parameters[3];
			
			return parameters[0] + parameters[1] * Math.exp(-((dx * dx) / (2 * parameters[4] * parameters[4]) + (dy * dy) / (2 * parameters[5] * parameters[5])));
		}
		
		@Override
		public void getGradient(double[] x, double[] parameters, double[] dyda) {
			
			double dx = x[0] - parameters[2];
			double dy = x[1] - parameters[3];
			
			dyda[0] = 1;
			dyda[1] = Math.exp(-((dx * dx) / (2 * parameters[4] * parameters[4]) + (dy * dy) / (2 * parameters[5] * parameters[5])));
			dyda[2] = (parameters[1] * dyda[1] * dx) / (parameters[4] * parameters[4]);
			dyda[3] = (parameters[1] * dyda[1] * dy) / (parameters[5] * parameters[5]);
			dyda[4] = (parameters[1] * dyda[1] * dx * dx) / (parameters[4] * parameters[4] * parameters[4]);
			dyda[5] = (parameters[1] * dyda[1] * dy * dy) / (parameters[5] * parameters[5] * parameters[5]);
			
		}
	};
	
	
	
	protected ImageOperations() {
		
	}
	
	private static ImageOperations instance;
	
	public static ImageOperations getInstance() {
		
		if (instance == null)
			instance = new ImageOperations();
		
		return instance;
	}
	
	/**
	 * The discoidal filter is used to amplify peaks in an image. It works by taking two circles: an inner circle and an outer circle. For each pixel
	 * the filter calculates the mean pixel value of all pixels that lie inside the inner circle minus the mean pixel value of all pixels that lie on the outer
	 * circle. When a pixel belong to a peak the difference between the mean pixel value of all pixels on the outer circle and the the mean pixel value of all pixels
	 * inside the inner circle will be huge (i.e. peaks will be amplified).
	 * 
	 * @param ip image processor that will be filtered. The given image processor will be changed after calling this method!
	 * @return the filtered image (same as the given parameter)
	 */
	public ImageProcessor discoidalFilter(ImageProcessor ip) {
		
		// create masks
		int size = (int)Math.pow(outerRadius * 2 + 1, 2);
		int[] xInner = new int[size];
		int[] yInner = new int[size];
		int[] xOuter = new int[size];
		int[] yOuter = new int[size];
		int innerPoints = 0;
		int outerPoints = 0;
		
		for (int y = -outerRadius; y <= outerRadius; y++) {
			for (int x = -outerRadius; x <= outerRadius; x++) {
				
				double d = Math.round(Math.sqrt(x * x + y * y));

				if (d <= innerRadius) {
					xInner[innerPoints] = x;
					yInner[innerPoints] = y;
					innerPoints++;
				}
				
				if (d == outerRadius) {
					xOuter[outerPoints] = x;
					yOuter[outerPoints] = y;
					outerPoints++;
				}
				
			}
		}
		
		// filter image
		Rectangle roi = ip.getRoi();

		double[] pixels = new double[ip.getWidth() * ip.getHeight()];
		int index = 0;
		
		for (int y = roi.y; y < roi.y + roi.height; y++) {
			for (int x = roi.x; x < roi.x + roi.width; x++) {
				
				double innerSum = 0;
				int innerCount = 0;
				
				for (int j = 0; j < innerPoints; j++) {
					if (roi.contains(x + xInner[j], y + yInner[j])) {
						innerSum += ip.getPixelValue(x + xInner[j], y + yInner[j]);
						innerCount++;
					}
				}
				
				double outerSum = 0;
				int outerCount = 0;
				
				for (int j = 0; j < outerPoints; j++) {
					if (roi.contains(x + xOuter[j], y + yOuter[j])) {
						outerSum += ip.getPixelValue(x + xOuter[j], y + yOuter[j]);
						outerCount++;
					}
				}
				
				double value = (innerSum / innerCount) - (outerSum / outerCount);
				pixels[index++] = value > 0 ? value : 0;
				
			}
		}
		
		// set pixels
		index = 0;
		for (int y = roi.y; y < roi.y + roi.height; y++) {
			for (int x = roi.x; x < roi.x + roi.width; x++) {
				ip.setf(x, y, (float)pixels[index++]);
			}
		}

		return ip;
	}
	
	/**
	 * Discoidal filter all images in the given stack.
	 * 
	 * @param imp The image that needs to be filtered. The given image will be changed after calling this method!
	 * @return the same image as the given parameter
	 */
	public ImagePlus discoidalFilter(ImagePlus imp) {
		
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice < stack.getSize(); slice++)
			discoidalFilter(stack.getProcessor(slice));
		
		return imp;
		
	}
	
	/**
	 * Find all the peaks in an image. This method first filters for peak (if useDiscoidalFilter is set). As a second step this method
	 * searches for the pixel with the maximum intensity. The position of this 'maximum pixel' is stored and then the second largest
	 * pixel value is searched for. This process continues until the found maximum pixel value is below the given threshold value.
	 *
	 * @param ip the ip
	 * @return the array list
	 */
	public ArrayList<Point> findPeaks(ImageProcessor ip) {
		
		ArrayList<Point> peaks = new ArrayList<Point>();
		
		if (useDiscoidalFilter) {
			ip = ip.duplicate();
			discoidalFilter(ip);
		}
		
		double thresholdValue = threshold;
		
		if (useStandardDeviation) {
			ImageStatistics statistics = ImageStatistics.getStatistics(ip, ImageStatistics.MEAN | ImageStatistics.STD_DEV, null);
			thresholdValue = statistics.mean + threshold * statistics.stdDev;
		}
		
		Rectangle roi = ip.getRoi();
		int maskWidth = minimumDistance * 2 + 1;
		int xPeak = 0;
		int yPeak = 0;
		double max;

		do {
			
			max = 0;
			
			for (int y = roi.y; y < roi.y + roi.height; y++) {
				for (int x = roi.x; x < roi.x + roi.width; x++) {
					
					double value = ip.getf(x, y);
					
					if (value > max) {
						max = value;
						xPeak = x;
						yPeak = y;
					}
				
				}
			}
			
			peaks.add(new Point(xPeak, yPeak));
			ip.setValue(0);
			ip.fillOval(xPeak - minimumDistance, yPeak - minimumDistance, maskWidth, maskWidth);
			
		} while (max > thresholdValue);
		
		return peaks;
	}
	
	public ArrayList<PointRoi> findPeaks(ImagePlus imp) {
		
		ArrayList<PointRoi> rois = new ArrayList<PointRoi>();
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice < stack.getSize(); slice++) {
			ImageProcessor ip = stack.getProcessor(slice);
			ip.setSliceNumber(slice);
			
			ArrayList<Point> peaks = findPeaks(ip);
			
			for (Point peak: peaks) {
				PointRoi roi = new PointRoi(peak.x, peak.y);
				roi.setPosition(slice);
				rois.add(roi);
			}
			
		}
		
		return rois;
	}
	
	
	public ResultsTable fitPeaks(ResultsTable table, ImageProcessor ip) {
		
		int size = (int)Math.pow(fitRadius * 2 + 1, 2);
		double[][] xValues = new double[size][2];
		double[] yValues = new double[size];
		double[] parameters = new double[6];
		double[] error = new double[6];
		double sigma = fwhm / SIGMA_TO_FWHM;
		
		for (Point peak: findPeaks(ip.duplicate())) {
			
			double min = ip.getPixelValue(peak.x, peak.y);
			double max = min;
			
			// get all pixels
			int index = 0;
			
			for (int y = peak.y - fitRadius; y <= peak.y + fitRadius; y++) {
				for (int x = peak.x - fitRadius; x <= peak.x + fitRadius; x++) {
					
					if (x >= 0 && y >= 0 && x < ip.getWidth() && y < ip.getHeight()) {
						
						double value = ip.getPixelValue(x, y); 
						
						if (value >= fittingThreshold) {
							xValues[index][0] = x;
							xValues[index][1] = y;
							yValues[index] = value;
							index++;
							
							if (value < min) min = value;
							if (value > max) max = value;
						}
						
					}
					
				}
			}
			
			parameters[0] = min;
			parameters[1] = max - min;
			parameters[2] = peak.x;
			parameters[3] = peak.y;
			parameters[4] = sigma;
			parameters[5] = sigma;
			
			lm.solve(xValues, yValues, null, index, parameters, null, error, 0.001);
			
			boolean isValid = true;
			
			for (int i = 0; i < error.length && isValid; i++) {
				
				if (Double.isNaN(parameters[i]) || Double.isNaN(error[i]) || error[i] > maxError[i])
					isValid = false;
				
			}
			
			if (isValid) {
				
				parameters[4] = Math.abs(parameters[4]);
				parameters[5] = Math.abs(parameters[5]);
				
				double fwhmx = parameters[4] * SIGMA_TO_FWHM;
				double fwhmy = parameters[5] * SIGMA_TO_FWHM;
				
				table.incrementCounter();
				table.addValue("baseline", 			parameters[0]);
				table.addValue("height", 			parameters[1]);
				table.addValue("x", 				parameters[2]);
				table.addValue("y", 				parameters[3]);
				table.addValue("sigma_x", 			parameters[4]);
				table.addValue("sigma_y", 			parameters[5]);
				table.addValue("error_baseline",	error[0]);
				table.addValue("error_height", 		error[1]);
				table.addValue("error_x", 			error[2]);
				table.addValue("error_y", 			error[3]);
				table.addValue("error_sigma_x", 	error[4]);
				table.addValue("error_sigma_y", 	error[5]);
				table.addValue("fwhm_x",   			fwhmx);
				table.addValue("fwhm_y",   			fwhmy);
				table.addValue("fwhm",   			(fwhmx + fwhmy) / 2);
				table.addValue("slice",				ip.getSliceNumber());
				
			}
			else {
				
				table.incrementCounter();
				table.addValue("height", 			ip.getPixel(peak.x, peak.y));
				table.addValue("x", 				peak.x);
				table.addValue("y", 				peak.y);
				table.addValue("slice",				ip.getSliceNumber());
				
			}
			
		}
		
		return table;
	}

	public ResultsTable fitPeaks(ImageProcessor ip) {
		return fitPeaks(new ResultsTable(), ip);
	}
	
	public ResultsTable fitPeaks(ResultsTable table, ImagePlus imp) {
		
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice < stack.getSize(); slice++) {
			ImageProcessor ip = stack.getProcessor(slice);
			ip.setSliceNumber(slice);
			fitPeaks(table, ip);
		}
		
		return table;
		
	}
	
	public ResultsTable fitPeaks(ImagePlus imp) {
		return fitPeaks(new ResultsTable(), imp);
	}
	
	public static double normalDistribution(double x, double y, double sigmaX, double sigmaY) {
		return Math.exp(-((x * x) / (2 * sigmaX * sigmaX) + (y * y) / (2 * sigmaY * sigmaY))) / (2 * Math.PI * sigmaX * sigmaY);
	}
	
	public ImagePlus reconstruction(ResultsTable table) {
	
		int x0 = (int)Math.round(table.getValue("x", 0));
		int y0 = (int)Math.round(table.getValue("y", 0));
		int x1 = x0;
		int y1 = y0;
		
		for (int row = 1; row < table.getCounter(); row++) {
			
			int x = (int)Math.round(table.getValue("x", row));
			int y = (int)Math.round(table.getValue("y", row));
			
			if (x < x0) x0 = x;
			if (x > x1) x1 = x;
			if (y < y0) y0 = y;
			if (y > y1) y1 = y;
			
		}
		
		return reconstruction(table, x0, y0, x1, y1);
	}
	
	
	public ImagePlus reconstruction(ResultsTable table, int x0, int y0, int x1, int y1) {
		
		// create image stack
		ImagePlus imp = IJ.createImage("reconstruction", "32-bit", (int)((x1 - x0) * magnification), (int)((y1 - y0) * magnification), 1);
		ImageProcessor ip = imp.getProcessor();
		
		for (int row = 0; row < table.getCounter(); row++) {
			
			double cx = (table.getValue("x", row) - x0) * magnification;
			double cy = (table.getValue("y", row) - y0) * magnification;
			double errorX = table.getValue("error_x", row) * magnification;
			double errorY = table.getValue("error_y", row) * magnification;
			
			for (int y = -10; y <= 10; y++) {
				for (int x = -10; x <= 10; x++) {
					
					double value = ip.getPixelValue((int)cx + x, (int)cy + y);
					value += normalDistribution(x + (cx % 1), y + (cy %1 ), errorX, errorY);
					ip.putPixelValue((int)cx + x, (int)cy + y, value);
				}
			}
		}
		
		Calibration c = imp.getCalibration();
		c.xOrigin = -x0 * magnification;
		c.yOrigin = -y0 * magnification;
		c.pixelWidth = 1 / magnification;
		c.pixelHeight = 1 / magnification;
		
		return imp;
	}
	public boolean showDialog() {
		
		GenericDialog dialog = new GenericDialog("Parameters");
		dialog.addMessage("Settings for filtering peaks :");
		dialog.addCheckbox("use_discoidalFilter", useDiscoidalFilter);
		dialog.addNumericField("inner_radius", innerRadius, 0);
		dialog.addNumericField("outer_radius", outerRadius, 0);
		dialog.addMessage("Settings for detecting peaks :");
		dialog.addCheckbox("use_standard_deviation for threshold", useStandardDeviation);
		dialog.addNumericField("threshold", threshold, 2);
		dialog.addNumericField("minimum_distance between peak", minimumDistance, 0);
		dialog.addMessage("Settings for fitting peaks :");
		dialog.addNumericField("fit_radius", fitRadius, 0);
		dialog.addNumericField("fwhm", fwhm, 2);
		dialog.addMessage("Settings for reconstructing images :");
		dialog.addNumericField("magnification", magnification, 2);
		dialog.addNumericField("fitting_threshold", fittingThreshold, 2);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return false;
		
		useDiscoidalFilter = dialog.getNextBoolean();
		innerRadius = (int)dialog.getNextNumber();
		outerRadius = (int)dialog.getNextNumber();
		useStandardDeviation = dialog.getNextBoolean();
		threshold = dialog.getNextNumber();
		minimumDistance = (int)dialog.getNextNumber();
		fitRadius = (int)dialog.getNextNumber();
		fwhm = dialog.getNextNumber();
		magnification = dialog.getNextNumber();
		fittingThreshold = dialog.getNextNumber();
		
		return true;
	}

	public boolean isUseDiscoidalFilter() {
		return useDiscoidalFilter;
	}

	public void setUseDiscoidalFilter(boolean useDiscoidalFilter) {
		this.useDiscoidalFilter = useDiscoidalFilter;
	}

	public int getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(int innerRadius) {
		this.innerRadius = innerRadius;
	}

	public int getOuterRadius() {
		return outerRadius;
	}

	public void setOuterRadius(int outerRadius) {
		this.outerRadius = outerRadius;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public boolean isUseStandardDeviation() {
		return useStandardDeviation;
	}

	public void setUseStandardDeviation(boolean useStandardDeviation) {
		this.useStandardDeviation = useStandardDeviation;
	}

	public int getMinimumDistance() {
		return minimumDistance;
	}

	public void setMinimumDistance(int minimumDistance) {
		this.minimumDistance = minimumDistance;
	}

	public int getFitRadius() {
		return fitRadius;
	}

	public void setFitRadius(int fitRadius) {
		this.fitRadius = fitRadius;
	}

	public double getFwhm() {
		return fwhm;
	}

	public void setFwhm(double fwhm) {
		this.fwhm = fwhm;
	}
	
	public double getMagnification() {
		return magnification;
	}

	public void setMagnification(double magnification) {
		this.magnification = magnification;
	}

	public double[] getMaxError() {
		return maxError;
	}

	public void setMaxError(double[] maxError) {
		this.maxError = maxError;
	}
	
	
	
}