/*
 * 
 */
package Deprecated;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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
 * The Class Draw_Location.
 */
public class Draw_Location implements PlugIn{
		
		/** The Control table. */
		ResultsTable ControlTable;
		
		/** The Peaks table. */
		ResultsTable PeaksTable;
		
		/** The Projection array. */
		String[][] ProjectionArray;
		
	
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException{
		new Draw_Location().run("");
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
				
				String[][] ControlTableArray = tools.iSBOps.getCSVContent2(ControlFile);
				String[][] PeaksFileArray = tools.iSBOps.getCSVContent2(PeaksFile);
				//Have to create a new array to store all that data
				ProjectionArray = new String[PeaksFileArray.length][PeaksFileArray[0].length+3];
				
				
				
				int PeakArrayCols = PeaksFileArray[0].length;
				//copy the header to the new array
				for(int i=0; i<PeakArrayCols; i++){
					ProjectionArray[0][i] = PeaksFileArray[0][i];
					
				}
				ProjectionArray [0] [ProjectionArray[0].length-3] = "Experiment"; // add last - 1 collumn
				ProjectionArray [0] [ProjectionArray[0].length-2] = "proj_x"; // add last - 1 collumn
				ProjectionArray [0] [ProjectionArray[0].length-1] = "proj_y"; // add last collumn
				
				int IndexOfProj_Y = tools.iSBOps.getCol("D", PeaksFileArray);
				int NEWIndexOfProj_Y = tools.iSBOps.getCol("proj_y", ProjectionArray);
				int IndexOfProj_X = tools.iSBOps.getCol("L_normalized", PeaksFileArray);
				int NEWIndexOfProj_X = tools.iSBOps.getCol("proj_x", ProjectionArray);
				
				
				
				
			
				
				
				//Assume that there is a collumn with 
				int ExperimentCol = tools.iSBOps.getCol("Group", ControlTableArray);
				int ExperimentColPeaks = tools.iSBOps.getCol("Group", ProjectionArray);
				
			
				if(ExperimentCol == -1){
					IJ.log("Add the collumn \"Experiment\" to the Control File.");
				}
				
				//System.out.println(ControlTable.getHeadings());
				
				//Create a table to store data from the dots and create 
				//Check how many experiments exist
				int NumberOfExperiments = Integer.parseInt(ControlTableArray[ControlTableArray.length-1][ExperimentCol]);
				//Create a table to store all the values;
				String[][] DataOverview = new String[NumberOfExperiments-1][6];
				//Giving names to Headers
				DataOverview[0][0] =  "Experiment";
				DataOverview[0][1] =  "MaxCellLength";
				DataOverview[0][2] =  "MaxCellWidth";
				DataOverview[0][3] =  "NCells";
				DataOverview[0][4] =  "NSpots";		
				DataOverview[0][5] =  "CumulateLength";
				
				
				System.out.println(ControlTableArray[0][4]);
				System.out.println(ControlTableArray[1][0]);
				//Populate the DataOverview Table
					
				//Get the indexes that will be used latter
				
				int LengthCol = iSBOps.getCol("L", PeaksFileArray);
				
				//get the List with all length averages
				
				int[] FolderIndextStatingPoint = new int[NumberOfExperiments+1];
				/**
				 * Based on the control file, it's easy to check how many experiments we have.
				 * Get that number and create a appropriate loop. 
				 */
				int FolderIndex = tools.iSBOps.getCol("FolderIndex", ControlTableArray);
				
				for(int nExperiments = 1; nExperiments<=NumberOfExperiments; nExperiments++){
					int FolderIndexMax = 0;
					//get the Value
					for (int row = 1; row< ControlTableArray.length; row++){
						
						if(Integer.parseInt(ControlTableArray[row][ExperimentCol]) == nExperiments){
							
							int CurrentFolderIndex = Integer.parseInt(ControlTableArray[row][FolderIndex]);
							if(CurrentFolderIndex>=FolderIndexMax){
								FolderIndexMax = CurrentFolderIndex;
							}
						}
						
					}
					
					FolderIndextStatingPoint[nExperiments] = FolderIndexMax;
			}
				
				
				//Populating with the averages
				
				List<Double> CellLength = new ArrayList<Double>();
				
				
				
				int BFSliceCol = iSBOps.getCol("BFSlice", PeaksFileArray);
				int CellNumberCol = iSBOps.getCol("cell", PeaksFileArray);
				int LengthNumberCol = iSBOps.getCol("length", PeaksFileArray);
				System.out.println("Cell Col: " + CellNumberCol);
				for(int nExperiments = 1; nExperiments<=NumberOfExperiments; nExperiments++){
					List<Double> temLength = new ArrayList<Double>();
					
					List<String> tempCodes = new ArrayList<String>(); // sote cellcodes like 1-13 (slice/cellnumber)
					
					
					int lowLimit = FolderIndextStatingPoint[nExperiments-1];
					int UpperLimit = FolderIndextStatingPoint[nExperiments];
					
					
				for (int row = 1; row< PeaksFileArray.length; row++){//check line by line 
					
					
					
					
					int cellNumber = Integer.parseInt(PeaksFileArray[row][CellNumberCol]);
					
					int BFNunber = Integer.parseInt(PeaksFileArray[row][BFSliceCol]);
					double length = Double.parseDouble(PeaksFileArray[row][LengthNumberCol]);
					String code = Integer.toString(BFNunber) + "-" +   Integer.toString(cellNumber);
					
					
						
					//check if it is a experiment in that range
					int CurrentBFSlice = Integer.parseInt(PeaksFileArray[row][BFSliceCol]);
					
					if(CurrentBFSlice > lowLimit && CurrentBFSlice <= UpperLimit){ // it is in the range
						//get the values:
					
						ProjectionArray[row][ExperimentColPeaks] = Integer.toString(nExperiments);
						
						//loop to check if the value should be added or not to the list
						if (tempCodes.contains(code)) {   //
							//System.out.println("Contains: "+ code) ;													
						}
						else{
							//System.out.println(code);
							temLength.add(length);
							tempCodes.add(code);
							
														
						}	
			
					}
				
					}
					//all the values are in the list. So get the average
					double sum = 0;
					for (int i = 0; i<temLength.size(); i++){
						sum += temLength.get(i);
						
					}
					
					CellLength.add(sum/temLength.size());
					
						
				
				
				}
				

				for (int row = 1; row< ControlTableArray.length; row++){
					
	
					//Now, start feeding the DataOverview using information from the PeakFileArray
					int PeakArrayRows = PeaksFileArray.length;
					IJ.log("Experiment: "+ row);
					
				//	int nCells = 0;
				//	int nSpot = 0;
					double ExperimentFinalCellLength = 0;
					double ExperimentFinalCellWidth = 0;
					
					for (int peakRow = 1; peakRow<PeakArrayRows; peakRow++){
						//copy the information to the new table
						for(int i=0; i<PeakArrayCols; i++){
							ProjectionArray[peakRow][i] = PeaksFileArray[peakRow][i];
						}
						
						
						
						int index = Integer.parseInt(ProjectionArray[peakRow][ExperimentColPeaks]);
						double normL = Double.parseDouble(PeaksFileArray[peakRow][IndexOfProj_X]);
						
						

						
						ProjectionArray[peakRow][NEWIndexOfProj_X] =  Double.toString((normL*CellLength.get(index-1)));
						
						
						
						
						ProjectionArray[peakRow][NEWIndexOfProj_Y] = PeaksFileArray[peakRow][IndexOfProj_Y];
						
						
						//get the average 
						
						
						
						
						
						//get Cell Length
						double currentCellLength = Double.parseDouble(PeaksFileArray[peakRow][LengthCol]);
						
						if (currentCellLength>=ExperimentFinalCellLength){
							ExperimentFinalCellLength = currentCellLength;
							
						}
						
						double currentCellWidth = Double.parseDouble(PeaksFileArray[peakRow][LengthCol]);
						if (currentCellWidth < 0){
							currentCellWidth = -currentCellWidth;
						}
						
						if (currentCellWidth>=ExperimentFinalCellWidth){
							ExperimentFinalCellWidth = currentCellWidth;
							
						}
						
							
					}
					
					
					
					
					
					
					
					
					
				}
				
				SaveFile(ProjectionArray, "testImageProjection", fControlFile);
			
				
				
			}
			else{
				
				//If already have - Start ploting location maps
				// One location map per experiment
				String PeaksFile =  IJ.getFilePath("Load: Peaks File");
				if (PeaksFile==null) return;	
				ProjectionArray = tools.iSBOps.getCSVContent2(PeaksFile);
				
			}
				
				
				//get number of experiments
				//Its dubm.. but the code is already to confused... so.. just re-do some loops and forgtet about eficience for now
				
