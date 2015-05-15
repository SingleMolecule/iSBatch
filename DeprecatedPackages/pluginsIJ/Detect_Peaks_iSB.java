/*
 * 
 */
package pluginsIJ;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import cellPeakPositions.MatlabMeshes;
import cellPeakPositions.Mesh;


import tools.iSBOps;
import External.DiscoidalAveragingFilter;
import External.PeakFinder;
import External.PeakFitter;




// TODO: Auto-generated Javadoc
/**
 * The Class Detect_Peaks_iSB.
 */
public class Detect_Peaks_iSB implements PlugIn {
	
	/** The CSV content. */
	static String[][] CSVContent = null;
	
	/** The file. */
	static File file;
	
	/** The max distance. */
	private static double maxDistance = 12;
	
	/** The mat filename. */
	static String matFilename;
	//String tableFilename = null;
	/** The roi folder. */
	static String roiFolder;
	
	/** The ROI file. */
	static File ROIFile;
	
	/** The Stats folder. */
	static String StatsFolder;
	
	/** The Constant SIGMA_TO_FWHM. */
	public static final double SIGMA_TO_FWHM = 2.0 * Math.sqrt(2.0 * Math.log(2));
	
	/** The Peak finder threshold. */
	private static int PeakFinderThreshold = 0;
	
	/** The table filename. */
	static String tableFilename;
	
	/** The labels. */
	String[] labels;
	
