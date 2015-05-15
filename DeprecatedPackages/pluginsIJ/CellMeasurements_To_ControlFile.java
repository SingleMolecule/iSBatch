/*
 * 
 */
package plugins_ij;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;


// TODO: Auto-generated Javadoc
/**
 * The Class CellMeasurements_To_ControlFile.
 */
public class CellMeasurements_To_ControlFile implements PlugIn {
	
	/** The table. */
	static ResultsTable table;
	
	/** The m table. */
	static ResultsTable mTable;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException{
		new CellMeasurements_To_ControlFile().run("");
		
		System.out.println("---------Done!-------------");
		
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	public void run(String arg0){
		
		
		
		String csvFilename =  IJ.getFilePath("Provide ControlFile.CSV");
		//String csvFilename = "D:\\20140604Results.csv";
		
		if (csvFilename==null) return;	
		//Load table
		String matLabTable =  IJ.getFilePath("CellMeasurements");
		//String matLabTable =  "D:\\cellIMeasurements.csv";
		if (matLabTable==null) return;	
		

		try {
			table = ResultsTable.open(csvFilename);
			mTable = ResultsTable.open(matLabTable);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//loadTable(mTable, matLabTable);
		//System.out.println(table.getCounter());
		
		for (int row =0; row<table.getCounter(); row++){
			int FieldOfView = (int) table.getValue("FolderIndex", row);
			
			List<Double> length = new ArrayList<Double>();
			int cellCount = 0;
			List<Double> area = new ArrayList<Double>();
			List<Double> width = new ArrayList<Double>();
			System.out.println( "Field of View: " + FieldOfView);
		
			//if(FieldOfView == 2){break;}
			
			for(int row2 = 0; row2< mTable.getCounter(); row2++){
				int MatLabFrame = (int) mTable.getValue("frame", row2);
				
				//System.out.println(MatLabFrame);
				if (FieldOfView == MatLabFrame){
					System.out.println("Frame: "+ MatLabFrame);
					int cellNumber = (int) mTable.getValue("cell", row2);
					//System.out.println("Same: " + MatLabFrame + "  Cell: " + cellNumber);
					length.add(mTable.getValue("length", row2));
					width.add(mTable.getValue("max width", row2));
					area.add(mTable.getValue("area", row2));
					//System.out.println(mTable.getValue("length", row2));
					
					cellCount++;
					
				}
				else{
					continue;
				}
				
			}
			
			//Get averages
			
			double averageL = calculateAverage(length);
			double averageA = calculateAverage(area);
			double averageW = calculateAverage(width);
			double stdL = getStandardDeviation(length, averageL);
			double stdA = getStandardDeviation(area, averageA);
			double stdW = getStandardDeviation(width, averageW);
			
			table.setValue("avgLength", row, averageL);
			table.setValue("sdtL", row, stdL);
			
			table.setValue("avgArea", row, averageA);
			table.setValue("sdtA", row, stdA);
			
			table.setValue("avgWidth", row, averageW);
			table.setValue("sdtW", row, stdW);
			
			table.setValue("TotalCells", row, cellCount);
			
			
			
			
		}
		
		//Add Group Average
		//Get how many groups we have
		//Just Bright Field... that what we have for now.
		int NumberOfGroups =0;
		for (int row = 0; row<table.getCounter(); row++){
			
			
			if(table.getStringValue("Channel", row).equalsIgnoreCase("BF")){
				int groupNumber =  (int) table.getValue("ExperimentIndex", row);
				if(groupNumber >=NumberOfGroups){
					NumberOfGroups = groupNumber;
				}
				
			}
			
		}
		//Create an array to store the values of average and number of cells
		double[][] GroupAverages = new double[NumberOfGroups+1][2];
		
		for (int group = 1; group<GroupAverages.length; group++){
			for (int row = 0; row<table.getCounter(); row++){
				
				if( table.getStringValue("Channel", row).equalsIgnoreCase("BF") &&  table.getValue("ExperimentIndex", row) == group){
					double average = table.getValue("avgLength", row);
					double NumberOfCells = table.getValue("TotalCells", row);
					
					GroupAverages[group][0]  += average * NumberOfCells ;
					GroupAverages[group][1]  += NumberOfCells;
				}
			}
		}
			
		for (int row = 0; row<table.getCounter(); row++){
			int group = (int) table.getValue("ExperimentIndex", row);
			
			double averageCells = GroupAverages[group][0] /GroupAverages[group][1];
			table.setValue("GroupAvg", row, averageCells);
			
		}
		
		try {
			table.saveAs(csvFilename);
			System.out.print("Saved");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	/**
	 * Gets the standard deviation.
	 *
	 * @param values the values
	 * @param mean the mean
	 * @return the standard deviation
	 */
	public static double getStandardDeviation(List<Double> values, double mean) {
        double deviation = 0.0;
        if ((values != null) && (values.size() > 1)) {
           
            for (double value : values) {
                double delta = value-mean;
                deviation += delta*delta;
            }
            deviation = Math.sqrt(deviation/values.size());
        }
        return deviation;
    }
	
	/**
	 * Calculate average.
	 *
	 * @param data the data
	 * @return the double
	 */
	private double calculateAverage(List<Double> data) {
		if(data != null){
			
		
	      double sum = 0;
	      for (int i=0; i< data.size(); i++) {
	    	  	double value = data.get(i);
	            sum += value;
	      }
	      if( data.size() == 0){
	    	  return 0;
	      }
	      else{
	    	  return sum / data.size();
	      }
		}
		return 0;
	
	}
	
	/**
	 * Load table.
	 *
	 * @param table the table
	 * @param csvFilename the csv filename
	 */
	private void loadTable(ResultsTable table, String csvFilename) {
		try {
			table = ResultsTable.open(csvFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	}
