/*
 * 
 */
package plugins_ij;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle.Control;

import javax.swing.JOptionPane;

import tools.iSBOps;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class Make_Location_Maps.
 */
public class Make_Location_Maps implements PlugIn{
		
		/** The Control table. */
		ResultsTable ControlTable;
		
		/** The Peaks table. */
		ResultsTable PeaksTable;
		
		/** The Projection table. */
		ResultsTable ProjectionTable;
		
		/** The Projection array. */
		String[][] ProjectionArray;
		
	
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException{
		new Make_Location_Maps().run("");
		IJ.log("Location Maps done!");
		java.awt.Toolkit.getDefaultToolkit().beep();
	}


	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	public void run(String arg0) {
		/**
		 * Files Needed:
		 *	1. Control File containing the "Experiment" Colum. That collum is the one that differentiate each set of replicates. That's what we have for now.
		 * 	2. File with peaks - FILTERED!
		 *
		
		
		String ControlFile =  IJ.getFilePath("Load: Control File");
			if (ControlFile==null) return;
		File fControlFile = new File(ControlFile);
	
		
		String PeaksFile =  IJ.getFilePath("Load: Peaks File");
		if (PeaksFile==null) return;
		
		 */
		
		//This for developing purposes. Uncomment upper lkines and erase the followuing 3 lines
		String ControlFile = "D:\\20140604Results.csv";
		try {
			ControlTable = ResultsTable.open(ControlFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//String PeaksFile = "D:\\20140521-UmuC\\Stats\\568.Peaks.LocalizedFiltered[500].txt";
		File fControlFile = new File(ControlFile);
		
		
		
		String LocationMapsFolder  = iSBOps.checkCreateSubDir(fControlFile.getParent(), "Location Maps");

		
		
		//All files needed are loaded.
		//Load data into tables
		//Results Table in ImageJ is NOT good.
		
			try {
				
				int choice = askToContinue();
				if (choice == 1 ) {			
					
				String PeaksFile =  IJ.getFilePath("Load: Peaks File");
				if (PeaksFile==null) return;	
					
			//also with a regular table
				
				//Have to create a new array to store all that data
				
				PeaksTable = ResultsTable.open(PeaksFile);
				
				
			//copy the header to the new array
				for(int row=0; row<PeaksTable.getCounter(); row++)
				{
					//Get BFview - since cell number does not matter
					//get group average
					int groupNumber; 
					int BFSlice = (int) PeaksTable.getValue("BFSlice", row);
					double L_Normalized = PeaksTable.getValue("L_normalized", row);
					double X_Position = L_Normalized;
					double Y_position = PeaksTable.getValue("D", row);
					for (int rowControl = 0; rowControl<ControlTable.getCounter(); rowControl++)
					{
						if(BFSlice == ControlTable.getValue("FolderIndex", rowControl))
						{
							groupNumber = (int) ControlTable.getValue("Group", rowControl);
							X_Position =  L_Normalized*ControlTable.getValue("GroupAvg", rowControl);
							
							break;
						}
					}
					
					//get Number of experiments
					
					PeaksTable.setValue("proj_x", row, X_Position);
					PeaksTable.setValue("proj_y", row, Y_position);
				}
				
				PeaksTable.saveAs(PeaksFile);
				ProjectionTable = PeaksTable;
				
				}
			
				
			if( choice != 1) {
				
				//If already have - Start ploting location maps
				// One location map per experiment
				String PeaksFile =  IJ.getFilePath("Load: Peaks File");
				if (PeaksFile==null) return;	
				ProjectionArray = tools.iSBOps.getCSVContent2(PeaksFile);
				ProjectionTable = ResultsTable.open(PeaksFile);
				}
				
				
				//get number of experiments
				//Its dubm.. but the code is already to confused... so.. just re-do some loops and forgtet about eficience for now
				
			
				
				//get longer cell of all 
				
				//getting Colindexes 
				
				int MinLength= 0;
				int  MaxLength = 0;
				int Maxheigth  = 13; // this is just to create a new image. Does not need to be precise.
				
				int NExperiments = 0;
				for (int row = 0; row<ProjectionTable.getCounter(); row++){
					double length = ProjectionTable.getValue("proj_x", row);
					
					double norm = ProjectionTable.getValue("L_normalized", row);
					double ratio = length/norm;
					
					if (ratio > MaxLength){
						MaxLength =(int) Math.round(length);
					}
					if(ratio < MinLength){
						MinLength =(int) Math.round(length);
					}
					
					
				}
				
				System.out.println(ControlTable.getCounter());
				for (int row = 0; row<ControlTable.getCounter(); row++){
					int check =  (int) ControlTable.getValue("Group", row);
					
					if (NExperiments<= check){
						System.out.println("Group: " + check);
						NExperiments = check;
					}
					
				}
				
				
				
				ImagePlus LocationMapNorm = IJ.createImage("LocationMapNorm", "32-bit", MaxLength*2, Maxheigth*2, NExperiments); 
				
				double sigma = 0.80;
				
				double pixelSize = 0.1;
				
				int xMin = -MinLength;

				int yMin = -Maxheigth;

				int xMax = MaxLength;

				int yMax = Maxheigth;

				int width = (xMax - xMin)*20;

				int height = 20*(Maxheigth + 10);
				
				System.out.println("Exps "+ NExperiments);
				ImagePlus LocationMap = IJ.createImage("LocationMap", "32-bit", width, height, NExperiments); 

				
				
				ImageStack LocationStacks = LocationMap.getStack();
				ImageStack LocationStacksNorm = LocationMapNorm.getStack();	
				
				ImageProcessor ip;
				//loop all lines experiments
				
				for (int row = 0; row<ProjectionTable.getCounter(); row++){
					
					double x_coord = ProjectionTable.getValue("proj_x", row);
					double y_coord = ProjectionTable.getValue("proj_y", row);
					int BFSlice = (int) ProjectionTable.getValue("BFSlice", row);
					int experiment = 0;
					//get Group NUmber based on the BFSlice
					for(int row2=0; row2 <ControlTable.getCounter(); row2++){
						int groupValue = (int) ControlTable.getValue("FolderIndex", row2);
						if(BFSlice == groupValue){
							experiment = (int) ControlTable.getValue("Group", row2);
							break;
						}
					}
					
					double xx  = x_coord - xMin;

					double yy  = y_coord - yMin;

					yy /= pixelSize;

					xx /= pixelSize;
					
					int x = (int) Math.round(xx)+ 100;
					int y = (int) Math.round(yy) + 100;
					//System.out.println(experiment);
					ip = LocationStacks.getProcessor(experiment);
					
					for (int y1 = -5; y1 <= 5; y1++) {

						for (int x1 = -5; x1 <= 5; x1++) {

							double value = gaussian(x1, y1, 1, sigma);
							

							ip.putPixelValue(x + x1, y + y1, ip.getPixelValue(x + x1, y + y1) + value);

						}

					}

				}
				
				
					
				IJ.saveAsTiff(LocationMap, LocationMapsFolder);
					
							
				
				
						
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Try.Catch ends here
			
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Save file.
	 *
	 * @param Array the array
	 * @param outputName the output name
	 * @param DIRECTORY the directory
	 */
	public static void SaveFile(String[][] Array, String outputName, File DIRECTORY){
		
		 try (
	                PrintStream output = new PrintStream(DIRECTORY.getParentFile() + File.separator + outputName+ ".csv");
	            ){

	            for(int i =0;i<Array.length;i++){
	            	String sc ="";
	                for (int j=0;j<Array[i].length;j++){
	                	sc+=Array[i][j]+",";
	                }
	                output.println(sc);
	                //output.println(Array[i][0] +"," + Array[i][1]+","+Array[i][2]+","+Array[i][3]+","+Array[i][4]+","+Array[i][5]+","+Array[i][6]+","+Array[i][7]+","+Array[i][8]+","+Array[i][9]+","+Array[i][10]+","+Array[i][11]+","+Array[i][12]+","+Array[i][13]+","+Array[i][14]+","+Array[i][15]+","+Array[i][16]+","+Array[i][17]+","+Array[i][18]+","+Array[i][19]+","+Array[i][20]);
	            }
	            output.close();

	        } catch (FileNotFoundException e) {

	            e.printStackTrace();
	        }
	}
	
	/**
	 * Ask to continue.
	 *
	 * @return the int
	 */
	private static int askToContinue() {
		Object[] options2 = {"Yes","No - Create one"};

	    Component frame2 = null;
	    int DKSelection= JOptionPane.showOptionDialog(frame2,
	    		"Do you have a PeakFile with proj_x and Proj_y Colum?: ",
	    		"Dark Count Correction",
	    		JOptionPane.YES_NO_OPTION,
	    		JOptionPane.QUESTION_MESSAGE,
	    		null,     //do not use a custom Icon
	    		options2,  //the titles of buttons
	    		options2[0]); //default button title
	return DKSelection;// TODO Auto-generated method stub
	}
	
	/**
	 * Gaussian.
	 *
	 * @param x the x
	 * @param y the y
	 * @param h the h
	 * @param s the s
	 * @return the double
	 */
	public double gaussian (double x , double y, double  h, double s) {

		return h * Math.exp(-(x * x + y * y) / (2 * s * s));

	}
	
	
	
	
	
	
	
	
	
	

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