	/** The max error. */
	private static double[] maxError = new double[] {
		Prefs.getDouble("PeakFitter.maxErrorBaseline", 5000),
		Prefs.getDouble("PeakFitter.maxErrorHeight",5000),
		Prefs.getDouble("PeakFitter.maxErrorX", 1),
		Prefs.getDouble("PeakFitter.maxErrorY", 1),
		Prefs.getDouble("PeakFitter.maxErrorSigmaX", 1),
		Prefs.getDouble("PeakFitter.maxErrorSigmaY", 1),};
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException{
		new Detect_Peaks_iSB().run("");
		
		System.out.println("---------Done!-------------");
		
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	public void run(String arg0){
		
		//Open BFMAt
		//Open ConfigFile
		//Open ROI
		tableFilename =  IJ.getFilePath("Load: Control File");
			if (tableFilename==null) return;
		
		
		
		
		System.out.println(tableFilename);


		
		File tableFile = new File(tableFilename);
		roiFolder =  IJ.getDirectory("Indicate the directory with ROI Files");
			if (roiFolder==null) return;	
		ROIFile = new File(roiFolder);
		
		matFilename =  IJ.getFilePath("Load: Mat File");
			if (matFilename==null) return;	
	
		StatsFolder = iSBOps.checkCreateSubDir(tableFile.getParent(), "Stats");

		
			try {
				CSVContent = tools.iSBOps.getCSVContent2(tableFilename);
				List<String> listchannel = tools.iSBOps.getCollumDataUniquesNoHeader(CSVContent, "Channel");
			
				labels = iSBOps.CheckBoxLabes(listchannel);
				decisionTree(labels);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
	    	 
	
		
		
	}
	
	
	
	
	/**
	 * Decision tree.
	 *
	 * @param labels2 the labels2
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void decisionTree(String[] labels2) throws IOException {
		
			
		int choice = chooseChannel(labels2);
		
    	while (startCalculation(choice)){
    		CalculatePeaksonChannel(labels[choice]);
    		
    		if(checkToContinue()){
    			decisionTree(labels2);
    		}
    		if(checkToContinue() == false){
    			break;
    		}
    		
    	
    	}
	}



	/**
	 * Calculate peakson channel.
	 *
	 * @param labelsName the labels name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void CalculatePeaksonChannel(String labelsName) throws IOException {
		
	 
	  		//System.out.println("Choice made: "+ choice);
	  		System.out.println("Detecting peaks");
	  		//Ask to continue
	  		
	  		//Ask threshold values
	  		//The channel was selected. Now, get a list of all images that will be analysed
	  		List<String> listchannel = tools.iSBOps.getSubFileList(CSVContent, labelsName);
	  		List<File> listRois = tools.iSBOps.getListofFiles(ROIFile);
	  		
	  		ResultsTable PeakData = new ResultsTable();
	  		PeakFinderThreshold = askThreshold();	
	  		
			for (int i=1; i<listchannel.size();i++){
							
			
			int Folder = FolderIndex(CSVContent, listchannel.get(i));
			IJ.log(listchannel.get(i) + "  Folder: "+ Folder);
				
			
			//load the Image and get the Rois
			List<File> subSetROIS = getROIs(listRois, Folder);
			ImagePlus imp =  new ImagePlus(listchannel.get(i));
			
			//put ROis in the manager
			
			RoiManager manager = RoiManager.getInstance();
			if (manager == null)
			    manager = new RoiManager();
			
			int subSetROISize = subSetROIS.size(); 
			
			for (int j = 0 ; j< subSetROISize; j++){
				
				String file = subSetROIS.get(j).getAbsolutePath();
				RoiDecoder roiD = new RoiDecoder(file);
			//	ImageProcessor mask = imp.getMask();
				//boolean hasmask = (mask !=null);
				
			//	if (hasmask){
			//		(new ImagePlus("The mask", mask)).show();
			//	}
				
				manager.addRoi(roiD.getRoi());
			}
			
			
			
			
			//Rois Loaded.
			//ImageLoaded
			
			//Loop in the ImagePlus
			ImageStack stack = imp.getStack();
			int stackSize = stack.getSize();
			for(int stackPosition = 1; stackPosition<=stackSize; stackPosition++){
//				ImageProcessor ip = stack.getProcessor(stackPosition);
				
				
				@SuppressWarnings("unchecked")
				Hashtable<String, Roi> table = (Hashtable<String, Roi>)manager.getROIs();   
				
				 for (String label : table.keySet()) {
					 //System.out.println(label);
				     Roi roi = table.get(label);
				     ImageProcessor ip2 = imp.getProcessor();
				     ip2.setRoi(roi);
					DiscoidalAveragingFilter filter1 = new DiscoidalAveragingFilter(ip2.getWidth(), 1, 3);
					PeakFinder peakFinder2 = new PeakFinder(true, filter1, 0, PeakFinderThreshold, 3);
					ArrayList<Point> positions = peakFinder2.findPeaks(ip2);
					//System.out.println("--" + positions.size() + "," + positions2.size());
						
					for (int j = 0; j < positions.size(); j++) {
						// fit peak
						
						double[] parameters= new double[6];
						double[] errors = new double[6];
						
						for (int k = 0; k < parameters.length; k++)
							parameters[k] = Double.NaN;
						
						int x = positions.get(j).x;
						int y = positions.get(j).y;
						//System.out.println( x+ "," + y + "*");
					
					
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
							
							
							PeakData.incrementCounter();	
							
							PeakData.addValue("BFSlice",Folder);
							PeakData.addLabel("Cell", label.substring(label.lastIndexOf('-') + 1));
							PeakData.addValue("slice", stackPosition);
							
							PeakData.addValue("baseline", parameters[0]);
							PeakData.addValue("height", parameters[1]);
							PeakData.addValue("x", parameters[2]);
							PeakData.addValue("y", parameters[3]);
							PeakData.addValue("sigma_x", parameters[4]);
							PeakData.addValue("sigma_y", parameters[5]);
							
							
							PeakData.addValue("fwhm_x",   fwhmx);
							PeakData.addValue("fwhm_y",   fwhmy);
							PeakData.addValue("fwhm",     (fwhmx + fwhmy) / 2);
							
							PeakData.addValue("error_baseline", errors[0]);
							PeakData.addValue("error_height",   errors[1]);
							PeakData.addValue("error_x",        errors[2]);
							PeakData.addValue("error_y",    	 errors[3]);
							PeakData.addValue("error_sigma_x",  errors[4]);
							PeakData.addValue("error_sigma_y",  errors[5]);
							
							double errorFwhmx = errors[4] * SIGMA_TO_FWHM;
							double errorFwhmy = errors[5] * SIGMA_TO_FWHM;
							
							PeakData.addValue("error_fwhm_x",   errorFwhmx);
							PeakData.addValue("error_fwhm_y",	 errorFwhmy);
							PeakData.addValue("error_fwhm",     Math.sqrt(errorFwhmx * errorFwhmx + errorFwhmy * errorFwhmy) / 2);
							
							//table.addValue("z", zScale * (fwhmx - fwhmy)); 
							//table.addValue("error_z", zScale * Math.sqrt(errorFwhmx * errorFwhmx + errorFwhmy * errorFwhmy));
							
							PeakData.addValue("slice", stackPosition);
							
				
					}
						
				 }
			}
			
			manager.close();
			imp.close();
			imp.flush();  
			
			/**
			if(i == 1){
				break;
			}
			*/
			
			}
			
			IJ.log( "Localizing within the cels.");
			
			addLocalization(PeakData,matFilename);
			//FilterOut the spots outside cells
			
			ResultsTable FilteredTable = new ResultsTable();
			System.out.println(PeakData.getCounter());
			
			
			if (PeakData.getCounter() == 0){ //table is empty
				IJ.log("No peaks detected for channel " +labelsName + " using threshold value: " + PeakFinderThreshold+ ". No table will be saved!");
				
			}
			
			else {
				
			String Destination = StatsFolder + File.separator + labelsName +".Peaks.LocalizedNonFiltered"+"["+PeakFinderThreshold+"]"+".txt";
			PeakData.saveAs(Destination);
			IJ.log( "Saving full table peak for channel " +labelsName + " using threshold value: " + PeakFinderThreshold);
			
			IJ.log( "Filtering peaks within cells.");
			
			
			int last = PeakData.getLastColumn();
			
			String[] labels = PeakData.getHeadings();
			
			
			
			
			
			for (int row = 1; row< PeakData.getCounter() ; row++) {
		
				int cell = (int) PeakData.getValue("cell", row);
				System.out.println(row);
				if (cell == 0){
			
				for(String  label : labels){
						String value = PeakData.getStringValue(label, row);
						FilteredTable.setValue(label, row, value);
			
					}
			
				}
			
			}
		

			
			String Destination1 = StatsFolder + File.separator + labelsName +".Peaks.LocalizedFiltered"+"["+PeakFinderThreshold+"]"+".txt";
			IJ.log( "Saving filtered table peak for channel " +labelsName + " using threshold value: " + PeakFinderThreshold);
			FilteredTable.saveAs(Destination1);
			}
			
	 		
	}
	
	
	/**
	 * Start calculation.
	 *
	 * @param choice the choice
	 * @return true, if successful
	 */
	private static boolean startCalculation(int choice) {
		if(choice==-1){
			return false;
		}
		else{
			return true;
		}
		
	}



