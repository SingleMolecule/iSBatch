/*
 * 
 */
package model;

import java.util.ArrayList;

import operations.Operation;

// TODO: Auto-generated Javadoc
/**
 * The Class Sample.
 */
public class Sample extends Node {

	/** The Constant type. */
	public static final String type = "Sample";
	
	/**
	 * Instantiates a new sample.
	 *
	 * @param parent the parent
	 */
	public Sample(Experiment parent) {
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
	 * Gets the experiment type.
	 *
	 * @return the experiment type
	 */
	public String getExperimentType(){
		return this.getParent().getProperty("type");
	}

	/**
	 * Gets the field of view.
	 *
	 * @return the field of view
	 */
	public  ArrayList<FieldOfView> getFieldOfView() {
		ArrayList<Node> nodes = this.getChildren();
		
		//convert to sample array
		ArrayList<FieldOfView> fov = new ArrayList<FieldOfView>();
		for(Node node : nodes){
			fov.add((FieldOfView) node);
			}
		return fov;
	}

	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	@Override
	public int getNumberOfFoV() {
		return this.getChildren().size();
	}

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	@SuppressWarnings("null")
	@Override
	public ArrayList<Sample> getSamples() {
		ArrayList<Sample> samples = null;
		samples.add(this);
		return samples;
	}

	/**
	 * Gets the beam profile.
	 *
	 * @param channel the channel
	 * @return the beam profile
	 */
	public String getBeamProfile(String channel){
		if(this.getProperty(channel+"_BeamProfile")== null){
			return this.getParent().getBeamProfile(channel);
		}
		return this.getProperty(channel+"_BeamProfile");
	}

	/**
	 * Gets the experiment name.
	 *
	 * @return the experiment name
	 */
	public String getExperimentName(){
		return this.getParent().getName();
	}

	/**
	 * Gets the sample name.
	 *
	 * @return the sample name
	 */
	public String getSampleName(){
		return this.getName();
	}
	
}
