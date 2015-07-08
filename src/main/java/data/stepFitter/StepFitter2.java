package data.stepFitter;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Plot;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.MaximumFinder;

/**
 * @author C.M. Punter
 *
 */
public class StepFitter2 implements PlugIn, ActionListener {

	
	private GenericDialog dialog; 
	private String[] headings = new String[]{"no column selected"};
	
	@Override
	public void run(String arg0) {
		
		
		// open file
		
		
		Button button = new Button("Choose file");
		button.addActionListener(this);
		Panel panel = new Panel();
		panel.add(button);
		
		dialog = new GenericDialog("Step Fitter");
		dialog.addStringField("Input file (results table)", "", 20);
		dialog.addPanel(panel);
		dialog.addChoice("Time column (x)", headings, headings[0]);
		dialog.addChoice("Signal column (y)", headings, headings[0]);
		dialog.addCheckbox("No time column", false);
		dialog.addNumericField("bins", 100, 0);
		dialog.addMessage("The tolerance is is used to find peaks in the signal histogram.\nIt is the minimum difference between peaks and is thus dependent on the number of bins.\nThe default tolerance is set to the standard deviation of the histogram.");
		dialog.addNumericField("Tolerance (minimum difference)", 100, 2);
		
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		// get all the parameters
		String path = dialog.getNextString();
		String timeColumn = dialog.getNextChoice();
		String signalColumn = dialog.getNextChoice();
		boolean noTimeColumn = dialog.getNextBoolean();
		int bins = (int)dialog.getNextNumber();
		double tolerance = dialog.getNextNumber();
		
		
		// create histogram of all the values
		// and find maximums in the histogram
		
		try {
			ResultsTable table = ResultsTable.open(path);
			float[] values = table.getColumn(table.getColumnIndex(signalColumn));
			
			int steps = (int)Math.sqrt(values.length);
			double[] x = getHistogramX(values, steps);
			double[] y = getHistogram(values, (int)Math.sqrt(values.length));
			
			
			double t = getStdDev(y, getMean(y));
			
			int[] maxima = MaximumFinder.findMaxima(y, t, true);
			
			double[] mx = new double[maxima.length];
			double[] my = new double[maxima.length];
			
			for (int i = 0; i < maxima.length; i++) {
				mx[i] = x[maxima[i]];
				my[i] = y[maxima[i]];
			}
			
			Plot plot = new Plot("Histogram signal", "I", "#", x, y);
			
			plot.setColor(Color.RED);
			for (int i = 0; i < maxima.length; i++) {
				plot.drawLine(mx[i], my[i], mx[i], 0);
			}
			plot.setColor(Color.BLACK);
			plot.show();
			
			
		}
		catch (IOException e) {
			IJ.showMessage(e.getMessage());
		}
		
		
	}
	
	public double getMax(double[] values) {
		double max = values[0];
		for (double value: values)
			max = Math.max(max, value);
		return max;
	}
	
	public double getMin(double[] values) {
		double min = values[0];
		for (double value: values)
			min = Math.min(min, value);
		return min;
	}
	
	public double getMean(double[] values) {
		double mean = 0;
		for (double value: values)
			mean += value;
		return mean / values.length;
	}
	
	public double getStdDev(double[] values, double mean) {
		double stdDev = 0;
		for (double value: values)
			stdDev += Math.pow(value - mean, 2);
		return Math.sqrt(stdDev / values.length);
	}
	
	public double getMax(float[] values) {
		double max = values[0];
		for (float value: values)
			max = Math.max(max, value);
		return max;
	}
	
	public double getMin(float[] values) {
		double min = values[0];
		for (float value: values)
			min = Math.min(min, value);
		return min;
	}
	
	public double getMean(float[] values) {
		double mean = 0;
		for (float value: values)
			mean += value;
		return mean / values.length;
	}
	
	public double getStdDev(float[] values, double mean) {
		double stdDev = 0;
		for (float value: values)
			stdDev += Math.pow(value - mean, 2);
		return Math.sqrt(stdDev / values.length);
	}
	
	public double getRange(float[] values) {
		return getMax(values) - getMin(values);
	}
	
	public double[] getHistogramX(float[] values, int steps) {
		double[] stepValues = new double[steps];
		double step = getRange(values) / steps;
		double min = getMin(values);
		
		for (int i = 0; i < steps; i++)
			stepValues[i] = min + step * i;
		
		return stepValues;
	}
	
	public double[] getHistogram(float[] values, int steps) {
	
		double[] bins = new double[steps];
		double step = getRange(values) / (steps - 1);
		double min = getMin(values);
		
		for (float value: values) {
			int bin = (int)((value - min) / step);
			bins[bin]++;
		}
		
		return bins;
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String path = IJ.getFilePath("Choose results table file");
		((TextField)dialog.getStringFields().get(0)).setText(path);
		
		try {
			ResultsTable table = ResultsTable.open(path);
			headings = table.getHeadings();
			
			((Choice)dialog.getChoices().get(0)).removeAll();
			((Choice)dialog.getChoices().get(1)).removeAll();
			
			for (int i = 0; i < headings.length; i++) {
				((Choice)dialog.getChoices().get(0)).add(headings[i]);
				((Choice)dialog.getChoices().get(1)).add(headings[i]);
			}
			
			((Choice)dialog.getChoices().get(0)).select(0);
			((Choice)dialog.getChoices().get(1)).select(0);
			
			// determine tolerance (standard deviation * 2)
			float[] values = table.getColumn(0);
			double[] y = getHistogram(values, (int)Math.sqrt(values.length));
			double t = getStdDev(y, getMean(y));
			
			((TextField)dialog.getNumericFields().get(0)).setText(Integer.toString((int)Math.sqrt(values.length)));
			((TextField)dialog.getNumericFields().get(1)).setText(Double.toString(t));
			
		} catch (IOException e1) {
			IJ.showMessage(e1.getMessage());
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StepFitter2().run("");
	}

}
