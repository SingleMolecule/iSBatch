/*
 * 
 */
package analysis;

import java.awt.AWTEvent;
import java.awt.TextField;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import ij.IJ;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultsFitter.
 */
public class ResultsFitter implements PlugIn, DialogListener {

	/** The functions. */
	private String[] functions = {
			"line",
			"2nd degree polynomial",
			"3nd degree polynomial",
			"4nd degree polynomial",
			"5nd degree polynomial",
			"6nd degree polynomial",
			"7nd degree polynomial",
			"8nd degree polynomial",
			"exponential",
			"gaussian"
	};
	
	/** The function definitions. */
	private String[] functionDefinitions = {
			"y = a + b * x",
			"y = a + x * (b + x * c)",
			"y = a + x * (b + x * (c + x * d))",
			"y = a + x * (b + x * (c + x * (d + x * e)))",
			"y = a + x * (b + x * (c + x * (d + x * (e + x * f))))",
			"y = a + x * (b + x * (c + x * (d + x * (e + x * (f + x * g)))))",
			"y = a + x * (b + x * (c + x * (d + x * (e + x * (f + x * (g + x * h))))))",
			"y = a + x * (b + x * (c + x * (d + x * (e + x * (f + x * (g + x * (h + x * i)))))))",
			"y = a * Math.exp(b * x)",
			"y = a * Math.exp(-((x - b) * (x - b)) / (2 * c * c))"
	};
	
	/** The intial parameter values. */
	private String[] intialParameterValues = {
		"1,1",
		"1,1,1",
		"1,1,1,1",
		"1,1,1,1,1",
		"1,1,1,1,1,1",
		"1,1,1,1,1,1,1",
		"1,1,1,1,1,1,1,1",
		"1,1,1,1,1,1,1,1,1",
		"1,1",
		"1,1,1",
	};
	
	/** The table. */
	private ResultsTable table;
	
	/** The function. */
	private int function = 0;
	
	/** The x column. */
	private String xColumn = "";
	
	/** The y column. */
	private String yColumn = "";
	
	/** The first. */
	private int first = 0;
	
	/** The last. */
	private int last = 0;
	
	/** The function definition. */
	private String functionDefinition;
	
	/** The initial. */
	private String initial = "";
	
	/** The p. */
	private double[] p;
	
	/** The engine. */
	private ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	@Override
	public void run(String arg0) {

		table = Analyzer.getResultsTable();
		
		if (table == null)
			IJ.error("No results table");
		
		String[] columns = table.getColumnHeadings().split("(,\\s*)|\\t+");
		
		GenericDialog dialog = new GenericDialog("Curve Fitter");
		dialog.addChoice("x_column", columns, xColumn);
		dialog.addChoice("y_column", columns, yColumn);
		dialog.addNumericField("first row", 1, 0);
		dialog.addNumericField("last row", table.getCounter(), 0);
		dialog.addChoice("function", functions, functions[function]);
		dialog.addStringField("definition", functionDefinitions[function]);
		dialog.addStringField("initial_parameters", intialParameterValues[function]);
		dialog.addDialogListener(this);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		int n = (last - first) + 1;
		double[][] xs = new double[n][1];
		double[] ys = new double[n];
		
		for (int i = 0; i < n; i++) {
			int row = (first + i) - 1;
			
			if (table.getColumnIndex(xColumn) != ResultsTable.COLUMN_NOT_FOUND)
				xs[i][0] = table.getValue(xColumn, row);
			else
				xs[i][1] = i;
			
			ys[i] = table.getValue(yColumn, row);
		}


		LevenbergMarquardt lm = new LevenbergMarquardt() {
			
			@Override
			public double getValue(double[] x, double[] p, double[] dyda) {
				
				double result = 0;
				
				try {
					
					String script = "x = " + x[0] + ";";
					double delta = 1e-6;
					
					for (int i = 0; i < p.length; i++) {
						script += (char)('a' + i) + " = " + p[i] + ";"; 
					}
					script += "result = " + functionDefinition + ";";
					
					for (int i = 0; i < p.length; i++) {
						script += (char)('a' + i) + " = " + (p[i] + delta) + ";";
						script += "dyda" + i + " = " + functionDefinition + ";";
						script += (char)('a' + i) + " = " + (p[i] - delta) + ";";
						script += "dyda" + i + " -= " + functionDefinition + ";";
						script += "dyda" + i + " /= " + (delta * 2) + ";";
						script += (char)('a' + i) + " = " + p[i] + ";";
					}
					
					engine.eval(script);
					
					result = Double.parseDouble(engine.get("result").toString());
					
					for (int i = 0; i < p.length; i++)
						dyda[i] = Double.parseDouble(engine.get("dyda" + i).toString());
					
				} catch (ScriptException e) {
					e.printStackTrace();
					IJ.log(e.getMessage());
				}
				
				return result;
			}
		};
		
		
		double[] e = new double[p.length];
		lm.solve(xs, ys, null, xs.length, p, null, e, 0.001);
		
		IJ.log("function : " + functionDefinition);
		IJ.log("initial parameters : " + initial);
		IJ.log("fitter parameters : ");
		
		for (int i = 0; i < p.length; i++)
			IJ.log((char)('a' + i) + " = " + p[i] + "(+- " + e[i] + ")");
		
		IJ.log("sum of squares = " + lm.chiSq);
	}
	
	/**
	 * Dialog item changed.
	 *
	 * @param dialog the dialog
	 * @param e the e
	 * @return true, if successful
	 */
	@Override
	public boolean dialogItemChanged(GenericDialog dialog, AWTEvent e) {
		
		xColumn = dialog.getNextChoice();
		yColumn = dialog.getNextChoice();
		first = (int) dialog.getNextNumber();
		last = (int) dialog.getNextNumber();
		int functionNew = dialog.getNextChoiceIndex();
		functionDefinition = dialog.getNextString();
		initial = dialog.getNextString();
		
		if (functionNew != function) {
			function = functionNew;
			
			Vector<?> stringFields = dialog.getStringFields();
			((TextField)stringFields.get(0)).setText(functionDefinitions[function]);
			((TextField)stringFields.get(1)).setText(intialParameterValues[function]);
			
			functionDefinition = functionDefinitions[function];
		}
		
		String[] values = initial.split(",");
		
		try {
			p = new double[values.length];
			
			for (int i = 0; i < values.length; i++)
				p[i] = Double.parseDouble(values[i]);
		}
		catch (NumberFormatException ex) {
			return false;
		}
		
		return first < last && table.getColumnIndex(yColumn) != ResultsTable.COLUMN_NOT_FOUND && p.length > 0;
	}
}
