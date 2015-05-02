/*
 * 
 */
package model;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Interface NodeInterface.
 */
public interface NodeInterface {
	
	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	public int getNumberOfFoV();
	
	/**
	 * Gets the field of view.
	 *
	 * @return the field of view
	 */
	public ArrayList<FieldOfView> getFieldOfView();
	
	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	public ArrayList<Sample> getSamples();
}
