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

/**
 * @author VictorCaldas
 *
 */
public class FitPeaksOperation implements Operation {
	private PeakFitterGui dialog;
	private String channel;
	private String method;
	private double innerRadius;
	private double outerRadius;
	private double threshold;
	private double SNRthreshold;
	private double minDistance;
	private double selectionRadius;
	private boolean useCells;
	private boolean useDiscoidal;
	private double zScale;
	private double errorSigmaY;
	private double errorSigmaX;
	private double errorY;
	private double errorX;
	private double errorHeight;
	private double errorBaseline;
	private DatabaseModel model;

	public FitPeaksOperation(DatabaseModel treeModel) {
		this.model = treeModel;
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

		dialog = new PeakFitterGui(node);
		if (dialog.isCanceled())
			return false;
		this.innerRadius = dialog.getInnerRadius();
		this.outerRadius = dialog.getOuterRadius();
		this.threshold = dialog.getThreshold();
		this.SNRthreshold = dialog.getSNRThreshold();
		this.minDistance = dialog.getMinDistance();
		this.selectionRadius = dialog.getSelectionRadius();
		this.useCells = dialog.useCells;
		this.useDiscoidal = dialog.useDiscoidal;
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();

		this.zScale = dialog.getZScale();

		this.errorSigmaY = dialog.getErrorSigmaY();
		this.errorSigmaX = dialog.getErrorSigmaX();
		this.errorY = dialog.getErrorY();
		this.errorX = dialog.getErrorX();
		this.errorHeight = dialog.getErrorHeight();
		this.errorBaseline = dialog.getErrorBaseline();
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
//		System.out.println(innerRadius);
//		System.out.println(outerRadius);
//		System.out.println(threshold);
//		System.out.println(SNRthreshold);
//		System.out.println(minDistance);
//		System.out.println(selectionRadius);
//		System.out.println(useCells);
//		System.out.println(useDiscoidal);
//		System.out.println(channel);
//		System.out.println(method);
//
//		System.out.println(zScale);
//		System.out.println(errorSigmaY);
//		System.out.println(errorSigmaX);
//		System.out.println(errorY);
//		System.out.println(errorX);
//		System.out.println(errorHeight);
//		System.out.println(errorBaseline);
		
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
