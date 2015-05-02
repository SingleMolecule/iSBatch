/*
 * 
 */
package operation.cellOutlines;

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
 * The Class CellOutlines.
 */
public class CellOutlines implements Operation {

	/**
	 * Instantiates a new cell outlines.
	 *
	 * @param treeModel the tree model
	 */
	public CellOutlines(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Cell Outlines";
	}

	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
		// TODO Auto-generated method stub

	}

	/**
	 * Visit.
	 *
	 * @param experiment the experiment
	 */
	@Override
	public void visit(Experiment experiment) {
		// TODO Auto-generated method stub

	}

	/**
	 * Visit.
	 *
	 * @param sample the sample
	 */
	@Override
	public void visit(Sample sample) {
		// TODO Auto-generated method stub

	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		// TODO Auto-generated method stub

	}

	/**
	 * Visit.
	 *
	 * @param fileNode the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
		// TODO Auto-generated method stub

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
