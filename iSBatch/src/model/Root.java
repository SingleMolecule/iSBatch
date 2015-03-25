package model;

import java.io.File;

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
		
		outputFolder += "/database_files";
		
		if (!new File(outputFolder).exists())
			new File(outputFolder).mkdirs();
		
		setProperty("name", "Database");
		setProperty("outputFolder", outputFolder);
	}

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}
	
	
}
