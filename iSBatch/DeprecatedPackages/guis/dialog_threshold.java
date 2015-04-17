package guis;

import java.io.IOException;


import ij.gui.GenericDialog;

public class dialog_threshold {

	public static void main(String[] args) throws IOException{

		askInputs();

		
		System.out.println("---------Done!-------------");
		
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
		
	
	private static double[] askThreshold() {
		GenericDialog gd = new GenericDialog("threshold values");
		double offset[] = new double[2];
		gd.addNumericField("Threshold - SNR: ", offset[0], 0);
		gd.addNumericField("Threshold - Intensity: ", offset[1], 0);
		gd.showDialog();
        	if (gd.wasCanceled()){
        		offset[0] = 0;
        		offset[1] = 0;
        	}
        offset[0] = gd.getNextNumber();
        offset[1] = gd.getNextNumber();
		return offset;
	}

	private static double[] askInputs() {
		GenericDialog gd = new GenericDialog("Peak Detections ");
		double offset[] = new double[2];
		gd.addNumericField("Threshold - SNR: ", offset[0], 0);
		gd.addNumericField("Threshold - Intensity: ", offset[1], 0);
		gd.showDialog();
        	if (gd.wasCanceled()){
        		offset[0] = 0;
        		offset[1] = 0;
        	}
        offset[0] = gd.getNextNumber();
        offset[1] = gd.getNextNumber();
		return offset;
	}

	
	
}
