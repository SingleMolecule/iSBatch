package plugins_ij;

/** 
 * Victor Caldas
 * This work released under the terms of the General Public License in its latest edition. 
 * */
//import fiji.util.gui.GenericDialogPlus;
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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import cellPeakPositions.MatlabMeshes;
import cellPeakPositions.Mesh;

import tools.iSBOps;

import External.DiscoidalAveragingFilter;
import External.PeakFinder;
import External.PeakFitter;

/** 
 *  Descrive the plugin
 * <p>
 * <b>Requires</b>: a directory with images, of any size and type (8, 16, 32-bit gray-scale or RGB color)
 * <p>
 * <b>Performs</b>: registration of a sequence of images, by 6 different registration models:
 * <ul>
 * 				<li> Translation (no deformation)</li>
 * 				<li> Rigid (translation + rotation)</li>
 * 				<li> Similarity (translation + rotation + isotropic scaling)</li>
 * 				<li> Affine (free affine transformation)</li>
 * 				<li> Elastic (consistent elastic deformations by B-splines)</li>
 * 				<li> Moving least squares (maximal warping)</li>
 * </ul>
 * <p>
 * <b>Outputs</b>: the list of new images, one for slice, into a target directory as .tif files.
 * <p>
 * For a detailed documentation, please visit the plugin website at:
 * <p>
 * <A target="_blank" href="http://fiji.sc/wiki/Register_Virtual_Stack_Slices">http://fiji.sc/wiki/Register_Virtual_Stack_Slices</A>
 * 
 * @author Victor E. A. Caldas (caldas.victor@gmail.com)
 */


public class Detect_Peaks_Harshad implements PlugIn {
	
	
	/** Defining Variables **/
	/** Input files and folders */
	String sourceDirectory = null;
	String outputDirectory = null;
	String CONTROLFile = null;
	String MATLABFile = null;
	String ROIFolder = null;
	
	
	
	
	private static double[] maxError = new double[] {
		Prefs.getDouble("PeakFitter.maxErrorBaseline", 5000),
		Prefs.getDouble("PeakFitter.maxErrorHeight",5000),
		Prefs.getDouble("PeakFitter.maxErrorX", 1),
		Prefs.getDouble("PeakFitter.maxErrorY", 1),
		Prefs.getDouble("PeakFitter.maxErrorSigmaX", 1),
		Prefs.getDouble("PeakFitter.maxErrorSigmaY", 1),};
	
	//Not reviewd
	static String[][] CSVContent = null;
	static File file;
	private static double maxDistance = 12;
	static String matFilename;
	//String tableFilename = null;
	static String roiFolder;
	static File ROIFile;
	static String StatsFolder;
	public static final double SIGMA_TO_FWHM = 2.0 * Math.sqrt(2.0 * Math.log(2));
	private static int PeakFinderThreshold = 0;
	
	String[] labels;
	static ResultsTable table;
	
