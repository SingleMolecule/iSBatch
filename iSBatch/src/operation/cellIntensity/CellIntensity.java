/*
 * 
 */
package operation.cellIntensity;

import filters.GenericFilter;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

// TODO: Auto-generated Javadoc
/**
 * The Class CellIntensity.
 */
public class CellIntensity implements Operation {
	
	/** The dialog. */
	private CellIntensityGUI dialog;
	
	/** The channel. */
	private String channel;
	
	/** The custom search. */
	private String customSearch;
	
	/** The tags. */
	private ArrayList<String> tags;

	/**
	 * Instantiates a new cell intensity.
	 *
	 * @param treeModel the tree model
	 */
	public CellIntensity(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	/** The traces. */
	ResultsTable traces = new ResultsTable();
	
	/** The traces corrected. */
	ResultsTable tracesCorrected = new ResultsTable();

	/**
	 * Run.
	 *
	 * @param node the node
	 */
	private void run(Node node) {

		// First, get a list of all files to execute
		String extention = null;
		// Run Peak Finder

		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, tags, extention, customSearch));
		// Generate Averages
		for (Node currentNode : filenodes) {
			System.out.println(currentNode.getFieldOfViewName());
			if (!node.getCellROIPath().isEmpty()) {
				//Make Output
				calculateCellIntensities(currentNode);
			}
		}
	}

	/**
	 * Calculate cell intensities.
	 *
	 * @param currentNode the current node
	 */
	private void calculateCellIntensities(Node currentNode) {
		FileNode fNode = (FileNode) currentNode;
		ImagePlus imp = fNode.getImage();

		RoiManager cellsManager = new RoiManager(true);
		cellsManager.runCommand("Open", currentNode.getCellROIPath());

		ImageStack stack = imp.getStack();
		int stackSize = stack.getSize(); //

		ResultsTable SUM = new ResultsTable();
		ResultsTable AVERAGE = new ResultsTable();
		ResultsTable AREA = new ResultsTable();
		ResultsTable STDDEV = new ResultsTable();
		ResultsTable MAX = new ResultsTable();
		ResultsTable MIN = new ResultsTable();

		// table of Rois

		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> table = (Hashtable<String, Roi>) cellsManager
				.getROIs();
		// for each ROI

		for (String label : table.keySet()) {

			Roi roi = table.get(label);

			// System.out.println(label);
			for (int k = 1; k <= stackSize; k++) {
				ImageProcessor ip = stack.getProcessor(k);
				ImageProcessor mask = roi != null ? roi.getMask() : null;
				Rectangle r = roi != null ? roi.getBounds() : new Rectangle(0,
						0, ip.getWidth(), ip.getHeight());

				// PeakFitter fitter = new PeakFitter();
				// fitter.run(ip);

				double[] average = getAverage(ip, mask, r);

				// System.out.println(k + "-- "+ average[0] + "---- " +
				// average[1] + "---- " + average[2] + "---- ");
				// SaveStats in Files
				/**
				 * Tables will have the format.\ Stats.568.MAX
				 * Stats.568.AVG..etc
				 * 
				 * Frame\Cell 1-1 1-2 2-1 ... 1 501 2111 5152 2 445 1800 4521 .
				 * . . . . . . . . . . .
				 * 
				 */
				SUM.setValue(label, k - 1, average[0]);

				AVERAGE.setValue(label, k - 1, average[1]);
				STDDEV.setValue(label, k - 1, average[2]);
				AREA.setValue(label, k - 1, average[3]);
				MIN.setValue(label, k - 1, average[4]);
				MAX.setValue(label, k - 1, average[5]);

			}

		}

		// Create a folder to Store the Results
		File outPutDir = new File(currentNode.getOutputFolder()+ File.separator + "Cells");
		outPutDir.mkdirs();


		try {

			SUM.saveAs(outPutDir + File.separator + currentNode.getName() +".Cell.SUM.csv");
			AVERAGE.saveAs(outPutDir + File.separator +  currentNode.getName()  +".Cell.AVERAGE.csv" );
			AREA.saveAs(outPutDir + File.separator + currentNode.getName() +".Cell.AREA.csv" );
			STDDEV.saveAs(outPutDir + File.separator + currentNode.getName()  +".Cell.STDDEV.csv");
			MAX.saveAs(outPutDir + File.separator +  currentNode.getName()  +".Cell.MAX.csv");
			MIN.saveAs(outPutDir + File.separator + currentNode.getName() + ".Cell.MIN.csv"	);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		traces.reset();
	}

	/**
	 * Gets the average.
	 *
	 * @param ip the ip
	 * @param mask the mask
	 * @param r the r
	 * @return the average
	 */
	private static double[] getAverage(ImageProcessor ip, ImageProcessor mask,
			Rectangle r) {
		double[] results = new double[7];
		results[0] = 0; // sum of all pixels
		results[1] = 0; // average
		results[2] = 0; // std deviation
		results[3] = 0; // number of pixels
		results[4] = 0; // min
		results[5] = 0; // max
		results[6] = 0; // will store slice value

		List<Double> ListOfValues = new ArrayList<Double>();

		for (int y = 0; y < r.height; y++) {
			for (int x = 0; x < r.width; x++) {
				if (mask == null || mask.getPixel(x, y) != 0) {

					double pixelValue = ip.getPixelValue(x + r.x, y + r.y);
					ListOfValues.add(pixelValue);
					results[0] += pixelValue;

					if (pixelValue > results[5]) {
						results[5] = pixelValue;
					}

					if (pixelValue < results[4]) {
						results[4] = pixelValue;
					}
					results[3]++;
				}
			}
		}
		results[1] = results[0] - results[3];
		results[2] = getSTDDEV(ListOfValues);

		return results;
	}

	/**
	 * Gets the stddev.
	 *
	 * @param listOfValues the list of values
	 * @return the stddev
	 */
	private static double getSTDDEV(List<Double> listOfValues) {
		int size = listOfValues.size();
		double sum = 0;

		List<Double> ListOfSquares = new ArrayList<Double>();

		for (int i = 0; i < size; i++) {
			double current = listOfValues.get(i);
			sum += current;
			ListOfSquares.add(current * current);

		}

		double average = sum / size;
		double sumSquareDiff = 0;

		for (int i = 0; i < size; i++) {
			double diference = listOfValues.get(i) - average;

			sumSquareDiff += diference * diference;
		}

		double s2 = sumSquareDiff / (size - 1);

		return s2;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Cell Intensity";
	}

	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new CellIntensityGUI(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();

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

		System.out.println("Does not apply to root");

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