	/**
	 * Check to continue.
	 *
	 * @return true, if successful
	 */
	private static boolean checkToContinue() {

		int choice2 = askToContinue();	
	  
		if (choice2==0){
  			return true;
  		}
		else{
			return false;
		}
		
		
	}



	/**
	 * Adds the localization.
	 *
	 * @param peakData the peak data
	 * @param matFilename2 the mat filename2
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void addLocalization(ResultsTable peakData,
			String matFilename2) throws IOException {
		
			ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(matFilename);
			
			for (int row = 0; row < peakData.getCounter(); row++) {
				
				int slice = (int)peakData.getValue("BFSlice", row);
				double x = peakData.getValue("x", row);
				double y = peakData.getValue("y", row);
				
				
				//System.out.println(slice +","+ x + ","+ y);
				if (Double.isNaN(x) || Double.isNaN(y))
					continue;

				// find closest mesh
				cellPeakPositions.Point p = new cellPeakPositions.Point(x, y);
				
				Mesh closestMesh = null;
				double minDistance = maxDistance;
				
				for (Mesh m: meshes) {
					
					if (m.getSlice() == slice) {
						
						double distance = m.distanceTo(p);
						
						if (distance < minDistance) {
							
							minDistance = distance;
							closestMesh = m;
							
						}
						
					}
					
				}
				
				if (closestMesh != null) {
					
					cellPeakPositions.Point projection = closestMesh.projectionOf(p);
					
					//peakData.incrementCounter();
					peakData.setValue("L", row,  projection.x);
					peakData.setValue("D",row, projection.y);
					peakData.setValue("L_normalized", row,projection.x / closestMesh.getTotalLength());
					peakData.setValue("cell", row,(int)closestMesh.getCell());
					peakData.setValue("length", row,closestMesh.getTotalLength());
					peakData.setValue("area",row, closestMesh.getArea());
					peakData.setValue("volume",row, closestMesh.getVolume());
					
					
				}
				
			}
			
			
			
		
	}

	/**
	 * Gets the RO is.
	 *
	 * @param listRois the list rois
	 * @param folder the folder
	 * @return the RO is
	 */
	private static List<File> getROIs(List<File> listRois, int folder) {
		List<File> results = new ArrayList<>();
		
		for (int i=0; i<listRois.size();i++){
			//Split the ROI name to get the usefull index
			String ROINAME = listRois.get(i).getName();
		// Split based on the "-"" 
			
			
			
			int intROIindex = Integer.parseInt(ROINAME.split("-")[0]);
			
			if (folder == intROIindex){
				results.add(listRois.get(i));
				
			}
		}
		return results;
	}

	/**
	 * Ask threshold.
	 *
	 * @return the int
	 */
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

	/**
	 * Folder index.
	 *
	 * @param cSVContent2 the c sv content2
	 * @param string the string
	 * @return the int
	 */
	private static int FolderIndex(String[][] cSVContent2, String string) {
		
		int inputCol = tools.iSBOps.getCol("SaveToPath", cSVContent2[0]);
		int folderCol = tools.iSBOps.getCol("FolderIndex", cSVContent2[0]);
		int valueToReturn =0;
		
		for (int i=0; i<cSVContent2.length; i++){
			
			
			if(string.equalsIgnoreCase(cSVContent2[i][inputCol])){
				valueToReturn = Integer.parseInt(cSVContent2[i][folderCol]);
			}
		}
		return valueToReturn;
		
	}

	/**
	 * Choose channel.
	 *
	 * @param labels the labels
	 * @return the int
	 */
	private static int chooseChannel(String[] labels) {
		
		
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
	
	/**
	 * Ask to continue.
	 *
	 * @return the int
	 */
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
}
