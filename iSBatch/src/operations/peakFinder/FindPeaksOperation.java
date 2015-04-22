/**
 * 
 */
package operations.peakFinder;


import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import filters.NodeFilterInterface;
import analysis.PeakFinder;
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
public class FindPeaksOperation implements Operation {
	private FindPeaksGui dialog;
	private String channel;
	private String method;
	private boolean useCells;
	private boolean useDiscoidal;
	private DatabaseModel model;
	iSBatchPreferences preferences;
	PeakFinder peakFinder;
	RoiManager roiManager;
	int NUMBER_OF_OPERATIONS;
	int currentCount;
	public FindPeaksOperation(DatabaseModel treeModel) {
		this.model = treeModel;
	}

	/* (non-Javadoc)
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "Find Peaks";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		// String to parse:
		
		preferences = model.preferences;
		 dialog = new FindPeaksGui(node, preferences);
		if (dialog.isCanceled())
			return false;
		this.useCells = dialog.useCells;
		this.useDiscoidal = dialog.useDiscoidal;
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();
		NUMBER_OF_OPERATIONS = node.getNumberOfFoV();
		currentCount =0;
		return true;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Root)
	 */

	@Override
	public void visit(Root root) {
		System.out.println("Not applicable to root. ");
	}

	private void run(Node node) {
		//Run Peak Finder
//		
		ImagePlus imp = IJ.openImage(node.getPath());
		peakFinder = new PeakFinder(useDiscoidal, 
				new DiscoidalAveragingFilter(imp.getWidth(),preferences.INNER_RADIUS , preferences.OUTER_RADIUS), 
				parseDouble(preferences.SNR_THRESHOLD ), parseDouble(preferences.INTENSITY_THRESHOLD), Integer.parseInt(preferences.DISTANCE_BETWEEN_PEAKS));
		
	
		ArrayList<Roi> rois = findPeaks(peakFinder, imp);
		
		String nameToSave = node.getName().replace(".TIF", "")+ "_PeakROIs.zip";
		System.out.println("Saving peak Rois @ " +  node.getOutputFolder() + File.separator + nameToSave );
		
		try {
			saveRoisAsZip(rois, node.getOutputFolder() + File.separator + nameToSave);
		} catch (IOException e) {
			e.printStackTrace();
		}
		node.setProperty("PeakROIs", "node.getParentFolder() + File.separator + nameToSave");
		
		currentCount++;
		
	}

	@Override
	public void visit(Experiment experiment) {
		for(Sample sample : experiment.getSamples()){
			visit(sample);
			
		}
	}

	@Override
	public void visit(Sample sample) {
		for(FieldOfView fov : sample.getFieldOfView()){
			visit(fov);
		}
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		for(FileNode fileNode : fieldOfView.getImages(channel)){
			visit(fileNode);
		}
	}

	
	@Override
	public void visit(FileNode fileNode) {
		System.out.println("Peak Find: " + currentCount + " of " + NUMBER_OF_OPERATIONS);
		if(currentCount==NUMBER_OF_OPERATIONS){
			System.out.println("Peak Finder finished.");
		}
		IJ.showProgress(currentCount, NUMBER_OF_OPERATIONS);
		run(fileNode);
		
	}

	
	public static void main(String[] args) {
	}

	@Override
	public void visit(OperationNode operationNode) {
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

public static void runPlugInFilter(PlugInFilter filter, ImagePlus imp) {
		
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice <= stack.getSize(); slice++)
			runPlugInFilter(filter, stack.getProcessor(slice));
	}
	
	public static void runPlugInFilter(PlugInFilter filter, ImageProcessor ip) {
		filter.run(ip);
	}
	public static ArrayList<Roi> findPeaks(PeakFinder finder, ImagePlus imp) {
		
		ArrayList<Roi> allPeaks = new ArrayList<Roi>();
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice <= stack.getSize(); slice++) {
			ImageProcessor ip = stack.getProcessor(slice);
			
			for (Point p: finder.findPeaks(ip)) {
				PointRoi roi = new PointRoi(p.x, p.y);
				roi.setPosition(slice);
				allPeaks.add(roi);
			}
			
		}
		
		return allPeaks;
	}
	
	public static void saveRoisAsZip(ArrayList<Roi> rois, String filename) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filename));
		
		int i = 0;
		
		for (Roi roi: rois) {
			byte[] b = RoiEncoder.saveAsByteArray(roi);
			zos.putNextEntry(new ZipEntry(i + ".roi"));
			zos.write(b, 0, b.length);
			i++;
		}
		
		zos.close();
		
	}
	
	
	
	
	
}
