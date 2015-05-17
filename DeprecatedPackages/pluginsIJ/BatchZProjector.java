/*
 * 
 */
package plugins_ij;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.ZProjector;
import ij.process.ImageProcessor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

// TODO: Auto-generated Javadoc
/**
 * The Class BatchZProjector.
 */
public class BatchZProjector implements PlugIn{
	
	/**  Defining Variables *. */
	/** Input files and folders */
	static String[] labels;
	
	/** The CSV content. */
	static String[][] CSVContent;
	
	/** The table. */
	static ResultsTable table;
	
	/** The Images folder. */
	static String ImagesFolder;
	
	/** The csv filename. */
	String csvFilename;
	
	/** The file. */
	File file;
	
	/** The uniques. */
	List<String> uniques;
	//sync test
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException{
		new BatchZProjector().run("");
		IJ.log("BatchZProjector Done");
	}
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	public void run(String arg0){
		
		
		getFileFromUser();
		createImagesFolder();
		loadTable(csvFilename);
		
		
		
		uniques = getUniqueTags("Channel", table);
		
		decision_tree(uniques);
	 
		try {
			table.saveAs(csvFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IJ.log("Batch Z Prokection Created!!");
	}
	
	


	/**
	 * Gets the file from user.
	 *
	 * @return the file from user
	 */
	private String getFileFromUser() {
		this.csvFilename =  IJ.getFilePath("Provide ControlFile.CSV");
		
		if (csvFilename==null);
			System.out.println("FilePath is empty. Choose a valid one.");
		
		if (csvFilename != null){
			this.file = new File(csvFilename);
		}
		
		return csvFilename;
		
	}

	/**
	 * Decision_tree.
	 *
	 * @param list the list
	 */
	public static void decision_tree(List<String> list) {
		//check if collum existe
		//int TrimColIndex = table.getColumnIndex("Trimmer");
		//table.incrementCounter();
		//System.out.println(TrimColIndex);
		
		String[] items = list.toArray(new String[list.size()]);
		
		
		int choice = ConfirmExecution(items);
	  	
	  	  	
	  	if( choice == -1){
	  		System.out.println("Action cancelled");
	  	}
	  	
	  	else{
	  		System.out.println("Choice made: "+ items[choice]);
	  	
	  		//Ask threshold values
	  		String channel = items[choice];
	  		//Create the image to store get the first match
	  		
	  		ImagePlus averageImp = null;
	  		int StackSize = 0;
	  		
	  		for(int row=0; row<table.getCounter(); row++){
	  			String currentChannel = table.getStringValue("Channel", row);
	  			String pathToFile = table.getStringValue("WorkingFile", row);
	  			
	  			if(currentChannel.equalsIgnoreCase(channel)){
	  				StackSize++;
	  			}
	  		}
	  		System.out.println(StackSize);
	  		
	  		for(int row=0; row<table.getCounter(); row++){
	  			String currentChannel = table.getStringValue("Channel", row);
	  			String pathToFile = table.getStringValue("WorkingFile", row);
	  			
	  			if(currentChannel.equalsIgnoreCase(channel)){
	  				ImagePlus tempImp = IJ.openImage(pathToFile);
	  				averageImp = IJ.createImage(channel+"_avgStack", tempImp.getWidth(), tempImp.getHeight(), StackSize, 16);
	  				tempImp.close();
	  			}
	  		}
	  		
	  		ImageStack stack = averageImp.getStack();
	  		int currentStack = 1;
	  		
	  		for(int row=0; row<table.getCounter(); row++){
	  			
	  			String currentChannel = table.getStringValue("Channel", row);
	  			if(currentChannel.equalsIgnoreCase(items[choice])){
	  				System.out.println(StackSize);
	  				String pathToFile = table.getStringValue("WorkingFile", row);
	  				
	  				ImagePlus imp = IJ.openImage(pathToFile);
	  				String ImageName = imp.getShortTitle();
	  				//get Average Projection
	  				
	  				ZProjector projector = new ZProjector(imp);
	  				projector.setMethod(ZProjector.AVG_METHOD);
	  				projector.doProjection();
	  				
	  				//
	  				
	  				stack.setSliceLabel(ImageName, currentStack);
	  				stack.setProcessor(projector.getProjection().getProcessor(), currentStack);
	  				
	  				currentStack++;
	  			}
	  		}
	  		
	  		IJ.saveAsTiff(averageImp, ImagesFolder+ File.separator + "Projection_"+ items[choice]);
	  		
	 				
			int choice2 = askToContinue();	
	  		
	  		if(choice2 == 0){
	  			decision_tree(list);
	  		}
	  		else if (choice2 == 1){
	  			System.out.println("Action cancelled");
	  	 		}
		}
	}

	/**
	 * Creates the images folder.
	 */
	private void createImagesFolder() {
		ImagesFolder = tools.iSBOps.checkCreateDir(file.getParent()+File.separator+"Projections");
	}
	
	/**
	 * Confirm execution.
	 *
	 * @param labels the labels
	 * @return the int
	 */
	private static int ConfirmExecution(String[] labels) {
		
		
		Object[] options2 = labels;

	    Component frame2 = null;
	    int DKSelection= JOptionPane.showOptionDialog(frame2,
	    		"Batch ZProjector: ",
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
	

		
}