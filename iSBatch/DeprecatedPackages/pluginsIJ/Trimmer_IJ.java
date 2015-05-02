/*
 * 
 */
package plugins_ij;

import ij.IJ;
	


import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;



import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

// TODO: Auto-generated Javadoc
/**
 * The Class Trimmer_IJ.
 */
public class Trimmer_IJ implements PlugIn  {

	 //Global Variables and configurations:
		/** The Slices begin. */
 	static int SlicesBegin; //How many slices to remove in the beginning of the movie
		
		/** The Slices end. */
		static int SlicesEnd; // How many slides to remove in the end
		
		/** The logpath. */
		static String logpath;
		
		/** The table. */
		static ResultsTable table;
		
		/**
		 * The main method.
		 *
		 * @param args the arguments
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void main(String[] args) throws IOException {
			
			new Trimmer_IJ().run("");
			IJ.log("Files Trimmed!");
			
		}
		
		
		//Get Main Folder that contains all subfolders from Olympus Excellence Software
		/**
		 * Run.
		 *
		 * @param arg0 the arg0
		 */
		public void run(String arg0){
			
			//Get file
			String csvFilename =  IJ.getFilePath("Provide ControlFile.CSV");
			if (csvFilename==null) return;	
				
			//Load table
			loadTable(csvFilename);
			
			//Get Unique tags related to the possible channels
		  	List<String> uniques = getUniqueTags("Channel", table);
			
			//ask user which channel to trimm
		  	
		  	decision_tree(uniques);
		  	try {
				table.saveAs(csvFilename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			
			
	    	
	    	
	    	
	    	
	    	
    	 //Check if some columm exist! If not, an extra columm will be created
	    //	CSVContent = checkCreateCollum("trimm",CSVContent);
	    	
	    	System.out.println("---------Done!-------------");
	    	
			IJ.showMessage("Done trimming!");
			java.awt.Toolkit.getDefaultToolkit().beep(); 
	    	    	
	    	
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
		
		
		

		/**
		 * Ask threshold.
		 */
		private static void askThreshold() {
			GenericDialog gd = new GenericDialog("Trim Slices.");
			int begin = 3;
			int end =	1;
			gd.addNumericField("Begin: ", begin, 0);
			gd.addNumericField("End: ", end, 0);
			gd.showDialog();
	        	if (gd.wasCanceled()){
	        		SlicesBegin = -1;
	        		SlicesEnd = -1;
	           		
	        	}
	        begin = (int)gd.getNextNumber();
	        end = (int)gd.getNextNumber();
	        SlicesBegin= begin;
	        SlicesEnd = end;
			
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
		  		System.out.println("Choice made: "+ choice);
		  		System.out.println("Detecting peaks");
		  		//Ask to continue
		  		
		  		//Ask threshold values
		  		
		  		askThreshold();
		  		
		  		for(int row=0; row<table.getCounter(); row++){
		  			
		  			String currentChannel = table.getStringValue("Channel", row);
		  			//table.incrementCounter();
		  			String pathToFile = table.getStringValue("WorkingFile", row);
		  			
		  			if(currentChannel.equalsIgnoreCase(items[choice])){
		  				//System.out.println(items[choice] + "here");
		  				//trimm image and save
		  				
		  				
		  				
		  				System.out.println(row + ": " + pathToFile);
		  				ImagePlus dataImage =  new ImagePlus(pathToFile);
		    			IJ.saveAsTiff(tools.ImageOperations.trimmer(dataImage,SlicesBegin,SlicesEnd),pathToFile);
		  				table.setValue("Trimmer", row, "Done");
		  				
		  				
		  				
		  			}
		  			
		  			
		  			
		  		}

		  			
		  		
		 				
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
		 * Confirm execution.
		 *
		 * @param labels the labels
		 * @return the int
		 */
		private static int ConfirmExecution(String[] labels) {
			
			
			Object[] options2 = labels;

		    Component frame2 = null;
		    int DKSelection= JOptionPane.showOptionDialog(frame2,
		    		"Trimming Images: ",
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
		    		"Trimming Images",
		    		JOptionPane.YES_NO_OPTION,
		    		JOptionPane.QUESTION_MESSAGE,
		    		null,     //do not use a custom Icon
		    		options2,  //the titles of buttons
		    		options2[0]); //default button title
		return DKSelection;// TODO Auto-generated method stub
			

		}
		
		
}

