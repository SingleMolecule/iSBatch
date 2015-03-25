package model;

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

}