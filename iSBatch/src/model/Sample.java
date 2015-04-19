package model;

import java.util.ArrayList;

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

	public  ArrayList<Node> getFoVs() {
		return this.getChildren();
	}


	
}
