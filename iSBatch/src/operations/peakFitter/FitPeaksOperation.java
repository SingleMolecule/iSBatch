/**
 * 
 */
package operations.peakFitter;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

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
import model.iSBatchPreferences;

/**
 * @author VictorCaldas
 *
 */
public class FitPeaksOperation implements Operation {
	private PeakFitterGui dialog;
	private String channel;

	private DatabaseModel model;
	public iSBatchPreferences preferences;
	private int currentCount;
	private int NUMBER_OF_OPERATIONS;
	RoiManager cellManager;
	ImageStack stack;
	public FitPeaksOperation(DatabaseModel treeModel) {
		this.model = treeModel;
		this.preferences = model.preferences;

	}

	public static final double SIGMA_TO_FWHM = 2.0 * Math.sqrt(2.0 * Math
			.log(2));

	/*
	 * (non-Javadoc)
	 * 
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "Peak Fitter";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		// String to parse:

		dialog = new PeakFitterGui(node, preferences);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		NUMBER_OF_OPERATIONS = node.getNumberOfFoV();
		currentCount = 1;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#visit(model.Root)
	 */

	@Override
	public void visit(Root root) {
		System.out.println("Operation not defined at root level.");
	}

	private void run(Node node) {
		System.out.println("I'm running!");
		FileNode fnode = (FileNode) node;
		imp = fnode.getImage();
		stack = imp.getImageStack();

		ResultsTable PeakData = getPeaksOnStacks();

		imp.close();

		// Save raw table
		String nameToSave = fnode.getName().replace(".TIF", "")
				+ "_RawPeakTable.csv";
		
		try {
			System.out.println("Saving peak Rois @ " + fnode.getOutputFolder()
					+ File.separator + nameToSave);
			PeakData.saveAs(fnode.getOutputFolder()
					+ File.separator + nameToSave);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void visit(Experiment experiment) {
		for (Sample sample : experiment.getSamples()) {
			visit(sample);
		}
	}

	@Override
	public void visit(Sample sample) {
		for (FieldOfView fov : sample.getFieldOfView()) {
			visit(fov);
		}
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		if (iSBatchPreferences.insideCell) {
			// Load cellular ROIS from ZIP into manager

			addRoisToManager(fieldOfView.getCellularROIs());
		}

		for (FileNode fileNode : fieldOfView.getImages(channel)) {
			visit(fileNode);

		}
		cleanROIManager();
	}

	private void cleanROIManager() {
		if (cellManager != null) {
			cellManager.close();
			cellManager = new RoiManager(true);
		}
	}

	private void addRoisToManager(String cellularROIsPATH) {
		if (cellularROIsPATH == null) {
			// return no ROI
			System.out.println("No Cellular ROIs detected.");
		}
		// Adding ROIS to the manager
		cellManager = new RoiManager(true);
		cellManager = RoiManager.getInstance();
		if (cellManager == null) {
			cellManager = new RoiManager();
		}

		// get the list of Rois

		cellManager.runCommand("Open", cellularROIsPATH);

		// for (int j = 0; j < subSetROISize; j++) {
		//
		// String file = subSetROIS.get(j).getAbsolutePath();
		// RoiDecoder roiD = new RoiDecoder(file);
		//
		// manager.addRoi(roiD.getRoi());
		// }

	}

	private ImagePlus imp;

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
	public void visit(FileNode fileNode) {
		System.out.println("Peak Fitter: " + currentCount + " of "
				+ NUMBER_OF_OPERATIONS+".");
		

		run(fileNode);

		if (currentCount == NUMBER_OF_OPERATIONS) {
			System.out.println("Peak Finder finished.");
		}
		IJ.showProgress(currentCount, NUMBER_OF_OPERATIONS);
		
	}

	public ResultsTable getPeaksOnStacks() {
		System.out.println("Start getPeaks");
		ResultsTable PeakData = new ResultsTable();
		PeakData = fillData();

		return PeakData;

	}

	private ResultsTable fillData() {
		ResultsTable data = new ResultsTable();
		if(iSBatchPreferences.insideCell){
			data = peaksInsideCells();
		}
		else{
			data = getPeaks();
		}
		// TODO Auto-generated method stub
		return data;
	}

	private ResultsTable getPeaks() {
		ResultsTable PeakData = new ResultsTable();
		int stackSize = stack.getSize();
		
		for (int stackPosition = 1; stackPosition <= stackSize; stackPosition++) {
			ImageProcessor ip = stack.getProcessor(stackPosition);

			
//			
//			@SuppressWarnings("unchecked")
//			Hashtable<String, Roi> table = (Hashtable<String, Roi>) cellManager
//					.getROIs();

//			for (String label : table.keySet()) {
				// System.out.println(label);

//				Roi roi = table.get(label);

				ImageProcessor ip2 = imp.getProcessor();

//				ip2.setRoi(roi);

				DiscoidalAveragingFilter filter1 = new DiscoidalAveragingFilter(
						ip2.getWidth(), iSBatchPreferences.INNER_RADIUS,
						iSBatchPreferences.OUTER_RADIUS);

				// PeakFinder peakFinder = new PeakFinder(true, filter, 0,
				// PeakFinderThreshold, 3);
				// PeakFinder peakFinder2 = new PeakFinder(true, filter1, 0,
				// PeakFinderThreshold, 3);
				PeakFinder finder = new PeakFinder(
						iSBatchPreferences.useDiscoidalFiltering, filter1,
						iSBatchPreferences.SNR_THRESHOLD,
						iSBatchPreferences.INTENSITY_THRESHOLD,
						iSBatchPreferences.DISTANCE_BETWEEN_PEAKS);

				// ArrayList<Point> positions = peakFinder.findPeaks(ip);
				ArrayList<Point> positions = finder.findPeaks(ip2);
				// System.out.println("--" + positions.size() + "," +
				// positions2.size());

				for (int j = 0; j < positions.size(); j++) {
					// System.out.println("here");
					// fit peak

					double[] parameters = new double[6];
					double[] errors = new double[6];

					for (int k = 0; k < parameters.length; k++)
						parameters[k] = Double.NaN;

					int x = positions.get(j).x;
					int y = positions.get(j).y;
					// System.out.println( x+ "," + y + "*");

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

					if (position_x < 1 || position_x > (ip.getWidth() - 1)
							|| Double.isNaN(position_x))
						continue;

					double position_y = parameters[3];

					if (position_y < 1 || position_x > (ip.getHeight() - 1)
							|| Double.isNaN(position_y))
						continue;

					double fwhmx = parameters[4] * SIGMA_TO_FWHM;

					if (fwhmx < 1 || fwhmx > 6 || Double.isNaN(fwhmx))
						continue;

					double fwhmy = parameters[5] * SIGMA_TO_FWHM;
					if (fwhmy < 1 || fwhmy > 6 || Double.isNaN(fwhmy))
						continue;

					PeakData.incrementCounter();
					
					addDataToTable(PeakData);
					// PeakData.addValue("BFSlice",Folder);
					// PeakData.addLabel("Cell",
					// label.substring(label.lastIndexOf('-') + 1));
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

//		}
		return PeakData;
	}

	private ResultsTable peaksInsideCells() {
		ResultsTable PeakData = new ResultsTable();
		int stackSize = stack.getSize();
		
		for (int stackPosition = 1; stackPosition <= stackSize; stackPosition++) {
			ImageProcessor ip = stack.getProcessor(stackPosition);

			
			
			@SuppressWarnings("unchecked")
			Hashtable<String, Roi> table = (Hashtable<String, Roi>) cellManager
					.getROIs();

			for (String label : table.keySet()) {
				// System.out.println(label);

				Roi roi = table.get(label);

				ImageProcessor ip2 = imp.getProcessor();

				ip2.setRoi(roi);

				DiscoidalAveragingFilter filter1 = new DiscoidalAveragingFilter(
						ip2.getWidth(), iSBatchPreferences.INNER_RADIUS,
						iSBatchPreferences.OUTER_RADIUS);

				// PeakFinder peakFinder = new PeakFinder(true, filter, 0,
				// PeakFinderThreshold, 3);
				// PeakFinder peakFinder2 = new PeakFinder(true, filter1, 0,
				// PeakFinderThreshold, 3);
				PeakFinder finder = new PeakFinder(
						iSBatchPreferences.useDiscoidalFiltering, filter1,
						iSBatchPreferences.SNR_THRESHOLD,
						iSBatchPreferences.INTENSITY_THRESHOLD,
						iSBatchPreferences.DISTANCE_BETWEEN_PEAKS);

				// ArrayList<Point> positions = peakFinder.findPeaks(ip);
				ArrayList<Point> positions = finder.findPeaks(ip2);
				// System.out.println("--" + positions.size() + "," +
				// positions2.size());

				for (int j = 0; j < positions.size(); j++) {
					// System.out.println("here");
					// fit peak

					double[] parameters = new double[6];
					double[] errors = new double[6];

					for (int k = 0; k < parameters.length; k++)
						parameters[k] = Double.NaN;

					int x = positions.get(j).x;
					int y = positions.get(j).y;
					// System.out.println( x+ "," + y + "*");

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

					if (position_x < 1 || position_x > (ip.getWidth() - 1)
							|| Double.isNaN(position_x))
						continue;

					double position_y = parameters[3];

					if (position_y < 1 || position_x > (ip.getHeight() - 1)
							|| Double.isNaN(position_y))
						continue;

					double fwhmx = parameters[4] * SIGMA_TO_FWHM;

					if (fwhmx < 1 || fwhmx > 6 || Double.isNaN(fwhmx))
						continue;

					double fwhmy = parameters[5] * SIGMA_TO_FWHM;
					if (fwhmy < 1 || fwhmy > 6 || Double.isNaN(fwhmy))
						continue;

					PeakData.incrementCounter();
					
					addDataToTable(PeakData);
					// PeakData.addValue("BFSlice",Folder);
					// PeakData.addLabel("Cell",
					// label.substring(label.lastIndexOf('-') + 1));
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

	private void addDataToTable(ResultsTable peakData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

	}

}
