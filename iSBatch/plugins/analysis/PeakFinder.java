/*
 * 
 */
package analysis;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import process.DiscoidalAveragingFilter;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class PeakFinder.
 */
public class PeakFinder implements ExtendedPlugInFilter, DialogListener {
	
	/** The flags. */
	private int flags = DOES_8G | DOES_16 | DOES_32 | NO_CHANGES | PARALLELIZE_STACKS;
	
	/** The use discoidal averaging. */
	private boolean useDiscoidalAveraging = Prefs.getBoolean("PeakFinder.useDiscoidalAveraging", true);
	
	/** The inner radius. */
	private int innerRadius = Prefs.getInt("PeakFinder.innerRadius", 1);
	
	/** The outer radius. */
	private int outerRadius = Prefs.getInt("PeakFinder.outerRadius", 3);
	
	/** The threshold. */
	private double threshold = Prefs.getDouble("PeakFinder.threshold", 6);
	
	/** The threshold value. */
	private double thresholdValue = Prefs.getDouble("PeakFinder.thresholdValue", 0);
	
	/** The selection radius. */
	private int selectionRadius = Prefs.getInt("PeakFinder.selectionRadius", 4);
	
	/** The minimum distance. */
	private int minimumDistance = Prefs.getInt("PeakFinder.minimumDistance", 8);
	
	/** The imp. */
	private ImagePlus imp;
	
	/** The roi manager. */
	private RoiManager roiManager;
	
	/** The filter. */
	private DiscoidalAveragingFilter filter = new DiscoidalAveragingFilter();
	
	/** The is preview. */
	private boolean isPreview = false;
	
	
	/**
	 * Gets the ROI manager.
	 *
	 * @return the ROI manager
	 */
	public RoiManager getROIManager(){
		return roiManager;
	}
	
	/**
	 * Instantiates a new peak finder.
	 */
	public PeakFinder() {
		
	}
	
	/**
	 * Instantiates a new peak finder.
	 *
	 * @param useDiscoidalAveraging the use discoidal averaging
	 * @param filter the filter
	 * @param threshold the threshold
	 * @param thresholdValue the threshold value
	 * @param minimumDistance the minimum distance
	 */
	public PeakFinder(boolean useDiscoidalAveraging, DiscoidalAveragingFilter filter,
			double threshold, double thresholdValue, int minimumDistance) {
		
		this.useDiscoidalAveraging = useDiscoidalAveraging;
		this.threshold = threshold;
		this.thresholdValue = thresholdValue;
		this.filter = filter;
		this.minimumDistance = minimumDistance;
		
	}
	
	/**
	 * Instantiates a new peak finder.
	 *
	 * @param b the b
	 * @param filter the filter
	 * @param SNR_THRESHOLD the snr threshold
	 * @param INTENSITY_THRESHOLD the intensity threshold
	 * @param DISTANCE_BETWEEN_PEAKS the distance between peaks
	 */
	public PeakFinder(boolean b, DiscoidalAveragingFilter filter,
			String SNR_THRESHOLD, String INTENSITY_THRESHOLD,
			String DISTANCE_BETWEEN_PEAKS) {
		this.useDiscoidalAveraging = b;
		this.threshold = Double.parseDouble(SNR_THRESHOLD);
		this.thresholdValue = Double.parseDouble(INTENSITY_THRESHOLD);
		this.filter = filter;
		this.minimumDistance = Integer.parseInt(DISTANCE_BETWEEN_PEAKS);
	}
	
	/**
	 * Run.
	 *
	 * @param imp the imp
	 */
	public void run(ImagePlus imp){
		
		
	}
	
	/**
	 * Run.
	 *
	 * @param ip the ip
	 */
	@Override
	public void run(ImageProcessor ip) {
		
		ArrayList<Point> peaks = findPeaks(ip);
		
		if (!peaks.isEmpty()) {
			
			if (isPreview) {
				
				Polygon poly = new Polygon();
				
				for (Point p: peaks)
					poly.addPoint(p.x, p.y);
				
				PointRoi peakRoi = new PointRoi(poly);
				imp.setRoi(peakRoi);
				
			}
			else {
				
				int selectionWidth = selectionRadius * 2 + 1;
				
				for (Point p: peaks) {
					Roi peakRoi = new Roi(p.x - selectionRadius, p.y - selectionRadius, selectionWidth, selectionWidth);
					peakRoi.setPosition(ip.getSliceNumber());
					roiManager.addRoi(peakRoi);
				}
				
			}
			
		}
		
	}
	
