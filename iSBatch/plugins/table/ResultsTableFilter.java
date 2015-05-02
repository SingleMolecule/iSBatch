/*
 * 
 */
package table;

import java.awt.AWTEvent;
import java.awt.TextField;
import java.util.Vector;

import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultsTableFilter.
 */
public class ResultsTableFilter implements PlugIn, DialogListener {
	
	/** The table. */
	private ResultsTable table;
	
	/** The columns. */
	private String[] columns;
	
	/** The column. */
	private String column;
	
	/** The column index. */
	private int columnIndex = 0;
	
	/** The min. */
	private double min = 0;
	
	/** The max. */
	private double max = 1;
	
	/**
	 * Instantiates a new results table filter.
	 */
	public ResultsTableFilter() {
		table = Analyzer.getResultsTable();
	}
	
	/**
	 * Instantiates a new results table filter.
	 *
	 * @param table the table
	 */
	public ResultsTableFilter(ResultsTable table) {
		this.table = table;
	}
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	@Override
	public void run(String arg0) {
		
		if (table == null)
			return;
		
		columns = table.getColumnHeadings().split(",|\\t+");
		columnIndex =  table.getColumnIndex(columns[0]);
		
		GenericDialog dialog = new GenericDialog("results filter");
		dialog.addChoice("column", columns, columns[0]);
		dialog.addNumericField("min", min, 2);
		dialog.addNumericField("max", max, 2);
		dialog.addDialogListener(this);
		dialog.showDialog();
		
		if (dialog.wasCanceled() || columnIndex == ResultsTable.COLUMN_NOT_FOUND)
			return;
		
		for (int i = table.getCounter() - 1; i >= 0; i--) {
			double value = table.getValue(column, i);
			
			if (value < min || value > max || Double.isNaN(value))
				table.deleteRow(i);
		}
		
		table.updateResults();
	}

	/**
	 * Dialog item changed.
	 *
	 * @param dialog the dialog
	 * @param arg1 the arg1
	 * @return true, if successful
	 */
	@Override
	public boolean dialogItemChanged(GenericDialog dialog, AWTEvent arg1) {
		
		column = dialog.getNextChoice();
		int newColumnIndex = table.getColumnIndex(column);
		min = dialog.getNextNumber();
		max = dialog.getNextNumber();
		
		if (newColumnIndex != ResultsTable.COLUMN_NOT_FOUND && newColumnIndex != columnIndex) {
			
			double[] values = table.getColumnAsDoubles(newColumnIndex);
			min = max = values[0];
			
			for (double value: values) {
				if (value < min)
					min = value;
				else if (value > max)
					max = value;
			}
			
			@SuppressWarnings("unchecked")
			Vector<TextField> stringFields = (Vector<TextField>)dialog.getNumericFields();
			stringFields.get(0).setText(Double.toString(min));
			stringFields.get(1).setText(Double.toString(max));
			
			columnIndex = newColumnIndex;
		}
		
		return min <= max;
	}

}
