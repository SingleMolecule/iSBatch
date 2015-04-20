package model;

import java.util.ArrayList;

import filters.NodeFilter;
import operations.Operation;

/**
 * The experiment class represents an experiment which contains samples, field of views and files.
 * An experiment can have samples, channels and file nodes as children.
 * An experiment node should have the following properties:
 * <dl>
 * <dt>path</dt><dd>the root path of the experiment (this is optional). This is the folder where all the raw measurement files come from.</dd>
 * <dt>name</dt><dd>the descriptive name for this experiment (usually the name of the root folder)</dd>
 * </dl>
 * 
 * @author C.M. Punter
 *
 */
public class Experiment extends Node {

	public static final String type = "Experiment";
	
	public Experiment(Root parent) {
		super(parent, type);
	}

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	public  ArrayList<Node> getSamples() {
		return this.getChildren(new NodeFilter(Sample.type));
	}
	
}
