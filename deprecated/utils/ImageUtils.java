package utils;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Measurements;
import ij.plugin.ZProjector;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.awt.Rectangle;

/**
 * @author Victor Caldas
 * 
 * This class handles simple operations in images. 
 * Every operation that involves changes in pixel values or generation of images should be places here.
 * Use @StringUtils for string operations.
 *
 */
public abstract class ImageUtils {
	private static StringUtils strUtils = new StringUtils(false);
	public void deflicker(ImagePlus imp) {

		int Fram = -1;
		/*
		 * Adapted from: Stack_Deflicker.java ImageJ plugin to remove flickering
		 * from movies by J. S. Pedersen. version 1 2008-12-30 Initial version
		 * based on Stack_Normalizer plugin by Joachim Walter
		 * 
		 * version 2 2010-09-09 Uses ImageJ functions to measure mean and
		 * multiply frames in stead of getPixelValue and putPixel These changes
		 * make the plugin about 4 times faster than version 1.
		 */
		/**
		 * The Stack_Deflicker calculates the average grey value for each frame
		 * and normalizes all frames so that they have same average grey level
		 * as a specified frame of the stack This plugin is very useful to
		 * remove flickering in movies caused by frame rates different from the
		 * frequency of 50/60Hz AC power used for the light-source that
		 * illuminate the scene. An input value of -1 corresponds to the
		 * brightest frame while an input value of zero corresponds to the
		 * faintest frame. If a ROI is selected the average frame intensity will
		 * be calculated from this region, but the whole scene will be
		 * normalized.
		 */

		ImageStack stack = imp.getStack();
		int size = stack.getSize();
		ImageProcessor ip = imp.getProcessor();
		Rectangle roi = ip.getRoi();

		// Find min and max

		double roiAvg[] = new double[size + 1];
		double fMin = Double.MAX_VALUE;
		double fMax = -Double.MAX_VALUE;
		int maxF = 1;
		int minF = 1;

		for (int slice = 1; slice <= size; slice++) {
			IJ.showStatus("Calculating: " + slice + "/" + size);
			IJ.showProgress((double) slice / size);
			ip = stack.getProcessor(slice);
			roiAvg[slice] = 0;

			ip.setRoi(roi);
			ImageStatistics is = ImageStatistics.getStatistics(ip,
					Measurements.MEAN, imp.getCalibration());

			roiAvg[slice] = is.mean;

			if (roiAvg[slice] > fMax) {
				maxF = slice;
				fMax = roiAvg[slice];
			}
			if (roiAvg[slice] < fMin) {
				minF = slice;
				fMin = roiAvg[slice];
			}

		}
		if (Fram < 0)
			Fram = maxF;
		else if (Fram < 1)
			Fram = minF;

		for (int slice = 1; slice <= size; slice++) {
			IJ.showStatus("Normalizing: " + slice + "/" + size);
			IJ.showProgress((double) slice / size);
			ip = stack.getProcessor(slice);
			ip.multiply(roiAvg[Fram] / roiAvg[slice]);
		}
	}

	public ImagePlus getAverageProjection(ImagePlus imp) {
		ZProjector projector = new ZProjector(imp);
		projector.setMethod(ZProjector.AVG_METHOD);
		projector.doProjection();
		return projector.getProjection();
	}

	public void averageProjection(ImagePlus imp) {
		ZProjector projector = new ZProjector(imp);
		projector.setMethod(ZProjector.AVG_METHOD);
		projector.doProjection();
		imp = projector.getProjection();
	}

	public ImageProcessor getAverageAsImageProcessor(ImagePlus imp) {
		ImageProcessor ip = imp.getProcessor();
		if (imp.getStack().getSize() != 1) {

			ZProjector projector = new ZProjector(imp);
			projector.setMethod(ZProjector.AVG_METHOD);
			projector.doProjection();

			ip = projector.getProjection().getProcessor();

		}
		return ip;
	}

	public static String getStackPosition(int i, ImageStack stack) {
		return getStackPosition(i, stack.getSize());
	}

	public static void appendStackPositiontoTitle(ImageStack stack) {
		int stackSize = stack.getSize();

		for (int i = 1; i <= stackSize; i++) {
			String currentStacklabel = stack.getShortSliceLabel(i);
			if (currentStacklabel == null) {
				stack.setSliceLabel(getStackPosition(i, stackSize), i);
			} 
			else if (strUtils.checkStackAssignment(currentStacklabel) == false) {
				String newLabel = currentStacklabel.concat(getStackPosition(i,
						stackSize));
				stack.setSliceLabel(newLabel, i);
			}

		}

	}

	public static String getStackPosition(int i, int size) {
		return "|S("+Integer.toString(i) + "|" + size+")";
	}

	public static void appendTitle(ImageStack stack, String string) {
		int stackSize = stack.getSize();

		for (int i = 1; i <= stackSize; i++) {
			String currentStacklabel = stack.getShortSliceLabel(i);
			if (currentStacklabel == null) {
				stack.setSliceLabel(string, i);
			} 
			else {
				String newLabel = currentStacklabel.concat(getStackPosition(i,
						stackSize));
				System.out.println("New label variable = "+ newLabel);
				stack.setSliceLabel(newLabel, i);
			}

		}
		
	}

	public static void makeFullTitle(ImageStack stack, String name) {
		int stackSize = stack.getSize();

		for (int i = 1; i <= stackSize; i++) {
			String currentStacklabel = name;
			currentStacklabel = currentStacklabel.concat(getStackPosition(i,stackSize));
			stack.setSliceLabel(currentStacklabel, i);
			System.out.println( "Stack name" + stack.getShortSliceLabel(i));
			}

		}
		
		
		
		
	}

