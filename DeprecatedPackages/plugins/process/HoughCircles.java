/*
 * 
 */
package process;

import java.util.ArrayList;
import java.util.Arrays;

import util.SMBGenericDialog;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class HoughCircles.
 */
public class HoughCircles implements PlugInFilter {
	
	/** The flags. */
	private int flags = DOES_8G | FINAL_PROCESSING;
	
	/** The min radius. */
	private double minRadius = Prefs.get("HoughCircles.minRadius", 5);
	
	/** The max radius. */
	private double maxRadius = Prefs.get("HoughCircles.maxRadius", 20);
	
	/** The step size. */
	private double stepSize = Prefs.get("HoughCircles.stepSize", 20);
	
	/** The x circle offsets. */
	private ArrayList<int[]> xCircleOffsets = new ArrayList<int[]>();
	
	/** The y circle offsets. */
	private ArrayList<int[]> yCircleOffsets = new ArrayList<int[]>();
	
	/** The hough image. */
	private ImagePlus houghImage;
	
	/**
	 * Run.
	 *
	 * @param ip the ip
	 */
	@Override
	public void run(ImageProcessor ip) {
		
		ImageStack stack = houghImage.getImageStack();
		
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		for (int i = 0; i < xCircleOffsets.size(); i++) {
			
			ImageProcessor houghIp = stack.getProcessor(i + 1);	
			int[] xs = xCircleOffsets.get(i);
			int[] ys = yCircleOffsets.get(i);
			
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					
					if (ip.get(x, y) > 0) {
						
						for (int j = 0; j < xs.length; j++) {
							int x1 = x + xs[j];
							int y1 = y + ys[j];
							
							int pixel = houghIp.getPixel(x1, y1);
							houghIp.putPixel(x1, y1, pixel + 1);
						}
						
					}
					
				}
			}
			
			IJ.showProgress(i, xCircleOffsets.size());
		}

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
		
		if (arg.equals("final")) {
			houghImage.show();
			return DONE;
		}
		
		SMBGenericDialog dialog = new SMBGenericDialog("Hough Transform");
		dialog.addNumericField("Minimum radius", minRadius, 2);
		dialog.addNumericField("Maximum radius", maxRadius, 2);
		dialog.addNumericField("Step size", stepSize, 2);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return DONE;
		
		minRadius = dialog.getNextNumber();
		maxRadius = dialog.getNextNumber();
		stepSize = dialog.getNextNumber();
		
		// make all the offsets
		IJ.showStatus("calculate circle offsets");
		
		int[] xs = new int[1000];
		int[] ys = new int[1000];
		
		for (double radius = minRadius; radius <= maxRadius; radius += stepSize) {
			
			int i = 0;
			
			for (int y = (int)-radius; y <= (int)Math.ceil(radius); y++) {
				for (int x = (int)-radius; x <= (int)Math.ceil(radius); x++) {
					
					if (Math.round(Math.sqrt(x * x + y * y)) == Math.round(radius)) {
						xs[i] = x;
						ys[i] = y;
						i++;
					}
					
				}
			}
			
			xCircleOffsets.add(Arrays.copyOf(xs, i));
			yCircleOffsets.add(Arrays.copyOf(ys, i));
		}
		
		IJ.showStatus("hough transform");
		
		houghImage =IJ.createImage("hough", "16-bit", imp.getWidth(), imp.getHeight(), (int)((maxRadius - minRadius) / stepSize) + 1);
		
		return flags;
	}

}
