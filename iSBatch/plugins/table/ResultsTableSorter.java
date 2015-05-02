/*
 * 
 */
package table;

import java.util.Collections;
import java.util.Comparator;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultsTableSorter.
 */
public class ResultsTableSorter implements PlugIn {
	
	/** The table. */
	private ResultsTable table;
	
	/**
	 * Instantiates a new results table sorter.
	 */
	public ResultsTableSorter() {
		table = Analyzer.getResultsTable();
	}
	
	/**
	 * Instantiates a new results table sorter.
	 *
	 * @param table the table
	 */
	public ResultsTableSorter(ResultsTable table) {
		this.table = table;
	}
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	@Override
	public void run(String arg0) {
		
		if (table == null) {
			IJ.showMessage("No results table!");
			return;
		}
		
		String[] headings = table.getHeadings();
		String[] headings2 = new String[headings.length + 1];
		
		headings2[0] = "no grouping";
		
		for (int i = 0; i < headings.length; i++)
			headings2[i + 1] = headings[i];
		
		GenericDialog dialog = new GenericDialog("Results Table Sorter");
		dialog.addChoice("column", headings, headings[0]);
		dialog.addChoice("group", headings2, headings2[0]);
		dialog.addCheckbox("ascending", true);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		String column = dialog.getNextChoice();
		String group = dialog.getNextChoice();
		boolean ascending = dialog.getNextBoolean();
		
		sort(table, ascending, column, group);
		
		table.updateResults();
	}
	
	/**
	 * Sort.
	 *
	 * @param table the table
	 * @param ascending the ascending
	 * @param columns the columns
	 */
	public static void sort(ResultsTable table, final boolean ascending, String... columns) {
		
		ResultsTableList list = new ResultsTableList(table);
		
		final int[] columnIndexes = new int[columns.length];
		
		for (int i = 0; i < columns.length; i++)
			columnIndexes[i] = table.getColumnIndex(columns[i]);
		
		Collections.sort(list, new Comparator<double[]>() {
			
			@Override
			public int compare(double[] o1, double[] o2) {
				
				for (int columnIndex: columnIndexes) {
					
					if (columnIndex != ResultsTable.COLUMN_NOT_FOUND) {
						
						int groupDifference = Double.compare(o1[columnIndex], o2[columnIndex]); 
					
						if (groupDifference != 0)
							return ascending ? groupDifference : -groupDifference;
						
					}
					
				}
				
				return 0;
			}
			
		});
		
	}
	
	
	
	

}
