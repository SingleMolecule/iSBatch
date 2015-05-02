/*
 * 
 */
package table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import analysis.Plot;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultsTablePlotter.
 */
public class ResultsTablePlotter implements PlugIn, ActionListener {

	/** The plot types. */
	private String[] plotTypes = {"Line Plot", "Scatter Plot", "Histogram"};
	
	/** The x column. */
	private String xColumn;
	
	/** The y column. */
	private String yColumn;
	
	/** The group by column. */
	private String groupByColumn;
	
	/** The plot type. */
	private int plotType;
	
	/** The bins. */
	private int bins;
	
	/** The frame. */
	private JFrame frame = new JFrame("Curve Plotter");
	
	/** The plot. */
	private Plot plot = new Plot();
	
	/** The previous button. */
	private JButton previousButton = new JButton("Previous");
	
	/** The next button. */
	private JButton nextButton = new JButton("Next");
	
	/** The table. */
	private ResultsTable table;
	
	/** The group offset. */
	private ArrayList<Integer> groupOffset = new ArrayList<Integer>();
	
	/** The group. */
	private int group = 0;
	
	/**
	 * Instantiates a new results table plotter.
	 */
	public ResultsTablePlotter() {
		table = Analyzer.getResultsTable();
	}
	
	/**
	 * Instantiates a new results table plotter.
	 *
	 * @param table the table
	 */
	public ResultsTablePlotter(ResultsTable table) {
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
			IJ.error("No results table!");
		
		if (table.getCounter() == 0)
			IJ.error("Empty results table!");
		
		String[] columns = table.getColumnHeadings().split(",|\\t+");
		
		GenericDialog dialog = new GenericDialog("Curve Plotter");
		dialog.addChoice("x_column", columns, columns[0]);
		dialog.addChoice("y_column", columns, columns[0]);
		dialog.addChoice("group_by_column", columns, columns[0]);
		dialog.addChoice("plot_type", plotTypes, plotTypes[0]);
		dialog.addNumericField("bins", Math.sqrt(table.getCounter()), 0);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		xColumn = dialog.getNextChoice();
		yColumn = dialog.getNextChoice();
		groupByColumn = dialog.getNextChoice();
		plotType = (int)dialog.getNextChoiceIndex();
		bins = (int)dialog.getNextNumber();

		// sort columns
		ResultsTableSorter.sort(table, true, xColumn, groupByColumn);
		
		// determine on which row each group starts and ends
		groupOffset.add(0);
		
		if (table.getColumnIndex(groupByColumn) != ResultsTable.COLUMN_NOT_FOUND) {
			double previousValue = table.getValue(groupByColumn, 0);
			
			for (int row = 1; row < table.getCounter(); row++) {
				
				double value = table.getValue(groupByColumn, row);
				
				if (value != previousValue)
					groupOffset.add(row);
				
				previousValue = value;
			}
			
		}
		
		groupOffset.add(table.getCounter());
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(previousButton);
		buttonPanel.add(nextButton);

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(plot, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		previousButton.addActionListener(this);
		nextButton.addActionListener(this);
		
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		setPlot();
	}

	/**
	 * Sets the plot.
	 */
	private void setPlot() {
		
		int rowFrom = groupOffset.get(group);
		int rowTo = groupOffset.get(group + 1);
		
		int n = rowTo - rowFrom;
		double[] x = new double[n];
		double[] y = new double[n];
		
		for (int i = 0; i < n; i++) {
			if (plotType != 2)
				x[i] = table.getValue(xColumn, rowFrom + i);
			y[i] = table.getValue(yColumn, rowFrom + i);
		}
		
		// clear plot
		plot.clear();
		
		switch (plotType) {
		case 0:	// line plot
			plot.addLinePlot(x, y, Color.BLACK, 1.0f);
			break;
			
		case 1:	// scatter plot
			plot.addScatterPlot(x, y, Color.BLACK, 1.0f);
			break;
			
		case 2:	// histogram
			plot.addHistogram(y, bins, Color.BLACK, 1.0f);
			break;
			
		}
		
		plot.repaint();
	}
	
	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == previousButton) {
			
			if (group > 0)
				group--;
			
			setPlot();
		}
		else if (e.getSource() == nextButton) {
			
			if (group < groupOffset.size() - 2)
				group++;
			
			setPlot();
		}
		
	}
}
