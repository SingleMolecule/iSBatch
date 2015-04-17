package pluginsIJ;

import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import tools.iSBOps;
import External.DiscoidalAveragingFilter;
import External.PeakFinder;
import External.PeakFitter;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;


public class Detect_PeaksIJ implements PlugIn{
		public static final double SIGMA_TO_FWHM = 2.0 * Math.sqrt(2.0 * Math.log(2));
		//static double PeakFinderThreshold = 30;
		//static String thresholdValue = "30";
		static File file;
		static String[][] CSVContent = null;
		private static double[] maxError = new double[] {
				Prefs.getDouble("PeakFitter.maxErrorBaseline", 5000),
				Prefs.getDouble("PeakFitter.maxErrorHeight",5000),
				Prefs.getDouble("PeakFitter.maxErrorX", 1),
				Prefs.getDouble("PeakFitter.maxErrorY", 1),
				Prefs.getDouble("PeakFitter.maxErrorSigmaX", 1),
				Prefs.getDouble("PeakFitter.maxErrorSigmaY", 1),
		};
		
		public static void main(String[] args) {
			new Detect_PeaksIJ().run("");
			System.out.println("All peaks localized");
		}

//public static void main (String[] args){
		public void run(String arg0){
	//Get file
			String csvFilename =  IJ.getFilePath("Provide the ControlFile.csv");
				if (csvFilename==null) return;	
			file = new File(csvFilename);
			long startTime = System.currentTimeMillis();
	    	
			
	    	try {
				CSVContent = tools.iSBOps.getCSVContent2(csvFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
	
	
	    	List<String> listchannel = tools.iSBOps.getCollumDataUniques(CSVContent, "Channel");
	    	
	      	//Create labels for the checkbox control
	      	String[] labels = iSBOps.CheckBoxLabes(listchannel);
	     
	  	//Open dialog to ask the used
	      	
	      	decision_tree(labels);
	      	System.out.println("Acabou");
    	

	    System.out.println("---------Done!-------------");
	    long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("It took "+ totalTime +" ms.");
		IJ.showMessage("Done");
		java.awt.Toolkit.getDefaultToolkit().beep(); 
				
			
}

private static void decision_tree(String[] labels) {
	int choice = ConfirmExecution(labels);
  	System.out.println(choice);
  	  	
  	if( choice == -1){
  		System.out.println("Action cancelled");
  	}
  	
  	
  	
  	else{
  		System.out.println("Choice made: "+ choice);
  		System.out.println("Detecting peaks");
  		//Ask to continue
  		
  		//Ask threshold values
  		
  		int thresholds = askThreshold();
  			//get list of images 
  			List<String> listchannel = tools.iSBOps.getSubFileList(CSVContent, labels[choice]);
  			
  			
  			String output = labels[choice];
			getAllPeak(listchannel, file.getParent(), output, thresholds,thresholds);

			
		int choice2 = askToContinue();	
  		
  		if(choice2 == 0){
  			decision_tree(labels);
  		}
  		
  		
	}
}


  	
private static int askThreshold() {
		GenericDialog gd = new GenericDialog("threshold values");
		int offset = 0;
		gd.addNumericField("Threshold: ", offset, 0);
		gd.showDialog();
        	if (gd.wasCanceled()){
        		return offset=0 ;
        	}
        offset = (int)gd.getNextNumber();
		return offset;
	}

private static int askToContinue() {
	Object[] options2 = {"Yes","No"};

    Component frame2 = null;
    int DKSelection= JOptionPane.showOptionDialog(frame2,
    		"Do you wish to continue with another channel?: ",
    		"Dark Count Correction",
    		JOptionPane.YES_NO_OPTION,
    		JOptionPane.QUESTION_MESSAGE,
    		null,     //do not use a custom Icon
    		options2,  //the titles of buttons
    		options2[0]); //default button title
return DKSelection;// TODO Auto-generated method stub
	

}

public static void getAllPeak(List<String> listchannel,String INPUT_DIR, String channel, int thresholdValue, int PeakFinderThreshold){
		if (listchannel.isEmpty()) 
			return;
		//Open cvs file to save results and write the headers
		
		
		try {
			FileWriter fw = new FileWriter(iSBOps.getTargetFile(INPUT_DIR, channel, Integer.toString(thresholdValue)));
			
			String headings = "";
			
			headings += "Sample,";
			headings += "baseline,";//param0
			headings += "height,";//param1
			headings += "x,";//param2
			headings += "y,";//param3
			headings += "sigma_x,";//param4
			headings += "sigma_y,";//param 5
			headings += "fwhm_x,";
			headings += "fwhm_y,";
			headings += "fwhm,";
			
			headings += "error_baseline,";
			headings += "error_height,";
			headings += "error_x,";
			headings += "error_y,";
			headings += "error_sigma_x,";
			headings += "error_sigma_y,";
			headings += "error_fwhm_x,";
			headings += "error_fwhm_y,";
			headings += "error_fwhm,";
			
			headings += "slice,";
			headings += "frame,";
			headings += "BFslice\n";
							
			fw.write(headings);
		
			
		//OutputHeaders Done! Lets get the values to fill it	
		
			
		for (int fileIndex = 1; fileIndex<listchannel.size(); fileIndex++){
			
		ImagePlus imp = IJ.openImage(listchannel.get(fileIndex));
		File file = new File(listchannel.get(fileIndex));
		
		
		
		
		int width = imp.getWidth();

		DiscoidalAveragingFilter filter = new DiscoidalAveragingFilter(width, 1, 3);
		PeakFinder peakFinder = new PeakFinder(true, filter, 0, PeakFinderThreshold, 3);
		ImageStack stack = imp.getStack();
		
		
		IJ.log("Calculating: "+ file.getName() + " And saving at: " + iSBOps.getTargetFile(INPUT_DIR, channel, Integer.toString(thresholdValue)) );
		
			for (int i = 1; i <= stack.getSize(); i++) {
				
				ImageProcessor ip = stack.getProcessor(i);
				ArrayList<Point> positions = peakFinder.findPeaks(ip);
				
				for (int j = 0; j < positions.size(); j++) {
					
					// fit peak
					
					double[] parameters = new double[6];
					double[] errors = new double[6];
					
					for (int k = 0; k < parameters.length; k++)
						parameters[k] = Double.NaN;
					
					int x = positions.get(j).x;
					int y = positions.get(j).y;
					
					parameters[2] = x;
					parameters[3] = y;
					
					
					ip.setRoi(x - 3, y - 3, 7, 7);
					
					PeakFitter.fitPeak(ip, parameters, errors);
					
					//Filtering conditions
										
											
					for (int k = 0; k < parameters.length; k++) {
						
						if (Double.isNaN(parameters[k]) || Double.isNaN(errors[k]) || Math.abs(errors[k]) > maxError[k])
							continue;
						
					}
								
					
					
					double position_x = parameters[2];
					
					if ( position_x<1 ||  position_x>(ip.getWidth()-1) || Double.isNaN(position_x))
						continue;
					
					double  position_y = parameters[3];
					
					if ( position_y<1 ||  position_x>(ip.getHeight()-1) || Double.isNaN(position_y))
						continue;
					
					double fwhmx = parameters[4] * SIGMA_TO_FWHM;
					
						if (fwhmx<1 || fwhmx>6  || Double.isNaN(fwhmx))
							continue;
						
					double fwhmy = parameters[5] * SIGMA_TO_FWHM;
						if (fwhmy<1 || fwhmy>6 || Double.isNaN(fwhmy))
							continue;

						String str = "";
						str += file.getName().replace(".tif", "")+",";
						
						str += String.format("%f,", parameters[0]);
						str += String.format("%f,", parameters[1]);
						str += String.format("%f,", position_x);
						str += String.format("%f,", position_y);
						str += String.format("%f,", parameters[4]);
						str += String.format("%f,", parameters[5]);
						
						
						
						str += String.format("%f,", fwhmx);
						str += String.format("%f,", fwhmy);
						str += String.format("%f,", (fwhmx + fwhmy) / 2) ;
						
						str += String.format("%f,", errors[0]);
						str += String.format("%f,", errors[1]);
						str += String.format("%f,", errors[2]);
						str += String.format("%f,", errors[3]);
						str += String.format("%f,", errors[4]);
						str += String.format("%f,", errors[5]);
						
						double errorFwhmx = errors[4] * SIGMA_TO_FWHM;
						double errorFwhmy = errors[5] * SIGMA_TO_FWHM;
						
						str += String.format("%f,", errorFwhmx);
						str += String.format("%f,", errorFwhmy);
						str += String.format("%f,", Math.sqrt(errorFwhmx * errorFwhmx + errorFwhmy * errorFwhmy) / 2);
						str +=  fileIndex+1 + ",";
						str += String.format("%d,", i);
						//Get the BFSlice
						int BFSlice = getBFSlice(CSVContent, file.getAbsolutePath())	;
						
						
						
						str += String.format("%d\n", BFSlice);
						
						
						fw.write(str);
					
				}
				
				
			}
			
			
		}
		
		fw.close();
		}
		catch (IOException e) {
			// show exception!
			IJ.showMessage(e.getMessage());
		}
		

		
		
		
	
	
}

private static int getBFSlice(String[][] cSVContent2, String name) {
	int Index = iSBOps.getCol("BFIndex", cSVContent2[0]);
	int Input = iSBOps.getCol("Input", cSVContent2[0]);
	int ValueToReturn = 0;
	for (int i=0; i<cSVContent2.length;i++){
		//System.out.println(cSVContent2[i][Input]);
		//System.out.println(name);
		if(cSVContent2[i][Input].equals(name)){
			
			ValueToReturn = Integer.parseInt(cSVContent2[i][Index]);
			return ValueToReturn;
		}
		
		
		
	}
	return 0;
}

public static void getAllPeaks(ImagePlus imp, String filePath, int PeakFinderThreshold){
	
	int width = imp.getWidth();
	DiscoidalAveragingFilter filter = new DiscoidalAveragingFilter(width, 1, 3);
	PeakFinder peakFinder = new PeakFinder(true, filter, 0, PeakFinderThreshold, 3);
//	PeakFitter peakFitter = new PeakFitter();
			
	ImageStack stack = imp.getStack();
	
	try {
		FileWriter fw = new FileWriter(filePath);
		
		String headings = "";
		
		headings += "Sample, ";
		headings += "baseline, ";
		headings += "height, ";
		headings += "x, ";
		headings += "y, ";
		headings += "sigma_x, ";
		headings += "sigma_y, ";
		headings += "fwhm_x, ";
		headings += "fwhm_y, ";
		headings += "fwhm, ";
		
		headings += "error_baseline, ";
		headings += "error_height, ";
		headings += "error_x, ";
		headings += "error_y, ";
		headings += "error_sigma_x, ";
		headings += "error_sigma_y, ";
		headings += "error_fwhm_x, ";
		headings += "error_fwhm_y, ";
		headings += "error_fwhm, ";
		
		headings += "slice,";
		headings += "frame\n";
		
		
		
		fw.write(headings);
	
		for (int i = 1; i <= stack.getSize(); i++) {
			
			ImageProcessor ip = stack.getProcessor(i);
			ArrayList<Point> positions = peakFinder.findPeaks(ip);
			
			for (int j = 0; j < positions.size(); j++) {
				
				// fit peak
				
				double[] parameters = new double[6];
				double[] errors = new double[6];
				
				for (int k = 0; k < parameters.length; k++)
					parameters[k] = Double.NaN;
				
				int x = positions.get(j).x;
				int y = positions.get(j).y;
				
				parameters[2] = x;
				parameters[3] = y;
				
				
				ip.setRoi(x - 3, y - 3, 7, 7);
				
				PeakFitter.fitPeak(ip, parameters, errors);
				
				
								
				for (int k = 0; k < parameters.length; k++) {
					
					if (Double.isNaN(parameters[k]) || Double.isNaN(errors[k]) || Math.abs(errors[k]) > maxError[k])
						continue;
				}
				
					
				
				
				
					String str = "";
					
					str += String.format("%f, ", parameters[0]);
					str += String.format("%f, ", parameters[1]);
					str += String.format("%f, ", parameters[2]);
					str += String.format("%f, ", parameters[3]);
					str += String.format("%f, ", parameters[4]);
					str += String.format("%f, ", parameters[5]);
					
					double fwhmx = parameters[4] * SIGMA_TO_FWHM;
					double fwhmy = parameters[5] * SIGMA_TO_FWHM;
					
					str += String.format("%f, ", fwhmx);
					str += String.format("%f, ", fwhmy);
					str += String.format("%f, ", (fwhmx + fwhmy) / 2) ;
					
					str += String.format("%f, ", errors[0]);
					str += String.format("%f, ", errors[1]);
					str += String.format("%f, ", errors[2]);
					str += String.format("%f, ", errors[3]);
					str += String.format("%f, ", errors[4]);
					str += String.format("%f, ", errors[5]);
					
					double errorFwhmx = errors[4] * SIGMA_TO_FWHM;
					double errorFwhmy = errors[5] * SIGMA_TO_FWHM;
					
					str += String.format("%f, ", errorFwhmx);
					str += String.format("%f, ", errorFwhmy);
					str += String.format("%f, ", Math.sqrt(errorFwhmx * errorFwhmx + errorFwhmy * errorFwhmy) / 2);
					str += String.format("%d\n", i);
					
					
					fw.write(str);
				
				
			}
			
			
		}
		
		fw.close();
	}
	catch (IOException e) {
		// show exception!
		IJ.showMessage(e.getMessage());
	}
	
	
	
}


private static int ConfirmExecution(String[] labels) {
	
	
	Object[] options2 = labels;

    Component frame2 = null;
    int DKSelection= JOptionPane.showOptionDialog(frame2,
    		"Detect peaks of channel: ",
    		"Choose Channel",
    		JOptionPane.YES_NO_OPTION,
    		JOptionPane.QUESTION_MESSAGE,
    		null,     //do not use a custom Icon
    		options2,  //the titles of buttons
    		options2[0]); //default button title
return DKSelection;// TODO Auto-generated method stub
	
}

	
}

