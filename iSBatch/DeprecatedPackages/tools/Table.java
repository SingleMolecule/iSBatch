package tools;

import ij.measure.ResultsTable;

import java.io.File;
import java.io.IOException;

/**
 * This implements an table, as an extenstion of the ResultsTable.class in imageJ.
 * 
 * 
 * @author Victor
 *
 */
// Import packages




//Define Variables




class Table {

	static ResultsTable table;
	
	private void loadtable(String path){
		try {
			table = ResultsTable.open(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void loadtable(File file){
		String path = file.getAbsolutePath();
		loadtable(path);
	}
	
	
	
	
	
}
