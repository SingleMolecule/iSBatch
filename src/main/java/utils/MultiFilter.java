package utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import model.Node;


import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;

public abstract class MultiFilter {
	static RoiManager cellRoiManager;
	ResultsTable table;
	CSVReader reader;
	
	
	int CELL_ROI_INDEX;
	public static File getTableRowsInsideCells(Node currentNode,
			String path) throws IOException {
		//load cell Rois
		cellRoiManager = new RoiManager(true);
		cellRoiManager.runCommand("Open", currentNode.getCellROIPath());
		
		CSVReader reader;
		reader = new CSVReader(new FileReader(path));
		String[] nextLine = null;
		
		
		
		String propertyName = currentNode.getChannel() + ".cellPeaks.csv";
		String savedPath = currentNode.getOutputFolder() + File.separator + propertyName;
		CSVWriter writer = new CSVWriter(new FileWriter(savedPath));
		File file = new File(savedPath);
		currentNode.getProperties().put(propertyName, savedPath);
		
		//get header
		nextLine = reader.readNext();
		int colX = getcolNumber(nextLine, "x");
		int colY = getcolNumber(nextLine, "y");
		if(colX==0 && colY==0){
			System.out.println("X and Y not found");
		}
		
		//write header with extras
				String[] ouputHeader = getOutputHeader(nextLine);
				writer.writeNext(ouputHeader);
				
				System.out.println(ouputHeader[4]);
				int cellCollum = getcolNumber(ouputHeader, "cellRoi");
				
				while ((nextLine = reader.readNext()) != null) {
//					System.out.println(nextLine[0] + "| "  + nextLine[colX] + " " + nextLine[colY]);
					
					Hashtable<String, Roi> tableRoi = (Hashtable<String, Roi>) cellRoiManager.getROIs();
					for (String label : tableRoi.keySet()) {
						Roi roi = tableRoi.get(label);
						int x =  (int) Math.round(Double.parseDouble(nextLine[colX]));
						int y =  (int) Math.round(Double.parseDouble(nextLine[colY]));
						if(roi.contains(x, y)){
							String[] rowOutput = addCols(nextLine,1);
							rowOutput[cellCollum] = label;
							writer.writeNext(rowOutput);
						}
					}
					
				}
		writer.close();
				
		return file;
	}

	public static void main(String[] args) throws IOException {
		ResultsTable table = new ResultsTable();
		File f = new File("D:\\ImageTest\\Results2.csv");
		if (!f.exists()) {
			System.out.println("Fix path");
		}
		System.out.println(f.getAbsolutePath());
		CSVReader reader = new CSVReader(new FileReader(f));
		String[] nextLine = null;
		
		CSVWriter writer = new CSVWriter(new FileWriter("D:\\ImageTest\\Results2Out.csv"));
		
		//get header
		nextLine = reader.readNext();
		int colX = getcolNumber(nextLine, "x");
		int colY = getcolNumber(nextLine, "y");
		if(colX==0 && colY==0){
			System.out.println("X and Y not found");
		}
		
		//write header with extras
		String[] ouputHeader = getOutputHeader(nextLine);
		writer.writeNext(ouputHeader);
		
		System.out.println(ouputHeader[4]);
		RoiManager cellRoiManager = new RoiManager(true);
		cellRoiManager.runCommand("Open", "D:\\ImageTest\\cellRoi.zip");
		int cellCollum = getcolNumber(ouputHeader, "cellRoi");
		
		while ((nextLine = reader.readNext()) != null) {
//			System.out.println(nextLine[0] + "| "  + nextLine[colX] + " " + nextLine[colY]);
			
			Hashtable<String, Roi> tableRoi = (Hashtable<String, Roi>) cellRoiManager.getROIs();
			for (String label : tableRoi.keySet()) {
				Roi roi = tableRoi.get(label);
				int x =  (int) Math.round(Double.parseDouble(nextLine[colX]));
				int y =  (int) Math.round(Double.parseDouble(nextLine[colY]));
				if(roi.contains(x, y)){
					String[] rowOutput = addCols(nextLine,1);
					rowOutput[cellCollum] = label;
					writer.writeNext(rowOutput);
				}
			}
			
		}
		writer.close();
		
	
		
		

	}
	
	public static String[] addCols(String[] array, int n){
		String[] outpStrings = new String[array.length+n];
		outpStrings = copyArray(outpStrings, array);
		return outpStrings;
	}
	
	
	public static String[] copyArray(String[] newArray, String[] oldArray){
		int lenght = oldArray.length;
		if(newArray.length<=lenght){
			return oldArray;
		}
		for(int i =0; i< lenght; i++){
			newArray[i] = oldArray[i];
		}
		
		return newArray;
	}

	private static String[] getOutputHeader(String[] nextLine) {
		String[] output = addCols(nextLine,3);
		// Add : 
		
		output[nextLine.length] = "cellRoi";
		
		return output;
	}

	private static int getcolNumber(String[] header, String colName) {
		int col = 0;
		for(int i=0; i<header.length; i++){
			if(colName.equalsIgnoreCase(header[i])){
				return i;
			}
		};
		return col;
	}

	public static void getTableRowsInsideCells(Node currentNode, String path,
			File f) throws NumberFormatException, IOException {
		//load cell Rois
				cellRoiManager = new RoiManager(true);
				cellRoiManager.runCommand("Open", currentNode.getCellROIPath());
				
				CSVReader reader;
				reader = new CSVReader(new FileReader(path));
				String[] nextLine = null;
				
				
				
				String propertyName = currentNode.getChannel() + ".cellPeaks.csv";
				String savedPath = f.getAbsolutePath() + File.separator + propertyName;
				CSVWriter writer = new CSVWriter(new FileWriter(savedPath));
				File file = new File(savedPath);
				currentNode.getProperties().put(propertyName, savedPath);
				
				//get header
				nextLine = reader.readNext();
				int colX = getcolNumber(nextLine, "x");
				int colY = getcolNumber(nextLine, "y");
				if(colX==0 && colY==0){
					System.out.println("X and Y not found");
				}
				
				//write header with extras
						String[] ouputHeader = getOutputHeader(nextLine);
						writer.writeNext(ouputHeader);
						
						System.out.println(ouputHeader[4]);
						int cellCollum = getcolNumber(ouputHeader, "cellRoi");
						
						while ((nextLine = reader.readNext()) != null) {
//							System.out.println(nextLine[0] + "| "  + nextLine[colX] + " " + nextLine[colY]);
							
							Hashtable<String, Roi> tableRoi = (Hashtable<String, Roi>) cellRoiManager.getROIs();
							for (String label : tableRoi.keySet()) {
								Roi roi = tableRoi.get(label);
								int x =  (int) Math.round(Double.parseDouble(nextLine[colX]));
								int y =  (int) Math.round(Double.parseDouble(nextLine[colY]));
								if(roi.contains(x, y)){
									String[] rowOutput = addCols(nextLine,1);
									rowOutput[cellCollum] = label;
									writer.writeNext(rowOutput);
								}
							}
							
						}
				writer.close();
						
		
	}

}
