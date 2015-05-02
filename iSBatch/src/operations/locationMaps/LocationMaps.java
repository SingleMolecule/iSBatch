/**
 * 
 */
package operations.locationMaps;


import java.util.HashMap;

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
 * The Class LocationMaps.
 *
 * @author VictorCaldas
 */
public class LocationMaps implements Operation {
	
	/** The dialog. */
	LocationMapsGui dialog;
	
	/** The channel. */
	private String channel;
	
	/** The method. */
	private String method;
	
	/** The custom filter. */
	private String customFilter;
	
	/** The mat file path. */
	private String matFilePath;
	
	/**
	 * Instantiates a new location maps.
	 *
	 * @param treeModel the tree model
	 */
	public LocationMaps(DatabaseModel treeModel) {
	}

	/* (non-Javadoc)
	 * @see context.ContextElement#getContext()
	 */
	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getName()
	 */
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Location Maps";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new LocationMapsGui(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();
		this.customFilter = dialog.getCustomFilter();
		this.matFilePath = dialog.getMatFilePath();
		return true;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {
		System.out.println("Operation finalized");

	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Root)
	 */

	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
		
		run(root);
		
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
	 * Run.
	 *
	 * @param node the node
	 */
	private void run(Node node) {
		System.out.println("Run class: " + channel + " using the method " + method);
		System.out.println(channel);
		System.out.println(method);
		System.out.println(customFilter);
		System.out.println(matFilePath);
		
		
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
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
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

}