	/**
	 * Find peaks.
	 *
	 * @param ip the ip
	 * @return the array list
	 */
	public ArrayList<Point> findPeaks(ImageProcessor ip) {
		
		ArrayList<Point> peaks = new ArrayList<Point>();
		ImageProcessor duplicate = ip.duplicate();
		
		if (useDiscoidalAveraging) {
			filter.run(duplicate);
		}
		
		Rectangle roi = ip.getRoi();
		double t = thresholdValue; 
		
		if (t == 0) {
			
			// determine mean and standard deviation
			double mean = 0;
			double stdDev = 0;
			
			for (int y = roi.y; y < roi.y + roi.height; y++) {
				for (int x = roi.x; x < roi.x + roi.width; x++) {
					
					mean += duplicate.getf(x, y);
				}
			}
			
			mean /= roi.width * roi.height;
			
			for (int y = roi.y; y < roi.y + roi.height; y++) {
				for (int x = roi.x; x < roi.x + roi.width; x++) {
					
					double d = duplicate.getf(x, y) - mean;
					
					stdDev += d * d;
				}
			}
			
			stdDev /= roi.width * roi.height;
			stdDev = Math.sqrt(stdDev);
			
			t = mean + threshold * stdDev;
			
		}
		
		// determine which pixels are above the threshold
		int[] offsets = new int[roi.width * roi.height];
		int numberOfPixels = 0;
		int width = ip.getWidth();
		
		for (int y = roi.y; y < roi.y + roi.height; y++) {
			for (int x = roi.x; x < roi.x + roi.width; x++) {
				
				double pixel = duplicate.getf(x, y);
				
				if (pixel >= t)
					offsets[numberOfPixels++] = x + y * width;
				
			}
		}
		
		if (numberOfPixels > 0) {
			
			int distanceWidth = minimumDistance * 2 + 1;
			double minValue = duplicate.minValue();
			
			while (true) {
			
				// find maximum
				double maxValue = minValue;
				int maxOffset = offsets[0];
				
				for (int i = 0; i < numberOfPixels; i++) {
					
					double pixel = duplicate.getf(offsets[i]);
					
					if (pixel > maxValue) {
						maxValue = pixel;
						maxOffset = offsets[i];
					}
					
				}
				
				if (maxValue < t)
					break;
				
				// remove peak so we don't count it twice
				int x = maxOffset % width;
				int y = maxOffset / width;
				
				duplicate.setValue(minValue);
				duplicate.fillOval(x - minimumDistance, y - minimumDistance, distanceWidth, distanceWidth);
				
				peaks.add(new Point(x, y));
			}
			
		}
		
		return peaks;
	}

	/**
	 * Setup.
	 *
	 * @param arg the arg
	 * @param imp the imp
	 * @return the int
	 */
	@Override
	public int setup(String arg, ImagePlus imp) {
		
		this.imp = imp;
		
		return flags;
	}

	/**
	 * Sets the n passes.
	 *
	 * @param n the new n passes
	 */
	@Override
	public void setNPasses(int n) {
		
	}

	/**
	 * Show dialog.
	 *
	 * @param imp the imp
	 * @param arg the arg
	 * @param pfr the pfr
	 * @return the int
	 */
	@Override
	public int showDialog(ImagePlus imp, String arg, PlugInFilterRunner pfr) {
		
		GenericDialog dialog = new GenericDialog("Peak Finder");
		
		dialog.addCheckbox("Use_Discoidal_Averaging_Filter", useDiscoidalAveraging);
		dialog.addNumericField("Inner_radius", innerRadius, 0);
		dialog.addNumericField("Outer_radius", outerRadius, 0);
		
		dialog.addNumericField("Threshold (mean + n times standard deviation)", threshold, 2);
		dialog.addNumericField("Threshold_value (0 = ignore)", thresholdValue, 2);
		dialog.addNumericField("Selection_radius (in pixels)", selectionRadius, 0);
		dialog.addNumericField("Minimum_distance between peaks (in pixels)", minimumDistance, 0);
		
		dialog.addDialogListener(this);
		dialog.addPreviewCheckbox(pfr);
		
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return DONE;
		
		isPreview = false;

		roiManager = RoiManager.getInstance();
		
		if (roiManager == null) {
			roiManager = new RoiManager();
		}
		
//		return IJ.setupDialog(imp, flags);
		return 1;
	}

	/**
	 * Dialog item changed.
	 *
	 * @param dialog the dialog
	 * @param e the e
	 * @return true, if successful
	 */
	@Override
	public boolean dialogItemChanged(GenericDialog dialog, AWTEvent e) {
		
		useDiscoidalAveraging = dialog.getNextBoolean();
		innerRadius = (int)dialog.getNextNumber();
		outerRadius = (int)dialog.getNextNumber();
		
		threshold = dialog.getNextNumber();
		thresholdValue = (int)dialog.getNextNumber();
		
		selectionRadius = (int)dialog.getNextNumber();
		minimumDistance = (int)dialog.getNextNumber();
		
		filter.setCircleOffsets(imp.getWidth(), innerRadius, outerRadius);
		
		return (!useDiscoidalAveraging || (innerRadius >= 0 && innerRadius < outerRadius)) && selectionRadius >= 0 && minimumDistance > 0;
	}
	
}
