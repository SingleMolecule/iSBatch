package model;

import java.io.File;
import java.util.ArrayList;

import operations.Operation;


/**
 * The root class represents the root of all experiments.
 * A root node should have the following properties:
 * <dl>
 * <dt>path</dt><dd>the path where to store all output files</dd>
 * </dl>
 * 
 * @author C.M. Punter
 *
 */
public class Root extends Node {

	public static final String type = "Root";
	
	public Root(String outputFolder) {
		super(null, type);
		
		outputFolder += File.separator + "database_files";
		
		if (!new File(outputFolder).exists())
			new File(outputFolder).mkdirs();
		
		setProperty("name", "Database");
		setProperty("outputFolder", outputFolder);
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

	@Override
	public ArrayList<Sample> getSamples() {
			// TODO Auto-generated method stub
		
		return null;
	}
	
	
}
