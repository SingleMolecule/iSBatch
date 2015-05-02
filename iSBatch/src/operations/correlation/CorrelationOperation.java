/**
 * 
 */
package operations.correlation;


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
 * The Class CorrelationOperation.
 *
 * @author VictorCaldas
 */
public class CorrelationOperation implements Operation {
	
	/** The dialog. */
	CorrelationOperationGui dialog;
	

	/** The filter1. */
	private String channel1,filter1;
	
	/** The filter2. */
	private String channel2,filter2;


	/** The type1. */
	private String type1;


	/** The type2. */
	private String type2;


	/** The project x. */
	private boolean projectX;


	/** The project y. */
	private boolean projectY;
	
	/**
	 * Instantiates a new correlation operation.
	 *
	 * @param treeModel the tree model
	 */
	public CorrelationOperation(DatabaseModel treeModel) {
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
		return "Correlation";
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
		dialog = new CorrelationOperationGui(node);
		if (dialog.isCanceled())
			return false;
		
		this.channel1 = dialog.getChannel1();
		this.channel2 = dialog.getChannel2();
		this.filter1 = dialog.getFilter1();
		this.filter2 = dialog.getFilter2();
		this.type1 = dialog.gettype1();
		this.type2 = dialog.gettype2();
		this.projectX = dialog.requireXProjection();
		this.projectY = dialog.requireYProjection();
		
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
		System.out.println(channel1);
		System.out.println(channel2);
		System.out.println(filter1);
		System.out.println(filter2);
		System.out.println(type1);
		System.out.println(type2);
		System.out.println( "Done");
		System.out.println(projectX);
		System.out.println(projectY);
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
