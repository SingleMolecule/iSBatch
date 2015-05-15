/*
 * 
 */
package operations;

import java.util.HashMap;

import context.ContextElement;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Interface Operation.
 */
public interface Operation extends ContextElement {
	
	/** The name. */
	String name = null; 
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean setup(Node node);
	
	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	public void finalize(Node node);
	
	/**
	 * Gets the created nodes.
	 *
	 * @return the created nodes
	 */
	public Node[] getCreatedNodes();
	
	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public HashMap<String, String> getParameters();
	
	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	public void visit(Root root);
	
	/**
	 * Visit.
	 *
	 * @param experiment the experiment
	 */
	public void visit(Experiment experiment);
	
	/**
	 * Visit.
	 *
	 * @param sample the sample
	 */
	public void visit(Sample sample);
	
	/**
	 * Visit.
	 *
	 * @param fieldOfView the field of view
	 */
	public void visit(FieldOfView fieldOfView);
	
	/**
	 * Visit.
	 *
	 * @param fileNode the file node
	 */
	public void visit(FileNode fileNode);
	
	/**
	 * Visit.
	 *
	 * @param operationNode the operation node
	 */
	public void visit(OperationNode operationNode);
	
}
