package operation.focusLifetime;

import filters.GenericFilter;
import iSBatch.iSBatchPreferences;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.measure.ResultsTable;
import ij.plugin.ZProjector;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import analysis.PeakFinder;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;
import operations.peakFitter.PeakFitterGui;
import process.DiscoidalAveragingFilter;

public class FocusLifetimes implements Operation {
	private FocusLifetimeGUI dialog;
	private String channel;
	private String customSearch;
	private ArrayList<String> tags;

	public FocusLifetimes(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	ResultsTable traces = new ResultsTable();
	ResultsTable tracesCorrected = new ResultsTable();

	private void run(Node node) {

		// First, get a list of all files to execute
		String extention = null;
		// Run Peak Finder
		//
		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, tags, extention, customSearch));

		// Generate Averages
		for (Node currentNode : filenodes) {
			System.out.println(currentNode.getName());
			FileNode fNode = (FileNode) currentNode;
			ImagePlus imp = fNode.getImage();
			traces = new ResultsTable();
			tracesCorrected = new ResultsTable();

			ZProjector projector = new ZProjector(imp);
			projector.setMethod(ZProjector.AVG_METHOD);
			projector.doProjection();
			// Create a folder to Store the Results
			File outPutDir = new File(currentNode.getOutputFolder());
			outPutDir.mkdirs();

			ImagePlus AVGImp = projector.getProjection();
			IJ.saveAsTiff(AVGImp, currentNode.getOutputFolder() + File.separator + "AVG_"+ currentNode.getName());
			// Detect Peaks in that single image
			boolean useDiscoidal = true;
			PeakFinder peakFinder = new PeakFinder(useDiscoidal,
					new DiscoidalAveragingFilter(AVGImp.getWidth(),
							iSBatchPreferences.INNER_RADIUS,
							iSBatchPreferences.OUTER_RADIUS),
					parseDouble(iSBatchPreferences.SNR_THRESHOLD),
					parseDouble(iSBatchPreferences.INTENSITY_THRESHOLD),
					Integer.parseInt(iSBatchPreferences.DISTANCE_BETWEEN_PEAKS));

			// Now I have all peaks detected in that Image.

			ArrayList<PointRoi> rois = findPeaks(peakFinder, AVGImp);
			

			RoiManager detectedPeaks = new RoiManager(true);
			//	Add all peaks to a Manager
			for (PointRoi roi : rois) {
				detectedPeaks.addRoi(roi);
			}
			
			detectedPeaks.runCommand("Save",currentNode.getOutputFolder() + File.separator
						+ "Pre.zip");
			System.out.println(currentNode.getParentFolder()+ File.separator + "cellRoi.zip");
			File roisFile = new File(currentNode.getOutputFolder()+ File.separator + "cellRoi.zip");
			if ( roisFile.exists()){
				System.out.println("Roi Found");
				currentNode.getParent().setCellROIPath(roisFile.getAbsolutePath());
			}
			if (!currentNode.getCellROIPath().isEmpty()) {
				System.out.println(currentNode.getCellROIPath());
				RoiManager cellRoiManager = new RoiManager(true);
				cellRoiManager.runCommand("Open", currentNode.getCellROIPath());

				RoiManager results = PeaksInsideCells(cellRoiManager, detectedPeaks);
				detectedPeaks.close();
				detectedPeaks = results;
				
			}

			
			detectedPeaks.runCommand("Save",currentNode.getOutputFolder() + File.separator
					+ "Post.zip");
			
			RoiManager debugManager = new RoiManager(true);
			
		
			// Get the trace for each peak, i.e. loop through all rois. Also,
			// draw a box around then.
			// Not checking Inside cells yet.

			ImageStack stack = imp.getStack();
			int stackSize = stack.getSize(); //

			for (int k = 1; k <= stackSize; k++) {
				// Get that particular stack
				ImageProcessor ip = stack.getProcessor(k);
				traces.incrementCounter();
				tracesCorrected.incrementCounter();

				traces.addValue("Frame", k);
				tracesCorrected.addValue("Frame", k);
				// Now, get value for each ROI

				@SuppressWarnings("unchecked")
				Hashtable<String, Roi> table = (Hashtable<String, Roi>) detectedPeaks
						.getROIs();

				for (String label : table.keySet()) {
					Roi tempRoi = table.get(label);
					Roi roi = new OvalRoi(tempRoi.getXBase() - 2, tempRoi.getYBase() - 2, 4, 4);
					debugManager.addRoi(roi);
					
					
					// Using 3x3 ROI Oval
					ImageProcessor mask = roi != null ? roi.getMask() : null;
					Rectangle r = roi != null ? roi.getBounds()
							: new Rectangle(0, 0, ip.getWidth(), ip.getHeight());

					double[] average = getAverage(ip, mask, r);

					traces.addValue(label, average[1]);
					tracesCorrected.addValue(label, average[1]);

				}
			}
//
			// save the table

			// Save raw table
			String nameToSave = currentNode.getName().replace(".tif", "")
					+ "Trace.csv";
			String nameToSave2 = currentNode.getName().replace(".tif", "")
					+ "TraceCorrected.csv";
			String nameToSave3 = currentNode.getName().replace(".tif", "")
					+"ImmobileFoci.zip";
			
			try {
				
				debugManager.runCommand("Save",currentNode.getOutputFolder() + File.separator
						+ nameToSave3 );
				debugManager.close();
				System.out.println("Saving peak Rois @ "
						+ currentNode.getOutputFolder() + File.separator
						+ nameToSave);
				System.out.println("Saving peak Rois @ "
						+ currentNode.getOutputFolder() + File.separator
						+ nameToSave2);

				traces.saveAs(currentNode.getOutputFolder() + File.separator
						+ nameToSave);
				tracesCorrected.saveAs(currentNode.getOutputFolder()
						+ File.separator + nameToSave2);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			traces.reset();
			tracesCorrected.reset();
			detectedPeaks.close();
		}
		
	}

	private RoiManager PeaksInsideCells(RoiManager cellsManager,
			RoiManager allPeaksManager) {
		RoiManager filtered = new RoiManager(true);
		// check if peaks are inside cells
		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfCells = (Hashtable<String, Roi>) cellsManager
				.getROIs();
		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfPeaks = (Hashtable<String, Roi>) allPeaksManager
				.getROIs();

		// for some reason, RoiManager.getRoi does not work.
		for (String label : listOfCells.keySet()) {
			ShapeRoi currentCell = new ShapeRoi(listOfCells.get(label));

			for (String peakLabel : listOfPeaks.keySet()) {
				Roi currentPeak = listOfPeaks.get(peakLabel);
				if (currentCell.contains(currentPeak.getBounds().x,
						currentPeak.getBounds().y)) {
					filtered.addRoi(currentPeak);
				}
			}

		}
		return filtered;
	}

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

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Focus Lifetimes";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new FocusLifetimeGUI(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();

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

		System.out.println("Does not apply to root");

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

	public static ArrayList<PointRoi> findPeaks(PeakFinder finder, ImagePlus imp) {

		ArrayList<PointRoi> allPeaks = new ArrayList<PointRoi>();

		ImageProcessor ip = imp.getProcessor();

			for (Point p : finder.findPeaks(ip)) {
				PointRoi roi = new PointRoi(p.x, p.y);
				// roi.setPosition(slice);
				allPeaks.add(roi);

		}

		return allPeaks;
	}

}