				int ExperimentCol = iSBOps.getCol("Experiment", ProjectionArray);
				System.out.println(ExperimentCol);
				System.out.println(ProjectionArray.length);
				
				int NExperiments = Integer.parseInt(ProjectionArray[ProjectionArray.length-1][ExperimentCol]);
				int ProjLengths = ProjectionArray.length;
				//get longer cell of all 
				int lengthCol = iSBOps.getCol("length", ProjectionArray);
				
				//getting Colindexes 
				int projxCol = iSBOps.getCol("proj_x", ProjectionArray);
				int projyCol = iSBOps.getCol("proj_y", ProjectionArray);
				int heigthCol = iSBOps.getCol("height", ProjectionArray);
				int LnormCol = iSBOps.getCol("L_normalized", ProjectionArray);
				
				int MinLength= 0;
				int  MaxLength = 0;
				int Maxheigth  = 13; // this is just to create a new image. Does not need to be precise.
				
				
				for (int i = 1; i<ProjLengths; i++){
					double length = Double.parseDouble(ProjectionArray[i][projxCol]);
					double norm = Double.parseDouble(ProjectionArray[i][LnormCol]);
					double ratio = length/norm;
					
					if (ratio > MaxLength){
						MaxLength =(int) Math.round(length);
					}
					if(ratio < MinLength){
						MinLength =(int) Math.round(length);
					}
					
				}
				
