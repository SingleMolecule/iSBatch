/**
 * 
 */
package operations.peakFinder;


import ij.IJ;
import ij.ImagePlus;
import ij.io.RoiEncoder;
import ij.plugin.frame.RoiManager;

import java.io.File;
import java.util.HashMap;

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
		
		ImagePlus imp = IJ.openImage(node.getPath());
		peakFinder = new PeakFinder(useDiscoidal, 
				new DiscoidalAveragingFilter(imp.getWidth(),preferences.INNER_RADIUS , preferences.OUTER_RADIUS), 
				parseDouble(preferences.SNR_THRESHOLD ), parseDouble(preferences.INTENSITY_THRESHOLD), Integer.parseInt(preferences.DISTANCE_BETWEEN_PEAKS));
		
		peakFinder.setup(null, imp);
		peakFinder.run(imp.getProcessor());
		String nameToSave = node.getName()+ "_PeakROIs.zip";
		System.out.println("Saving peak Rois @ " +  node.getParentFolder() + File.separator + nameToSave );
		
		
		
		roiManager.runCommand("Save", node.getParentFolder() + File.separator + nameToSave);
		System.out.println("Saving peak Rois @ " + node.getFolder() + File.separator + nameToSave );
		
		
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

}
