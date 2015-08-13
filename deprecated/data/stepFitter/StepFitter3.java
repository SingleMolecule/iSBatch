package data.stepFitter;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;

public class StepFitter3 implements PlugIn, ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(String arg0) {

		ResultsTable table = Analyzer.getResultsTable();
		
		if (table == null) {
			IJ.showMessage("PlugIn requires a results table");
			return;
		}
		
		String[] headings = table.getHeadings();
		
		GenericDialog dialog = new GenericDialog("Step Fitter");
		dialog.addChoice("Column_x", headings, headings[0]);
		dialog.addChoice("Column_y", headings, headings[0]);
		dialog.addCheckbox("No_x_column", true);
		dialog.addNumericField("Number_of_steps (0=auto detect)", 0, 0);
		dialog.addCheckbox("Plot_steps", true);
		dialog.showDialog();
		
		String xColumn = dialog.getNextChoice();
		String yColumn = dialog.getNextChoice();
		boolean noXColumn = dialog.getNextBoolean();
		int steps = (int)dialog.getNextNumber();
		boolean plotSteps = dialog.getNextBoolean();
		
		
		double[] x = new double[table.getCounter()];
		double[] y = new double[table.getCounter()];
		
		for (int row = 0; row < table.getCounter(); row++) {
			x[row] = noXColumn ? row : table.getValue(xColumn, row);
			y[row] = table.getValue(yColumn, row);
		}
		
		StepFitter fitter = new StepFitter(y, 1);
		
		double previousRatio = 0;
		double ratio = Double.MAX_VALUE;
		
		if (steps == 0) {
			
			do {
				steps++;
				fitter.addStep();
				previousRatio = ratio;
				ratio = fitter.getChiSquared() / fitter.getCounterChiSquared();
			
			} while (ratio < previousRatio);
			
		}
		else {
			for (int step = 0; step < steps; step++)
				fitter.addStep();
		}
		
		table.reset();
		
		
		double[] xSteps = fitter.getStepsX();
		double[] ySteps = fitter.getStepsX();
		double[] counterXSteps = fitter.getCounterStepsX();
		double[] counterYSteps = fitter.getCounterStepsY();
		
		for (int i = 0; i < xSteps.length; i += 2) {
			
			int from = (int)xSteps[i];
			int to = (int)xSteps[i + 1];
			
			if (to >= x.length)
				to = x.length - 1;
			
			table.incrementCounter();
			table.addValue("from", x[from]);
			table.addValue("to", x[to]);
			table.addValue("signal", ySteps[i]);

			if (i < counterXSteps.length) {
				int from1 = (int)counterXSteps[i];
				int to1 = (int)counterXSteps[i + 1];
				
				table.addValue("counter_from", x[from1]);
				table.addValue("counter_to", x[to1]);
				table.addValue("counter_to", counterYSteps[i]);
			}

		}
		
		table.updateResults();
		table.show("Results");
		
		
		if (plotSteps) {
			Plot plot = new Plot();
			plot.add(x, y, Color.BLACK, Plot.LINE, 1);

			
			double[] x1 = fitter.getCounterStepsX();
			double[] x2 = fitter.getStepsX();
			
			for (int i = 0; i < x1.length; i++)
				x1[i] = (x1[i] < x.length) ? x[(int)x1[i]] : x[x.length - 1];
			for (int i = 0; i < x2.length; i++)
				x2[i] = (x2[i] < x.length) ? x[(int)x2[i]] : x[x.length - 1];
				
			plot.add(x1, fitter.getCounterStepsY(), Color.BLUE, Plot.LINE, 2);
			plot.add(x2, fitter.getStepsY(), Color.RED, Plot.LINE, 2);
			
			JFrame frame = new JFrame("Step Fitter");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(500, 500);
			frame.getContentPane().add(plot);
			frame.setVisible(true);
		}
		
	}
	
	public static void main(String[] args) {
		
		try {
		
			
			ResultsTable table = ResultsTable.open("C:\\Users\\p262597\\Desktop\\flat_table.csv");
			table.show("Results");
			Analyzer.setResultsTable(table);
			
			new StepFitter3().run("");
		}
		catch(IOException e)
		{
			
		}
	}

}