	public static void main(String[] args) throws IOException{
		new Detect_Peaks_Harshad().run("");
		System.out.println("---------Done!-------------");
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	
	
	public void run(String arg0){
		
		/** Defining Generic Dialog- ASking for inputs */
		
		GenericDialogPlus gd = new GenericDialogPlus("Detect Peaks");
		
		gd.addFileField("Control File", CONTROLFile,50);
		gd.addDirectoryField("Roi Folder", ROIFolder, 50);
		gd.addFileField("MatLab File", MATLABFile , 50);
		
		//gd.addChoice("Feature extraction model: ", featuresModelStrings, featuresModelStrings[featuresModelIndex]);
		//gd.addChoice("Registration model: ", registrationModelStrings, registrationModelStrings[registrationModelIndex]);
		//gd.addCheckbox("Advanced setup", advanced);	
		//gd.addCheckbox("Shrinkage constrain", non_shrinkage);
		//gd.addCheckbox("Save transforms", save_transforms);
		
		gd.showDialog();
		
		// Exit when canceled
		if (gd.wasCanceled()) 
			return;
		
		CONTROLFile = gd.getNextString();
		ROIFolder = gd.getNextString();
		MATLABFile = gd.getNextString();
		
		// Control File checking
		if (null == CONTROLFile) 
		{
			IJ.error("Control File Error: No source was provided or incorrect format.");
			return;
		}
		
		loadTable(CONTROLFile);
		File file = new File(CONTROLFile);
		
/**		ROI Folder Checking	
		Check if source directory exists*/
		if( (new File( ROIFolder )).exists() == false )
			{
			IJ.error("Error: source directory " + ROIFolder + " does not exist.");
			return;
		}
		ROIFile = new File(roiFolder);
		
		/**	Matlab File checking*/
		if (null == MATLABFile) 
		{
			IJ.error("Control File Error: No source was provided or incorrect format.");
			return;
		}
	
		StatsFolder = iSBOps.checkCreateSubDir(file.getParent(), "Stats");
		
		List<String> uniques = getUniqueTags("Channel", table);
		String[] labels = iSBOps.CheckBoxLabes(uniques);
		
		try {
				decisionTree(labels);
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private List<String> getUniqueTags(String collum, ResultsTable table) {
		List<String> list = getAllTags(collum, table);
		HashSet<String> repeated = new HashSet<String>();
		repeated.addAll(list);
		list.clear();
		list.addAll(repeated);
		return list;
		
	}
	
	private List<String> getAllTags(String collum, ResultsTable table)
	{
		
		List<String> list = new ArrayList<>();
		
		
		for (int i=0; i<table.getCounter(); i++){
			String tag = table.getStringValue(collum, i);
			list.add(tag);
			}
			
				
		return list;
		
	}
	
	
	
	private void decisionTree(String[] labels2) throws IOException {
		
		//System.out.println(labels2);
		for (String string : labels2){
			//System.out.println(string);
		}
		int choice = chooseChannel(labels2);
		//System.out.println(choice + labels2[choice]);

    	while (startCalculation(choice)){
    		CalculatePeaksonChannel(labels2[choice]);
    		
    		if(checkToContinue()){
    			decisionTree(labels2);
    		}
    		if(checkToContinue() == false){
    			break;
    		}
    		
    	
    	}
	}



	private static void CalculatePeaksonChannel(String labelsName) throws IOException {
		
	 
	  		
	  		System.out.println("Detecting peaks");
	  		
	  		
	  		//List<String> listchannel = getSubFileList(table, "Channel",labelsName, "SaveToPath");
	  			  		
	  		List<File> listRois = tools.iSBOps.getListofFiles(ROIFile);
	  		
	  		ResultsTable PeakData = new ResultsTable();
	  		double thresholds[] = askThreshold();
	  		
	  		double SignalToNoiseTreshold = thresholds[0];
	  		double PeakFinderThreshold = thresholds[1];
	  		
	  		
	  		for (int row=0; row<table.getCounter(); row++)
	  		
	  		{
	  			//Check if Channel is ok
	  			String currentChannel = table.getStringValue("Channel", row);
	  			
	  			if (currentChannel.equalsIgnoreCase(labelsName)){
	  				int Folder = (int) table.getValue("FolderIndex", row);	
	  				int ExperimentIndexNumber = (int) table.getValue("ExperimentIndex", row);
	  				List<File> subSetROIS = getROIs(listRois, Folder);
	  				String fileToPath = table.getStringValue("WorkingFile", row);
	  				System.out.println(fileToPath);
	  				ImagePlus imp =  new ImagePlus(fileToPath);
	  				
	  			//put ROis in the manager
	  				
	  				RoiManager manager = RoiManager.getInstance();
	  				if (manager == null)
	  				    manager = new RoiManager();
	  				
	  				int subSetROISize = subSetROIS.size(); 
	  				
	  				for(int k=0; k<subSetROIS.size(); k++){
	  					
	  					//System.out.println(subSetROIS.get(k));
	  				}
	  				for (int j = 0 ; j< subSetROISize; j++){
	  					
	  					String file = subSetROIS.get(j).getAbsolutePath();
	  					RoiDecoder roiD = new RoiDecoder(file);
	  				
	  					manager.addRoi(roiD.getRoi());
	  				}
	  				
	  			//Loop in the ImagePlus
	  				ImageStack stack = imp.getStack();
	  				int stackSize = stack.getSize();
	  				for(int stackPosition = 1; stackPosition<=stackSize; stackPosition++)
	  				{
	  					ImageProcessor ip = stack.getProcessor(stackPosition);
	  					
	  					
	  					@SuppressWarnings("unchecked")
	  					Hashtable<String, Roi> table = (Hashtable<String, Roi>)manager.getROIs();   
	  					
	  				 for (String label : table.keySet()) {
	  						 //System.out.println(label);
	  						 
	  						 
	  					 Roi roi = table.get(label);
	  				
	  					 ImageProcessor ip2 = imp.getProcessor();
	  					
	  				     ip2.setRoi(roi);
	  				     
	  					DiscoidalAveragingFilter filter1 = new DiscoidalAveragingFilter(ip2.getWidth(), 1, 3);
	  					
	  					//PeakFinder peakFinder = new PeakFinder(true, filter, 0, PeakFinderThreshold, 3);
	  					//PeakFinder peakFinder2 = new PeakFinder(true, filter1, 0, PeakFinderThreshold, 3);
	  					PeakFinder peakFinder2 = new PeakFinder(false, filter1, SignalToNoiseTreshold,PeakFinderThreshold, 5);

	  					
	  					//ArrayList<Point> positions = peakFinder.findPeaks(ip);						
	  					ArrayList<Point> positions = peakFinder2.findPeaks(ip2);
	  					//System.out.println("--" + positions.size() + "," + positions2.size());
	  					
	  					
	  					
	  					for (int j = 0; j < positions.size(); j++) {
	  						//	System.out.println("here");
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
	  								//PeakData.addLabel("Cell", label.substring(label.lastIndexOf('-') + 1));
	  								PeakData.addValue("slice", stackPosition);
	  								PeakData.addValue("ExperimentIndex", ExperimentIndexNumber);
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
	  				//imp.flush();  	
			}
			
			
			/**
			
			if(row == 1){
				break;
			}
			*/
				
				}
	  		String Destination2 = StatsFolder + File.separator + labelsName +".Peaks.LocalizedNonFiltered"+"[RawPeakList]"+".csv";
	  		PeakData.saveAs(Destination2);
	  		
			IJ.log( "Localizing within the cels.");
			
			addLocalization(PeakData,matFilename);
			//FilterOut the spots outside cells
			
			ResultsTable FilteredTable = new ResultsTable();
			System.out.println(PeakData.getCounter());
			
			
			if (PeakData.getCounter() == 0){ //table is empty
				IJ.log("No peaks detected for channel " +labelsName + " using threshold value: " + PeakFinderThreshold+ ". No table will be saved!");
				
			}
			
			else {
				
			String Destination = StatsFolder + File.separator + labelsName +".Peaks.LocalizedNonFiltered"+"["+PeakFinderThreshold+"]"+".csv";
			PeakData.saveAs(Destination);
			IJ.log( "Saving full table peak for channel " +labelsName + " using threshold value: " + PeakFinderThreshold);
			
			IJ.log( "Filtering peaks within cells.");
			
			
			
			
			String[] labels = PeakData.getHeadings();
			
			
			
			
			
			for (int row = 0; row< PeakData.getCounter() ; row++) {
		
				int cell = (int) PeakData.getValue("cell", row);
			//	System.out.println(row);
				if (cell != 0){
				FilteredTable.incrementCounter();
				for(String  label : labels){
						String value = PeakData.getStringValue(label, row);
						FilteredTable.addValue(label, value);
			
					}
				
			
				}
			
			}
		

			
			String Destination1 = StatsFolder + File.separator + labelsName +".Peaks.LocalizedFiltered"+"["+PeakFinderThreshold+"]"+".csv";
			IJ.log( "Saving filtered table peak for channel " +labelsName + " using threshold value: " + PeakFinderThreshold);
			FilteredTable.saveAs(Destination1);
			IJ.log("Done!");
			}
			
	 		
	}



	
	
	
	private static boolean startCalculation(int choice) {
		if(choice==-1){
			return false;
		}
		else{
			return true;
		}
		
	}



	private static boolean checkToContinue() {

		int choice2 = askToContinue();	
	  
		if (choice2==0){
  			return true;
  		}
		else{
			return false;
		}
		
		
	}



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
	private static double[] askThreshold() {
		GenericDialog gd = new GenericDialog("threshold values");
		double offset[];
		gd.addNumericField("Threshold: ", offset, 0);
		gd.showDialog();
        	if (gd.wasCanceled()){
        		return offset=0 ;
        	}
        offset = (int)gd.getNextNumber();
		return offset[];
	}
*/
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
	private void loadTable(String CONTROLFile) {
		try {
			table = ResultsTable.open(CONTROLFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
