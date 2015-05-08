/*
 * 
 */
package operations.tracking;

import ij.gui.PolygonRoi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import filters.GenericFilter;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;
import operations.cellIntensity.CellIntensityGUI;
import table.ResultsTableSorter;

// TODO: Auto-generated Javadoc
/**
 * The Class Tracking.
 */
public class Tracking implements Operation {
	
	/** The dialog. */
	private TrackingGUI dialog;
	
	/** The channel. */
	private String channel;
	
	/** The custom search. */
	private String customSearch;
	
	/** The tags. */
	private ArrayList<String> tags;
	
	/** The maxdistance. */
	private int maxdistance;
	
	/** The look ahead. */
	private int lookAhead = 1;
	
	/** The max step size. */
	private double maxStepSize = 8;
	
	/** The minimum width. */
	private double minimumWidth = 0;
	
	/** The minimum height. */
	private double minimumHeight = 0;
	
	/** The show trajectories. */
	private boolean showTrajectories = true;

	/**
	 * Instantiates a new tracking.
	 *
	 * @param treeModel the tree model
	 */
	public Tracking(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	/**
	 * Run.
	 *
	 * @param node the node
	 */
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

	/**
	 * Track.
	 *
	 * @param currentNode the current node
	 */
	@SuppressWarnings("static-access")
	private void track(Node currentNode) {
		// Open the results Table with peaks
		// Just peaks inside cells

		ResultsTable table = new ResultsTable();
		
		
			try {
				table = ResultsTable.open(currentNode.getOutputFolder() + File.separator
						+ "514_flat.tif_PeakTable.csv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		// Table loaded. Just to solve the proble
		// TODO: Implement it properly

		ResultsTableSorter.sort(table, true, "slice");

		// initialize trajectory column and set step size to 0
		for (int row = 0; row < table.getCounter(); row++) {
			table.setValue("trajectory", row, -1);
			table.setValue("step_size", row, 0);
		}

		// determine which rows belong to which slice
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		offsets.add(0);

		for (int row = 1; row < table.getCounter(); row++) {
			int s0 = (int) table.getValue("slice", row - 1);
			int s1 = (int) table.getValue("slice", row);

			if (s0 != s1)
				offsets.add(row);
		}

		offsets.add(table.getCounter());

		// find all trajectories
		int trajectoryCount = 0;

		for (int i = 0; i < offsets.size() - 1; i++) {

			int from = offsets.get(i);
			int to = offsets.get(i + 1);

			// make a list of all possible links
			ArrayList<double[]> links = new ArrayList<double[]>();

			for (int row1 = from; row1 < to; row1++) {

				int s1 = (int) table.getValue("slice", row1);
				double x1 = table.getValue("x", row1);
				double y1 = table.getValue("y", row1);

				for (int row2 = to; row2 < table.getCounter(); row2++) {

					int s2 = (int) table.getValue("slice", row2);
					double x2 = table.getValue("x", row2);
					double y2 = table.getValue("y", row2);

					if (s2 - s1 > lookAhead)
						break;

					double dx = x2 - x1;
					double dy = y2 - y1;
					double dsq = dx * dx + dy * dy;

					if (dsq < maxStepSize * maxStepSize)
						links.add(new double[] { s2 - s1, dsq, row1, row2, dx,
								dy });

				}

			}

			// sort all possible links on distance (or slice number)
			Collections.sort(links, new Comparator<double[]>() {

				@Override
				public int compare(double[] o1, double[] o2) {
					if (o1[0] != o2[0])
						return Double.compare(o1[0], o2[0]);

					return Double.compare(o1[1], o2[1]);
				}

			});

			// filter out all links that are not possible
			Set<Integer> linked = new HashSet<Integer>();

			for (double[] link : links) {

				int r1 = (int) link[2];
				int r2 = (int) link[3];
				int t1 = (int) table.getValue("trajectory", r1);

				if (!linked.contains(r1) && !linked.contains(r2)) {

					if (t1 == -1) {
						t1 = trajectoryCount++;
						table.setValue("trajectory", r1, t1);
					}

					table.setValue("trajectory", r2, t1);
					table.setValue("dx", r2, link[4]);
					table.setValue("dy", r2, link[5]);
					table.setValue("step_size", r2, Math.sqrt(link[1]));
					table.setValue("displacement_sq", r2, link[1]);

					linked.add(r1);
					linked.add(r2);
				}

			}

		}

		// sort on slice column
		ResultsTableSorter.sort(table, true, "slice", "trajectory");

		// filter out trajectories based on minimum/maximum bounding box
		int from = table.getCounter() - 1;
		double x = table.getValue("x", from);
		double y = table.getValue("y", from);
		Rectangle2D.Double boundingBox = new Rectangle2D.Double(x, y, 0, 0);

		for (int row = from - 1; row >= 0; row--) {

			x = table.getValue("x", row);
			y = table.getValue("y", row);

			if (table.getValue("trajectory", row) == table.getValue(
					"trajectory", row + 1)) {
				boundingBox.add(x, y);
			} else {

				if (boundingBox.getWidth() < minimumWidth
						|| boundingBox.getHeight() < minimumHeight) {

					// delete rows
					for (int r = from; r > row; r--)
						table.deleteRow(r);

				}

				from = row;
				boundingBox = new Rectangle2D.Double(x, y, 0, 0);
			}

		}

		// delete particles (rows) that don't belong to any trajectory
		for (int row = table.getCounter() - 1; row >= 0; row--) {
			if (table.getValue("trajectory", row) == -1)
				table.deleteRow(row);
		}
		showTrajectories = false;
		if (showTrajectories) {

			RoiManager roiManager = RoiManager.getInstance();

			if (roiManager == null)
				roiManager = new RoiManager();

			Polygon poly = new Polygon();
			x = table.getValue("x", 0);
			y = table.getValue("y", 0);
			poly.addPoint((int) x, (int) y);

			for (int row = 1; row < table.getCounter(); row++) {

				x = table.getValue("x", row);
				y = table.getValue("y", row);

				if (table.getValue("trajectory", row) == table.getValue(
						"trajectory", row - 1)) {
					poly.addPoint((int) x, (int) y);
				} else {
					roiManager
							.addRoi(new PolygonRoi(poly, PolygonRoi.POLYLINE));

					poly = new Polygon();
					poly.addPoint((int) x, (int) y);
				}
			}

			roiManager.addRoi(new PolygonRoi(poly, PolygonRoi.POLYLINE));
			roiManager.run("Show All");
			roiManager.runCommand("Save", currentNode.getOutputFolder() + File.separator + currentNode.getName()+ ".track.zip" );
			roiManager.removeAll();
		}

		// table.show("Results");
		try {

			table.saveAs(currentNode.getOutputFolder() + File.separator + currentNode.getName()
					+ "tracks.csv");
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		table.reset();

	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Tracking";
	}

	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new TrackingGUI(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();
		this.lookAhead = dialog.getLookAhead();
		this.maxStepSize = dialog.getMaxStepSize();

		return true;
	}

	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the created nodes.
	 *
	 * @return the created nodes
	 */
	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
		// TODO Auto-generated method stub

	}

	/**
	 * Visit.
	 *
	 * @param experiment the experiment
	 */
	@Override
	public void visit(Experiment experiment) {
		run(experiment);

	}

	/**
	 * Visit.
	 *
	 * @param sample the sample
	 */
	@Override
	public void visit(Sample sample) {
		run(sample);

	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	/**
	 * Visit.
	 *
	 * @param fileNode the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);

	}

	/**
	 * Visit.
	 *
	 * @param operationNode the operation node
	 */
	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

	}

}