				System.out.println(NExperiments);
				//For each experiment create one locationMap// Create a stack!
				
				ImagePlus LocationMapNorm = IJ.createImage("LocationMap514", "32-bit", MaxLength*2, Maxheigth*2, NExperiments); 
				
				double sigma = 0.80;
				
				double pixelSize = 0.1;
				
				int xMin = -MinLength;

				int yMin = -Maxheigth;

				int xMax = MaxLength;

				int yMax = Maxheigth;

				int width = (xMax - xMin)*20;

				int height = 20*(Maxheigth + 10);
				
				System.out.println(height);
				System.out.println(width);
				
				ImagePlus LocationMap = IJ.createImage("LocationMap", "32-bit", width, height, NExperiments); 

				
				
				ImageStack LocationStacks = LocationMap.getStack();
				ImageStack LocationStacksNorm = LocationMapNorm.getStack();	
				
				ImageProcessor ip;
				//loop all lines experiments
				
				for (int row = 1; row< ProjLengths; row ++){
					//filtering dimm spots
					//double intensity = Double.parseDouble(ProjectionArray[row][heigthCol]);
				//	if (intensity < 2000){
				//		continue;
				//	}
					
					
					double x_coord = Double.parseDouble(ProjectionArray[row][projxCol]);
					double y_coord = Double.parseDouble(ProjectionArray[row][projyCol]);
					int experiment = Integer.parseInt(ProjectionArray[row][ExperimentCol]);
					
					
					double xx  = x_coord - xMin;

					double yy  = y_coord - yMin;

					yy /= pixelSize;

					xx /= pixelSize;
					
					int x = (int) Math.round(xx)+ 100;
					int y = (int) Math.round(yy) + 100;
					
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


