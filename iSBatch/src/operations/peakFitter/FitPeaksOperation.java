/*
 * 
 */
package operations.peakFitter;

/**
 * 
 */

import filters.GenericFilter;
import iSBatch.iSBatchPreferences;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import analysis.PeakFinder;
import analysis.PeakFitter;
import operations.Operation;
import process.DiscoidalAveragingFilter;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class FitPeaksOperation.
 *
 * @author VictorCaldas
 */
public class FitPeaksOperation implements Operation {
	
	/** The dialog. */
	private PeakFitterGui dialog;
	
	/** The channel. */
	private String channel;
	
	/** The use discoidal. */
	private boolean useDiscoidal;
	
	/** The model. */
	private DatabaseModel model;
	
	/** The preferences. */
	iSBatchPreferences preferences;
	
	/** The peak finder. */
	PeakFinder peakFinder;
	
	/** The roi manager. */
	RoiManager roiManager;
	
	/** The number of operations. */
	int NUMBER_OF_OPERATIONS;
	
	/** The current count. */
	int currentCount;
	
	/** The image tag. */
	private String imageTag;
	
	/** The custom search. */
	private String customSearch;
	
	/** The tags. */
	ArrayList<String> tags = new ArrayList<String>();
	
	/** The current node. */
	private Node currentNode;
	
	/** The cell roi manager. */
	RoiManager cellRoiManager;
	
	/** The peaks manager. */
	RoiManager peaksManager;

	/**
	 * Instantiates a new fit peaks operation.
	 *
	 * @param treeModel the tree model
	 */
	public FitPeaksOperation(DatabaseModel treeModel) {
		this.model = treeModel;
	}

