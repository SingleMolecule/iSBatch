package utils;

import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;

public abstract class IJUtils {

	public static void emptyAll() {
		ResultsTable table = Analyzer.getResultsTable();
		table.reset();

		RoiManager manager = RoiManager.getInstance();
		if(manager!=null){
			manager.removeAll();
		}
	}
	
	public static void clearResultsTable()
	{
		ResultsTable table = Analyzer.getResultsTable();
		table.reset();
	}
	
	public static void clearROIManager()
	{
		RoiManager manager = RoiManager.getInstance();
		manager.removeAll();
	}
	
	
	
}
