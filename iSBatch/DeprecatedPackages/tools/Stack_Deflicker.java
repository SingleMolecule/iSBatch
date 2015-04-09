package tools;

import ij.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.measure.*;
import ij.gui.*;
import java.awt.*;

/* ImageJ plugin to remove flickering from movies by J. S. Pedersen. 
 * version 1 2008-12-30 Initial version based on Stack_Normalizer plugin by Joachim Walter
 *
 * version 2 2010-09-09 Uses ImageJ functions to measure mean and multiply frames in stead of getPixelValue and putPixel
 *                      These changes make the plugin about 4 times faster than version 1.
*/
/** The Stack_Deflicker calculates the average grey value for each frame and normalizes all frames so that they have same average grey level as a specified frame of the stack
 * This plugin is very useful to remove flickering in movies caused by frame rates different from the frequency of 50/60Hz AC power used for the light-source that illuminate the scene.
 * An input value of -1 corresponds to the brightest frame while an input value of zero corresponds to the faintest frame.
 * If a ROI is selected the average frame intensity will be calculated from this region, but the whole scene will be normalized.
 */

public class Stack_Deflicker implements PlugInFilter {

	ImagePlus imp;
	static int Fram=-1;
    private  boolean           verbose = IJ.debugMode;

	public int setup(String arg, ImagePlus imp) {
		IJ.register(Stack_Deflicker.class);
		if (IJ.versionLessThan("1.32c"))
			return DONE;
		this.imp = imp;
		return DOES_8G+DOES_16+DOES_32+DOES_RGB;
	}

	public void run(ImageProcessor ip) {

		GenericDialog dia = new GenericDialog("Normalize to... ", IJ.getInstance());

		dia.addNumericField("Frame number (0=min,-1=max)", Fram, 0);

		dia.showDialog();

		if (dia.wasCanceled()) return;

		if(dia.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input Number");
			return;
		}

		Fram = (int)dia.getNextNumber();
		if (verbose) IJ.log("Fram="+Fram);
		normalizeStack();
		imp.getProcessor().resetMinAndMax();
		imp.updateAndRepaintWindow();
	}


	void normalizeStack() {

		ImageStack stack = imp.getStack();
		int size = stack.getSize();
		double v;
		int width, height;
		int rx, ry, rw, rh;

		ImageProcessor ip = imp.getProcessor();
		Rectangle roi = ip.getRoi();

		// Find min and max

		double roiAvg[] = new double[size+1];
		double fMin = Double.MAX_VALUE;
		double fMax = -Double.MAX_VALUE;
		int maxF = 1;
		int minF = 1; 
		
		for (int slice=1; slice<=size; slice++) {
			IJ.showStatus("Calculating: "+slice+"/"+size);
			IJ.showProgress((double)slice/size);
			ip = stack.getProcessor(slice);
		int t =0;
		roiAvg[slice]=0;
		
		ip.setRoi(roi);
		ImageStatistics is = ImageStatistics.getStatistics(ip, Measurements.MEAN, imp.getCalibration());
		
		roiAvg[slice]=is.mean; 
		
		if (roiAvg[slice]>fMax) {
		maxF= slice ;
		fMax=roiAvg[slice];
		}
		if (roiAvg[slice]<fMin) {
		minF = slice;
		fMin=roiAvg[slice];
		}
			if (verbose) IJ.log("frame="+slice+",avg="+roiAvg[slice]);
		}
		if (Fram<0) Fram=maxF;
		else if (Fram<1) Fram=minF;
		
		if (verbose) {
		IJ.log("maxF="+maxF+",fMax="+fMax);
		IJ.log("minF="+minF+",fMin="+fMin);
		IJ.log("Normalizing to frame:"+Fram);	
		}
		
		for (int slice=1; slice<=size; slice++) {
			IJ.showStatus("Normalizing: "+slice+"/"+size);
			IJ.showProgress((double)slice/size);
			ip = stack.getProcessor(slice);
			ip.multiply(roiAvg[Fram]/roiAvg[slice]);  
		}

	}

}