package model;

import java.util.ArrayList;

import operations.Operation;

public class OperationNode extends Node {

	public static final String type = "Operation";
	
	public OperationNode(Node parent) {
		super(parent, type);
	}

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	@Override
	public int getNumberOfFoV() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		// TODO Auto-generated method stub
		return null;
	}

}
