/**
 * 
 */
package operations.flatImages;

import ij.IJ;
import ij.ImagePlus;
import imageOperations.NodeToImageStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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

/**
 * @author VictorCaldas
 *
 */
public class SetBackGround implements Operation {
	SetBackgroundGui dialog;

	private String channel;
	private String method;
	private String imageTag;

	public SetBackGround(DatabaseModel treeModel) {
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
		return "Set BackGround";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new SetBackgroundGui(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.imageTag = dialog.getImageTag();
		this.method = dialog.getMethod();

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		System.out.println("Operation finalized");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#visit(model.Root)
	 */

	@Override
	public void visit(Root root) {
		// Does not apply to ROOT.
	}

	@Override
	public void visit(Experiment experiment) {
		run(experiment);

	}

	private void run(Node node) {
		System.out.println("Run class: " + channel + " using the method "
				+ method);
		// Get all images with the same characteristics

		if (method.equalsIgnoreCase("Load Image")) {
			// TODO: add gui to ask file from the user

			ImagePlus imp = null;
			save(node,imp);
			
			
		} else if (method.equalsIgnoreCase("Average Images")) {

			ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
					channel, imageTag));
			// get ImageStack from the ArrayList
			
			
			
			
			NodeToImageStack temp = new NodeToImageStack(filenodes, channel);
			
			
			
			ImagePlus imp = temp.getImagePlus();

			save(node, imp);
		}

	}

	private void save(Node node, ImagePlus imp) {
		//Properly save and keep track of that file now.
		IJ.saveAsTiff(imp, node.getOutputFolder() + File.separator + imp.getTitle());
		
	}

	@Override
	public void visit(Sample sample) {
		run(sample);

	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		System.out.println("Call load image or ignore");
	}

	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
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

}
