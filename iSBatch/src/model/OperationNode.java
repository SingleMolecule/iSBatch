/*
 * 
 */
package model;

import java.util.ArrayList;

import operations.Operation;

// TODO: Auto-generated Javadoc
/**
 * The Class OperationNode.
 */
public class OperationNode extends Node {

	/** The Constant type. */
	public static final String type = "Operation";
	
	/**
	 * Instantiates a new operation node.
	 *
	 * @param parent the parent
	 */
	public OperationNode(Node parent) {
		super(parent, type);
	}

	/**
	 * Accept.
	 *
	 * @param operation the operation
	 */
	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	@Override
	public int getNumberOfFoV() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Gets the field of view.
	 *
	 * @return the field of view
	 */
	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	@Override
	public ArrayList<Sample> getSamples() {
		// TODO Auto-generated method stub
		return null;
	}

}