	/** The Constant SIGMA_TO_FWHM. */
	public static final double SIGMA_TO_FWHM = 2.0 * Math.sqrt(2.0 * Math
			.log(2));

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
		return "Peak Fitter";
	}

	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new PeakFitterGui(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();
		NUMBER_OF_OPERATIONS = node.getNumberOfFoV();
		currentCount = 1;

		return true;
	}

	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {

	}

	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
		System.out.println("Not applicable to root. ");
	}

	/**
	 * Run.
	 *
	 * @param node the node
	 */
	private void run(Node node) {
		String extention = null;
		// Run Peak Finder
		//
		ResultsTable fittedPeaks = null;
		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, tags, extention, customSearch));
		for (Node currentNode : filenodes) {
			System.out.println(currentNode.getName());
			fittedPeaks = getFittedPeaks(currentNode);
			
			

		// Save raw table
		String nameToSave = currentNode.getName().replace(".tif", "")
				+ "_PeakTable.csv";

		try {
			System.out.println("Saving peak Rois @ " + currentNode.getOutputFolder()
					+ File.separator + nameToSave);
			fittedPeaks.saveAs(currentNode.getOutputFolder() + File.separator
					+ nameToSave);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fittedPeaks.reset();
		}
		

	}

	/**
	 * Gets the fitted peaks.
	 *
	 * @param node the node
	 * @return the fitted peaks
	 */
	private ResultsTable getFittedPeaks(Node node) {
		FileNode fnode = (FileNode) node;
		this.currentNode = node;
		ImagePlus imp = fnode.getImage();
		System.out.println(fnode.getPath());
		// Check if calculation will be done inside cells and load the manager
		if (iSBatchPreferences.insideCell) {
			cellRoiManager = new RoiManager(true);
			cellRoiManager.runCommand("Open", node.getCellROIPath());

		}

		if (iSBatchPreferences.insideCell) {
			return getPeaks(imp);
		}

		return getPeaks(imp);
	}

	/**
	 * Gets the peaks.
	 *
	 * @param imp the imp
	 * @return the peaks
	 */
	private ResultsTable getPeaks(ImagePlus imp) {
		ResultsTable PeakData = new ResultsTable();
		ImageStack stack = imp.getStack();
		int stackSize = stack.getSize();
		for (int stackPosition = 1; stackPosition <= stackSize; stackPosition++) {
			ImageProcessor ip = stack.getProcessor(stackPosition);
			@SuppressWarnings("unchecked")
			Hashtable<String, Roi> table = (Hashtable<String, Roi>) cellRoiManager
					.getROIs();
			for (String label : table.keySet()) {

				Roi roi = table.get(label);
				// ImageProcessor ip2 = imp.getProcessor();
				ip.setRoi(roi);

				DiscoidalAveragingFilter filter1 = new DiscoidalAveragingFilter(
						ip.getWidth(), iSBatchPreferences.INNER_RADIUS,
						iSBatchPreferences.OUTER_RADIUS);

				PeakFinder finder = new PeakFinder(
						iSBatchPreferences.useDiscoidalFiltering, filter1,
						iSBatchPreferences.SNR_THRESHOLD,
						iSBatchPreferences.INTENSITY_THRESHOLD,
						iSBatchPreferences.DISTANCE_BETWEEN_PEAKS);
				
				
				
				ArrayList<Point> positions = finder.findPeaks(ip);
				
				for (int j = 0; j < positions.size(); j++) {
					// fit peak
					
					double[] parameters= new double[6];
					double[] errors = new double[6];
					
					for (int k = 0; k < parameters.length; k++)
						parameters[k] = Double.NaN;
					
					int x = positions.get(j).x;
					int y = positions.get(j).y;
					
					parameters[2] = x;
					parameters[3] = y;

					ip.setRoi(x - 3, y - 3, 7, 7);

					PeakFitter.fitPeak(ip, parameters, errors);

					// Filtering conditions

					for (int k = 0; k < parameters.length; k++) {

						if (Double.isNaN(parameters[k])
								|| Double.isNaN(errors[k])
								|| Math.abs(errors[k]) > iSBatchPreferences.maxError[k])
							continue;

					}
					double position_x = parameters[2];
					
					if ( position_x<1 ||  position_x>(ip.getWidth()-1) || Double.isNaN(position_x))
						continue;
					
					double  position_y = parameters[3];
					
					if ( position_y<1 ||  position_x>(ip.getHeight()-1) || Double.isNaN(position_y))
						continue;
					
					double fwhmx = parameters[4] * SIGMA_TO_FWHM;
					
						if (fwhmx<1 || fwhmx>6  || Double.isNaN(fwhmx))
							continue;
						
					double fwhmy = parameters[5] * SIGMA_TO_FWHM;
						if (fwhmy<1 || fwhmy>6 || Double.isNaN(fwhmy))
							continue;
						
						
						//Add values to table
						PeakData.incrementCounter();

//						addDataToTable(PeakData);
						PeakData.addValue("Experiment", currentNode.getExperimentName());
						PeakData.addValue("Sample", currentNode.getSampleName());
						PeakData.addValue("FoV", currentNode.getFieldOfViewName());
						PeakData.addValue("Cell", label);
						PeakData.addValue("slice", stackPosition);
						// PeakData.addValue("ExperimentIndex",
						// ExperimentIndexNumber);
						PeakData.addValue("baseline", parameters[0]);
						PeakData.addValue("height", parameters[1]);
						PeakData.addValue("x", parameters[2]);
						PeakData.addValue("y", parameters[3]);
						PeakData.addValue("sigma_x", parameters[4]);
						PeakData.addValue("sigma_y", parameters[5]);

						PeakData.addValue("fwhm_x", fwhmx);
						PeakData.addValue("fwhm_y", fwhmy);
						PeakData.addValue("fwhm", (fwhmx + fwhmy) / 2);

						PeakData.addValue("error_baseline", errors[0]);
						PeakData.addValue("error_height", errors[1]);
						PeakData.addValue("error_x", errors[2]);
						PeakData.addValue("error_y", errors[3]);
						PeakData.addValue("error_sigma_x", errors[4]);
						PeakData.addValue("error_sigma_y", errors[5]);

						double errorFwhmx = errors[4] * SIGMA_TO_FWHM;
						double errorFwhmy = errors[5] * SIGMA_TO_FWHM;

						PeakData.addValue("error_fwhm_x", errorFwhmx);
						PeakData.addValue("error_fwhm_y", errorFwhmy);
						PeakData.addValue(
								"error_fwhm",
								Math.sqrt(errorFwhmx * errorFwhmx + errorFwhmy
										* errorFwhmy) / 2);

						// table.addValue("z", zScale * (fwhmx - fwhmy));
						// table.addValue("error_z", zScale * Math.sqrt(errorFwhmx *
						// errorFwhmx + errorFwhmy * errorFwhmy));

						PeakData.addValue("slice", stackPosition);
					
					
				}
			}

		}
		return PeakData;
	}

	/**
	 * Peaks inside cells.
	 *
	 * @param imp the imp
	 * @return the results table
	 */
	private ResultsTable peaksInsideCells(ImagePlus imp) {
		// TODO Auto-generated method stub
		return null;
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
		// System.out.println("Peak Find: " + currentCount + " of "
		// + NUMBER_OF_OPERATIONS);
		// if (currentCount == NUMBER_OF_OPERATIONS) {
		// System.out.println("Peak Fitter finished.");
		// }
		// IJ.showProgress(currentCount, NUMBER_OF_OPERATIONS);
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
	 * Parses the double.
	 *
	 * @param str the str
	 * @return the double
	 * @throws NumberFormatException the number format exception
	 */
	private double parseDouble(String str) throws NumberFormatException {
		double toReturn = 0;
		// System.out.println(str);
		if (!str.equalsIgnoreCase("") || !str.equals(null)) {
			try {
				toReturn = Double.parseDouble(str);
				// System.out.println("Value parsed :" + toReturn);
			} catch (NumberFormatException ex) {
				System.err.println("Ilegal input");
				toReturn = 0;
				// Discard input or request new input ...
				// clean up if necessary
			}
		}

		return toReturn;
	}

	/**
	 * Run plug in filter.
	 *
	 * @param filter the filter
	 * @param imp the imp
	 */
	public static void runPlugInFilter(PlugInFilter filter, ImagePlus imp) {

		ImageStack stack = imp.getImageStack();

		for (int slice = 1; slice <= stack.getSize(); slice++)
			runPlugInFilter(filter, stack.getProcessor(slice));
	}

	/**
	 * Run plug in filter.
	 *
	 * @param filter the filter
	 * @param ip the ip
	 */
	public static void runPlugInFilter(PlugInFilter filter, ImageProcessor ip) {
		filter.run(ip);
	}

	/**
	 * Find peaks.
	 *
	 * @param finder the finder
	 * @param imp the imp
	 * @return the array list
	 */
	public static ArrayList<Roi> findPeaks(PeakFinder finder, ImagePlus imp) {

		ArrayList<Roi> allPeaks = new ArrayList<Roi>();
		ImageStack stack = imp.getImageStack();

		for (int slice = 1; slice <= stack.getSize(); slice++) {
			ImageProcessor ip = stack.getProcessor(slice);

			for (Point p : finder.findPeaks(ip)) {
				PointRoi roi = new PointRoi(p.x, p.y);
				roi.setPosition(slice);
				allPeaks.add(roi);
			}

		}

		return allPeaks;
	}

	/**
	 * Save rois as zip.
	 *
	 * @param rois the rois
	 * @param filename the filename
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void saveRoisAsZip(ArrayList<Roi> rois, String filename)
			throws IOException {
		ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(filename));

		int i = 0;

		for (Roi roi : rois) {
			byte[] b = RoiEncoder.saveAsByteArray(roi);
			zos.putNextEntry(new ZipEntry(i + ".roi"));
			zos.write(b, 0, b.length);
			i++;
		}

		zos.close();

	}

}
