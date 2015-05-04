package operation.diffusion;


import ij.gui.PolygonRoi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import analysis.LevenbergMarquardt;
import analysis.Plot;
import filters.GenericFilter;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operation.cellIntensity.CellIntensityGUI;
import operations.Operation;
import table.ResultsTableSorter;

public class DiffusioOperation implements Operation {
	private DiffusionOperationGUI dialog;
	private String channel;
	private String customSearch;
	private ArrayList<String> tags;
	private int maxdistance;
	private int lookAhead = 1;
	private double maxStepSize = 8;
	private double minimumWidth = 0;
	private double minimumHeight = 0;
	private boolean showTrajectories = true;
	private double pixelSize;
	private double timeInterval;
	private int NumberOfPoints;
	private double fitUntil;

	private boolean doAverage;
	
	
	
	public DiffusioOperation(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	public void run(Node node) {
		// First, get a list of all files to execute
		String extention = null;
		// Run Peak Finder

		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, tags, extention, customSearch));
		// Generate Averages
		for (Node currentNode : filenodes) {
			System.out.println(currentNode.getFieldOfViewName());
			if (!node.getCellROIPath().isEmpty()) {
				// Make Output
				File f = new File(currentNode.getOutputFolder() + File.separator
						+ "514_flat.tif_PeakTable.csv");
				if(f.exists()){
					track(currentNode);
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	private void track(Node currentNode, boolean showRawValues) {
		// Open the results Table with peaks
		// Just peaks inside cells

		ResultsTable table = new ResultsTable();
		
		
			try {
				table = ResultsTable.open(currentNode.getOutputFolder() + File.separator
						+ "514_flat.tiftracks.csv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			ResultsTable rawTable = new ResultsTable();
			
			
		// Table loaded. Just to solve the proble
			// determine which rows belong to which trajectory
			ArrayList<Integer> offsets = new ArrayList<Integer>();
			offsets.add(0);
			
			for (int row = 1; row < table.getCounter(); row++) {
				int s0 = (int)table.getValue("trajectory", row - 1);
				int s1 = (int)table.getValue("trajectory", row);
				
				if (s0 != s1)
					offsets.add(row);
			}
			
			offsets.add(table.getCounter());
			
			ArrayList<Integer> trajectories = new ArrayList<Integer>();
			ArrayList<Integer> deltat = new ArrayList<Integer>();
			ArrayList<Double> displacementsSq = new ArrayList<Double>();
			
			for (int i = 0; i < offsets.size() - 1; i++) {
				
				int from = offsets.get(i);
				int to = offsets.get(i + 1);
				int trajectory = (int)table.getValue("trajectory", from);
				
				if (trajectory >= 0) {
					for (int row1 = from; row1 < to; row1++) {
						
						int s1 = (int)table.getValue("slice", row1);
						double x1 = table.getValue("x", row1);
						double y1 = table.getValue("y", row1);
								
						for (int row2 = row1 + 1; row2 < to; row2++) { 
							
							int s2 = (int)table.getValue("slice", row2);
							double x2 = table.getValue("x", row2);
							double y2 = table.getValue("y", row2);
							int dt = s2 - s1;
							double dx = x2 - x1;
							double dy = y2 - y1;
							double dsq = dx * dx + dy * dy; 
							
							trajectories.add(trajectory);
							deltat.add(dt);
							displacementsSq.add(dsq);
						}
					}
				}
			}
			
			
			// show raw values
			if (showRawValues) {
				ResultsTable rawTable2 = new ResultsTable();
				
				for (int i = 0; i < trajectories.size(); i++) {
					rawTable.incrementCounter();
					rawTable.addValue("trajectory", trajectories.get(i));
					rawTable.addValue("dt", deltat.get(i) * timeInterval);
					rawTable.addValue("sd", displacementsSq.get(i) * pixelSize * pixelSize);
				}
				
				rawTable.show("MSD - Raw Values");
			}
			
			
			// calculate mean and standard deviation per delta t
			int[] counts = new int[table.getCounter()];
			double[] mean = new double[table.getCounter()];
			double[] stdDev = new double[table.getCounter()];
			int n = 0;
			
			for (int i = 0; i < trajectories.size(); i++) {
				int dt = deltat.get(i);
				counts[dt]++;
				mean[dt] += displacementsSq.get(i);
				
				if (dt > n)
					n = dt;
			}

			for (int i = 0; i < n; i++)
				if (counts[i] > 0)
					mean[i] /= counts[i];
			
			// standard deviation
			for (int i = 0; i < trajectories.size(); i++) {
				int dt = deltat.get(i);
				double d = displacementsSq.get(i) - mean[dt];
				stdDev[dt] += d * d;
			}
			
			for (int i = 0; i < n; i++)
				if (counts[i] > 0)
					stdDev[i] = Math.sqrt(stdDev[i] / counts[i]);
			
			// set mean and standard deviation to the correct pixel size
			for (int i = 0; i < n; i++) {
				mean[i] *= pixelSize * pixelSize;
				stdDev[i] *= pixelSize * pixelSize;
			}
			
			
			// plot everything
			mean = Arrays.copyOf(mean, n);
			stdDev = Arrays.copyOf(stdDev, n);
			
			double[] dt = new double[n];
			double[][] dt2 = new double[n][1];
			
			for (int i = 0; i < n; i++) {
				dt[i] = i * timeInterval;
				dt2[i][0] = i * timeInterval;
			}
			
			// fit data
			LevenbergMarquardt lm = new LevenbergMarquardt() {
				
				@Override
				public double getValue(double[] x, double[] p, double[] dyda) {
					dyda[0] = 4 * x[0];
					return 4 * p[0] * x[0];
				}
			};
			
			double msd = mean[mean.length - 1];
			double t = dt[dt.length - 1];
			
			double[] p = new double[]{msd / (4 * t)};
			double[] e = new double[1];
			
			lm.solve(dt2, mean, stdDev, n, p, null, e, 0.001);
			
			double[] fx = new double[2];
			double[] fy = new double[2];
			
			fx[0] = 0;
			fy[0] = 0;
			
			fx[1] = t;
			fy[1] = 4 * p[0] * t;
			
			Plot plot = new Plot();
			plot.addErrorBars(dt, mean, stdDev, Color.GRAY, 1f);
			plot.addScatterPlot(dt, mean, Color.BLACK, 1f);
			plot.addLinePlot(fx, fy, Color.RED, 1f);
			plot.setCaption("D = " + p[0] + " µm^2/s  fitting error = " + e[0]);
			plot.setxAxisLabel("Time Step (s)");
			plot.setyAxisLabel("Mean Square Displacement (µm)");
			plot.showPlot("Mean Square Displacement");
			
			
			
			
			
		// TODO: Implement it properly

		
		try {

			table.saveAs(currentNode.getOutputFolder() + File.separator + currentNode.getName()
					+ "Diffusion.csv");
			rawTable.saveAs(currentNode.getOutputFolder() + File.separator + currentNode.getName()
					+ "DiffusionRaw.csv");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		table.reset();
		rawTable.reset();

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Diffusion";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new DiffusionOperationGUI(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();
		this.maxStepSize= dialog.getMaxStepSize();
		this.pixelSize = dialog.getPixelSize();
		this.timeInterval = dialog.getTimeInterval();
		this.NumberOfPoints = dialog.getNumberOfPoints();
		this.fitUntil = dialog.getFitUntil();
		this.doAverage  = dialog.getDoAverage();
		return true;
	}


	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visit(Root root) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Experiment experiment) {
		run(experiment);

	}

	@Override
	public void visit(Sample sample) {
		run(sample);

	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);

	}

	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

	}

}
