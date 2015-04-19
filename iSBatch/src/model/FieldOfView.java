package model;

import java.util.ArrayList;

import operations.Operation;

public class FieldOfView extends Node {

	public static final String type = "FieldOfView";
	
	public FieldOfView(Sample parent) {
		super(parent, type);
	}
	
	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}
	
	public String getExperimentType(){
		return this.getParent().getParent().getProperty("type");
	}

	public  ArrayList<Node> getImages() {
		return this.getChildren();
	}
	
	
	
	
}
