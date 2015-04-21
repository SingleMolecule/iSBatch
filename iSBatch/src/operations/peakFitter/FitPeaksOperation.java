/**
 * 
 */
package operations.peakFitter;

import java.util.HashMap;

import filters.NodeFilterInterface;
import operations.Operation;
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
	
	public FitPeaksOperation(DatabaseModel treeModel) {
		this.model = treeModel;
		this.preferences = model.preferences;
	}

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
		return "Fit Peaks";
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

		
		System.out.println("Running on node : " + node.getProperty("name"));
			
		
		
	}

	@Override
	public void visit(Experiment experiment) {
		for(Node sample : experiment.getSamples()){
			System.out.println(sample.getProperty("name"));
			visit((Sample)sample);
			
		}
	}

	@Override
	public void visit(Sample sample) {
		for(Node fov : sample.getFoVs()){
			System.out.println(fov.getProperty("name"));
			visit((FieldOfView)fov);
		}
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		for(Node fileNode : fieldOfView.getImages()){
			System.out.println(fileNode.getProperty("name"));
			visit((FileNode)fileNode);
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

	/** The image file node filter. */
	private NodeFilterInterface imageFileNodeFilter = new NodeFilterInterface() {
		
		@Override
		public boolean accept(Node node) {

			if (!node.getType().equals(FileNode.type))
				return false;
			
			String ch = node.getProperty("channel");
			
			// check the channel of this file
			if (ch == null || !ch.equals(channel))
				return false;
			
			String path = node.getProperty("path");
			
			// check if this file is an image
			if (path == null || !(path.toLowerCase().endsWith(".tiff") || path.toLowerCase().endsWith(".tif")))
				return false;
			
			return true;
		}
	};
}
