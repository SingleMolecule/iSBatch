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
		ArrayList<Node> nodes = this.getChildren(new NodeFilter(FieldOfView.type));
		
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



	
}
