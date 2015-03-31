package table;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

public class ResultsGroupFilter implements PlugIn {

	@Override
	public void run(String arg0) {
		
		ResultsTable table = Analyzer.getResultsTable();
		
		if (table == null) {
			IJ.showMessage("no results table");
			return;
		}
		
		// determine columns
		String[] columns = table.getColumnHeadings().split(",|\\t+");
		int min = 0;
		int max = 100;
		
		GenericDialog dialog = new GenericDialog("filter on group length");
		dialog.addChoice("group_column", columns, columns[0]);
		dialog.addNumericField("min_length", min, 2);
		dialog.addNumericField("max_length", max, 2);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		String group = dialog.getNextChoice();
		min = (int)dialog.getNextNumber();
		max = (int)dialog.getNextNumber();
		
		if (table.getColumnIndex(group) == ResultsTable.COLUMN_NOT_FOUND) {
			IJ.showMessage("column does not exist");
			return;
		}
		
		// sort on group column
		ResultsTableSorter.sort(table, true, group);
		
		ResultsTableList rtl = new ResultsTableList(table);
		
		int to = table.getCounter();
		double currentGroup = table.getValue(group, to - 1);
		int count = 1;
		
		for (int row = table.getCounter() - 2; row >= 0; row--) {
			
			if (table.getValue(group, row) == currentGroup) {
				count++;
			}
			else {
				if (count < min || count > max)
					rtl.removeRange(row + 1, to);
				
				to = row + 1;
				currentGroup = table.getValue(group, row);
				count = 1;
			}
			
		}
		
		if (count < min || count > max)
			rtl.removeRange(0, to);
		
		table.show("Results");
	}

}
