package model;

import java.util.ArrayList;

import filters.NodeFilter;
import operations.Operation;

public class Sample extends Node {

	public static final String type = "Sample";
	
	public Sample(Experiment parent) {
		super(parent, type);
	}

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}
	
	public String getExperimentType(){
		return this.getParent().getProperty("type");
	}

	public  ArrayList<FieldOfView> getFieldOfView() {
		ArrayList<Node> nodes = this.getChildren();
		
		//convert to sample array
		ArrayList<FieldOfView> fov = new ArrayList<FieldOfView>();
		for(Node node : nodes){
			fov.add((FieldOfView) node);
			}
		return fov;
	}

	@Override
	public int getNumberOfFoV() {
		return this.getChildren().size();
	}

	@SuppressWarnings("null")
	@Override
	public ArrayList<Sample> getSamples() {
		ArrayList<Sample> samples = null;
		samples.add(this);
		return samples;
	}

	public String getBeamProfile(String channel){
		if(this.getProperty(channel+"_BeamProfile")== null){
			return this.getParent().getBeamProfile(channel);
		}
		return this.getProperty(channel+"_BeamProfile");
	}


	
}
