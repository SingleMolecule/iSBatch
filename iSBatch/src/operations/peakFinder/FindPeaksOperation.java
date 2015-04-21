/**
 * 
 */
package operations.peakFinder;


import java.util.HashMap;

import filters.NodeFilterInterface;
import analysis.PeakFinder;
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
public class FindPeaksOperation implements Operation {
	private FindPeaksGui dialog;
	private String channel;
	private String method;
	private boolean useCells;
	private boolean useDiscoidal;
	private DatabaseModel model;
	
	PeakFinder peakFidnder;
	
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

		 dialog = new FindPeaksGui(node, model.preferences);
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

	}

	@Override
	public void visit(Experiment experiment) {
		for(Sample sample : experiment.getSamples()){
//			System.out.println(sample.getName());
			visit(sample);
		}
	}

	@Override
	public void visit(Sample sample) {
		for(FieldOfView fov : sample.getFieldOfView()){
//			System.out.println(fov.getName());
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
		System.out.println(fileNode.getProperty("name"));
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


}
