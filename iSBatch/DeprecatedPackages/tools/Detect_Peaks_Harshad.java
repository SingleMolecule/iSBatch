/*
 * 
 */


import fiji.util.gui.GenericDialogPlus;
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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cellPeakPositions.MatlabMeshes;
import cellPeakPositions.Mesh;


import tools.iSBOps;
import External.DiscoidalAveragingFilter;
import External.PeakFinder;
import External.PeakFitter;




// TODO: Auto-generated Javadoc
/**
 * The Class Detect_Peaks_Harshad.
 */
public class Detect_Peaks_Harshad implements PlugIn {
	
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
	
	/** The labels. */
	String[] labels;
	
	/** The table. */
	static ResultsTable table;
	
	/**  source directory *. */
	public static String sourceDirectory="";
	
	/**  output directory *. */
	public static String outputDirectory="";
	
	
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

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		new Detect_Peaks_Harshad().run("");
		
		System.out.println("---------Done!-------------");
		
		java.awt.Toolkit.getDefaultToolkit().beep();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	public void run(String arg0){
		
		// ASk control File
		// Ask ROI folder
		// Ask MatlabFile
		

		GenericDialogPlus gd = new GenericDialogPlus("Register Virtual Stack");

		gd.addDirectoryField("Source directory", sourceDirectory, 50);
		gd.addDirectoryField("Output directory", outputDirectory, 50);
		gd.addChoice("Feature extraction model: ", featuresModelStrings, featuresModelStrings[featuresModelIndex]);
		gd.addChoice("Registration model: ", registrationModelStrings, registrationModelStrings[registrationModelIndex]);
		gd.addCheckbox("Advanced setup", advanced);	
		gd.addCheckbox("Shrinkage constrain", non_shrinkage);
		gd.addCheckbox("Save transforms", save_transforms);
		
		gd.showDialog();
		
		// Exit when canceled
		if (gd.wasCanceled()) 
			return;
				
		sourceDirectory = gd.getNextString();
		outputDirectory = gd.getNextString();
		featuresModelIndex = gd.getNextChoiceIndex();
		registrationModelIndex = gd.getNextChoiceIndex();
		advanced = gd.getNextBoolean();
		non_shrinkage = gd.getNextBoolean();
		save_transforms = gd.getNextBoolean();

		String source_dir = sourceDirectory;
		if (null == source_dir) 
		{
			IJ.error("Error: No source directory was provided.");
			return;
		}
		
		// Check if source directory exists
		if( (new File( source_dir )).exists() == false )
		{
			IJ.error("Error: source directory " + source_dir + " does not exist.");
			return;
		}
		
		source_dir = source_dir.replace('\\', '/');
		if (!source_dir.endsWith("/")) source_dir += "/";
		
		String target_dir = outputDirectory;
		if (null == target_dir) 
		{
			IJ.error("Error: No output directory was provided.");
			return;
		}
		
		// Check if output directory exists
		if( (new File( target_dir )).exists() == false )
		{
			IJ.error("Error: output directory " + target_dir + " does not exist.");
			return;
		}
		
		target_dir = target_dir.replace('\\', '/');
		if (!target_dir.endsWith("/")) target_dir += "/";
		
		// Select folder to save the transformation files if
		// the "Save transforms" check-box was checked.
		String save_dir = null;
		if(save_transforms)
		{
			// Choose target folder to save images into
			JFileChooser chooser = new JFileChooser(source_dir); 			
			chooser.setDialogTitle("Choose directory to store Transform files");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(true);
			if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
		    	return;
			
			save_dir = chooser.getSelectedFile().toString();
			if (null == save_dir) 
				return;
			save_dir = save_dir.replace('\\', '/');
			if (!save_dir.endsWith("/")) save_dir += "/";
		}
		
		// Select reference
		String referenceName = null;						
		if(non_shrinkage == false)
		{		
			JFileChooser chooser = new JFileChooser(source_dir); 
			// Choose reference image
			chooser.setDialogTitle("Choose reference image");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(true);
			if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
				return;
			referenceName = chooser.getSelectedFile().getName();
		}


		// Execute registration
		
		
		
		
		String csvFilename =  IJ.getFilePath("Provide ControlFile.CSV");
		if (csvFilename==null) return;	
		loadTable(csvFilename);
		File file = new File(csvFilename);
		
		roiFolder =  IJ.getDirectory("Indicate the directory with ROI Files");
			if (roiFolder==null) return;	
		ROIFile = new File(roiFolder);
		
		matFilename =  IJ.getFilePath("Load: Mat File");
			if (matFilename==null) return;	
	
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
	
	/**
	 * Gets the unique tags.
	 *
	 * @param collum the collum
	 * @param table the table
	 * @return the unique tags
	 */
	private List<String> getUniqueTags(String collum, ResultsTable table) {
		List<String> list = getAllTags(collum, table);
		HashSet<String> repeated = new HashSet<String>();
		repeated.addAll(list);
		list.clear();
		list.addAll(repeated);
		return list;
		
	}
	
	/**
	 * Gets the all tags.
	 *
	 * @param collum the collum
	 * @param table the table
	 * @return the all tags
	 */
	private List<String> getAllTags(String collum, ResultsTable table)
	{
		
		List<String> list = new ArrayList<>();
		
		
		for (int i=0; i<table.getCounter(); i++){
			String tag = table.getStringValue(collum, i);
			list.add(tag);
			}
			
				
		return list;
		
	}
	
	
	
	/**
	 * Decision tree.
	 *
	 * @param labels2 the labels2
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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



	/**
	 * Calculate peakson channel.
	 *
	 * @param labelsName the labels name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
 * 	private static double[] askThreshold() {
 * 		GenericDialog gd = new GenericDialog("threshold values");
 * 		double offset[];
 * 		gd.addNumericField("Threshold: ", offset, 0);
 * 		gd.showDialog();
 *         	if (gd.wasCanceled()){
 *         		return offset=0 ;
 *         	}
 *         offset = (int)gd.getNextNumber();
 * 		return offset[];
 * 	}
 *
 * @return the double[]
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
	
	/**
	 * Load table.
	 *
	 * @param csvFilename the csv filename
	 */
	private void loadTable(String csvFilename) {
		try {
			table = ResultsTable.open(csvFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
