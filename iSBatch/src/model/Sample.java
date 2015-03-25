package model;

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
	
}
