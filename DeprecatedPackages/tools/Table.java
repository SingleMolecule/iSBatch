/*
 * 
 */
package tools;

import ij.measure.ResultsTable;

import java.io.File;
import java.io.IOException;

// TODO: Auto-generated Javadoc
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

	/** The table. */
	static ResultsTable table;
	
	/**
	 * Loadtable.
	 *
	 * @param path the path
	 */
	private void loadtable(String path){
		try {
			table = ResultsTable.open(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loadtable.
	 *
	 * @param file the file
	 */
	@SuppressWarnings("unused")
	private void loadtable(File file){
		String path = file.getAbsolutePath();
		loadtable(path);
	}
	
	
	
	
	
}
